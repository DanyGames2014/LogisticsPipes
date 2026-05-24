package net.danygames2014.logisticspipes.gui.popup;

import net.danygames2014.logisticspipes.gui.SubScreen;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class RequestPopupSubScreen extends SubScreen {
    private String[] text;
    private int mWidth = 0;
    private PlayerEntity player;

    public RequestPopupSubScreen(PlayerEntity player, Object... message) {
        super(200, (message.length * 10) + 40, 0, 0);
        List<String> textArray = new ArrayList<>();
        for(Object o:message) {
            if(o instanceof Object[]) {
                for(Object o2:(Object[])o) {
                    textArray.add(o2.toString());
                }
            } else {
                textArray.add(o.toString());
            }
        }
        text = textArray.toArray(new String[]{});
        this.ySize = (text.length * 10) + 40;
        this.player = player;
    }

    @Override
    public void init() {
        super.init();
        buttons.add(new ButtonWidget(0, xCenter - 55, bottom - 25, 50,20,"OK"));
        buttons.add(new ButtonWidget(1, xCenter + 5, bottom - 25, 50,20,"Log"));
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
        switch(button.id) {
            case 0:
                super.exitScreen();
                break;
            case 1:
                for(String msg:text) {
                    player.sendMessage(msg);
                }
                ((ButtonWidget)buttons.get(1)).active = false;
                break;
        }
    }
}
