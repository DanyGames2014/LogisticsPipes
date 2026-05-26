package net.danygames2014.logisticspipes.util;

import net.danygames2014.buildcraft.util.ItemUtil;
import net.danygames2014.logisticspipes.interfaces.routing.SaveState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.LinkedList;

public class SimpleInventory implements Inventory, SaveState {
    public ItemStack[] contents;
    private final String name;
    private int stackLimit;
    private final MarkDirtyCallback markDirtyCallback;

    public SimpleInventory(int size, String name, int stackLimit, MarkDirtyCallback markDirtyCallback) {
        this.contents = new ItemStack[size];
        this.name = name;
        this.stackLimit = stackLimit;
        this.markDirtyCallback = markDirtyCallback;
    }

    @Override
    public int size() {
        return contents.length;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= contents.length) {
            return null;
        }

        return contents[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 0 || slot >= contents.length) {
            return null;
        }

        if (this.contents[slot] != null) {
            ItemStack stack;

            if (this.contents[slot].count <= amount) {
                stack = this.contents[slot];
                this.contents[slot] = null;
            } else {
                stack = this.contents[slot].split(amount);
                if (this.contents[slot].count == 0) {
                    this.contents[slot] = null;
                }

            }

            this.markDirty();
            return stack;
        }

        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= contents.length) {
            return;
        }

        this.contents[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getMaxCountPerStack() {
        return stackLimit;
    }

    @Override
    public void markDirty() {
        this.markDirtyCallback.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    public interface MarkDirtyCallback {
        void markDirty();
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

    public void dropContents(World world, int x, int y, int z) {
        if (!world.isRemote) {
            ItemUtil.dropItems(world, this, x, y, z);
            Arrays.fill(contents, null);
        }
    }

    public void handleItemStackList(LinkedList<ItemIdentifierStack> _allItems) {
        int i = 0;
        for (ItemIdentifierStack stack : _allItems) {
            if (contents.length <= i) break;
            if (stack == null) {
                contents[i] = null;
            } else {
                contents[i] = stack.makeNormalStack();
            }
            i++;
        }
        markDirty();
    }

    public int addCompressed(ItemStack stack) {
        if(stack == null) return 0;
        stack = stack.copy();
        for(int i=0; i<this.contents.length;i++) {
            if(stack.count <= 0) {
                break;
            }
            if(contents[i] == null) continue; //Skip Empty Slots on first attempt.
            int added = tryAddToSlot(i, stack);
            stack.count -= added;
        }
        for(int i=0; i<this.contents.length;i++) {
            if(stack.count <= 0) {
                break;
            }
            int added = tryAddToSlot(i, stack);
            stack.count -= added;
        }
        markDirty();
        return stack.count;
    }

    private int tryAddToSlot(int i, ItemStack stack) {
        ItemStack slot = contents[i];
        if(slot == null) {
            contents[i] = stack.copy();
            return stack.count;
        }
        ItemIdentifier slotIdent = ItemIdentifier.get(slot);
        ItemIdentifier stackIdent = ItemIdentifier.get(stack);
        if(slotIdent.equals(stackIdent)) {
            slot.count += stack.count;
            if(slot.count > this.stackLimit) {
                int ans = stack.count - (slot.count - this.stackLimit);
                slot.count = this.stackLimit;
                return ans;
            } else {
                return stack.count;
            }
        } else {
            return 0;
        }
    }
}
