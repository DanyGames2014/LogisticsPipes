package net.danygames2014.logisticspipes.gui.hud;

import net.danygames2014.logisticspipes.block.entity.ProviderLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.danygames2014.logisticspipes.util.gui.hud.BasicHUDButton;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class ProviderHud extends BasicGuiHud {
    ProviderLogisticPipeBlockEntity pipe;
    private int page = 0;
    private int pageB = 0;

    public ProviderHud(ProviderLogisticPipeBlockEntity pipe) {
        this.pipe = pipe;

        this.addButton(new BasicHUDButton("<", -2, -50, 8, 8) {
            @Override
            public void clicked() {
                if(page > 0) {
                    page--;
                }
            }

            @Override
            public boolean shouldRenderButton() {
                return true;
            }

            @Override
            public boolean buttonEnabled() {
                return page > 0;
            }
        });
        this.addButton(new BasicHUDButton(">", 37, -50, 8, 8) {
            @Override
            public void clicked() {
                if(page + 1 < getMaxPage()) {
                    page++;
                }
            }

            @Override
            public boolean shouldRenderButton() {
                return true;
            }

            @Override
            public boolean buttonEnabled() {
                return page + 1 < getMaxPage();
            }
        });
        this.addButton(new BasicHUDButton("<", -2, 21, 8, 8) {
            @Override
            public void clicked() {
                if(pageB > 0) {
                    pageB--;
                }
            }

            @Override
            public boolean shouldRenderButton() {
                return true;
            }

            @Override
            public boolean buttonEnabled() {
                return pageB > 0;
            }
        });
        this.addButton(new BasicHUDButton(">", 37, 21, 8, 8) {
            @Override
            public void clicked() {
                if(pageB + 1 < getMaxPageOrderer()) {
                    pageB++;
                }
            }

            @Override
            public boolean shouldRenderButton() {
                return true;
            }

            @Override
            public boolean buttonEnabled() {
                return pageB + 1 < getMaxPageOrderer();
            }
        });
    }

    @Override
    public void renderHeadUpDisplay(double distance, boolean day, Minecraft mc) {
        if(day) {
            GL11.glColor4b((byte)64, (byte)64, (byte)64, (byte)64);
        } else {
            GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)64);
        }
        BasicGuiHelper.drawGuiBackGround(mc, -50, -55, 50, 55, 0, false);
        if(day) {
            GL11.glColor4b((byte)64, (byte)64, (byte)64, (byte)127);
        } else {
            GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)127);
        }

        GL11.glTranslatef(0.0F, 0.0F, -0.01F);
        super.renderHeadUpDisplay(distance, day, mc);

        GL11.glTranslatef(0.0F, 0.0F, -0.005F);
        GL11.glScalef(1.5F, 1.5F, 0.0001F);
        GL11.glScalef(0.8F, 0.8F, -1F);
        BasicGuiHelper.renderItemIdentifierStackListIntoHud(pipe.itemList, null, page, -35, -35, 4, 12, 18, 18, mc, true, true, true, true);
        BasicGuiHelper.renderItemIdentifierStackListIntoHud(pipe.itemListOrderer, null, pageB, -35, 25, 4, 4, 18, 18, mc, true, true, true, true);
        GL11.glScalef(0.8F, 0.8F, -1F);
        String message = "(" + Integer.toString(page + 1) + "/" + Integer.toString(getMaxPage()) + ")";
        mc.textRenderer.draw(message , 9, -50, 0);
        message = "(" + (pageB + 1) + "/" + getMaxPageOrderer() + ")";
        mc.textRenderer.draw(message , 9, 23, 0);
    }

    public int getMaxPage() {
        int ret = pipe.itemList.size() / 12;
        if(pipe.itemList.size() % 12 != 0 || ret == 0) {
            ret++;
        }
        return ret;
    }

    public int getMaxPageOrderer() {
        int ret = pipe.itemListOrderer.size() / 4;
        if(pipe.itemListOrderer.size() % 4 != 0 || ret == 0) {
            ret++;
        }
        return ret;
    }

    @Override
    public boolean display() {
        return true;
    }

    @Override
    public boolean cursorOnWindow(int x, int y) {
        return -50 < x && x < 50 && -55 < y && y < 55;
    }
}
