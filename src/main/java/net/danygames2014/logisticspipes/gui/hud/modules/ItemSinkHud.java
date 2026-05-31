package net.danygames2014.logisticspipes.gui.hud.modules;

import net.danygames2014.logisticspipes.interfaces.HUDButton;
import net.danygames2014.logisticspipes.interfaces.HUDModuleRenderer;
import net.danygames2014.logisticspipes.module.ItemSinkModule;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ItemSinkHud implements HUDModuleRenderer {
    private final ItemSinkModule module;

    public ItemSinkHud(ItemSinkModule module) {
        this.module = module;
    }

    @Override
    public void renderContent() {
        Minecraft mc = Minecraft.INSTANCE;
        GL11.glScalef(1.0F, 1.0F, -0.00001F);
        BasicGuiHelper.renderItemIdentifierStackListIntoHud(ItemIdentifierStack.getListFromInventory(module.getFilterInventory()), null, 0, -25, -32, 3, 9, 18, 18, mc, false, false, true, true);
        GL11.glScalef(1.0F, 1.0F, 1 / -0.00001F);
        mc.textRenderer.draw("Default:" , -29, 25, 0);
        if(module.isDefaultRoute()) {
            mc.textRenderer.draw("Yes" , 11, 25, 0);
        } else {
            mc.textRenderer.draw("No" , 15, 25, 0);
        }
    }

    @Override
    public List<HUDButton> getButtons() {
        return null;
    }
}
