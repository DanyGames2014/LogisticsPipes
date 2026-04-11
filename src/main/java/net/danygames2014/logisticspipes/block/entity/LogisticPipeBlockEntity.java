package net.danygames2014.logisticspipes.block.entity;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.block.pipe.LogisticsManager;
import net.danygames2014.logisticspipes.config.Config;
import net.danygames2014.logisticspipes.gui.hud.TestHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.routing.LogisticsNetwork;
import net.danygames2014.logisticspipes.routing.LogisticsNetworkManager;
import net.danygames2014.logisticspipes.routing.RouteDestination;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.*;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.danygames2014.logisticspipes.util.tuple.Pair3;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class LogisticPipeBlockEntity extends PipeBlockEntity implements Router, HUDRendererProvider, RequestItems {
    public static int pipeCount = 0;
    public int updateOffset = 0;

    private boolean enabled = true;

    public int statSessionSent;
    public int statSessionReceived;
    public int statSessionRelayed;

    public long statLifetimeSent;
    public long statLifetimeReceived;
    public long statLifetimeRelayed;

    protected int throttleTime = 20;
    private int throttleTimeLeft = 0;

    protected final LinkedList<Pair3<RoutedItem, Direction, ItemSendMode>> sendQueue = new LinkedList<>();

    // Routing
    // Routing Table: Destination Router ID -> Next Hop Router ID
    public Long2ObjectOpenHashMap<RouteDestination> routingTable = new Long2ObjectOpenHashMap<>(32, 0.5F);
    // Neighbor Table: Router ID -> Neighbor Direction
    public Long2IntOpenHashMap neighborTable = new Long2IntOpenHashMap(32, 0.5F);

    private final Long2LongOpenHashMap nextHopCache = new Long2LongOpenHashMap(32, 0.5F);
    private boolean advertiseRequired = true;

    public boolean updateNeighbors = true;

    // HUD Rendering
    private final TestHud HUD = new TestHud(this);

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
            updateNeighbors();
            updateNeighbors = false;
        }

        if (!sendQueue.isEmpty()) {
            if (getItemSendMode() == ItemSendMode.Normal) {
                Pair<RoutedItem, Direction> itemToSend = sendQueue.getFirst();
                sendRoutedItem(itemToSend.getValue1(), itemToSend.getValue2());
                sendQueue.removeFirst();
                for (int i = 0; i < 16 && !sendQueue.isEmpty() && sendQueue.getFirst().getValue3() == ItemSendMode.Fast; i++) {
                    if (!sendQueue.isEmpty()) {
                        itemToSend = sendQueue.getFirst();
                        sendRoutedItem(itemToSend.getValue1(), itemToSend.getValue2());
                        sendQueue.removeFirst();
                    }
                }
                sendQueueChanged();
            } else if (getItemSendMode() == ItemSendMode.Fast) {
                for (int i = 0; i < 16; i++) {
                    if (!sendQueue.isEmpty()) {
                        Pair<RoutedItem, Direction> itemToSend = sendQueue.getFirst();
                        sendRoutedItem(itemToSend.getValue1(), itemToSend.getValue2());
                        sendQueue.removeFirst();
                    }
                }
                sendQueueChanged();
            } else if (getItemSendMode() == null) {
                throw new UnsupportedOperationException("getItemSendMode() can't return null. " + this.getClass().getName());
            } else {
                throw new UnsupportedOperationException("getItemSendMode() returned unhandled value. " + getItemSendMode().name() + " in " + this.getClass().getName());
            }
        }

        if (!isEnabled()) {
            return;
        }

        if (--throttleTimeLeft <= 0) {
            throttledUpdateEntity();
            resetThrottle();
        }

        if (getLogisticsModule() == null) {
            return;
        }

        getLogisticsModule().tick();
    }

    public void throttledUpdateEntity() {
    }

    protected void resetThrottle() {
        throttleTimeLeft = throttleTime;
    }

    public Direction itemArrived(RoutedItem item) {
        if (item.getItemStack() != null) {
            statLifetimeReceived++;
            statSessionReceived++;
        }

        if (this instanceof RequireReliableTransport reliableTransport) {
            reliableTransport.itemArrived(ItemIdentifier.get(item.getItemStack()));
        }

        LinkedList<AdjacentBlockEntity> adjacentEntities = getConnectedEntities();
        LinkedList<Direction> possibleDirections = new LinkedList<>();

        for (AdjacentBlockEntity adjacent : adjacentEntities) {
            if (adjacent.blockEntity instanceof PipeBlockEntity) {
                continue;
            }
            if (isLockedExit(adjacent.direction)) {
                continue;
            }
            possibleDirections.add(adjacent.direction);
        }
        if (!possibleDirections.isEmpty()) {
            return possibleDirections.get(world.random.nextInt(possibleDirections.size()));
        }

        for (AdjacentBlockEntity adjacent : adjacentEntities) {
            if (neighborTable.containsValue(adjacent.direction.getId())) {
                continue;
            }
            if (isLockedExit(adjacent.direction)) {
                continue;
            }
            possibleDirections.add(adjacent.direction);
        }

        if (possibleDirections.isEmpty()) {
            return null;
        }

        return possibleDirections.get(world.random.nextInt(possibleDirections.size()));
    }

    public boolean stillWantItem(RoutedItem item) {
        return true;
    }

    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if (wrenchMode == WrenchMode.MODE_WRENCH && !isSneaking) {
            if (getLogisticsModule() != null) {
                GuiHelper.openGUI(player, getLogisticsModule().getScreenIdentifier(), this, null, (messagePacket) -> {
                    messagePacket.ints = new int[]{x, y, z};
                });
            }
            return true;
        }
        return false;
    }

    public Direction getDirectionForItem(RoutedItem item) {
        if (!item.isDestinationValid() && world.isRemote) {
            return null;
        }

        //If items have no destination, see if we can get one (unless it has a source, then drop it)
        if (!item.isDestinationValid()) {
            if (item.isSourceValid()) {
                return null;
            }
            item = LogisticsManager.getInstance().assignDestinationFor(world, item, getRouterId(), false);
        }

        //If the destination is unknown / unroutable
        if (item.isDestinationValid() && item.getDestination() != getRouterId() && !getNetwork().routers.contains(RoutingUtil.getRouter(world, item.getDestination()))) {
            item = LogisticsManager.getInstance().destinationUnreachable(world, item, getRouterId());
        }

        //If we still have no destination, drop it
        if (!item.isDestinationValid()) {
            return null;
        }

        //Is the destination ourself? Deliver it
        if (item.getDestination() == getRouterId()) {
            if (!stillWantItem(item)) {
                return getDirectionForItem(LogisticsManager.getInstance().assignDestinationFor(world, item, getRouterId(), true));
            }

            item.setDoNotBuffer(true);
            return itemArrived(item);
        }

        long nextHopId = getNextHop(item.getDestination());

        // If we do not know the route to destination, we cannot route
        if (nextHopId == -1) {
            return null;
        }

        int nextHopDirection = neighborTable.getOrDefault(nextHopId, -1);

        // We do not know in which direction the router is
        if (nextHopDirection == -1) {
            return null;
        }

        return Direction.byId(nextHopDirection);
    }

    public LinkedList<AdjacentBlockEntity> getConnectedEntities() {
        LinkedList<AdjacentBlockEntity> adjacent = WorldUtil.getAdjacentBlockEntities(world, x, y, z);
        adjacent.removeIf(blockEntity -> connections.get(blockEntity.direction) == PipeConnectionType.NONE);
        return adjacent;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract ItemSendMode getItemSendMode();

    public void queueRoutedItem(RoutedItem routedItem, Direction from) {
        sendQueue.addLast(new Pair3<>(routedItem, from, ItemSendMode.Normal));
        sendQueueChanged();
    }

    public void queueRoutedItem(RoutedItem routedItem, Direction from, ItemSendMode mode) {
        sendQueue.addLast(new Pair3<>(routedItem, from, mode));
        sendQueueChanged();
    }

    private void sendRoutedItem(RoutedItem routedItem, Direction from) {
        if (transporter instanceof ItemPipeTransporter itemTransporter) {
            itemTransporter.receiveTravellingItem(routedItem.getTravellingItemEntity(), from.getOpposite());
            statLifetimeSent++;
            statSessionSent++;
            ItemUtil.spawnRoutedItem(world, routedItem, x, y, z, from);
        }
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
    public LogisticsNetwork getNetwork() {
        return LogisticsNetworkManager.fetchNetwork(world, this);
    }

    @Override
    public Long2IntOpenHashMap getNeighborTable() {
        return neighborTable;
    }

    @Override
    public Long2ObjectOpenHashMap<RouteDestination> getRoutingTable() {
        return routingTable;
    }

    @Override
    public void topologyChanged() {
        nextHopCache.clear();
        routingTable.clear();
        advertiseRequired = true;
    }

    // Routing
    public long getNextHop(long destinationId) {
        if (!nextHopCache.containsKey(destinationId)) {
            nextHopCache.put(destinationId, calculateNextHop(destinationId));
        }

        return nextHopCache.get(destinationId);
    }

    public long calculateNextHop(long destinationId) {
        while (true) {
            if (!routingTable.containsKey(destinationId)) {
                return -1;
            } else {
                if (routingTable.get(destinationId).routerId == destinationId) {
                    return destinationId;
                }

                RouteDestination route = routingTable.get(destinationId);
                destinationId = route.routerId;
            }
        }
    }

    @Override
    public void learnRoutesFromNeighbors() {
        for (long routerId : neighborTable.keySet()) {
            Router router = RoutingUtil.getRouter(world, routerId);
            int metricToRouter = routingTable.containsKey(routerId) ? routingTable.get(routerId).metric : -1;

            if (router == null || metricToRouter == -1) {
                continue;
            }

            for (var routingEntry : router.getRoutingTable().long2ObjectEntrySet()) {
                long destinationId = routingEntry.getLongKey();
                RouteDestination route = routingEntry.getValue();

                learnRoute(destinationId, routerId, router, route.metric + metricToRouter);
            }
        }
    }

    @Override
    public void learnRoutesToTargetFromNeighbors(long targetRouterId) {
        for (long routerId : neighborTable.keySet()) {
            Router router = RoutingUtil.getRouter(world, routerId);
            int metricToRouter = routingTable.containsKey(routerId) ? routingTable.get(routerId).metric : -1;

            if (router == null || metricToRouter == -1) {
                continue;
            }

            RouteDestination nextHop = router.getRoutingTable().getOrDefault(targetRouterId, null);
            if (nextHop != null) {
                learnRoute(targetRouterId, routerId, router, nextHop.metric + metricToRouter);
            }
        }
    }

    public void learnRoute(long destinationId, long nextHopId, Router nextHopRouter, int metric) {
        if (destinationId == this.getRouterId()) {
            return;
        }

        RouteDestination knownRoute = routingTable.get(destinationId);
        if (knownRoute != null) {
            if (metric < knownRoute.metric) {
                routingTable.put(destinationId, new RouteDestination(nextHopId, nextHopRouter, metric));
            }
        } else {
            routingTable.put(destinationId, new RouteDestination(nextHopId, nextHopRouter, metric));
        }
    }

    @Override
    public void smartAdvertiseRouter() {
        if (advertiseRequired) {
            advertiseRouter();
            advertiseRequired = false;
        }
    }

    @Override
    public void advertiseRouter() {
        // Contains routers to be explored
        ObjectArrayFIFOQueue<Router> open = new ObjectArrayFIFOQueue<>();

        // Contains all the routers that have been discovered
        ObjectOpenHashSet<Router> closed = new ObjectOpenHashSet<>();

        // Initialize the starting point and add the starting router
        open.enqueue(this);

        long targetRouterId = this.getRouterId();

        while (!open.isEmpty()) {
            Router current = open.dequeue();

            // Check if this router has not already been closed
            if (closed.contains(current)) {
                continue;
            }

            current.learnRoutesToTargetFromNeighbors(targetRouterId);

            // Discover the routers neigbors
            Long2IntOpenHashMap neighborTable = current.getNeighborTable();
            for (long neighborId : neighborTable.keySet()) {
                Router neighborRouter = RoutingUtil.getRouter(world, neighborId);
                if (!closed.contains(neighborRouter)) {
                    open.enqueue(RoutingUtil.getRouter(world, neighborId));
                }
            }

            closed.add(current);
        }
    }

    public void propagateRoutes() {
        // Contains routers to be explored
        ObjectArrayFIFOQueue<Router> open = new ObjectArrayFIFOQueue<>();

        // Contains all the routers that have been discovered
        ObjectOpenHashSet<Router> closed = new ObjectOpenHashSet<>();

        // Initialize the starting point and add the starting router
        open.enqueue(this);

        while (!open.isEmpty()) {
            Router current = open.dequeue();

            // Check if this router has not already been closed
            if (closed.contains(current)) {
                continue;
            }

            current.learnRoutesFromNeighbors();

            // Discover the routers neigbors
            Long2IntOpenHashMap neighborTable = current.getNeighborTable();
            for (long neighborId : neighborTable.keySet()) {
                Router neighborRouter = RoutingUtil.getRouter(world, neighborId);
                if (!closed.contains(neighborRouter)) {
                    open.enqueue(RoutingUtil.getRouter(world, neighborId));
                }
            }

            closed.add(current);
        }
    }

    public void updateNeighbors() {
        discoverNeighbors(false);

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

    public void discoverNeighbors(boolean clearRoutingTable) {
        int maxRange = Config.NETWORK_CONFIG.neighborDetectionDistance;
        int maxMetric = maxRange * 16;

        // We keep track of the neighbor hash to discover topology changes on local level
        long prevNeighborHash = neighborTable.hashCode();

        // Clear old local data before a fresh scan
        this.neighborTable.clear();
        if (clearRoutingTable) {
            this.routingTable.clear();
        }

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

            if (blockEntity == this) {
                continue;
            }

            // If we find a router, check if the route is better than an existing one
            if (blockEntity instanceof Router router) {
                RouteDestination existing = routingTable.get(pos);
                int currentMetric = existing != null ? existing.metric : -1;

                // Write this to the neighbor table
                this.neighborTable.put(pos, current.firstDir.getId());

                // If we find a better route to a router, add it to the routing table
                if (currentMetric == -1 || current.metric < currentMetric) {
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

        // Clear out invalid routing entries
        for (var routingEntry : routingTable.long2ObjectEntrySet()) {
            long nextHopId = getNextHop(routingEntry.getLongKey());
            if (nextHopId == -1 || !neighborTable.containsKey(nextHopId)) {
                routingTable.remove(routingEntry.getLongKey());
            }
        }

        // We take the the hash of the neighbor table after discovery to compare if the physical topology has changed 
        long neighborHash = neighborTable.hashCode();
        if (prevNeighborHash != neighborHash) {
            LogisticsNetworkManager.invalidateNetwork(world, this);
        }
    }

    public void debugPrint(PlayerEntity player) {
        System.err.println("Router " + getRouterId());
        player.sendMessage("Router " + getRouterId());

        System.err.println("Neighbor Table: ");
        player.sendMessage("Neighbor Table: ");
        for (var entry : neighborTable.long2IntEntrySet()) {
            System.err.println(entry.getLongKey() + " -> " + Direction.byId(entry.getIntValue()));
            player.sendMessage(entry.getLongKey() + " -> " + Direction.byId(entry.getIntValue()));
        }

        System.err.println("Routing Table: ");
        player.sendMessage("Routing Table: ");
        for (var entry : routingTable.long2ObjectEntrySet()) {
            System.err.println(entry.getLongKey() + " -> " + entry.getValue());
            player.sendMessage(entry.getLongKey() + " -> " + entry.getValue());
        }
    }

    private record SearchNode(int x, int y, int z, int metric, Direction firstDir) {

    }

    public boolean isLockedExit(Direction direction) {
        return false;
    }

    @Override
    public boolean isRoutedExit(Direction direction) {
        if (direction == null) {
            return false;
        }
        return neighborTable.containsValue(direction.getId());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        statLifetimeSent = nbt.getLong("statLifetimeSent");
        statLifetimeReceived = nbt.getLong("statLifetimeReceived");
        statLifetimeRelayed = nbt.getLong("statLifetimeReplayed");

        if (getLogisticsModule() != null) {
            getLogisticsModule().readNbt(nbt, "");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putLong("statLifetimeSent", statLifetimeSent);
        nbt.putLong("statLifetimeReceived", statLifetimeReceived);
        nbt.putLong("statLifetimeReplayed", statLifetimeRelayed);

        if (getLogisticsModule() != null) {
            getLogisticsModule().writeNbt(nbt, "");
        }
    }

    @Override
    public void itemCouldNotBeSend(ItemIdentifierStack item) {
        //Override by subclasses
    }

    @Override
    public Router getRouter() {
        return this;
    }
}
