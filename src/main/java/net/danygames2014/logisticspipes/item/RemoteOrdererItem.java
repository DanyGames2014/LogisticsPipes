package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.entity.RemoteOrdererLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.registry.DimensionRegistry;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemoteOrdererItem extends TemplateItem implements CustomTooltipProvider {
    public RemoteOrdererItem(Identifier identifier) {
        super(identifier);
        setMaxCount(1);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(world.isRemote) {
            return stack;
        }

        RemoteOrdererLogisticPipeBlockEntity pipe = getPipe(stack, world);

        if(pipe != null) {
            GuiHelper.openGUI(user, LogisticsPipes.NAMESPACE.id("remote_order"), null, new ModuleScreenHandler(user, null), (messagePacket) -> {
                messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, pipe.x, pipe.y, pipe.z, pipe.world.dimension.id};
            });
        }

        return stack;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        if(world.getBlockEntity(x, y, z) instanceof RemoteOrdererLogisticPipeBlockEntity pipe) {
            if(!world.isRemote) {
                connectToPipe(stack, pipe);
            }
            return true;
        }
        return false;
    }

    public static void connectToPipe(ItemStack stack, RemoteOrdererLogisticPipeBlockEntity pipe) {
        stack.getStationNbt().putInt("connectedPipeX", pipe.x);
        stack.getStationNbt().putInt("connectedPipeY", pipe.y);
        stack.getStationNbt().putInt("connectedPipeZ", pipe.z);

        Optional<Identifier> id = DimensionRegistry.INSTANCE.getIdByLegacyId(pipe.world.dimension.id);
        id.ifPresent(identifier -> stack.getStationNbt().putString("connectedPipeDimension", identifier.toString()));
    }

    public static RemoteOrdererLogisticPipeBlockEntity getPipe(ItemStack stack, World world) {
        if(stack == null){
            return null;
        }
        if(!stack.getStationNbt().contains("connectedPipeX") || !stack.getStationNbt().contains("connectedPipeY") || !stack.getStationNbt().contains("connectedPipeZ")) {
            return null;
        }
        if(!stack.getStationNbt().contains("connectedPipeDimension")) {
            return null;
        }
        int x = stack.getStationNbt().getInt("connectedPipeX");
        int y = stack.getStationNbt().getInt("connectedPipeY");
        int z = stack.getStationNbt().getInt("connectedPipeZ");

        Identifier id = Identifier.of(stack.getStationNbt().getString("connectedPipeDimension"));

        return SideUtil.get(() -> {
            Optional<Identifier> worldDimId = DimensionRegistry.INSTANCE.getIdByLegacyId(world.dimension.id);
            if(worldDimId.isEmpty()){
                return null;
            }
            if(id.equals(worldDimId.get()) && world.getBlockEntity(x, y, z) instanceof RemoteOrdererLogisticPipeBlockEntity pipe){
                return pipe;
            }
            return null;
        }, () -> {
            MinecraftServer server = MinecraftServer.class.cast(FabricLoader.getInstance().getGameInstance());
            for(ServerWorld serverWorld : server.worlds) {
                Optional<Identifier> worldDimId = DimensionRegistry.INSTANCE.getIdByLegacyId(serverWorld.dimension.id);
                if(worldDimId.isEmpty()){
                    continue;
                }
                if(id.equals(worldDimId.get()) && serverWorld.getBlockEntity(x, y, z) instanceof RemoteOrdererLogisticPipeBlockEntity pipe){
                    return pipe;
                }
            }
            return null;
        });
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack itemStack, String s) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(s);
        if(itemStack.getStationNbt().contains("connectedPipeDimension")) {
            tooltip.add("Has Remote Pipe");
        }
        return tooltip.toArray(new String[0]);
    }
}
