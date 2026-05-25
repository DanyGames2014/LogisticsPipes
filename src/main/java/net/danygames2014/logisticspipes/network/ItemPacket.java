package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.util.SendNbtCompound;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ItemPacket extends CoordinatesPacket {
    public ItemStack stack;

    public ItemPacket() {
        super();
    }

    public ItemPacket(int x, int y, int z, ItemStack stack) {
        super(x, y, z);
        this.stack = stack;
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            if(stack != null) {
                stream.writeInt(stack.itemId);
                stream.writeInt(stack.count);
                stream.writeInt(stack.getDamage());
                SendNbtCompound.writeNbtCompound(stack.getStationNbt(), stream);
            } else {
                stream.writeInt(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            final int itemID = stream.readInt();

            if(itemID != 0) {
                stack = new ItemStack(itemID, stream.readInt(), stream.readInt());
                StationNBTSetter.cast(stack).setStationNbt(SendNbtCompound.readNbtCompound(stream));
            } else {
                stack = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
