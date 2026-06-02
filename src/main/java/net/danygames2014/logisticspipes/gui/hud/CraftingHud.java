package net.danygames2014.logisticspipes.gui.hud;

import net.danygames2014.logisticspipes.block.entity.CraftingLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CraftingHud extends BasicGuiHud{
    private final CraftingLogisticPipeBlockEntity pipe;

    public CraftingHud(CraftingLogisticPipeBlockEntity pipe) {
        this.pipe = pipe;
    }

    @Override
    public void renderHeadUpDisplay(double d, boolean day, Minecraft mc) {
        if(day) {
            GL11.glColor4b((byte)64, (byte)64, (byte)64, (byte)64);
        } else {
            GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)64);
        }
        if(!pipe.displayList.isEmpty()) {
            BasicGuiHelper.drawGuiBackGround(mc, -50, -28, 50, 30, 0, false);
        } else {
            BasicGuiHelper.drawGuiBackGround(mc, -30, -22, 30, 25, 0, false);
        }
        if(day) {
            GL11.glColor4b((byte)64, (byte)64, (byte)64, (byte)127);
        } else {
            GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)127);
        }

        GL11.glTranslatef(0.0F, 0.0F, -0.005F);
        GL11.glScalef(1.5F, 1.5F, 0.0001F);

        if(!pipe.displayList.isEmpty()) {
            String message = "Result:";
            mc.textRenderer.draw(message , -28, -10, 0);
            message = "Todo:";
            mc.textRenderer.draw(message , -28, 5, 0);
        } else {
            String message = "Result:";
            mc.textRenderer.draw(message , -16, -10, 0);
        }
        GL11.glScalef(0.8F, 0.8F, -1F);
        List<ItemIdentifierStack> list = new ArrayList<>();
        ItemIdentifier item = pipe.getCraftedItem();
        if(item != null && pipe.getCraftedItemLogic() != null){
            list.add(item.makeStack(pipe.getCraftedItemLogic().count));
        }
        if(!pipe.displayList.isEmpty()) {
            BasicGuiHelper.renderItemIdentifierStackListIntoHud(list, null, 0, 13, -17, 1, 1, 18, 18, mc, true, true, true, true);
            BasicGuiHelper.renderItemIdentifierStackListIntoHud(pipe.displayList, null, 0, 13, 3, 1, 1, 18, 18, mc, true, true, true, true);
        } else {
            BasicGuiHelper.renderItemIdentifierStackListIntoHud(list, null, 0, -9, 0, 1, 1, 18, 18, mc, true, true, true, true);
        }
    }

    @Override
    public boolean display() {
        return true;
    }

    @Override
    public boolean cursorOnWindow(int x, int y) {
        if(!pipe.displayList.isEmpty()) {
            return -50 < x && x < 50 && -28 < y && y < 30;
        } else {
            return -30 < x && x < 30 && -22 < y && y < 25;
        }
    }
}
