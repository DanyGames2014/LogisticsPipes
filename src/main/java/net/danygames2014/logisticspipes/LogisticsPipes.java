package net.danygames2014.logisticspipes;

import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.logisticspipes.block.LogisticPipeBlock;
import net.danygames2014.logisticspipes.block.entity.*;
import net.danygames2014.logisticspipes.block.pipe.behavior.LogisticPipeBehavior;
import net.danygames2014.logisticspipes.block.pipe.transporter.LogisticItemPipeTransporter;
import net.danygames2014.logisticspipes.item.MagicWand;
import net.danygames2014.logisticspipes.item.ModuleItem;
import net.danygames2014.logisticspipes.item.RoutingDebugger;
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
    public static Block providerItemPipe;
    public static Block supplierItemPipe;
    public static Block craftingItemPipe;
    public static Block satelliteItemPipe;

    public static Block chassisItemPipeMk1;
    public static Block chassisItemPipeMk2;
    public static Block chassisItemPipeMk3;
    public static Block chassisItemPipeMk4;
    public static Block chassisItemPipeMk5;

    public static Item magicWand;
    public static Item routingDebugger;

    public static Item blankModule;
    public static Item providerModule;
    public static Item passiveSupplierModule;
    public static Item itemSinkModule;
    public static Item polymorphicItemSinkModule;
    public static Item extractorModule;
    public static Item extractorModuleMk2;
    public static Item extractorModuleMk3;
    public static Item advancedExtractorModule;
    public static Item advancedExtractorModuleMk2;
    public static Item advancedExtractorModuleMk3;
    public static Item terminusModule;
    public static Item quickSortModule;

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
                BasicLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "request_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        requestItemPipeMk2 = new LogisticPipeBlock(
                NAMESPACE.id("request_item_pipe_mk2"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/request_item_pipe_mk2"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                BasicLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "request_item_pipe_mk2").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        providerItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("provider_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/provider_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                BasicLogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "provider_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

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
        polymorphicItemSinkModule = new ModuleItem(NAMESPACE.id("polymorphic_item_sink_module"), PolymorphicItemSinkModule::new).setTranslationKey(NAMESPACE, "polymorphic_item_sink_module");
        extractorModule = new ModuleItem(NAMESPACE.id("extractor_module"), ExtractorModule::new).setTranslationKey(NAMESPACE, "extractor_module");
        extractorModuleMk2 = new ModuleItem(NAMESPACE.id("extractor_module_mk2"), ExtractorModuleMk2::new).setTranslationKey(NAMESPACE, "extractor_module_mk2");
        extractorModuleMk3 = new ModuleItem(NAMESPACE.id("extractor_module_mk3"), ExtractorModuleMk3::new).setTranslationKey(NAMESPACE, "extractor_module_mk3");
        advancedExtractorModule = new ModuleItem(NAMESPACE.id("advanced_extractor_module"), AdvancedExtractorModule::new).setTranslationKey(NAMESPACE, "advanced_extractor_module");
        advancedExtractorModuleMk2 = new ModuleItem(NAMESPACE.id("advanced_extractor_module_mk2"), AdvancedExtractorModuleMk2::new).setTranslationKey(NAMESPACE, "advanced_extractor_module_mk2");
        advancedExtractorModuleMk3 = new ModuleItem(NAMESPACE.id("advanced_extractor_module_mk3"), AdvancedExtractorModuleMk3::new).setTranslationKey(NAMESPACE, "advanced_extractor_module_mk3");
        terminusModule = new ModuleItem(NAMESPACE.id("terminus_module"), TerminusModule::new).setTranslationKey(NAMESPACE, "terminus_module");
        quickSortModule = new ModuleItem(NAMESPACE.id("quick_sort_module"), QuickSortModule::new).setTranslationKey(NAMESPACE, "quick_sort_module");

    }
}
