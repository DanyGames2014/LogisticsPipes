package net.danygames2014.logisticspipes.routing;

import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.world.World;

import java.util.LinkedList;

public class LogisticsOrderManager {
    public interface ChangeListener {
        void listenedChanged();
    }

    public LogisticsOrderManager() {
    }

    public LogisticsOrderManager(ChangeListener listener) {
        this.listener = listener;
    }

    private LinkedList<Pair<ItemIdentifierStack, RequestItems>> orders = new LinkedList<>();
    private ChangeListener listener = null;

    private void listen() {
        if (listener != null) {
            listener.listenedChanged();
        }
    }

    public LinkedList<ItemIdentifierStack> getContentList(World world) {
        if (world.isRemote) return new LinkedList<>();
        LinkedList<ItemIdentifierStack> list = new LinkedList<>();
        for (Pair<ItemIdentifierStack, RequestItems> request : orders) {
            addToList(request.getValue1(), list);
        }
        return list;
    }

    private void addToList(ItemIdentifierStack stack, LinkedList<ItemIdentifierStack> list) {
        for (ItemIdentifierStack ident : list) {
            if (ident.getItem().equals(stack.getItem())) {
                ident.stackSize += stack.stackSize;
                return;
            }
        }
        list.addLast(stack.clone());
    }

    public boolean hasOrders() {
        return !orders.isEmpty();
    }

    public Pair<ItemIdentifierStack, RequestItems> getNextRequest() {
        return orders.getFirst();
    }

    public void sendSuccessfull(int number) {
        orders.getFirst().getValue1().stackSize -= number;
        if (orders.getFirst().getValue1().stackSize <= 0) {
            orders.removeFirst();
        }
        listen();
    }

    public void sendFailed() {
        orders.getFirst().getValue2().itemCouldNotBeSend(orders.getFirst().getValue1());
        if (!orders.isEmpty()) {
            orders.removeFirst();
        }
        listen();
    }

    public void addOrder(ItemIdentifierStack stack, RequestItems requester) {
        orders.addLast(new Pair<>(stack, requester));
        listen();
    }

    public int totalItemsCountInOrders(ItemIdentifier item) {
        int itemCount = 0;
        for (Pair<ItemIdentifierStack, RequestItems> request : orders) {
            if (request.getValue1().getItem() != item) continue;
            itemCount += request.getValue1().stackSize;
        }
        return itemCount;
    }
}
