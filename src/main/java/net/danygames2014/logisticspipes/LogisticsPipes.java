package net.danygames2014.logisticspipes;

import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.logisticspipes.block.LogisticPipeBlock;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.behavior.LogisticPipeBehavior;
import net.danygames2014.logisticspipes.block.pipe.transporter.LogisticItemPipeTransporter;
import net.danygames2014.logisticspipes.item.MagicWand;
import net.danygames2014.logisticspipes.item.RoutingDebugger;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
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

    public static Item magicWand;
    public static Item routingDebugger;

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
                LogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "basic_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        requestItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("request_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/request_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                LogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "request_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        requestItemPipeMk2 = new LogisticPipeBlock(
                NAMESPACE.id("request_item_pipe_mk2"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/request_item_pipe_mk2"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                LogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "request_item_pipe_mk2").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        providerItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("provider_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/provider_item_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                LogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "provider_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);
    }

    @EventListener
    public void registerItems(ItemRegistryEvent event){
        if(FabricLoader.getInstance().isDevelopmentEnvironment()){
            magicWand = new MagicWand(NAMESPACE.id("magic_wand")).setTranslationKey(NAMESPACE, "magic_wand");
        }
        routingDebugger = new RoutingDebugger(NAMESPACE.id("routing_debugger")).setTranslationKey(NAMESPACE, "routing_debugger");
    }
}
