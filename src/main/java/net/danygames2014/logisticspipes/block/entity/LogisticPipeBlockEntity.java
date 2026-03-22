package net.danygames2014.logisticspipes.block.entity;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.logisticspipes.gui.hud.TestHud;
import net.danygames2014.logisticspipes.interfaces.HUDRenderer;
import net.danygames2014.logisticspipes.interfaces.HUDRendererProvider;
import net.danygames2014.logisticspipes.routing.RouteDestination;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.RoutingUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class LogisticPipeBlockEntity extends PipeBlockEntity implements Router, HUDRendererProvider {
    // Routing
    public Long2ObjectOpenHashMap<RouteDestination> routingTable = new Long2ObjectOpenHashMap<>(32, 0.5F);
    public Long2ByteOpenHashMap neighborTable = new Long2ByteOpenHashMap(32, 0.5F);

    private final TestHud HUD = new TestHud();

    public LogisticPipeBlockEntity() {
        this.neighborTable.defaultReturnValue((byte) -1);
    }

    public LogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
        this.neighborTable.defaultReturnValue((byte) -1);
    }

    @Override
    public HUDRenderer getRenderer() {
        return HUD;
    }

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

        discoverNeighbors(64);
        learnRoutesFromNeighbors();

        for (Map.Entry<Direction, PipeConnectionType> connection : connections.entrySet()) {
            if (connection.getValue() != PipeConnectionType.NONE) {
                connections.put(connection.getKey(), neighborTable.containsValue((byte) connection.getKey().getId()) ? PipeConnectionType.ALTERNATE : PipeConnectionType.NORMAL);
            }
        }
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
    
    public void discoverNeighbors(int maxRange) {
        // Clear old local data before a fresh scan
        this.neighborTable.clear();
        this.routingTable.clear();

        Queue<SearchNode> queue = new LinkedList<>();
        Long2IntOpenHashMap visited = new Long2IntOpenHashMap();
        visited.defaultReturnValue(Integer.MAX_VALUE);

        // 1. Look in all 6 directions to start the search
        for (Direction side : Direction.values()) {
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
            if (current.metric >= visited.get(pos) || current.metric > maxRange * 16) {
                continue;
            }
            visited.put(pos, current.metric);

            BlockEntity blockEntity = world.getBlockEntity(current.x, current.y, current.z);

            // If we find a router, check if the route is better than an existing one
            if (blockEntity instanceof Router router && blockEntity != this) {
                int currentMetric = routingTable.containsKey(pos) ? routingTable.get(pos).metric : -1;

                // If we find a better route to a router, add it to the table
                if (currentMetric == -1 || current.metric < currentMetric) {
                    this.neighborTable.put(pos, (byte) current.firstDir.getId());
                    this.routingTable.put(pos, new RouteDestination(pos, router, current.metric));
                }

                // Don't search further than the nearest neighbor router
                continue;
            }

            // Continue walking thru Buildcraft Pipes
            if (blockEntity instanceof PipeBlockEntity) {
                for (Direction side : Direction.values()) {
                    int nextX = current.x + side.getOffsetX();
                    int nextY = current.y + side.getOffsetY();
                    int nextZ = current.z + side.getOffsetZ();

                    int metric = RoutingUtil.getPipeMetric(world, nextX, nextY, nextZ);
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
        for (var entry : neighborTable.long2ByteEntrySet()) {
            player.sendMessage(entry.getLongKey() + " -> " + Direction.byId(entry.getByteValue()));
        }
        player.sendMessage("Routing Table: ");
        for (var entry : routingTable.long2ObjectEntrySet()) {
            player.sendMessage(entry.getLongKey() + " -> " + entry.getValue());
        }
    }

    private static class SearchNode {
        int x, y, z;
        int metric;
        Direction firstDir;

        SearchNode(int x, int y, int z, int metric, Direction firstDir) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.metric = metric;
            this.firstDir = firstDir;
        }
    }
}
