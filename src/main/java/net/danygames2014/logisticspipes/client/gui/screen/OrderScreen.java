package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.config.Config;
import net.danygames2014.logisticspipes.gui.CheckBoxWidget;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.gui.SubScreenController;
import net.danygames2014.logisticspipes.gui.popup.RequestPopupSubScreen;
import net.danygames2014.logisticspipes.interfaces.ItemSearch;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.network.SendScreenContentS2CPacket;
import net.danygames2014.logisticspipes.network.SubmitRequestC2SPacket;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.ItemMessage;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.glasslauncher.mods.gcapi3.api.GCAPI;
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.TooltipHelper;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class OrderScreen extends LogisticsBaseScreen implements ItemSearch {

    protected final RequestItems itemRequester;

    protected ItemIdentifierStack selectedItem = null;
    public final LinkedList<ItemIdentifierStack> allItems = new LinkedList<>();
    protected String searchinput1 = "";
    protected String searchinput2 = "";
    protected boolean editsearch = false;
    protected boolean editsearchb = false;
    protected boolean displaycursor = true;
    protected long oldSystemTime = 0;
    protected static int searchWidth = 150;

    protected ArsenicItemRenderer itemRenderer = new ArsenicItemRenderer(new ItemRenderer());

    protected int lastClickedx = 0;
    protected int lastClickedy = 0;
    protected int lastClickedk = 0;

    protected final String title = "Request items";
    protected boolean clickWasButton = false;

    protected int page = 0;
    protected int maxPage = 0;

    protected int requestCount = 1;
    protected Object[] tooltip = null;

    protected boolean listbyserver = false;


    public OrderScreen(RequestItems itemRequester, PlayerEntity player) {
        super(player, null, new ModuleScreenHandler(player, null));

        this.itemRequester = itemRequester;

        this.backgroundWidth = 220;
        this.backgroundHeight = 240;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if(tooltip == null) return;

        BasicGuiHelper.drawToolTip((int)tooltip[0], (int)tooltip[1], TooltipHelper.getTooltipForItemStack(TranslationStorage.getInstance().get(((ItemStack)tooltip[2]).getTranslationKey() + ".name"), (ItemStack)tooltip[2], player.inventory, this), 0xFFFFFF);
    }

    protected abstract void  refreshItems();

    public void handlePacket(SendScreenContentS2CPacket packet) {
        listbyserver = true;
        allItems.clear();
        allItems.addAll(packet.allItems);
    }

    @Override
    public void init() {
        super.init();
        buttons.clear();
        buttons.add(new ButtonWidget(0, right - 55, bottom - 25, 50,20,"Request")); // Request
        buttons.add(new SmallButtonWidget(1, right - 15, guiTop + 5, 10 ,10 ,">")); // Next page
        buttons.add(new SmallButtonWidget(2, right - 90, guiTop + 5, 10, 10, "<")); // Prev page
        buttons.add(new ButtonWidget(3, guiLeft + 10, bottom - 25, 46, 20, "Refresh")); // Refresh
        buttons.add(new SmallButtonWidget(10, xCenter - 41, bottom - 15, 26, 10, "---")); // -64
        buttons.add(new SmallButtonWidget(4, xCenter - 41, bottom - 26, 15, 10, "--")); // -10
        buttons.add(new SmallButtonWidget(5, xCenter - 25, bottom - 26, 10, 10, "-")); // -1
        buttons.add(new SmallButtonWidget(6, xCenter + 16, bottom - 26, 10, 10, "+")); // +1
        buttons.add(new SmallButtonWidget(7, xCenter + 28, bottom - 26, 15, 10, "++")); // +10
        buttons.add(new SmallButtonWidget(11, xCenter + 16, bottom - 15, 26, 10, "+++")); // +64
        buttons.add(new CheckBoxWidget(8, guiLeft + 9, bottom - 60, 14, 14, Config.HUD_CONFIG.displayPopup)); // Popup

        refreshItems();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void drawBackground(float tickDelta) {
        BasicGuiHelper.drawGuiBackGround(minecraft, guiLeft, guiTop, right, bottom, zOffset, true);

        maxPage = (getSearchedItemNumber() - 1) / 70;
        if(maxPage == -1) maxPage = 0;
        if (page > maxPage){
            page = maxPage;
        }

        textRenderer.draw(title, guiLeft + textRenderer.getWidth(title) / 2, guiTop + 6, 0x404040);
        String pageString = "Page " + (page + 1) + " / " + (maxPage + 1);
        textRenderer.draw(pageString, right - 47 - textRenderer.getWidth(pageString) / 2 , guiTop + 6 , 0x404040);

        if(buttons.get(9) instanceof CheckBoxWidget checkBoxWidget && checkBoxWidget.getState()) {
            textRenderer.draw("Popup", guiLeft + 25 , bottom - 56, 0x404040);
        } else {
            textRenderer.draw("Popup", guiLeft + 25 , bottom - 56, 0xA0A0A0);
        }

        String StackrequestCount = ""+(requestCount/getStackAmount()) + "+" + (requestCount % getStackAmount());

        textRenderer.draw(requestCount + "", xCenter - textRenderer.getWidth(requestCount+"") / 2, bottom - 24, 0x404040);
        textRenderer.draw(StackrequestCount + "", xCenter - textRenderer.getWidth(StackrequestCount+"") / 2, bottom - 14, 0x404040);

        //SearchInput
        if(editsearch) {
            fill(guiLeft + 30, bottom - 80, right - 28, bottom - 63, Colors.Black);
            fill(guiLeft + 31, bottom - 79, right - 29, bottom - 64, Colors.White);
        } else {
            fill(guiLeft + 31, bottom - 79, right - 29, bottom - 64, Colors.Black);
        }
        fill(guiLeft + 32, bottom - 78, right - 30, bottom - 65, Colors.DarkGrey);

        textRenderer.draw(searchinput1 + searchinput2, guiLeft + 35, bottom - 75, 0xFFFFFF);
        if(editsearch) {
            int linex = guiLeft + 35 + textRenderer.getWidth(searchinput1);
            if(System.currentTimeMillis() - oldSystemTime > 500) {
                displaycursor = !displaycursor;
                oldSystemTime = System.currentTimeMillis();
            }
            if(displaycursor) {
                fill(linex, bottom - 77, linex + 1, bottom - 66, Colors.White);
            }
        }

        //Click into search
        if(lastClickedx != -10000000 &&	lastClickedy != -10000000) {
            if (lastClickedx >= guiLeft + 32 && lastClickedx < right - 28 &&
                        lastClickedy >= bottom - 80 && lastClickedy < bottom - 63){
                editsearch = true;
                lastClickedx = -10000000;
                lastClickedy = -10000000;
                if(lastClickedk == 1) {
                    searchinput1 = "";
                    searchinput2 = "";
                }
            } else {
                editsearch = false;
            }
        }

        int ppi = 0;

        int panelxSize = 20;
        int panelySize = 20;

        tooltip = null;

        fill(guiLeft + 8, guiTop + 16, right - 12, bottom - 84, Colors.MiddleGrey);

        if(!listbyserver) {
            int graphic = ((int)(System.currentTimeMillis() / 250) % 5);
//			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture());
            minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/gui/icons.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = Tessellator.INSTANCE;
            tessellator.startQuads();
            int xPosition = xCenter - 50;
            int yPosition = guiTop + 40;
            tessellator.vertex(xPosition			, yPosition + 100		, zOffset, 0.04	, 0.72 + (graphic * 0.03125));
            tessellator.vertex(xPosition + 100	, yPosition + 100		, zOffset, 0.08	, 0.72 + (graphic * 0.03125));
            tessellator.vertex(xPosition + 100	, yPosition				, zOffset, 0.08	, 0.69 + (graphic * 0.03125));
            tessellator.vertex(xPosition			, yPosition				, zOffset, 0.04	, 0.69 + (graphic * 0.03125));
            tessellator.draw();
        } else {
            for(ItemIdentifierStack itemStack : allItems) {
                ItemIdentifier item = itemStack.getItem();
                if(!itemSearched(item)) continue;
                ppi++;

                if (ppi <= 70 * page) continue;
                if (ppi > 70 * (page+1)) break;
                int row = ((ppi - 1) % 70) / 10;
                int column = (ppi - 1) % 10;
                ItemStack st = itemStack.makeNormalStack();
                int x = guiLeft + 10 + panelxSize * column;
                int y = guiTop + 18 + panelySize * row;

                GL11.glDisable(2896 /*GL_LIGHTING*/);
                int mouseX = Mouse.getX() * this.width / this.minecraft.displayWidth;
                int mouseY = this.height - Mouse.getY() * this.height / this.minecraft.displayHeight - 1;

                if (mouseX >= x && mouseX < x + panelxSize && mouseY >= y && mouseY < y + panelySize) {
                    fill(x - 2, y - 2, x + panelxSize - 2, y + panelySize - 2, Colors.Black);
                    fill(x - 1, y - 1, x + panelxSize - 3, y + panelySize - 3, Colors.DarkGrey);

                    tooltip = new Object[]{mouseX,mouseY,st};
                }

                if (lastClickedx >= x && lastClickedx < x + panelxSize && lastClickedy >= y && lastClickedy < y + panelySize){
                    selectedItem = itemStack;
                    fill(x - 2, y - 2, x + panelxSize - 2, y + panelySize - 2, Colors.Black);
                    fill(x - 1, y - 1, x + panelxSize - 3, y + panelySize - 3, Colors.LightGrey);
                    fill(x, y, x + panelxSize - 4, y + panelySize - 4, Colors.DarkGrey);
                    specialItemRendering(item, x, y);
                }
            }
            BasicGuiHelper.renderItemIdentifierStackListIntoGui(allItems, this, page, guiLeft + 10, guiTop + 18, 10, 70, panelxSize, panelySize, minecraft, true, false);
        }
        GL11.glDisable(2896 /*GL_LIGHTING*/);
    }

    public abstract void specialItemRendering(ItemIdentifier item, int x, int y);

    // TODO: implement
    @Override
    protected void drawForeground() {
        if(super.hasSubScreen()) return;
    }

    @Override
    public boolean itemSearched(ItemIdentifier item) {
        if(Objects.equals(searchinput1, "") && Objects.equals(searchinput2, "")) return true;
        if(isSearched(item.getFriendlyName().toLowerCase(),(searchinput1 + searchinput2).toLowerCase())) return true;
        if(isSearched(String.valueOf(item.item.id),(searchinput1 + searchinput2))) return true;
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

    private int getSearchedItemNumber() {
        int count = 0;
        for(ItemIdentifierStack item : allItems) {
            if(itemSearched(item.getItem())) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        clickWasButton = false;
        editsearchb = true;
        super.mouseClicked(mouseX, mouseY, button);
        if((!clickWasButton && mouseX >= guiLeft + 10 && mouseX < right - 10 && mouseY >= guiTop + 18 && mouseY < bottom - 63) || editsearch) {
            if(!editsearchb) {
                editsearch = false;
            }
            selectedItem = null;
            lastClickedx = mouseX;
            lastClickedy = mouseY;
            lastClickedk = button;
        }
    }

    @Override
    public void onMouseEventSub() {
        boolean isShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        boolean isControl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        int wheel = Mouse.getEventDWheel() / 120;
        if (wheel == 0){
            super.onMouseEventSub();
            return;
        }
        if (isShift && !isControl && isShiftPageChange()){
            if (wheel > 0){
                if (!Config.HUD_CONFIG.invertWheel){
                    prevPage();
                } else {
                    nextPage();
                }
            } else {
                if (!Config.HUD_CONFIG.invertWheel){
                    nextPage();
                } else {
                    prevPage();
                }
            }
        } else if (isShift && !isControl && !isShiftPageChange()){
            if (wheel > 0){
                if (!Config.HUD_CONFIG.invertWheel) {
                    requestCount = Math.max(1, requestCount - (wheel * getAmountChangeMode(4)));
                } else {
                    if(requestCount == 1) requestCount-=1;
                    requestCount+= wheel * getAmountChangeMode(4);
                }
            } else {
                if (!Config.HUD_CONFIG.invertWheel) {
                    if(requestCount == 1) requestCount-=1;
                    requestCount+= -(wheel * getAmountChangeMode(4));
                } else {
                    requestCount = Math.max(1, requestCount + wheel * getAmountChangeMode(4));
                }
            }
        } else if(!isControl) {
            if (wheel > 0){
                if (!Config.HUD_CONFIG.invertWheel) {
                    requestCount = Math.max(1, requestCount - (wheel * getAmountChangeMode(1)));
                } else {
                    requestCount+= wheel * getAmountChangeMode(1);
                }
            } else {
                if (!Config.HUD_CONFIG.invertWheel) {
                    requestCount+= -(wheel * getAmountChangeMode(1));
                } else {
                    requestCount = Math.max(1, requestCount + wheel * getAmountChangeMode(1));
                }
            }
        } else if(isControl && !isShift) {
            if (wheel > 0){
                if (!Config.HUD_CONFIG.invertWheel) {
                    requestCount = Math.max(1, requestCount - wheel * getAmountChangeMode(2));
                } else {
                    if(requestCount == 1) requestCount-=1;
                    requestCount+= wheel * getAmountChangeMode(2);
                }
            } else {
                if (!Config.HUD_CONFIG.invertWheel) {
                    if(requestCount == 1) requestCount-=1;
                    requestCount+= -wheel * getAmountChangeMode(2);
                } else {
                    requestCount = Math.max(1, requestCount + wheel * getAmountChangeMode(2));
                }
            }
        } else if(isControl && isShift) {
            if (wheel > 0){
                if (!Config.HUD_CONFIG.invertWheel) {
                    requestCount = Math.max(1, requestCount - wheel * getAmountChangeMode(3));
                } else {
                    if(requestCount == 1) requestCount-=1;
                    requestCount+= wheel * getAmountChangeMode(3);
                }
            } else {
                if (!Config.HUD_CONFIG.invertWheel) {
                    if(requestCount == 1) requestCount-=1;
                    requestCount+= -wheel * getAmountChangeMode(3);
                } else {
                    requestCount = Math.max(1, requestCount + wheel * getAmountChangeMode(3));
                }
            }
        }
        super.onMouseEventSub();
    }

    public void handleRequestAnswer(ItemMessage itemMessage, boolean error, SubScreenController control, PlayerEntity player) {
        List<ItemMessage> list = new ArrayList<>();
        list.add(itemMessage);
        handleRequestAnswer(list, error, control, player);
    }

    public void handleRequestAnswer(List<ItemMessage> items, boolean error, SubScreenController control, PlayerEntity player) {
        if (!error){
            ArrayList<String> msg = new ArrayList<>();
            msg.add("You are missing:");
            for (ItemMessage item : items){
                if(!Config.HUD_CONFIG.displayPopup) {
                    player.sendMessage("Missing: " + item.toString());
                } else {
                    msg.add(item.toString());
                }
            }
            if(Config.HUD_CONFIG.displayPopup) {
                control.setSubScreen(new RequestPopupSubScreen(player, msg.toArray()));
            }
        } else {
            if(Config.HUD_CONFIG.displayPopup) {
                if(control.hasSubScreen()) {
                    SubScreenController newcontroller = control;
                    while(newcontroller.hasSubScreen()) {
                        newcontroller = newcontroller.getSubScreen();
                    }
                    newcontroller.setSubScreen(new RequestPopupSubScreen(player, "Request successful!",items.toArray()));
                } else {
                    control.setSubScreen(new RequestPopupSubScreen(player, "Request successful!",items.toArray()));
                }
            } else {
                for(ItemMessage item:items) {
                    player.sendMessage("Requested: " + item);
                }
                player.sendMessage("Request successful!");
            }
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(editsearch) {
            editsearchb = false;
        }
        clickWasButton = true;

        if (button.id == 0 && selectedItem != null){
            LogisticPipeBlockEntity requestPipe = (LogisticPipeBlockEntity)itemRequester;
            PacketHelper.send(new SubmitRequestC2SPacket(requestPipe.x,requestPipe.y,requestPipe.z,selectedItem.getItem(),requestCount));
            refreshItems();
        } else if (button.id == 1){
            nextPage();
        } else if (button.id == 2) {
            prevPage();
        } else if (button.id == 3) {
            refreshItems();
        } else if (button.id == 10) {
            requestCount = Math.max(1, requestCount - 64);
        } else if (button.id == 4) {
            requestCount = Math.max(1, requestCount - 10);
        } else if (button.id == 5) {
            requestCount = Math.max(1, requestCount - 1);
        } else if (button.id == 6) {
            requestCount+=1;
        } else if (button.id == 7) {
            if(requestCount == 1) {
                requestCount-=1;
            }
            requestCount+=10;
        } else if (button.id == 11) {
            if(requestCount == 1) {
                requestCount-=1;
            }
            requestCount+=64;
        } else if (button.id == 8) {
            CheckBoxWidget checkbox = (CheckBoxWidget)buttons.get(10);
            GlassYamlFile modConfigFile = new GlassYamlFile();
            modConfigFile.set ("speed", checkbox.change());
            GCAPI.reloadConfig(Identifier.of("logisticspipes:hud").toString(), modConfigFile);
        }
        super.buttonClicked(button);
    }

    private void nextPage(){
        if (page < maxPage){
            page++;
        } else {
            page = 0;
        }
    }

    private void prevPage(){
        if (page > 0){
            page--;
        } else {
            page = maxPage;
        }
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if(editsearch) {
            if (character == 13) {
                editsearch = false;
                return;
            } else if (keyCode == 47 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                searchinput1 = searchinput1 + getClipboard();
            } else if (character == 8) {
                if (!searchinput1.isEmpty())
                    searchinput1 = searchinput1.substring(0, searchinput1.length() - 1);
                return;
            } else if (Character.isLetterOrDigit(character) || character == ' ') {
                if (textRenderer.getWidth(searchinput1 + character + searchinput2) <= searchWidth) {
                    searchinput1 += character;
                }
                return;
            } else if(keyCode == 203) { //Left
                if(!searchinput1.isEmpty()) {
                    searchinput2 = searchinput1.substring(searchinput1.length() - 1) + searchinput2;
                    searchinput1 = searchinput1.substring(0, searchinput1.length() - 1);
                }
            } else if(keyCode == 205) { //Right
                if(!searchinput2.isEmpty()) {
                    searchinput1 += searchinput2.substring(0,1);
                    searchinput2 = searchinput2.substring(1);
                }
            } else if(keyCode == 1) { //ESC
                editsearch = false;
            } else if(keyCode == 28) { //Enter
                editsearch = false;
            } else if(keyCode == 199) { //Pos
                searchinput2 = searchinput1 + searchinput2;
                searchinput1 = "";
            } else if(keyCode == 207) { //Ende
                searchinput1 = searchinput1 + searchinput2;
                searchinput2 = "";
            } else if(keyCode == 211) { //Entf
                if (!searchinput2.isEmpty())
                    searchinput2 = searchinput2.substring(1);
            }
        } else {
            super.keyPressed(character, keyCode);
        }
    }

    @Override
    public void resetSubScreen() {
        super.resetSubScreen();
        refreshItems();
    }

    protected boolean isShiftPageChange() {
        return true;
    }

    protected int getAmountChangeMode(int step) {
        if(step == 1) {
            return 1;
        } else if(step == 2) {
            return 10;
        } else {
            return 64;
        }
    }

    protected int getStackAmount() {
        return 64;
    }
}
