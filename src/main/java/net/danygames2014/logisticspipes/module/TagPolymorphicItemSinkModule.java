package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.util.InventoryUtil;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class TagPolymorphicItemSinkModule extends PolymorphicItemSinkModule {
    @Override
    public SinkReply sinksItem(ItemStack item) {
        Inventory targetInventory = invProvider.getInventory();
        if (targetInventory == null) {
            return null;
        }

        InventoryUtil invUtil = new InventoryUtil(targetInventory, false);
        if (!invUtil.containsItemTag(ItemIdentifier.get(item))) {
            return null;
        }

        SinkReply reply = new SinkReply();
        reply.fixedPriority = SinkReply.FixedPriority.ItemSink;
        reply.isDefault = false;
        reply.isPassive = true;
        return reply;
    }
}
