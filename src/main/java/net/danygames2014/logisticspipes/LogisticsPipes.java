package net.danygames2014.logisticspipes;

import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.logisticspipes.block.LogisticPipeBlock;
import net.danygames2014.logisticspipes.block.RequestMk2LogisticPipeBlock;
import net.danygames2014.logisticspipes.block.RequestTableLogisticPipeBlock;
import net.danygames2014.logisticspipes.block.entity.*;
import net.danygames2014.logisticspipes.block.pipe.behavior.LogisticPipeBehavior;
import net.danygames2014.logisticspipes.block.pipe.transporter.LogisticItemPipeTransporter;
import net.danygames2014.logisticspipes.item.*;
import net.danygames2014.logisticspipes.module.*;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;

public class LogisticsPipes {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER;

    public static Material pipeMaterial;

    public static LogisticPipeBehavior logisticPipeBehavior;

    public static Block basicItemPipe;
    public static Block requestItemPipe;
    public static Block requestItemPipeMk2;
    public static Block remoteOrdererItemPipe;
    public static Block providerItemPipe;
    public static Block providerItemPipeMk2;
    public static Block supplierItemPipe;
    public static Block craftingItemPipe;
    public static Block craftingItemPipeMk2;
    public static Block satelliteItemPipe;

    public static Block chassisItemPipeMk1;
    public static Block chassisItemPipeMk2;
    public static Block chassisItemPipeMk3;
    public static Block chassisItemPipeMk4;
    public static Block chassisItemPipeMk5;

    public static Block requestTable;

    public static Item magicWand;
    public static Item routingDebugger;

    public static Item blankModule;
    public static Item providerModule;
    public static Item passiveSupplierModule;
    public static Item itemSinkModule;
    public static Item modItemSinkModule;
    public static Item tagItemSinkModule;
    public static Item polymorphicItemSinkModule;
    public static Item modPolymorphicItemSinkModule;
    public static Item tagPolymorphicItemSinkModule;
    public static Item extractorModule;
    public static Item extractorModuleMk2;
    public static Item extractorModuleMk3;
    public static Item advancedExtractorModule;
    public static Item advancedExtractorModuleMk2;
    public static Item advancedExtractorModuleMk3;
    public static Item terminusModule;
    public static Item quickSortModule;

    public static Item disk;
    public static Item hudBow;
    public static Item hudGlass;
    public static Item hudNoseBridge;
    public static Item hudGlasses;

    public static Item remoteOrderer;
    public static Item whiteRemoteOrderer;
    public static Item orangeRemoteOrderer;
    public static Item magentaRemoteOrderer;
    public static Item lightBlueRemoteOrderer;
    public static Item yellowRemoteOrderer;
    public static Item limeRemoteOrderer;
    public static Item pinkRemoteOrderer;
    public static Item grayRemoteOrderer;
    public static Item lightGrayRemoteOrderer;
    public static Item cyanRemoteOrderer;
    public static Item purpleRemoteOrderer;
    public static Item blueRemoteOrderer;
    public static Item brownRemoteOrderer;
    public static Item greenRemoteOrderer;
    public static Item redRemoteOrderer;
    public static Item blackRemoteOrderer;

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        pipeMaterial = new PipeMaterial(MapColor.LIGHT_GRAY);

        logisticPipeBehavior = new LogisticPipeBehavior();

        basicItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("basic_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/basic_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                BasicLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "basic_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        requestItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("request_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/request_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                RequestLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "request_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        requestItemPipeMk2 = new RequestMk2LogisticPipeBlock(
                NAMESPACE.id("request_item_pipe_mk2"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/request_item_pipe_mk2"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                RequestLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "request_item_pipe_mk2").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        remoteOrdererItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("remote_orderer_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/remote_orderer_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                RemoteOrdererLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "remote_orderer_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        providerItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("provider_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/provider_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                ProviderLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "provider_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        providerItemPipeMk2 = new LogisticPipeBlock(
                NAMESPACE.id("provider_item_pipe_mk2"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/provider_item_pipe_mk2"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                ProviderLogisticPipeBlockEntityMk2::new
        ).setTranslationKey(NAMESPACE, "provider_item_pipe_mk2").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        supplierItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("supplier_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/supplier_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                SupplierLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "supplier_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        craftingItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("crafting_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/crafting_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                CraftingLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "crafting_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        craftingItemPipeMk2 = new LogisticPipeBlock(
                NAMESPACE.id("crafting_item_pipe_mk2"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/crafting_item_pipe_mk2"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                CraftingLogisticPipeBlockEntityMk2::new
        ).setTranslationKey(NAMESPACE, "crafting_item_pipe_mk2").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        satelliteItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("satellite_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/satellite_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                SatelliteLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "satellite_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        chassisItemPipeMk1 = new LogisticPipeBlock(
                NAMESPACE.id("chassis_item_pipe_mk1"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/chassis_item_pipe_mk1"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                ChassisLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "chassis_item_pipe_mk1").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        chassisItemPipeMk2 = new LogisticPipeBlock(
                NAMESPACE.id("chassis_item_pipe_mk2"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/chassis_item_pipe_mk2"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                ChassisLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "chassis_item_pipe_mk2").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        chassisItemPipeMk3 = new LogisticPipeBlock(
                NAMESPACE.id("chassis_item_pipe_mk3"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/chassis_item_pipe_mk3"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                ChassisLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "chassis_item_pipe_mk3").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        chassisItemPipeMk4 = new LogisticPipeBlock(
                NAMESPACE.id("chassis_item_pipe_mk4"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/chassis_item_pipe_mk4"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                ChassisLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "chassis_item_pipe_mk4").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        chassisItemPipeMk5 = new LogisticPipeBlock(
                NAMESPACE.id("chassis_item_pipe_mk5"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/chassis_item_pipe_mk5"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                ChassisLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "chassis_item_pipe_mk5").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        requestTable = new RequestTableLogisticPipeBlock(
                NAMESPACE.id("request_table"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/chassis_item_pipe_mk5"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                RequestTableLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "request_table").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);
    }

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            magicWand = new MagicWand(NAMESPACE.id("magic_wand")).setTranslationKey(NAMESPACE, "magic_wand");
        }
        routingDebugger = new RoutingDebugger(NAMESPACE.id("routing_debugger")).setTranslationKey(NAMESPACE, "routing_debugger");

        blankModule = new TemplateItem(NAMESPACE.id("blank_module")).setTranslationKey(NAMESPACE, "blank_module");
        providerModule = new ModuleItem(NAMESPACE.id("provider_module"), ProviderModule::new).setTranslationKey(NAMESPACE, "provider_module");
        passiveSupplierModule = new ModuleItem(NAMESPACE.id("passive_supplier_module"), PassiveSupplierModule::new).setTranslationKey(NAMESPACE, "passive_supplier_module");
        itemSinkModule = new ModuleItem(NAMESPACE.id("item_sink_module"), ItemSinkModule::new).setTranslationKey(NAMESPACE, "item_sink_module");
        modItemSinkModule = new ModuleItem(NAMESPACE.id("mod_item_sink_module"), ModItemSinkModule::new).setTranslationKey(NAMESPACE, "mod_item_sink_module");
        tagItemSinkModule = new ModuleItem(NAMESPACE.id("tag_item_sink_module"), TagItemSinkModule::new).setTranslationKey(NAMESPACE, "tag_item_sink_module");
        polymorphicItemSinkModule = new ModuleItem(NAMESPACE.id("polymorphic_item_sink_module"), PolymorphicItemSinkModule::new).setTranslationKey(NAMESPACE, "polymorphic_item_sink_module");
        modPolymorphicItemSinkModule = new ModuleItem(NAMESPACE.id("mod_polymorphic_item_sink_module"), ModPolymorphicItemSinkModule::new).setTranslationKey(NAMESPACE, "mod_polymorphic_item_sink_module");
        tagPolymorphicItemSinkModule = new ModuleItem(NAMESPACE.id("tag_polymorphic_item_sink_module"), TagPolymorphicItemSinkModule::new).setTranslationKey(NAMESPACE, "tag_polymorphic_item_sink_module");
        extractorModule = new ModuleItem(NAMESPACE.id("extractor_module"), ExtractorModule::new).setTranslationKey(NAMESPACE, "extractor_module");
        extractorModuleMk2 = new ModuleItem(NAMESPACE.id("extractor_module_mk2"), ExtractorModuleMk2::new).setTranslationKey(NAMESPACE, "extractor_module_mk2");
        extractorModuleMk3 = new ModuleItem(NAMESPACE.id("extractor_module_mk3"), ExtractorModuleMk3::new).setTranslationKey(NAMESPACE, "extractor_module_mk3");
        advancedExtractorModule = new ModuleItem(NAMESPACE.id("advanced_extractor_module"), AdvancedExtractorModule::new).setTranslationKey(NAMESPACE, "advanced_extractor_module");
        advancedExtractorModuleMk2 = new ModuleItem(NAMESPACE.id("advanced_extractor_module_mk2"), AdvancedExtractorModuleMk2::new).setTranslationKey(NAMESPACE, "advanced_extractor_module_mk2");
        advancedExtractorModuleMk3 = new ModuleItem(NAMESPACE.id("advanced_extractor_module_mk3"), AdvancedExtractorModuleMk3::new).setTranslationKey(NAMESPACE, "advanced_extractor_module_mk3");
        terminusModule = new ModuleItem(NAMESPACE.id("terminus_module"), TerminusModule::new).setTranslationKey(NAMESPACE, "terminus_module");
        quickSortModule = new ModuleItem(NAMESPACE.id("quick_sort_module"), QuickSortModule::new).setTranslationKey(NAMESPACE, "quick_sort_module");

        disk = new DiskItem(NAMESPACE.id("disk")).setTranslationKey(NAMESPACE, "disk");
        hudBow = new TemplateItem(NAMESPACE.id("hud_bow")).setTranslationKey(NAMESPACE, "hud_bow");
        hudGlass = new TemplateItem(NAMESPACE.id("hud_glass")).setTranslationKey(NAMESPACE, "hud_glass");
        hudNoseBridge = new TemplateItem(NAMESPACE.id("hud_nose_bridge")).setTranslationKey(NAMESPACE, "hud_nose_bridge");
        hudGlasses = new HudGlassesItem(NAMESPACE.id("hud_glasses")).setTranslationKey(NAMESPACE, "hud_glasses");

        remoteOrderer = new RemoteOrdererItem(NAMESPACE.id("remote_orderer")).setTranslationKey(NAMESPACE, "remote_orderer");
        whiteRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("white_remote_orderer")).setTranslationKey(NAMESPACE, "white_remote_orderer");
        orangeRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("orange_remote_orderer")).setTranslationKey(NAMESPACE, "orange_remote_orderer");
        magentaRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("magenta_remote_orderer")).setTranslationKey(NAMESPACE, "magenta_remote_orderer");
        lightBlueRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("light_blue_remote_orderer")).setTranslationKey(NAMESPACE, "light_blue_remote_orderer");
        yellowRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("yellow_remote_orderer")).setTranslationKey(NAMESPACE, "yellow_remote_orderer");
        limeRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("lime_remote_orderer")).setTranslationKey(NAMESPACE, "lime_remote_orderer");
        pinkRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("pink_remote_orderer")).setTranslationKey(NAMESPACE, "pink_remote_orderer");
        grayRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("gray_remote_orderer")).setTranslationKey(NAMESPACE, "gray_remote_orderer");
        lightGrayRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("light_gray_remote_orderer")).setTranslationKey(NAMESPACE, "light_gray_remote_orderer");
        cyanRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("cyan_remote_orderer")).setTranslationKey(NAMESPACE, "cyan_remote_orderer");
        purpleRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("purple_remote_orderer")).setTranslationKey(NAMESPACE, "purple_remote_orderer");
        blueRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("blue_remote_orderer")).setTranslationKey(NAMESPACE, "blue_remote_orderer");
        brownRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("brown_remote_orderer")).setTranslationKey(NAMESPACE, "brown_remote_orderer");
        greenRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("green_remote_orderer")).setTranslationKey(NAMESPACE, "green_remote_orderer");
        redRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("red_remote_orderer")).setTranslationKey(NAMESPACE, "red_remote_orderer");
        blackRemoteOrderer = new RemoteOrdererItem(NAMESPACE.id("black_remote_orderer")).setTranslationKey(NAMESPACE, "black_remote_orderer");
    }
}
