package net.danygames2014.logisticspipes.item;

import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiskItem extends TemplateItem implements CustomTooltipProvider {
    public DiskItem(Identifier identifier) {
        super(identifier);
        this.setMaxCount(1);
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        List<String> strings = new ArrayList<>();

        strings.add(originalTooltip);

        if(!stack.getStationNbt().values().isEmpty()) {
            if(stack.getStationNbt().contains("name")) {
                String name = "§8" + stack.getStationNbt().getString("name");
                strings.add(name);
            }
        }

        return strings.toArray(new String[0]);
    }
}
