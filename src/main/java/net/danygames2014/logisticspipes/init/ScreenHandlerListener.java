package net.danygames2014.logisticspipes.init;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.SupplierLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.client.gui.screen.ChassisScreen;
import net.danygames2014.logisticspipes.client.gui.screen.PassiveSupplierScreen;
import net.danygames2014.logisticspipes.client.gui.screen.ProviderScreen;
import net.danygames2014.logisticspipes.client.gui.screen.SupplierScreen;
import net.danygames2014.logisticspipes.module.PassiveSupplierModule;
import net.danygames2014.logisticspipes.module.ProviderModule;
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

    private Screen openChassisScreen(PlayerEntity player, Inventory inventory) {
        return new ChassisScreen(player, (ChassisLogisticPipeBlockEntity) inventory);
    }

    private Screen openSupplierScreen(PlayerEntity player, Inventory inventory) {
        return new SupplierScreen(player.inventory, ((SupplierLogisticPipeBlockEntity) inventory).getDummyInventory(), (SupplierLogisticPipeBlockEntity) inventory);
    }
}
