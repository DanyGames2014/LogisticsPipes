package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.item.ModuleItem;
import net.danygames2014.logisticspipes.screen.DummyScreenHandler;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ChassisScreen extends LogisticsBaseScreen{
    ChassisLogisticPipeBlockEntity pipe;
    PlayerEntity player;
    Inventory moduleInventory;

    private int left;
    private int top;

    public ChassisScreen(PlayerEntity player, ChassisLogisticPipeBlockEntity pipe){
        super(null);
        this.player = player;
        this.pipe = pipe;
        this.moduleInventory = pipe.getModuleInventory();

        DummyScreenHandler dummy = new DummyScreenHandler(player.inventory, moduleInventory);
        if (pipe.getChassisSize() < 5){
            dummy.addNormalSlotsForPlayerInventory(18, 97);
        } else {
            dummy.addNormalSlotsForPlayerInventory(18, 174);
        }
        if (pipe.getChassisSize() > 0) dummy.addModuleSlot(0, moduleInventory, 19, 9, pipe);
        if (pipe.getChassisSize() > 1) dummy.addModuleSlot(1, moduleInventory, 19, 29, pipe);
        if (pipe.getChassisSize() > 2) dummy.addModuleSlot(2, moduleInventory, 19, 49, pipe);
        if (pipe.getChassisSize() > 3) dummy.addModuleSlot(3, moduleInventory, 19, 69, pipe);
        if (pipe.getChassisSize() > 4) {
            dummy.addModuleSlot(4, moduleInventory, 19, 89, pipe);
            dummy.addModuleSlot(5, moduleInventory, 19, 109, pipe);
            dummy.addModuleSlot(6, moduleInventory, 19, 129, pipe);
            dummy.addModuleSlot(7, moduleInventory, 19, 149, pipe);
        }

        this.handler = dummy;

        this.backgroundWidth = 194;
        this.backgroundHeight = 256;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();
        left = width / 2 - backgroundWidth / 2;
        top = height /2 - backgroundHeight / 2;

        buttons.clear();
        for (int i = 0; i < pipe.getChassisSize(); i++) {
            buttons.add(new SmallButtonWidget(i, left + 5, top + 12 + 20 * i, 10, 10, "!"));
            if(moduleInventory == null){
                continue;
            }
            ItemStack module = moduleInventory.getStack(i);
            ((SmallButtonWidget)buttons.get(i)).visible = module != null && pipe.getLogisticsModule().getSubModule(i) != null;
        }
    }

    @Override
    protected void drawForeground() {
        super.drawForeground();
        for(int i = 0; i < pipe.getChassisSize(); i++){
            ItemStack module = moduleInventory.getStack(i);
            ((SmallButtonWidget)buttons.get(i)).visible = module != null && pipe.getLogisticsModule().getSubModule(i) != null;
        }
        if (pipe.getChassisSize() > 0) {
            textRenderer.draw(getModuleName(0), 40, 14, 0x404040);
        }
        if (pipe.getChassisSize() > 1) {
            textRenderer.draw(getModuleName(1), 40, 34, 0x404040);
        }
        if (pipe.getChassisSize() > 2) {
            textRenderer.draw(getModuleName(2), 40, 54, 0x404040);
        }
        if (pipe.getChassisSize() > 3) {
            textRenderer.draw(getModuleName(3), 40, 74, 0x404040);
        }
        if (pipe.getChassisSize() > 4) {
            textRenderer.draw(getModuleName(4), 40, 94, 0x404040);
            textRenderer.draw(getModuleName(5), 40, 114, 0x404040);
            textRenderer.draw(getModuleName(6), 40, 134, 0x404040);
            textRenderer.draw(getModuleName(7), 40, 154, 0x404040);
        }
    }

    private String getModuleName(int slot){
        if (this.moduleInventory == null) return "";
        if (this.moduleInventory.getStack(slot) == null) return "";
        if (!(this.moduleInventory.getStack(slot).getItem() instanceof ModuleItem)) return "";
        return TranslationStorage.getInstance().get(this.moduleInventory.getStack(slot).getTranslationKey());
    }

    @Override
    protected void drawBackground(float tickDelta) {
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/chassis_size" + pipe.getChassisSize() + ".png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        drawTexture(j, k, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id >= 0 && button.id <= 7){
            LogisticsModule module = pipe.getLogisticsModule().getSubModule(button.id);
            if(module != null){
//                PacketDispatcher.sendPacketToServer(new PacketPipeInteger(NetworkConstants.CHASSI_GUI_PACKET_ID,_chassiPipe.xCoord,_chassiPipe.yCoord,_chassiPipe.zCoord,guibutton.id).getPacket());
            }
        }
    }
}
