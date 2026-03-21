package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.logisticspipes.gui.hud.TestHud;
import net.danygames2014.logisticspipes.interfaces.HUDBlockRendererProvider;
import net.danygames2014.logisticspipes.interfaces.HUDRenderer;
import net.danygames2014.logisticspipes.interfaces.HUDRendererProvider;
import net.minecraft.world.World;

public class LogisticPipeBlockEntity extends PipeBlockEntity implements HUDRendererProvider {
    private final TestHud HUD = new TestHud();

    public LogisticPipeBlockEntity(){}

    public LogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public HUDRenderer getRenderer() {
        return HUD;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void startWatching() {

    }

    @Override
    public void stopWatching() {

    }
}
