package net.danygames2014.logisticspipes.request;

import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RequestTreeNode {
    public RequestTreeNode(ItemStack item, RequestItems requester) {
        this.request = item;
        this.target = requester;
    }

    protected final RequestItems target;
    protected final ItemStack request;
    protected List<RequestTreeNode> subRequests = new ArrayList<>();
    protected List<LogisticsPromise> promises = new ArrayList<>();
    protected List<LogisticsPromise> extrapromises = new ArrayList<>();

    public int getPromiseItemCount() {
        int count = 0;
        for (LogisticsPromise promise : promises) {
            count += promise.numberOfItems;
        }
        return count;
    }

    public int getMissingItemCount() {
        return request.count - getPromiseItemCount();
    }

    public boolean addPromise(LogisticsPromise promise) {
        if (!promise.item.isItemEqual(request)) {
            return false;
        }
        if (getMissingItemCount() == 0) return false;
        if (promise.numberOfItems > getMissingItemCount()) {
            int more = promise.numberOfItems - getMissingItemCount();
            promise.numberOfItems = getMissingItemCount();
            //Add Extra
            LogisticsPromise extra = new LogisticsPromise();
            extra.extra = true;
            extra.item = promise.item;
            extra.numberOfItems = more;
            extra.sender = promise.sender;
            extrapromises.add(extra);
        }
        if (promise.numberOfItems > 0) {
            promises.add(promise);
            return true;
        }
        return false;
    }

    public void usePromise(LogisticsPromise promise) {
        if (extrapromises.contains(promise)) {
            extrapromises.remove(promise);
        }
    }

    public boolean isDone() {
        return getMissingItemCount() <= 0;
    }

    public ItemStack getStack() {
        return request;
    }

    public RequestTreeNode copy() {
        RequestTreeNode result = new RequestTreeNode(request, target);
        for (RequestTreeNode subNode : subRequests) {
            result.subRequests.add(subNode.copy());
        }
        for (LogisticsPromise subpromises : promises) {
            result.promises.add(subpromises.copy());
        }
        for (LogisticsPromise subpromises : extrapromises) {
            result.extrapromises.add(subpromises.copy());
        }
        return result;
    }
}
