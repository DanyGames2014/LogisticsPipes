package net.danygames2014.logisticspipes.block.entity;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.config.Config;
import net.danygames2014.logisticspipes.gui.hud.TestHud;
import net.danygames2014.logisticspipes.interfaces.HUDRenderer;
import net.danygames2014.logisticspipes.interfaces.HUDRendererProvider;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.routing.RouteDestination;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.RoutingUtil;
import net.danygames2014.logisticspipes.util.tuple.Pair3;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class LogisticPipeBlockEntity extends PipeBlockEntity implements Router, HUDRendererProvider {
    public static int pipeCount = 0;
    public int updateOffset = 0;

    private boolean enabled = true;

    protected final LinkedList<Pair3<RoutedItem, Direction, ItemSendMode>> _sendQueue = new LinkedList<>();

    // Routing
    // Routing Table: Destination Router ID -> Next Hop Router ID
    public Long2ObjectOpenHashMap<RouteDestination> routingTable = new Long2ObjectOpenHashMap<>(32, 0.5F);
    // Neighbor Table: Router ID -> Neighbor Direction
    public Long2IntOpenHashMap neighborTable = new Long2IntOpenHashMap(32, 0.5F);

    public boolean updateNeighbors = true;

    // HUD Rendering
    private final TestHud HUD = new TestHud();

    public LogisticPipeBlockEntity() {
        init();
    }

    public LogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
        init();
    }

    public void init() {
        this.neighborTable.defaultReturnValue(-1);

        // Offset the updates of this pipe evenly between ticks
        pipeCount++;
        this.updateOffset = pipeCount % Config.NETWORK_CONFIG.neighborDetectionFrequency;

        setup();
    }

    @Override
    public void tick() {
        super.tick();

        if (updateNeighbors || (world.getTime() % Config.NETWORK_CONFIG.neighborDetectionFrequency == this.updateOffset)) {
            long nanoTime = System.nanoTime();
            updateNeighbors();
            System.err.println("Took " + ((System.nanoTime() - nanoTime) / 1000) + "us to update neighbors on time " + world.getTime());
            updateNeighbors = false;
        }

        if (getLogisticsModule() == null) {
            return;
        }

        if (!isEnabled()) {
            return;
        }

        getLogisticsModule().tick();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract ItemSendMode getItemSendMode();

    public void queueRoutedItem(RoutedItem routedItem, Direction from) {
        _sendQueue.addLast(new Pair3<>(routedItem, from, ItemSendMode.Normal));
        sendQueueChanged();
    }

    public void queueRoutedItem(RoutedItem routedItem, Direction from, ItemSendMode mode) {
        _sendQueue.addLast(new Pair3<>(routedItem, from, mode));
        sendQueueChanged();
    }

    protected void sendQueueChanged() {
    }

    @Override
    public HUDRenderer getRenderer() {
        return HUD;
    }

    public abstract LogisticsModule getLogisticsModule();

    public abstract void setup();

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void startWatching() {

    }

    @Override
    public void stopWatching() {

    }

    @Override
    public void updateConnections() {
        super.updateConnections();

        updateNeighbors();
    }

    // Router
    @Override
    public long getRouterId() {
        return RoutingUtil.packRouterId(this.x, this.y, this.z);
    }

    @Override
    public Long2ObjectOpenHashMap<RouteDestination> getRoutingTable() {
        return routingTable;
    }

    // Routing
    public void learnRoutesFromNeighbors() {
        for (long routerId : neighborTable.keySet()) {
            Router router = RoutingUtil.getRouter(world, routerId);

            if (router == null) {
                continue;
            }

            for (RouteDestination route : router.getRoutingTable().values()) {
                int metricToRouter = routingTable.containsKey(routerId) ? routingTable.get(routerId).metric : -1;

                if (metricToRouter != -1) {
                    learnRoute(route.routerId, routerId, router, route.metric + metricToRouter);
                }
            }
        }
    }

    public void learnRoute(long destinationId, long nextHopId, Router nextHopRouter, int metric) {
        if (destinationId == this.getRouterId()) {
            return;
        }

        if (routingTable.containsKey(destinationId)) {
            RouteDestination knownRoute = routingTable.get(destinationId);
            if (metric < knownRoute.metric) {
                routingTable.put(destinationId, new RouteDestination(nextHopId, nextHopRouter, metric));
            }
        } else {
            routingTable.put(destinationId, new RouteDestination(nextHopId, nextHopRouter, metric));
        }
    }

    public void updateNeighbors() {
        discoverNeighbors();

        boolean changed = false;
        for (Map.Entry<Direction, PipeConnectionType> connection : connections.entrySet()) {
            if (connection.getValue() == PipeConnectionType.NONE) {
                continue;
            }

            PipeConnectionType oldValue = connection.getValue();
            PipeConnectionType newValue = neighborTable.containsValue(connection.getKey().getId()) ? PipeConnectionType.ALTERNATE : PipeConnectionType.NORMAL;

            if (oldValue != newValue) {
                connections.put(connection.getKey(), newValue);
                changed = true;
            }
        }

        if (changed) {
            this.refreshRenderState();
            world.setBlockDirty(x, y, z);
        }
    }

    private final Queue<SearchNode> queue = new LinkedList<>();
    private final Long2IntOpenHashMap visited = new Long2IntOpenHashMap();
    private static final Direction[] DIRECTIONS = Direction.values();

    public void discoverNeighbors() {
        int maxRange = Config.NETWORK_CONFIG.neighborDetectionDistance;
        int maxMetric = maxRange * 16;

        // Clear old local data before a fresh scan
        this.neighborTable.clear();
        this.routingTable.clear();

        queue.clear();
        visited.clear();
        visited.defaultReturnValue(Integer.MAX_VALUE);

        // 1. Look in all 6 directions to start the search
        for (Direction side : DIRECTIONS) {
            int nx = this.x + side.getOffsetX();
            int ny = this.y + side.getOffsetY();
            int nz = this.z + side.getOffsetZ();

            if (this.connections.get(side) == PipeConnectionType.NONE) {
                continue;
            }

            int metric = RoutingUtil.getPipeMetric(world, nx, ny, nz);
            if (metric > 0) {
                queue.add(new SearchNode(nx, ny, nz, metric, side));
            }
        }

        while (!queue.isEmpty()) {
            SearchNode current = queue.poll();
            long pos = RoutingUtil.packRouterId(current.x, current.y, current.z);

            // We already have a better route to this router
            if (current.metric >= visited.get(pos) || current.metric > maxMetric) {
                continue;
            }
            visited.put(pos, current.metric);

            BlockEntity blockEntity = world.getBlockEntity(current.x, current.y, current.z);

            // If we find a router, check if the route is better than an existing one
            if (blockEntity instanceof Router router && blockEntity != this) {
                RouteDestination existing = routingTable.get(pos);
                int currentMetric = existing != null ? existing.metric : -1;

                // If we find a better route to a router, add it to the table
                if (currentMetric == -1 || current.metric < currentMetric) {
                    this.neighborTable.put(pos, current.firstDir.getId());
                    this.routingTable.put(pos, new RouteDestination(pos, router, current.metric));
                }

                // Don't search further than the nearest neighbor router
                continue;
            }

            // Continue walking thru Buildcraft Pipes
            if (blockEntity instanceof PipeBlockEntity pipe) {
                for (Direction side : DIRECTIONS) {
                    int nextX = current.x + side.getOffsetX();
                    int nextY = current.y + side.getOffsetY();
                    int nextZ = current.z + side.getOffsetZ();

                    int metric = RoutingUtil.getPipeMetric(pipe);
                    if (metric > 0) {
                        queue.add(new SearchNode(nextX, nextY, nextZ, current.metric + metric, current.firstDir));
                    }
                }
            }
        }
    }

    public void debugPrint(PlayerEntity player) {
        player.sendMessage("Router " + getRouterId());
        player.sendMessage("Neighbor Table: ");
        for (var entry : neighborTable.long2IntEntrySet()) {
            player.sendMessage(entry.getLongKey() + " -> " + Direction.byId(entry.getIntValue()));
        }
        player.sendMessage("Routing Table: ");
        for (var entry : routingTable.long2ObjectEntrySet()) {
            player.sendMessage(entry.getLongKey() + " -> " + entry.getValue());
        }
    }

    private record SearchNode(int x, int y, int z, int metric, Direction firstDir) {

    }
}
