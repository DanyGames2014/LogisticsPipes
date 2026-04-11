package net.danygames2014.logisticspipes.init;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.logisticspipes.block.entity.BasicLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.SupplierLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.client.gui.screen.*;
import net.danygames2014.logisticspipes.interfaces.SneakyDirectionReceiver;
import net.danygames2014.logisticspipes.module.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.util.Namespace;

public class ScreenHandlerListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerScreenHandlers(GuiHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("chassis"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openChassisScreen, ChassisLogisticPipeBlockEntity::new));
        event.register(NAMESPACE.id("supplier"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openSupplierScreen, SupplierLogisticPipeBlockEntity::new));
        event.register(NAMESPACE.id("provider"), new GuiHandler(this::openProviderScreen, () -> null));
        event.register(NAMESPACE.id("passive_supplier"), new GuiHandler(this::openPassiveSupplierScreen, () -> null));
        event.register(NAMESPACE.id("item_sink"), new GuiHandler(this::openItemSinkScreen, () -> null));
        event.register(NAMESPACE.id("extractor"), new GuiHandler(this::openExtractorScreen, () -> null));
        event.register(NAMESPACE.id("advanced_extractor"), new GuiHandler(this::openAdvancedExtractorScreen, () -> null));
        event.register(NAMESPACE.id("terminus"), new GuiHandler(this::openTerminusScreen, () -> null));
    }

    private Screen openProviderScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[0], message.ints[1], message.ints[2]);
        if(blockEntity instanceof ChassisLogisticPipeBlockEntity pipe){
            return new ProviderScreen(player.inventory, pipe, (ProviderModule) pipe.getLogisticsModule().getSubModule(message.ints[3]), Minecraft.INSTANCE.currentScreen, message.ints[3]);
        }
        return null;
    }

    private Screen openPassiveSupplierScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[0], message.ints[1], message.ints[2]);
        if(blockEntity instanceof ChassisLogisticPipeBlockEntity pipe){
            return new PassiveSupplierScreen(player.inventory, pipe, (PassiveSupplierModule) pipe.getLogisticsModule().getSubModule(message.ints[3]), Minecraft.INSTANCE.currentScreen);
        }
        return null;
    }

    private Screen openItemSinkScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[0], message.ints[1], message.ints[2]);
        if(blockEntity instanceof ChassisLogisticPipeBlockEntity pipe){
            return new ItemSinkScreen(player.inventory, pipe, (ItemSinkModule) pipe.getLogisticsModule().getSubModule(message.ints[3]), Minecraft.INSTANCE.currentScreen, message.ints[3]);
        }
        if(blockEntity instanceof BasicLogisticPipeBlockEntity pipe){
            return  new ItemSinkScreen(player.inventory, pipe, (ItemSinkModule) pipe.getLogisticsModule(), null, -1);
        }
        return null;
    }

    private Screen openExtractorScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[0], message.ints[1], message.ints[2]);
        if(blockEntity instanceof ChassisLogisticPipeBlockEntity pipe){
            return new ExtractorScreen(player.inventory, pipe, (SneakyDirectionReceiver) pipe.getLogisticsModule().getSubModule(message.ints[3]), Minecraft.INSTANCE.currentScreen, message.ints[3]);
        }
        return null;
    }

    private Screen openAdvancedExtractorScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[0], message.ints[1], message.ints[2]);
        if(blockEntity instanceof ChassisLogisticPipeBlockEntity pipe){
            return new AdvancedExtractorScreen(player.inventory, pipe, (AdvancedExtractorModule) pipe.getLogisticsModule().getSubModule(message.ints[3]), Minecraft.INSTANCE.currentScreen, message.ints[3]);
        }
        return null;
    }

    private Screen openTerminusScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[0], message.ints[1], message.ints[2]);
        if(blockEntity instanceof ChassisLogisticPipeBlockEntity pipe){
            return new TerminusScreen(player.inventory, pipe, (TerminusModule) pipe.getLogisticsModule().getSubModule(message.ints[3]), Minecraft.INSTANCE.currentScreen);
        }
        return null;
    }

    private Screen openChassisScreen(PlayerEntity player, Inventory inventory) {
        return new ChassisScreen(player, (ChassisLogisticPipeBlockEntity) inventory);
    }

    private Screen openSupplierScreen(PlayerEntity player, Inventory inventory) {
        return new SupplierScreen(player.inventory, ((SupplierLogisticPipeBlockEntity) inventory).getDummyInventory(), (SupplierLogisticPipeBlockEntity) inventory);
    }
}
