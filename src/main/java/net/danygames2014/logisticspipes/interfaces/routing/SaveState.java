package net.danygames2014.logisticspipes.interfaces.routing;

import net.minecraft.nbt.NbtCompound;

public interface SaveState {
    /**
     * Called to read every information for the given class from the NBTTagCompount
     * @param nbt to read from
     * @param prefix before every key to seperate information
     */
    void readNbt(NbtCompound nbt, String prefix);

    /**
     * Called to save all information of the given class into an NBTTagCompount
     * @param nbt to save the information in
     * @param prefix before every key to seperate information
     */
    void writeNbt(NbtCompound nbt, String prefix);
}
