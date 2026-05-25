package net.danygames2014.logisticspipes.gui.popup;

import net.danygames2014.logisticspipes.client.gui.screen.LogisticsBaseScreen;
import net.danygames2014.logisticspipes.client.gui.screen.NormalOrderScreenMk2;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.gui.SubScreen;
import net.danygames2014.logisticspipes.gui.TextInputWidget;
import net.danygames2014.logisticspipes.interfaces.ItemSearch;
import net.danygames2014.logisticspipes.network.RequestDiskContentC2SPacket;
import net.danygames2014.logisticspipes.network.SetDiskContentC2SPacket;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.client.TooltipHelper;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class AddMacroPopupSubScreen extends SubScreen implements ItemSearch {
    private NormalOrderScreenMk2 mainScreen;

    private int mousePosX = 0;
    private int mousePosY = 0;
    private int mousebutton = 0;

    private int pageAll = 0;
    private int maxPageAll = 0;
    private int pageMacro = 0;
    private int maxPageMacro = 0;
    private int wheelup = 0;
    private int wheeldown = 0;
    private final LinkedList<ItemIdentifierStack> macroItems = new LinkedList<>();

    private TextInputWidget searchInputWidget;
    private TextInputWidget nameInputWidget;

    private Object[] tooltip;

    public AddMacroPopupSubScreen(NormalOrderScreenMk2 mainScreen) {
        super(200, 200, 0, 0);
        this.mainScreen = mainScreen;
    }

    @Override
    public void init() {
        super.init();
        buttons.clear();
        buttons.add(new SmallButtonWidget(0, right - 15, guiTop + 5, 10 ,10 ,">")); // Next pageAll
        buttons.add(new SmallButtonWidget(1, right - 90, guiTop + 5, 10, 10, "<")); // Prev pageAll
        buttons.add(new SmallButtonWidget(2, right - 15, guiTop + 135, 10 ,10 ,">")); // Next pageAll
        buttons.add(new SmallButtonWidget(3, right - 90, guiTop + 135, 10, 10, "<")); // Prev pageAll
        buttons.add(new ButtonWidget(4, right - 39, bottom - 27, 35, 20, "Save")); // Prev pageAll

        this.searchInputWidget = new TextInputWidget(
                this.textRenderer,
                guiLeft + 50,
                guiTop + 118,
                (right - 10) - (guiLeft + 50),
                17
        );

        this.nameInputWidget = new TextInputWidget(
                this.textRenderer,
                guiLeft + 36,
                bottom - 25,
                (right - 40) - (guiLeft + 36),
                17
        );
        this.nameInputWidget.setText("Macro1");
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        mousePosX = mouseX;
        mousePosY = mouseY;
        mousebutton = button;

        this.searchInputWidget.mouseClicked(mouseX, mouseY, button);
        this.nameInputWidget.mouseClicked(mouseX, mouseY, button);

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onMouseEventSub() {
        int wheel = Mouse.getDWheel() / 120;
        if(wheel == 0) super.onMouseEventSub();
        if(wheel < 0) {
            wheeldown = wheel * -1;
        } else {
            wheelup = wheel;
        }
    }

    @Override
    public void render(int mX, int mY, float delta) {
        BasicGuiHelper.drawGuiBackGround(minecraft, guiLeft, guiTop, right, bottom, zOffset, false);

        textRenderer.draw("Add Macro", guiLeft + textRenderer.getWidth("Add Macro") / 2, guiTop + 6, 0x404040);

        maxPageAll = (int) Math.floor((getSearchedItemNumber(mainScreen.allItems) - 1)  / 45F);
        if(maxPageAll == -1) maxPageAll = 0;
        if (pageAll > maxPageAll){
            pageAll = maxPageAll;
        }

        String pageString1 = "Page " + (pageAll + 1) + " / " + (maxPageAll + 1);
        textRenderer.draw(pageString1, right - 47 - textRenderer.getWidth(pageString1) / 2 , guiTop + 6 , 0x404040);

        textRenderer.draw("Macro Items", guiLeft + textRenderer.getWidth("Add Macro") / 2, guiTop + 136, 0x404040);

        maxPageMacro = (int) Math.floor((getSearchedItemNumber(macroItems) - 1)  / 9F);
        if(maxPageMacro == -1) maxPageMacro = 0;
        if (pageMacro > maxPageMacro){
            pageMacro = maxPageMacro;
        }

        String pageString2 = "Page " + (pageMacro + 1) + " / " + (maxPageMacro + 1);
        textRenderer.draw(pageString2, right - 47 - textRenderer.getWidth(pageString2) / 2 , guiTop + 136 , 0x404040);


        textRenderer.draw("Search:", guiLeft + 8, guiTop + 122, 0x404040);
        searchInputWidget.render();

        textRenderer.draw("Name:", guiLeft + 8, bottom - 20, 0x404040);
        nameInputWidget.render();

        int panelxSize = 20;
        int panelySize = 20;

        int ppi = 0;
        int column = 0;
        int row = 0;

        int mouseX = Mouse.getX() * this.width / this.minecraft.displayWidth;
        int mouseY = this.height - Mouse.getY() * this.height / this.minecraft.displayHeight - 1;

        int wheel = org.lwjgl.input.Mouse.getDWheel() / 120;
        if(wheel != 0) {
            if(wheel < 0) {
                mousebutton = 0;
            } else {
                mousebutton = 1;
            }
            mousePosX = mouseX;
            mousePosY = mouseY;
        }

        tooltip = null;

        fill(guiLeft + 8, guiTop + 16, right - 12, bottom - 84, BasicGuiHelper.ConvertEnumToColor(LogisticsBaseScreen.Colors.MiddleGrey));
        fill(guiLeft + 8, bottom - 52, right - 12, bottom - 32, BasicGuiHelper.ConvertEnumToColor(LogisticsBaseScreen.Colors.DarkGrey));

        for(ItemIdentifierStack itemStack : mainScreen.allItems) {
            ItemIdentifier item = itemStack.getItem();
            if(!itemSearched(item)) continue;
            ppi++;

            if (ppi <= 45 * pageAll) continue;
            if (ppi > 45 * (pageAll+1)) continue;
            ItemStack st = itemStack.makeNormalStack();
            int x = guiLeft + 10 + panelxSize * column;
            int y = guiTop + 18 + panelySize * row;

            GL11.glDisable(2896 /*GL_LIGHTING*/);

            if(!super.hasSubScreen()) {
                if (mouseX >= x && mouseX < x + panelxSize && mouseY >= y && mouseY < y + panelySize) {
                    fill(x - 2, y - 2, x + panelxSize - 2, y + panelySize - 2, BasicGuiHelper.ConvertEnumToColor(LogisticsBaseScreen.Colors.Black));
                    fill(x - 1, y - 1, x + panelxSize - 3, y + panelySize - 3, BasicGuiHelper.ConvertEnumToColor(LogisticsBaseScreen.Colors.DarkGrey));

                    tooltip = new Object[]{mouseX + guiLeft,mouseY + guiTop,st, false};
                }


                if(mousePosX != 0 && mousePosY != 0) {
                    if ((mousePosX >= x && mousePosX < x + panelxSize && mousePosY >= y && mousePosY < y + panelySize) || (mouseX >= x && mouseX < x + panelxSize && mouseY >= y && mouseY < y + panelySize && (wheeldown != 0 || wheelup != 0))) {
                        boolean handled = false;
                        for(ItemIdentifierStack stack:macroItems) {
                            if(stack.getItem().equals(item)) {
                                if(mousebutton == 0 || wheelup != 0) {
                                    stack.stackSize += 1 + (wheelup != 0 ? wheelup - 1: 0);
                                } else if(mousebutton == 1 || wheeldown != 0) {
                                    stack.stackSize -= 1 + (wheeldown != 0 ? wheeldown - 1: 0);
                                    if(stack.stackSize <= 0) {
                                        macroItems.remove(stack);
                                    }
                                }
                                handled = true;
                                break;
                            }
                        }
                        if(!handled) {
                            int i = 0;
                            for(ItemIdentifierStack stack:macroItems) {
                                if(item.item.id == stack.getItem().item.id && item.itemDamage < stack.getItem().itemDamage) {
                                    if(mousebutton == 0 || wheelup != 0) {
                                        macroItems.add(i, item.makeStack(1 + (wheelup != 0 ? wheelup - 1: 0)));
                                    } else if(mousebutton == 2) {
                                        macroItems.add(i, item.makeStack(64));
                                    }
                                    handled = true;
                                    break;
                                }
                                if(item.item.id < stack.getItem().item.id) {
                                    if(mousebutton == 0 || wheelup != 0) {
                                        macroItems.add(i, item.makeStack(1 + (wheelup != 0 ? wheelup - 1: 0)));
                                    } else if(mousebutton == 2) {
                                        macroItems.add(i, item.makeStack(64));
                                    }
                                    handled = true;
                                    break;
                                }
                                i++;
                            }
                            if(!handled) {
                                if(mousebutton == 0 || wheelup != 0) {
                                    macroItems.addLast(item.makeStack(1 + (wheelup != 0 ? wheelup - 1: 0)));
                                } else if(mousebutton == 2) {
                                    macroItems.addLast(item.makeStack(64));
                                }
                            }
                        }
                        mousePosX = 0;
                        mousePosY = 0;
                    }
                }
            }
            column++;
            if (column == 9){
                row++;
                column = 0;
            }
        }

        BasicGuiHelper.renderItemIdentifierStackListIntoGui(mainScreen.allItems, this, pageAll, guiLeft + 10, guiTop + 18, 9, 45, panelxSize, panelySize, minecraft, false, false);

        ppi = 0;
        column = 0;
        row = 0;

        for(ItemIdentifierStack itemStack : macroItems) {
            ItemIdentifier item = itemStack.getItem();
            if(!itemSearched(item)) continue;
            ppi++;

            if (ppi <= 9 * pageMacro) continue;
            if (ppi > 9 * (pageMacro+1)) continue;
            ItemStack st = itemStack.makeNormalStack();
            int x = guiLeft + 10 + panelxSize * column;
            int y = guiTop + 150 + panelySize * row;

            GL11.glDisable(2896 /*GL_LIGHTING*/);

            if(!super.hasSubScreen()) {
                if (mouseX >= x && mouseX < x + panelxSize && mouseY >= y && mouseY < y + panelySize) {
                    tooltip = new Object[]{mouseX + guiLeft,mouseY + guiTop,st};
                }
            }
            column++;
            if (column == 9){
                row++;
                column = 0;
            }
        }

        BasicGuiHelper.renderItemIdentifierStackListIntoGui(macroItems, this, pageMacro, guiLeft + 10, guiTop + 150, 9, 9, panelxSize, panelySize, minecraft, true, true);

        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        super.render(mouseX, mouseY, delta);

        if(!this.hasSubScreen() && tooltip != null) {
            BasicGuiHelper.drawToolTip((int)tooltip[0] - mainScreen.guiLeft - 10, (int)tooltip[1] - mainScreen.guiTop - 15, TooltipHelper.getTooltipForItemStack(TranslationStorage.getInstance().get(((ItemStack)tooltip[2]).getTranslationKey() + ".name"), (ItemStack)tooltip[2], mainScreen.player.inventory, mainScreen), 0xFFFFFF);
        }
    }

    private int getSearchedItemNumber(List<ItemIdentifierStack> list) {
        int count = 0;
        for(ItemIdentifierStack item : list) {
            if(itemSearched(item.getItem())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean itemSearched(ItemIdentifier item) {
        String query = searchInputWidget.getText().trim().toLowerCase();
        if (query.isEmpty()) return true;

        if (isSearched(item.getFriendlyName().toLowerCase(), query)) return true;
        if (isSearched(String.valueOf(item.item.id), query)) return true;
        return false;
    }

    private boolean isSearched(String value, String search) {
        boolean flag = true;
        for(String s:search.split(" ")) {
            if(!value.contains(s)) {
                flag = false;
            }
        }
        return flag;
    }

    private void nextPageAll(){
        if (pageAll < maxPageAll){
            pageAll++;
        } else {
            pageAll = 0;
        }
    }

    private void prevPageAll(){
        if (pageAll > 0){
            pageAll--;
        } else {
            pageAll = maxPageAll;
        }
    }

    private void nextPageMacro(){
        if (pageMacro < maxPageMacro){
            pageMacro++;
        } else {
            pageMacro = 0;
        }
    }

    private void prevPageMacro(){
        if (pageMacro > 0){
            pageMacro--;
        } else {
            pageMacro = maxPageMacro;
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            nextPageAll();
        } else if (button.id == 1) {
            prevPageAll();
        } else if (button.id == 2) {
            nextPageMacro();
        } else if (button.id == 3) {
            prevPageMacro();
        } else if (button.id == 4) {
            if(!nameInputWidget.getText().isEmpty() && !macroItems.isEmpty()) {
                boolean flag = false;
                NbtList list = this.mainScreen.getDisk().getStationNbt().getList("macroList");

                for(int i = 0; i < list.size(); i++) {
                    NbtCompound tag = (NbtCompound) list.get(i);
                    String name = tag.getString("name");
                    if(name.equals(nameInputWidget.getText())) {
                        flag = true;
                    }
                }
                if(flag) {
                    this.setSubScreen(new MessagePopupSubScreen("Name '"+nameInputWidget.getText()+"' already exists", "Please chose a different one"));
                } else {
                    NbtCompound nbt = new NbtCompound();
                    nbt.putString("name", nameInputWidget.getText());
                    NbtList inventar = new NbtList();
                    for(ItemIdentifierStack stack:macroItems) {
                        NbtCompound itemNBT = new NbtCompound();
                        itemNBT.putString("identifier", ItemRegistry.INSTANCE.getId(stack.getItem().item).toString());
                        itemNBT.putInt("data", stack.getItem().itemDamage);
                        itemNBT.put("nbt", stack.getItem().nbt);
                        itemNBT.putInt("amount", stack.stackSize);
                        inventar.add(itemNBT);
                    }
                    nbt.put("inventar", inventar);
                    list.add(nbt);
                    this.mainScreen.getDisk().getStationNbt().put("macroList", list);
                    PacketHelper.send(new SetDiskContentC2SPacket(mainScreen.pipe.x, mainScreen.pipe.y, mainScreen.pipe.z, mainScreen.getDisk()));
                    this.exitScreen();
                }
            } else if(!macroItems.isEmpty()) {
                this.setSubScreen(new MessagePopupSubScreen("Please enter a name"));
            } else {
                this.setSubScreen(new MessagePopupSubScreen("Select some items"));
            }
        } else {
            super.buttonClicked(button);
        }
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (this.searchInputWidget.keyPressed(character, keyCode)) {
            return;
        }
        if (this.nameInputWidget.keyPressed(character, keyCode)) {
            return;
        }

        if (keyCode == 1) {
            this.exitScreen();
            return;
        }

        super.keyPressed(character, keyCode);
    }
}
