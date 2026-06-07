package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.interfaces.ClientInformationProvider;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.module.ModuleFactory;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModuleItem extends TemplateItem implements CustomTooltipProvider {

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
        if (module == null || itemStack == null) {
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

    @Override
    public @NotNull String[] getTooltip(ItemStack itemStack, String s) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(s);

        if((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && itemStack.getStationNbt().contains("informationList")) {
            NbtList list = itemStack.getStationNbt().getList("informationList");
            for(int i = 0; i < list.size(); i++) {
                NbtElement element = list.get(i);
                String data = ((NbtString)element).value;
                if(data.equals("<inventory>") && i + 1 < list.size()){
                    element = list.get(i + 1);
                    data = ((NbtString)element).value;
                    if(data.startsWith("<that>")) {
                        String prefix = data.substring(6);
                        NbtCompound module = itemStack.getStationNbt().getCompound("moduleInformation");
                        int size = module.getList(prefix + "items").size();

                        if(module.contains(prefix + "itemsCount")) {
                            size = module.getInt(prefix + "itemsCount");
                        }

                        SimpleInventory inv = new SimpleInventory(size, "InformationTempInventory", Integer.MAX_VALUE, () -> {});
                        inv.readNbt(module, prefix);
                        for(int pos = 0; pos < inv.size(); pos++) {
                            ItemStack stack = inv.getStack(pos);
                            if(stack != null) {
                                if(stack.count > 1) {
                                    tooltip.add("  " + stack.count+"x " + ItemIdentifier.get(stack).getFriendlyName());
                                } else {
                                    tooltip.add("  " + ItemIdentifier.get(stack).getFriendlyName());
                                }
                            }
                        }
                        i++;
                    } else {
                        tooltip.add("<inventory>");
                    }
                } else {
                    tooltip.add(data);
                }
            }
        }

        return tooltip.toArray(new String[0]);
    }
}
