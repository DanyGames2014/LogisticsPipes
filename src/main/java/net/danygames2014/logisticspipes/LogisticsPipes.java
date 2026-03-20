package net.danygames2014.logisticspipes;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.logisticspipes.block.LogisticPipeBlock;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.behavior.LogisticPipeBehavior;
import net.danygames2014.logisticspipes.block.pipe.transporter.LogisticItemPipeTransporter;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
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

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        pipeMaterial = new PipeMaterial(MapColor.LIGHT_GRAY);

        logisticPipeBehavior = new LogisticPipeBehavior();

        basicItemPipe = new LogisticPipeBlock(
                NAMESPACE.id("basic_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/basic_pipe"),
                null,
                PipeType.ITEM,
                logisticPipeBehavior,
                LogisticItemPipeTransporter::new,
                LogisticPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "basic_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);
    }
}
