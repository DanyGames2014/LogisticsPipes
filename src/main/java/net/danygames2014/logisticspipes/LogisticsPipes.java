package net.danygames2014.logisticspipes;

import net.danygames2014.logisticspipes.block.GenericPipeBlock;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;

public class LogisticsPipes {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER;

    public static Block pipeBlock;

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        pipeBlock = new GenericPipeBlock(NAMESPACE.id("pipe_block")).setTranslationKey(NAMESPACE, "pipe_block");
    }
}
