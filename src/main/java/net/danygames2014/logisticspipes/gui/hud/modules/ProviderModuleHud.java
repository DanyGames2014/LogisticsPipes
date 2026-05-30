package net.danygames2014.logisticspipes.gui.hud.modules;

import net.danygames2014.logisticspipes.interfaces.HUDButton;
import net.danygames2014.logisticspipes.interfaces.HUDModuleRenderer;
import net.danygames2014.logisticspipes.module.ProviderModule;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.danygames2014.logisticspipes.util.gui.hud.BasicHUDButton;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ProviderModuleHud implements HUDModuleRenderer {
    private List<HUDButton> buttons = new ArrayList<>();

    private int page = 0;

    private final ProviderModule module;

    public ProviderModuleHud(ProviderModule module) {
        this.module = module;
        buttons.add(new BasicHUDButton("<" , 8, -35, 8, 8) {
            @Override
            public boolean shouldRenderButton() {
                return true;
            }

            @Override
            public void clicked() {
                page--;
            }

            @Override
            public boolean buttonEnabled() {
                return page > 0;
            }
        });
        buttons.add(new BasicHUDButton(">" , 20, -35, 8, 8) {
            @Override
            public boolean shouldRenderButton() {
                return true;
            }

            @Override
            public void clicked() {
                page++;
            }

            @Override
            public boolean buttonEnabled() {
                return page + 1 < getMaxPage();
            }
        });
    }

    public int getMaxPage() {
        int ret = module.displayList.size() / 9;
        if(module.displayList.size() % 9 != 0 || ret == 0) {
            ret++;
        }
        return ret;
    }

    @Override
    public void renderContent() {
        Minecraft mc = Minecraft.INSTANCE;
        GL11.glScalef(1.0F, 1.0F, -0.00001F);
        BasicGuiHelper.renderItemIdentifierStackListIntoGui(module.displayList, null, page, -25, -24, 3, 9, 18, 18, mc, true, true, true, true);
        GL11.glScalef(1.0F, 1.0F, 1 / -0.00001F);
    }

    @Override
    public List<HUDButton> getButtons() {
        return buttons;
    }
}
