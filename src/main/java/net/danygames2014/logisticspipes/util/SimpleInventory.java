package net.danygames2014.logisticspipes.util;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.util.ItemUtil;
import net.danygames2014.logisticspipes.interfaces.routing.SaveState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import java.util.Arrays;

public class SimpleInventory implements Inventory, SaveState {
    private ItemStack[] contents;
    private String name;
    private int stackLimit;

    @Override
    public int size() {
        return contents.length;
    }

    @Override
    public ItemStack getStack(int slot) {
        return contents[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (contents[slot] == null) return null;
        if (contents[slot].count > amount) return contents[slot].split(amount);
        ItemStack ret = contents[slot];
        contents[slot] = null;
        return ret;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        contents[slot] = stack;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxCountPerStack() {
        return stackLimit;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {
        NbtList nbtList = nbt.getList(prefix + "items");

        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound item = (NbtCompound) nbtList.get(i);
            int index = item.getInt("index");
            contents[index] = new ItemStack(item);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < contents.length; ++i) {
            if (contents[i] != null && contents[i].count > 0) {
                NbtCompound item = new NbtCompound();
                item.putInt("index", i);
                contents[i].writeNbt(item);
                nbtList.add(item);
            }
        }
        nbt.put(prefix + "items", nbtList);
    }

    public void dropContents(World world, int x, int y, int z){
        if(!world.isRemote){
            ItemUtil.dropItems(world, this, x, y, z);
            Arrays.fill(contents, null);
        }
    }
}
