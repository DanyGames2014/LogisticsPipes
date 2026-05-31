package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.util.InventoryUtil;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.minecraft.item.ItemStack;

public class TagItemSinkModule extends ItemSinkModule{
    @Override
    public SinkReply sinksItem(ItemStack item) {
        InventoryUtil invUtil = new InventoryUtil(filterInventory, false);
        if (invUtil.containsItemTag(ItemIdentifier.get(item))) {
            SinkReply reply = new SinkReply();
            reply.fixedPriority = SinkReply.FixedPriority.ItemSink;
            reply.isPassive = true;
            return reply;
        }
        if (isDefaultRoute) {
            SinkReply reply = new SinkReply();
            reply.fixedPriority = SinkReply.FixedPriority.DefaultRoute;
            reply.isPassive = true;
            reply.isDefault = true;
            return reply;
        }
        return null;
    }
}
