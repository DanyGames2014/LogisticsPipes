package net.danygames2014.logisticspipes.gui.hud.modules;

import net.danygames2014.logisticspipes.interfaces.HUDButton;
import net.danygames2014.logisticspipes.interfaces.HUDModuleRenderer;
import net.danygames2014.logisticspipes.module.PassiveSupplierModule;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class PassiveSupplierHud implements HUDModuleRenderer {

    private final PassiveSupplierModule module;

    public PassiveSupplierHud(PassiveSupplierModule module) {
        this.module = module;
    }

    @Override
    public void renderContent() {
        Minecraft mc = Minecraft.INSTANCE;
        GL11.glScalef(1.0F, 1.0F, -0.000001F);
        BasicGuiHelper.renderItemIdentifierStackListIntoHud(ItemIdentifierStack.getListFromInventory(module.getFilterInventory()), null, 0, -25, -32, 3, 9, 18, 18, mc, true, true, true, true);
        GL11.glScalef(1.0F, 1.0F, 1 / -0.000001F);
    }

    @Override
    public List<HUDButton> getButtons() {
        return null;
    }
}
