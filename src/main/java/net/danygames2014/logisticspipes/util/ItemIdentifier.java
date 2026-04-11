package net.danygames2014.logisticspipes.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;

import java.util.LinkedList;

public class ItemIdentifier {
    private final static LinkedList<ItemIdentifier> itemIdentifierCacheServer = new LinkedList<>();
    private final static LinkedList<ItemIdentifier> itemIdentifierCacheClient = new LinkedList<>();

    private ItemIdentifier(Item item, int itemDamage, NbtCompound nbt){
        this.item = item;
        this.itemDamage = itemDamage;
        this.nbt = nbt;
    }

    public final Item item;
    public final int itemDamage;
    public final NbtCompound nbt;

    public static boolean allowNullsForTesting;

    public static ItemIdentifier get(Item item, int itemDamage, NbtCompound nbt){
        for(ItemIdentifier itemIdentifier : FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? itemIdentifierCacheClient : itemIdentifierCacheServer){
            if(itemIdentifier.item == item && itemIdentifier.itemDamage == itemDamage && nbtequal(itemIdentifier.nbt, nbt)){
                return itemIdentifier;
            }
        }
        ItemIdentifier unknownItem = new ItemIdentifier(item, itemDamage, nbt);
        (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? itemIdentifierCacheClient : itemIdentifierCacheServer).add(unknownItem);
        return unknownItem;
    }

    public static ItemIdentifier get(ItemStack stack) {
        if(stack == null && allowNullsForTesting){
            return null;
        }
        int itemDamage = 0;
        if(!stack.getItem().isDamageable()){
            itemDamage = stack.getDamage();
        }
        return get(stack.getItem(), itemDamage, stack.getStationNbt());
    }

    private static boolean nbtequal(NbtCompound nbt1, NbtCompound nbt2) {
        if(nbt1 == null && nbt2 == null) {
            return true;
        }
        if(nbt1 == null) {
            return false;
        }
        if(nbt2 == null) {
            return false;
        }
        return nbt1.equals(nbt2);
    }

    private String getName(ItemStack stack) {

        return TranslationStorage.getInstance().get(stack.getTranslationKey() + ".name");

    }

    public String getFriendlyName() {
        return getName(this.makeNormalStack(1));
    }

    public ItemIdentifierStack makeStack(int stackSize){
        return new ItemIdentifierStack(this, stackSize);
    }

    public ItemStack makeNormalStack(int stackSize){
        ItemStack stack = new ItemStack(this.item, stackSize, this.itemDamage);
        StationNBTSetter.cast(stack).setStationNbt(nbt);
        return stack;
    }

    @Override
    public String toString() {
        return getFriendlyName();
    }
}
