package net.danygames2014.logisticspipes.block;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.logisticspipes.init.TextureListener;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class LogisticPipeBlock extends PipeBlock {
    public LogisticPipeBlock(Identifier identifier, Material material, Identifier texture, @Nullable Identifier alternativeTexture, PipeType type, PipeBehavior behavior, PipeTransporter.PipeTransporterFactory transporter, PipeBlockEntityFactory blockEntityFactory) {
        super(identifier, material, texture, alternativeTexture, type, behavior, transporter, blockEntityFactory);
    }

    @Override
    public Identifier getTextureIdentifierForSide(PipeBlockEntity pipe, @Nullable Direction direction, @Nullable PipeConnectionType connectionType) {
        if(connectionType == PipeConnectionType.NORMAL && direction != null){
            return TextureListener.notRoutedStatus;
        }
        if(connectionType == PipeConnectionType.ALTERNATE && direction != null){
            return TextureListener.routedStatus;
        }
        return super.getTextureIdentifierForSide(pipe, direction, connectionType);
    }
}
