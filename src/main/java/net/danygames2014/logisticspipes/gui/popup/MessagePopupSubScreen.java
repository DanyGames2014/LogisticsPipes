package net.danygames2014.logisticspipes.gui.popup;

import net.danygames2014.logisticspipes.gui.SubScreen;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.awt.*;

public class MessagePopupSubScreen extends SubScreen {
    private final String[] text;
    private int mWidth = 0;

    public MessagePopupSubScreen( Object... message) {
        super(200, (message.length * 10) + 40, 0, 0);
        text = new String[message.length];
        int i=0;
        for(Object o:message) {
            if(o instanceof Object[]) {
                for(Object oZwei:(Object[])o) {
                    text[i++] = oZwei.toString();
                }
            } else {
                text[i++] = o.toString();
            }
        }
    }

    @Override
    public void init() {
        super.init();
        buttons.clear();
        buttons.add(new ButtonWidget(0, xCenter - 25, bottom - 25, 50,20,"OK"));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if(mWidth == 0) {
            int lWidth = 0;
            for(String msg:text) {
                int tWidth = this.textRenderer.getWidth(msg);
                if(tWidth > lWidth) {
                    lWidth = tWidth;
                }
            }
            xSize = mWidth = Math.max(Math.min(lWidth + 20,400),120);
            super.init();
        }
        BasicGuiHelper.drawGuiBackGround(minecraft, guiLeft, guiTop, right, bottom, zOffset, true);
        for(int i=0;i < 9 && i < this.text.length;i++) {
            if(this.text[i] == null) continue;
            String msg = BasicGuiHelper.getCuttedString(this.text[i], mWidth - 10, this.textRenderer);
            int stringWidth = this.textRenderer.getWidth(msg);
            this.textRenderer.draw(msg, xCenter - (stringWidth / 2), guiTop + 10 + (i * 10), 0x404040);
        }
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            super.exitScreen();
        }
    }
}
