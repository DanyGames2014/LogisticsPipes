package net.danygames2014.logisticspipes.request;

import net.danygames2014.logisticspipes.interfaces.ProvideItems;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.interfaces.routing.CraftItems;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.util.ItemUtil;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class RequestTree extends RequestTreeNode{
    public RequestTree(ItemStack item, RequestItems requester) {
        super(item, requester);
    }

    public Map<ItemStack, Integer> getAllPromissesFor(ProvideItems provider) {
        Map<ItemStack, Integer> result = new HashMap<>();
        checkSubPromises(provider,this,result);
        return result;
    }

    private void checkSubPromises(ProvideItems provider, RequestTreeNode node, Map<ItemStack, Integer> result) {
        for(LogisticsPromise promise: node.promises) {
            if(promise.sender == provider) {
                result.put(promise.item, promise.numberOfItems);
            }
        }
        for(RequestTreeNode subNode:node.subRequests) {
            checkSubPromises(provider,subNode,result);
        }
    }

    public boolean isAllDone() {
        return checkSubDone(this);
    }

    private boolean checkSubDone(RequestTreeNode node) {
        boolean done = true;
        done &= node.isDone();
        for(RequestTreeNode subNode:node.subRequests) {
            done &= checkSubDone(subNode);
        }
        return done;
    }

    public LinkedHashMap<LogisticsPromise,RequestTreeNode> getExtrasFor(ItemStack item) {
        LinkedHashMap<LogisticsPromise,RequestTreeNode> extras = new LinkedHashMap<>();
        checkForExtras(item,this,extras);
        return extras;
    }

    private void checkForExtras(ItemStack item, RequestTreeNode node, LinkedHashMap<LogisticsPromise,RequestTreeNode> extras) {
        for(LogisticsPromise extra:extrapromises) {
            if(extra.item == item) {
                extras.put(extra, node);
            }
        }
        for(RequestTreeNode subNode:node.subRequests) {
            checkForExtras(item,subNode,extras);
        }
    }

    public RequestTree copy() {
        RequestTree result = new RequestTree(request, target);
        for(RequestTreeNode subNode:subRequests) {
            result.subRequests.add(subNode.copy());
        }
        for(LogisticsPromise subpromises:promises) {
            result.promises.add(subpromises.copy());
        }
        for(LogisticsPromise subpromises:extrapromises) {
            result.extrapromises.add(subpromises.copy());
        }
        return result;
    }

    public void fullFillAll() {
        fullFill(this);
    }

    private void fullFill(RequestTreeNode node) {
        for(LogisticsPromise promise:node.promises) {
            promise.sender.fullFill(promise, node.target);
        }
        for(RequestTreeNode subNode:node.subRequests) {
            fullFill(subNode);
        }
    }

    public void sendMissingMessage(RequestLog log) {
        LinkedList<ItemStack> missing = new LinkedList<>();
        sendMissingMessage(missing, this);
        ItemUtil.compress(missing);
        log.handleMissingItems(missing);
    }

    private void sendMissingMessage(LinkedList<ItemStack> missing, RequestTreeNode node) {
        if(!node.isDone()) {
            ItemStack stack = node.getStack().copy();
            stack.count = node.getMissingItemCount();
            missing.add(stack);
        }
        for(RequestTreeNode subNode:node.subRequests) {
            sendMissingMessage(missing, subNode);
        }
    }

    public void registerExtras() {
        registerExtras(this);
    }

    private void registerExtras(RequestTreeNode node) {
        for(LogisticsPromise promise:node.extrapromises) {
            if(promise.sender instanceof CraftItems) {
                ((CraftItems)promise.sender).registerExtras(promise.numberOfItems);
            }
        }
        for(RequestTreeNode subNode:node.subRequests) {
            registerExtras(subNode);
        }
    }
}
