package net.danygames2014.logisticspipes.init;

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
import net.modificationstation.stationapi.api.block.BlockState;
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
        event.register(NAMESPACE.id("chassis"), new GuiHandler(this::openChassisScreen, ChassisLogisticPipeBlockEntity::new));
        event.register(NAMESPACE.id("supplier"), new GuiHandler(this::openSupplierScreen, SupplierLogisticPipeBlockEntity::new));
        event.register(NAMESPACE.id("provider"), new GuiHandler(this::openProviderScreen, ProviderModule::new));
        event.register(NAMESPACE.id("passive_supplier"), new GuiHandler(this::openPassiveSupplierScreen, PassiveSupplierModule::new));
        event.register(NAMESPACE.id("item_sink"), new GuiHandler(this::openItemSinkScreen, ItemSinkModule::new));
        event.register(NAMESPACE.id("extractor"), new GuiHandler(this::openExtractorScreen, ExtractorModule::new));
        event.register(NAMESPACE.id("advanced_extractor"), new GuiHandler(this::openAdvancedExtractorScreen, AdvancedExtractorModule::new));
        event.register(NAMESPACE.id("terminus"), new GuiHandler(this::openTerminusScreen, TerminusModule::new));
    }

    private Screen openChassisScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockState state = player.world.getBlockState(message.ints[1], message.ints[2], message.ints[3]);
        if (inventory instanceof ChassisLogisticPipeBlockEntity pipe) {
            if (player.world.isRemote) {
                pipe.initModuleInventory(state);
            }
            return new ChassisScreen(player, pipe);
        }

        return null;
    }

    private Screen openSupplierScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if (blockEntity instanceof SupplierLogisticPipeBlockEntity pipe) {
            return new SupplierScreen(player, pipe);
        }
        
        return null;
    }
    
    // Modules
    private Screen openProviderScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if (blockEntity instanceof ChassisLogisticPipeBlockEntity pipe) {
            if (player.world.isRemote) {
                if (inventory instanceof ProviderModule provider) {
                    provider.registerPosition(message.ints[1], message.ints[2], message.ints[3], message.ints[4]);
                    return new ProviderScreen(player, pipe, provider, Minecraft.INSTANCE.currentScreen, message.ints[4]);
                }
            }
            
            return new ProviderScreen(player, pipe, (ProviderModule) pipe.getLogisticsModule().getSubModule(message.ints[4]), Minecraft.INSTANCE.currentScreen, message.ints[4]);
        }
        
        return null;
    }

    private Screen openPassiveSupplierScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if (blockEntity instanceof ChassisLogisticPipeBlockEntity pipe) {
            if (player.world.isRemote) {
                if (inventory instanceof PassiveSupplierModule passiveSupplier) {
                    passiveSupplier.registerPosition(message.ints[1], message.ints[2], message.ints[3], message.ints[4]);
                    return new PassiveSupplierScreen(player, pipe, passiveSupplier, Minecraft.INSTANCE.currentScreen);
                }
            } else {
                return new PassiveSupplierScreen(player, pipe, (PassiveSupplierModule) pipe.getLogisticsModule().getSubModule(message.ints[4]), Minecraft.INSTANCE.currentScreen);
            }
        }
        
        return null;
    }

    private Screen openItemSinkScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if (blockEntity instanceof ChassisLogisticPipeBlockEntity pipe) {
            if (player.world.isRemote) {
                if (inventory instanceof ItemSinkModule itemSink) {
                    itemSink.registerPosition(message.ints[1], message.ints[2], message.ints[3], message.ints[4]);
                    return new ItemSinkScreen(player, pipe, itemSink, Minecraft.INSTANCE.currentScreen, message.ints[4]);
                }
            } else {
                return new ItemSinkScreen(player, pipe, (ItemSinkModule) pipe.getLogisticsModule().getSubModule(message.ints[4]), Minecraft.INSTANCE.currentScreen, message.ints[4]);
            }
        }

        if (blockEntity instanceof BasicLogisticPipeBlockEntity pipe) {
            return new ItemSinkScreen(player, pipe, (ItemSinkModule) pipe.getLogisticsModule(), null, -1);
        }
        
        return null;
    }

    private Screen openExtractorScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if (blockEntity instanceof ChassisLogisticPipeBlockEntity pipe) {
            if (player.world.isRemote) {
                if (inventory instanceof ExtractorModule extractorModule) {
                    extractorModule.registerPosition(message.ints[1], message.ints[2], message.ints[3], message.ints[4]);
                    return new ExtractorScreen(player, pipe, extractorModule, Minecraft.INSTANCE.currentScreen, message.ints[4]);
                }
            } else {
                return new ExtractorScreen(player, pipe, (SneakyDirectionReceiver) pipe.getLogisticsModule().getSubModule(message.ints[4]), Minecraft.INSTANCE.currentScreen, message.ints[4]);
            }
        }
        
        return null;
    }

    private Screen openAdvancedExtractorScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if (blockEntity instanceof ChassisLogisticPipeBlockEntity pipe) {
            if (player.world.isRemote) {
                if (inventory instanceof AdvancedExtractorModule advancedExtractor) {
                    advancedExtractor.registerPosition(message.ints[1], message.ints[2], message.ints[3], message.ints[4]);
                    return new AdvancedExtractorScreen(player, pipe, advancedExtractor, Minecraft.INSTANCE.currentScreen, message.ints[4]);
                }
            } else {
                return new AdvancedExtractorScreen(player, pipe, (AdvancedExtractorModule) pipe.getLogisticsModule().getSubModule(message.ints[4]), Minecraft.INSTANCE.currentScreen, message.ints[4]);
            }
        }
        
        return null;
    }

    private Screen openTerminusScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if (blockEntity instanceof ChassisLogisticPipeBlockEntity pipe) {
            if (player.world.isRemote) {
                if (inventory instanceof TerminusModule terminus) {
                    terminus.registerPosition(message.ints[1], message.ints[2], message.ints[3], message.ints[4]);
                    return new TerminusScreen(player, pipe, terminus, Minecraft.INSTANCE.currentScreen);
                }
            } else {
                return new TerminusScreen(player, pipe, (TerminusModule) pipe.getLogisticsModule().getSubModule(message.ints[4]), Minecraft.INSTANCE.currentScreen);
            }
        }
        
        return null;
    }
}
