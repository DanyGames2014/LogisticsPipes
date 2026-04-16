package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.client.particle.SparkleParticle;
import net.danygames2014.logisticspipes.util.ParticleColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

public class PipeParticleS2CPacket extends Packet implements ManagedPacket<PipeParticleS2CPacket> {
    public static final PacketType<PipeParticleS2CPacket> TYPE = PacketType.builder(true, false, PipeParticleS2CPacket::new).build();

    private static Random random = new Random();

    private int[] queuedParticles = new int[ParticleColor.values().length];
    private BlockPos pos;

    public PipeParticleS2CPacket() {
    }

    public PipeParticleS2CPacket(int[] queuedParticles, int x, int y, int z) {
        this.queuedParticles = queuedParticles;
        this.pos = new BlockPos(x, y, z);
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            for (int i = 0; i < queuedParticles.length; i++) {
                queuedParticles[i] = stream.readInt();
            }
            pos = StationBlockPos.fromLong(stream.readLong());
        } catch (IOException ignored) {

        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            for (int i = 0; i < queuedParticles.length; i++) {
                stream.writeInt(queuedParticles[i]);
            }
            stream.writeLong(pos.asLong());
        } catch (IOException ignored) {

        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> {
            applyClient(networkHandler);
        }, () -> {
        });
    }

    @Environment(EnvType.CLIENT)
    public void applyClient(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if (player != null && player.world != null) {
            for (int i = 0; i < queuedParticles.length; i++) {
                int amount = queuedParticles[i];
                ParticleColor color = ParticleColor.values()[i];
                if (amount > 0) {
                    float boundry = 0.4F;
                    int pipeWidth = 3;

                    float width = boundry + random.nextInt(pipeWidth) / 10.0F;
                    float length = boundry + random.nextInt(pipeWidth) / 10.0F;
                    float height = random.nextInt(7) / 10.0F + 0.2F;

                    float scalemult = 1f + (float) Math.log10(amount);

                    Minecraft.INSTANCE.particleManager.addParticle(new SparkleParticle(player.world, pos.getX() + length, pos.getY() + height, pos.getZ() + width, scalemult, color.getR(), color.getG(), color.getB(), 6 + random.nextInt(3)));
                }
            }
        }
    }

    @Override
    public int size() {
        return queuedParticles.length * Integer.BYTES;
    }

    @Override
    public @NotNull PacketType<PipeParticleS2CPacket> getType() {
        return TYPE;
    }
}
