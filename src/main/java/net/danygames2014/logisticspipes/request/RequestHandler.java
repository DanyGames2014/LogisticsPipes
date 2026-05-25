package net.danygames2014.logisticspipes.request;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.LogisticsManager;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.network.ItemMessageS2CPacket;
import net.danygames2014.logisticspipes.network.RequestScreenContentC2SPacket;
import net.danygames2014.logisticspipes.network.SendScreenContentS2CPacket;
import net.danygames2014.logisticspipes.network.SubmitRequestC2SPacket;
import net.danygames2014.logisticspipes.routing.LogisticsNetworkManager;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.ItemMessage;
import net.danygames2014.logisticspipes.util.MessageManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RequestHandler {
    public enum DisplayOptions {
        Both,
        SupplyOnly,
        CraftOnly;
    }

    public static void request(final PlayerEntity player, final SubmitRequestC2SPacket packet, LogisticPipeBlockEntity pipe) {
        LinkedList<ItemMessage> errors = new LinkedList<>();
        boolean result = RequestManager.request(ItemIdentifier.get(Item.ITEMS[packet.itemID], packet.dataValue, packet.tag).makeStack(packet.amount), pipe, LogisticsNetworkManager.fetchRoutersByMetric(pipe.world, pipe.getRouter()), new RequestLog() {
            @Override
            public void handleSucessfullRequestOf(ItemMessage item) {
                LinkedList<ItemMessage> list = new LinkedList<>();
                list.add(new ItemMessage(Item.ITEMS[packet.itemID], packet.dataValue, packet.amount, packet.tag));
                MessageManager.requested(player, list);
            }

            @Override
            public void handleMissingItems(LinkedList<ItemMessage> list) {
                MessageManager.errors(player, list);
            }

            @Override
            public void handleSucessfullRequestOfList(LinkedList<ItemMessage> items) {
            }
        });
    }

    public static void refresh(PlayerEntity player, LogisticPipeBlockEntity pipe, DisplayOptions option) {
        HashMap<ItemIdentifier, Integer> _availableItems;
        LinkedList<ItemIdentifier> craftableItems;
        LinkedList<ItemIdentifierStack>allItems = new LinkedList<>();

        if (option == DisplayOptions.SupplyOnly || option == DisplayOptions.Both){
            _availableItems = LogisticsManager.getInstance().getAvailableItems(pipe.world, LogisticsNetworkManager.fetchNetwork(pipe.world, pipe.getRouter()).routers);
        } else {
            _availableItems = new HashMap<>();
        }
        if (option == DisplayOptions.CraftOnly || option == DisplayOptions.Both){
            craftableItems = LogisticsManager.getInstance().getCraftableItems(pipe.world, LogisticsNetworkManager.fetchNetwork(pipe.world, pipe.getRouter()).routers);
        } else {
            craftableItems = new LinkedList<>();
        }
        allItems.clear();

        outer:
        for (ItemIdentifier item : _availableItems.keySet()){
            for (int i = 0; i <allItems.size(); i++){
                if (item.item.id < allItems.get(i).getItem().item.id || item.item.id == allItems.get(i).getItem().item.id && item.itemDamage < allItems.get(i).getItem().itemDamage){
                    allItems.add(i, item.makeStack(_availableItems.get(item)));
                    continue outer;
                }
            }
            allItems.addLast(item.makeStack(_availableItems.get(item)));
        }

        outer:
        for (ItemIdentifier item : craftableItems){
            if (_availableItems.containsKey(item)) continue;
            for (int i = 0; i <allItems.size(); i++){
                if (item.item.id < allItems.get(i).getItem().item.id || item.item.id == allItems.get(i).getItem().item.id && item.itemDamage < allItems.get(i).getItem().itemDamage){
                    allItems.add(i, item.makeStack(0));
                    continue outer;
                }
            }
            allItems.addLast(item.makeStack(0));
        }
        PacketHelper.sendTo(player, new SendScreenContentS2CPacket(allItems));
    }

    public static void requestMacrolist(NbtCompound itemlist, RequestItems requester, final PlayerEntity player) {
        NbtList list = itemlist.getList("inventar");
        LinkedList<ItemIdentifierStack> transaction = new LinkedList<>();
        List<ItemMessage> items = new ArrayList<>();
        for(int i = 0;i < list.size();i++) {
            NbtCompound itemnbt = (NbtCompound) list.get(i);
            NbtCompound itemNBTContent = itemnbt.getCompound("nbt");
            if(!itemnbt.contains("nbt")) {
                itemNBTContent = null;
            }
            // TODO: maybe invalid identifier check is needed
            ItemIdentifierStack stack = ItemIdentifier.get(ItemRegistry.INSTANCE.get(Identifier.of(itemnbt.getString("identifier"))),itemnbt.getInt("data"),itemNBTContent).makeStack(itemnbt.getInt("amount"));
            transaction.add(stack);
            items.add(new ItemMessage(stack));
        }
        RequestManager.request(transaction, requester, LogisticsNetworkManager.fetchRoutersByMetric(requester.getRouter().getPipe().world, requester.getRouter()), new RequestLog() {

            @Override
            public void handleSucessfullRequestOfList(LinkedList<ItemMessage> items) {
                PacketHelper.sendTo(player, new ItemMessageS2CPacket(items, false));
            }

            @Override
            public void handleSucessfullRequestOf(ItemMessage item) {
                //Not used here
            }

            @Override
            public void handleMissingItems(LinkedList<ItemMessage> list) {
                PacketHelper.sendTo(player, new ItemMessageS2CPacket(list, true));
            }
        });
    }
}
