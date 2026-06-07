package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.SendNbtCompound;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class InventoryContentPacket extends CoordinatesPacket {
    public LinkedList<ItemIdentifierStack> allItems = new LinkedList<>();

    public InventoryContentPacket() {
    }

    public InventoryContentPacket(int x, int y, int z, LinkedList<ItemIdentifierStack> allItems) {
        super(x, y, z);
        this.allItems = allItems;
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            for (final ItemIdentifierStack item : allItems) {
                if(item == null) {
                    stream.writeByte(2);
                    continue;
                }
                stream.writeByte(1); // byte
                stream.writeInt(item.getItem().item.id);
                stream.writeInt(item.getItem().itemDamage);
                stream.writeInt(item.stackSize);
                SendNbtCompound.writeNbtCompound(item.getItem().nbt, stream);
            }
            stream.writeByte(0);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            byte v;
            while ((v = stream.readByte()) != 0) { // read until the end
                if(v == 1) {
                    final int itemID = stream.readInt();
                    final int dataValue = stream.readInt();
                    final int amount = stream.readInt();
                    final NbtCompound tag = SendNbtCompound.readNbtCompound(stream);
                    allItems.add(ItemIdentifier.get(Item.ITEMS[itemID], dataValue, tag).makeStack(amount));
                } else {
                    allItems.add(null);
                }
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
