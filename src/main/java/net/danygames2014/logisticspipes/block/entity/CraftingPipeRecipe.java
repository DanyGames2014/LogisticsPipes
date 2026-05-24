package net.danygames2014.logisticspipes.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;

public class CraftingPipeRecipe {
    public ObjectArrayList<ItemStack> inputs;
    public ItemStack output;

    public CraftingPipeRecipe() {
        this.inputs = new ObjectArrayList<>();
    }

    public CraftingPipeRecipe(ItemStack output, ObjectArrayList<ItemStack> inputs) {
        this.output = output;
        this.inputs = inputs;
    }

    // Input
    public void setInputs(ObjectArrayList<ItemStack> inputs) {
        this.inputs = inputs;
    }

    public void addInput(ItemStack input) {
        if (input == null) {
            return;
        }

        for (ItemStack item : inputs) {
            if (item == null || item.count >= item.getMaxCount()) {
                continue;
            }
            
            if (item.isItemEqual(input)) {
                int itemCount = item.count + input.count;
                
                if (itemCount <= input.getMaxCount()) {
                    item.count = itemCount;
                } else {
                    item.count = input.getMaxCount();
                    itemCount -= input.getMaxCount();
                    inputs.add(new ItemStack(input.getItem(), itemCount, input.getDamage()));
                }
                
                return;
            }
        }
        
        inputs.add(input);
    }

    // Output
    public void setOutput(ItemStack output) {
        this.output = output;
    }
}
