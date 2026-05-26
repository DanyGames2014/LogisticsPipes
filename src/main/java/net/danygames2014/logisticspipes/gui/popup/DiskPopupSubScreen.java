package net.danygames2014.logisticspipes.gui.popup;

import net.danygames2014.logisticspipes.client.gui.screen.LogisticsBaseScreen;
import net.danygames2014.logisticspipes.client.gui.screen.NormalOrderScreenMk2;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.gui.SubScreen;
import net.danygames2014.logisticspipes.gui.TextInputWidget;
import net.danygames2014.logisticspipes.network.RequestDiskMacroC2SPacket;
import net.danygames2014.logisticspipes.network.SetDiskNameC2SPacket;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class DiskPopupSubScreen extends SubScreen {
    NormalOrderScreenMk2 mainScreen;
    private int scroll = 0;
    private int selected = -1;

    private TextInputWidget textInputWidget;

    public DiskPopupSubScreen(NormalOrderScreenMk2 mainScreen) {
        super(150, 200, 0, 0);
        this.mainScreen = mainScreen;
    }

    @Override
    public void init() {
        super.init();

        buttons.clear();
        buttons.add(new SmallButtonWidget(0, xCenter + 16	, bottom - 27, 50, 10, "Request"));
        buttons.add(new SmallButtonWidget(1, xCenter + 16	, bottom - 15, 50, 10, "Exit"));
        buttons.add(new SmallButtonWidget(2, xCenter - 66	, bottom - 27, 50, 10, "Add"));
        buttons.add(new SmallButtonWidget(3, xCenter - 66	, bottom - 15, 50, 10, "Delete"));
        buttons.add(new SmallButtonWidget(4, xCenter - 12	, bottom - 27, 25, 10, "/\\"));
        buttons.add(new SmallButtonWidget(5, xCenter - 12	, bottom - 15, 25, 10, "\\/"));

        this.textInputWidget = new TextInputWidget(
            this.textRenderer,
            guiLeft + 10,
            guiTop + 28,
            (right - 10) - (guiLeft + 10),
            17
        );

        if(mainScreen.getDisk().getStationNbt().contains("name")) {
            this.textInputWidget.setText(mainScreen.getDisk().getStationNbt().getString("name"));
        } else {
            this.textInputWidget.setText("Disk");
        }
    }

    private void writeDiskName() {
        PacketHelper.send(new SetDiskNameC2SPacket(mainScreen.pipe.x, mainScreen.pipe.y, mainScreen.pipe.z, textInputWidget.getText()));
        mainScreen.getDisk().getStationNbt().putString("name", textInputWidget.getText());
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        BasicGuiHelper.drawGuiBackGround(minecraft, guiLeft, guiTop, right, bottom, zOffset, true);
        textRenderer.drawWithShadow("Disk", xCenter - (textRenderer.getWidth("Disk") / 2), guiTop + 10, 0xFFFFFF);

        textInputWidget.render();

        if(!mainScreen.getDisk().getStationNbt().contains("macroList")) {
            mainScreen.getDisk().getStationNbt().put("macroList", new NbtList());
        }

        fill(guiLeft + 6, guiTop + 46, right - 6, bottom - 30, BasicGuiHelper.ConvertEnumToColor(LogisticsBaseScreen.Colors.MiddleGrey));

        NbtList list = mainScreen.getDisk().getStationNbt().getList("macroList");

        if(scroll + 12 > list.size()) {
            scroll = list.size() - 12;
        }
        if(scroll < 0) {
            scroll = 0;
        }

        boolean flag = false;

        for(int i = scroll;i < list.size() && (i - scroll) < 12;i++) {
            if(guiLeft + 8 < mouseX && mouseX < right - 8 && guiTop + 48 + ((i - scroll) * 10) < mouseY && mouseY < guiTop + 59 + ((i - scroll) * 10) && Mouse.isButtonDown(0)) {
                selected = i;
                mouseX = 0;
                mouseY = 0;
            }
            if(i == selected) {
                fill(guiLeft + 8, guiTop + 48 + ((i - scroll) * 10), right - 8, guiTop + 59 + ((i - scroll) * 10), BasicGuiHelper.ConvertEnumToColor(LogisticsBaseScreen.Colors.DarkGrey));
                flag = true;
            }
            NbtCompound entry = (NbtCompound) list.get(i);
            String name = entry.getString("name");
            textRenderer.draw(name, guiLeft + 10, guiTop + 50 + ((i - scroll) * 10), 0xFFFFFF);
        }

        if(!flag) {
            selected = -1;
        }

        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void onMouseEventSub() {
        int wheel = org.lwjgl.input.Mouse.getDWheel() / 120;
        if(wheel == 0) super.onMouseEventSub();
        if(wheel < 0) {
            scroll++;
        } else if(wheel > 0) {
            if(scroll > 0) {
                scroll--;
            }
        }
    }

    private void handleRequest() {
        PacketHelper.send(new RequestDiskMacroC2SPacket(mainScreen.pipe.x, mainScreen.pipe.y, mainScreen.pipe.z, selected));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            handleRequest();
        } else if (button.id == 1) {
            this.exitScreen();
        } else if (button.id == 2) {
            this.setSubScreen(new AddMacroPopupSubScreen(mainScreen));
        } else if (button.id == 3) {
            if(!mainScreen.getDisk().getStationNbt().contains("macroList")) {
                mainScreen.getDisk().getStationNbt().put("macroList", new NbtList());
            }

            NbtList list = mainScreen.getDisk().getStationNbt().getList("macroList");
            NbtList listnew = new NbtList();

            for(int i = 0;i < list.size();i++) {
                if(i != selected) {
                    listnew.add(list.get(i));
                }
            }
            selected = -1;
            mainScreen.getDisk().getStationNbt().put("macroList", listnew);
        } else if (button.id == 4) {
            if(scroll > 0) {
                scroll--;
            }
        } else if (button.id == 5) {
            scroll++;
        } else {
            super.buttonClicked(button);
        }
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        boolean focused = textInputWidget.isFocused();

        if (textInputWidget.keyPressed(character, keyCode)) {
            if (focused && !textInputWidget.isFocused()) {
                writeDiskName();
            }
            return;
        }

        if (keyCode == 1) {
            this.exitScreen();
            return;
        }

        super.keyPressed(character, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            boolean focused = textInputWidget.isFocused();

            textInputWidget.mouseClicked(mouseX, mouseY, button);

            if (focused && !textInputWidget.isFocused()) {
                writeDiskName();
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
}
