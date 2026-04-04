package net.danygames2014.logisticspipes.request;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ProvideItems;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.interfaces.routing.CraftItems;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class RequestManager {
    public static boolean request(LinkedList<ItemStack> items, RequestItems requester, LinkedList<Router> validDestinations, RequestLog log) {
        LinkedList<ProvideItems> providers = getProviders(validDestinations);
        LinkedList<CraftingTemplate> crafters = getCrafters(validDestinations);
        LinkedList<ItemStack> messages = new LinkedList<>();
        RequestTree tree = new RequestTree(new ItemStack(1,0, 0), requester);
        for(ItemStack stack:items) {
            RequestTree node = new RequestTree(stack, requester);
            tree.subRequests.add(node);
            messages.add(stack);
            generateRequestTree(tree, node, crafters, providers);
        }
        if(tree.isAllDone()) {
            handleRequestTree(tree);
            if(log != null) {
                log.handleSucessfullRequestOfList(messages);
            }
            return true;
        } else {
            if(log != null) {
                for(RequestTreeNode node:tree.subRequests) {
                    if(node instanceof RequestTree) {
                        ((RequestTree)node).sendMissingMessage(log);
                    }
                }
            }
            return false;
        }
    }

    public static boolean request(ItemStack item, RequestItems requester, List<Router> validDestinations, RequestLog log) {
        LinkedList<ProvideItems> providers = getProviders(validDestinations);
        LinkedList<CraftingTemplate> crafters = getCrafters(validDestinations);
        RequestTree tree = new RequestTree(item, requester);
        generateRequestTree(tree, tree, crafters, providers);
        if(tree.isAllDone()) {
            handleRequestTree(tree);
            if(log != null) {
                log.handleSucessfullRequestOf(tree.getStack());
            }
            return true;
        } else {
            if(log != null) {
                tree.sendMissingMessage(log);
            }
            return false;
        }
    }

    private static LinkedList<CraftingTemplate> getCrafters(List<Router> validDestinations) {
        LinkedList<CraftingTemplate> crafters = new LinkedList<>();
        for(Router r : validDestinations) {
            LogisticPipeBlockEntity pipe = r.getPipe();
            if (pipe instanceof CraftItems craftItems){
                LinkedList<CraftingTemplate> added = new LinkedList<>();
                craftItems.addCrafting(added);
                for(CraftingTemplate template:added) {
                    boolean done = false;
                    for(int i=0; i<crafters.size();i++) {
                        if(template.getPriority() > crafters.get(i).getPriority()) {
                            crafters.add(i, template);
                            done = true;
                            break;
                        }
                    }
                    if(!done) {
                        crafters.addLast(template);
                    }
                }
            }
        }
        return crafters;
    }

    private static LinkedList<ProvideItems> getProviders(List<Router> validDestinations) {
        LinkedList<ProvideItems> providers = new LinkedList<>();
        for(Router r : validDestinations) {
            LogisticPipeBlockEntity pipe = r.getPipe();
            if (pipe instanceof ProvideItems provideItems){
                providers.add(provideItems);
            }
        }

        return providers;
    }

    private static void handleRequestTree(RequestTree tree) {
        tree.fullFillAll();
        tree.registerExtras();
    }

    private static boolean generateRequestTree(RequestTree tree, RequestTreeNode treeNode, LinkedList<CraftingTemplate> crafters, LinkedList<ProvideItems> providers) {
        checkProvider(tree,treeNode,providers);
        if(treeNode.isDone()) {
            return true;
        }
        checkExtras(tree, treeNode);
        if(treeNode.isDone()) {
            return true;
        }
        checkCrafting(tree,treeNode,crafters,providers);
        return treeNode.isDone();
    }

    private static void checkExtras(RequestTree tree, RequestTreeNode treeNode) {
        LinkedHashMap<LogisticsPromise,RequestTreeNode> map = tree.getExtrasFor(treeNode.getStack());
        for (LogisticsPromise extraPromise : map.keySet()){
            if(treeNode.isDone()) {
                break;
            }
            treeNode.addPromise(extraPromise);
            map.get(extraPromise).usePromise(extraPromise);
        }
    }

    private static void checkCrafting(RequestTree tree, RequestTreeNode treeNode, LinkedList<CraftingTemplate> crafters, LinkedList<ProvideItems> providers) {
        List<RequestTreeNode> lastNode = null;
        CraftingTemplate lastNodeTemplate = null;
        boolean handled = false;
        for(CraftingTemplate template:crafters) {
            if(template.getResultStack().getItem() != treeNode.getStack().getItem()) continue;
            List<Pair<ItemStack,RequestItems>> stacks = new ArrayList<Pair<ItemStack,RequestItems>>();
            RequestTreeNode treeNodeCopy = treeNode.copy();
            while(treeNodeCopy.addPromise(template.generatePromise())) {
                for(Pair<ItemStack,RequestItems> stack:template.getSource()) {
                    boolean done = false;
                    for(Pair<ItemStack,RequestItems> part:stacks) {
                        if(part.getValue1().getItem() == stack.getValue1().getItem() && part.getValue2() == stack.getValue2()) {
                            part.getValue1().count += stack.getValue1().count;
                            done = true;
                            break;
                        }
                    }
                    if(!done) {
                        Pair<ItemStack, RequestItems> pair = new Pair<>(stack.getValue1().copy(),stack.getValue2());
                        stacks.add(pair);
                    }
                }
            }
            boolean failed = false;
            lastNode = new ArrayList<RequestTreeNode>();
            lastNodeTemplate = template;
            for(Pair<ItemStack,RequestItems> stack:stacks) {
                RequestTreeNode node = new RequestTreeNode(stack.getValue1(), stack.getValue2());
                lastNode.add(node);
                treeNode.subRequests.add(node);
                if(!generateRequestTree(tree,node,getCraftersWithOutCrafter(crafters,template),providers)) {
                    failed = true;
                }
            }
            if(failed) {
                for(RequestTreeNode subNode:lastNode) {
                    treeNode.subRequests.remove(subNode);
                }
                continue;
            }
            handled = true;
            while(treeNode.addPromise(template.generatePromise()));
            lastNode = null;
            break;
        }
        if(!handled) {
            if(lastNode != null && lastNodeTemplate != null) {
                while(treeNode.addPromise(lastNodeTemplate.generatePromise()));
                treeNode.subRequests.addAll(lastNode);
            }
        }
    }

    private static void checkProvider(RequestTree tree, RequestTreeNode treeNode, LinkedList<ProvideItems> providers) {
        for(ProvideItems provider : providers) {
            provider.canProvide(treeNode, tree.getAllPromissesFor(provider));
        }
    }

    private static LinkedList<CraftingTemplate> getCraftersWithOutCrafter(LinkedList<CraftingTemplate> crafters, CraftingTemplate crafter) {
        LinkedList<CraftingTemplate> result = new LinkedList<>();
        for(CraftingTemplate template:crafters) {
            if(template != crafter) {
                result.add(template);
            }
        }
        return result;
    }
}
