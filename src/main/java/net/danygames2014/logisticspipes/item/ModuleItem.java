package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.interfaces.ClientInformationProvider;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.module.ModuleFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.List;
import java.util.Random;

public class ModuleItem extends TemplateItem {

    private final ModuleFactory moduleFactory;

    public ModuleItem(Identifier identifier, ModuleFactory moduleFactory) {
        super(identifier);
        this.moduleFactory = moduleFactory;
    }

    public LogisticsModule getLogisticsModule() {
        return moduleFactory.create();
    }

    public ModuleFactory getModuleFactory() {
        return moduleFactory;
    }

    public static void saveInformation(ItemStack itemStack, LogisticsModule module, World world) {
        if (module == null) {
            return;
        }

        NbtCompound nbt = new NbtCompound();
        module.writeNbt(nbt, "");
        if (nbt.equals(new NbtCompound())) {
            return;
        }
        if (world.isRemote) {
            NbtList list = new NbtList();
            String info1 = "Please reopen the window to see the information.";
            list.add(new NbtString(info1));
            list.add(new NbtString(info1));
            itemStack.getStationNbt().put("informationList", list);
            itemStack.getStationNbt().putDouble("Random-Stack-Prevent", new Random().nextDouble());
            return;
        }
        itemStack.getStationNbt().put("moduleInformation", nbt);
        if (module instanceof ClientInformationProvider clientInformationProvider) {
            List<String> information = clientInformationProvider.getClientInformation();
            if (!information.isEmpty()) {
                NbtList list = new NbtList();
                for (String info : information) {
                    list.add(new NbtString(info));
                }
                itemStack.getStationNbt().put("informationList", list);
            }
            itemStack.getStationNbt().putDouble("Random-Stack-Prevent", new Random().nextDouble());
        }
    }

    public static void readInformation(ItemStack itemStack, LogisticsModule module, World world) {
        if (module == null) {
            return;
        }
        if (itemStack.getStationNbt().contains("moduleInformation")) {
            module.readNbt(itemStack.getStationNbt().getCompound("moduleInformation"), "");
        }
    }

    public static void removeInformation(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }
        // lp does a more proper check here but I dont think it matters
        itemStack.getStationNbt().values().clear();
    }
}
