package net.danygames2014.logisticspipes.block;

import net.danygames2014.logisticspipes.block.entity.GenericPipeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class GenericPipeBlock extends TemplateBlockWithEntity {
    public GenericPipeBlock(Identifier identifier) {
        super(identifier, Material.GLASS);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new GenericPipeBlockEntity();
    }
}
