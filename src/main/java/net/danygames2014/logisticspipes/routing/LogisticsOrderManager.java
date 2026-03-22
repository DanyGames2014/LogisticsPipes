package net.danygames2014.logisticspipes.routing;

import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.item.ItemStack;
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

    private LinkedList<Pair<ItemStack, RequestItems>> orders = new LinkedList<>();
    private ChangeListener listener = null;

    private void listen() {
        if (listener != null) {
            listener.listenedChanged();
        }
    }

    public LinkedList<ItemStack> getContentList(World world) {
        if (world.isRemote) return new LinkedList<>();
        LinkedList<ItemStack> list = new LinkedList<>();
        for (Pair<ItemStack, RequestItems> request : orders) {
            addToList(request.getValue1(), list);
        }
        return list;
    }

    private void addToList(ItemStack stack, LinkedList<ItemStack> list) {
        for (ItemStack ident : list) {
            if (ident.getItem().equals(stack.getItem())) {
                ident.count += stack.count;
                return;
            }
        }
        list.addLast(stack.copy());
    }

    public boolean hasOrders() {
        return !orders.isEmpty();
    }

    public Pair<ItemStack, RequestItems> getNextRequest() {
        return orders.getFirst();
    }

    public void sendSuccessfull(int number) {
        orders.getFirst().getValue1().count -= number;
        if (orders.getFirst().getValue1().count <= 0) {
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

    public void addOrder(ItemStack stack, RequestItems requester) {
        orders.addLast(new Pair<>(stack, requester));
        listen();
    }

    public int totalItemsCountInOrders(ItemStack item) {
        int itemCount = 0;
        for (Pair<ItemStack, RequestItems> request : orders) {
            if (!request.getValue1().isItemEqual(item)) continue;
            itemCount += request.getValue1().count;
        }
        return itemCount;
    }
}
