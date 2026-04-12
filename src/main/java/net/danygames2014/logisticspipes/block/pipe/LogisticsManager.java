package net.danygames2014.logisticspipes.block.pipe;

import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.interfaces.ProvideItems;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.interfaces.routing.CraftItems;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.RoutingUtil;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class LogisticsManager implements net.danygames2014.logisticspipes.interfaces.LogisticsManager {
    private static final LogisticsManager INSTANCE = new LogisticsManager();

    public static LogisticsManager getInstance() {
        return INSTANCE;
    }

    @Override
    public RoutedItem assignDestinationFor(World world, RoutedItem item, long sourceRouterUUID, boolean excludeSource) {
        Router router = RoutingUtil.getRouter(world, sourceRouterUUID);

        //If the source router does not exist we can't do anything with this
        if (router == null) {
            return item;
        }

        //Wipe current destination
        item.setDestination(Long.MIN_VALUE);

        Pair<Long, SinkReply> bestReply = getBestReply(item.getItemStack(), router, excludeSource);

        item.setSource(sourceRouterUUID);
        if (bestReply.getValue1() != null) {
            item.setDestination(bestReply.getValue1());
            Router replyRouter = RoutingUtil.getRouter(world, bestReply.getValue1());

            if (replyRouter != null) {
                replyRouter.smartAdvertiseRouter();
                if (bestReply.getValue2().isPassive) {
                    if (bestReply.getValue2().isDefault) {
                        item.setTransportMode(RoutedItem.TransportMode.Default);
                    } else {
                        item.setTransportMode(RoutedItem.TransportMode.Passive);
                    }
                }
            }
        }
        return item;
    }

    @Override
    public RoutedItem destinationUnreachable(World world, RoutedItem item, long currentRouter) {
        return assignDestinationFor(world, item, currentRouter, false);
    }

    @Override
    public boolean hasDestination(World world, ItemStack stack, boolean allowDefault, long sourceRouter, boolean excludeSource) {
        Router router = RoutingUtil.getRouter(world, sourceRouter);

        //If the source router does not exist we can't do anything with this
        if (router == null) {
            return false;
        }
        Pair<Long, SinkReply> search = getBestReply(stack, router, excludeSource);

        if (search.getValue2() == null) return false;
        if (search.getValue1() != null) {
            RoutingUtil.getRouter(world, search.getValue1()).smartAdvertiseRouter();
        }


        return (allowDefault || !search.getValue2().isDefault);
    }

    @Override
    public LinkedList<ItemIdentifier> getCraftableItems(World world, Set<Router> validDestinations) {
        LinkedList<ItemIdentifier> craftableItems = new LinkedList<>();
        for (Router r : validDestinations){
            if(r == null) continue;
            if (!(r.getPipe() instanceof CraftItems crafter)) continue;

            ItemIdentifier craftedItem = crafter.getCraftedItem();
            if (craftedItem != null && !craftableItems.contains(craftedItem)){
                craftableItems.add(craftedItem);
            }
        }
        return craftableItems;
    }

    @Override
    public HashMap<ItemIdentifier, Integer> getAvailableItems(World world, Set<Router> validDestinations) {
        HashMap<ItemIdentifier, Integer> allAvailableItems = new HashMap<>();
        for (Router r : validDestinations) {
            if (r == null) {
                continue;
            }
            if (r.getPipe() instanceof ProvideItems provider) {
                HashMap<ItemIdentifier, Integer> allItems = provider.getAllItems();

                for (ItemIdentifier item : allItems.keySet()) {
                    if (!allAvailableItems.containsKey(item)) {
                        allAvailableItems.put(item, allItems.get(item));
                    } else {
                        allAvailableItems.put(item, allAvailableItems.get(item) + allItems.get(item));
                    }
                }
            }
        }
        return allAvailableItems;
    }

    private Pair<Long, SinkReply> getBestReply(ItemStack item, Router sourceRouter, boolean excludeSource) {
        long potentialDestination = Long.MIN_VALUE;
        SinkReply bestReply = null;
        for (Router candidateRouter : sourceRouter.getNetwork().routers) {
            LogisticsModule module = candidateRouter.getPipe().getLogisticsModule();
            if (candidateRouter.getPipe() == null || !candidateRouter.getPipe().isEnabled()) {
                continue;
            }
            if (module == null) {
                continue;
            }
            if (excludeSource && candidateRouter.getRouterId() == sourceRouter.getRouterId()) {
                continue;
            }
            SinkReply reply = module.sinksItem(item);
            if (reply == null) {
                continue;
            }
            if (bestReply == null) {
                potentialDestination = candidateRouter.getRouterId();
                bestReply = reply;
                continue;
            }

            if (reply.fixedPriority.ordinal() > bestReply.fixedPriority.ordinal()) {
                bestReply = reply;
                potentialDestination = candidateRouter.getRouterId();
                continue;
            }

            if (reply.fixedPriority == bestReply.fixedPriority && reply.customPriority > bestReply.customPriority) {
                bestReply = reply;
                potentialDestination = candidateRouter.getRouterId();
                continue;
            }
        }

        return new Pair<>(potentialDestination, bestReply);
    }
}
