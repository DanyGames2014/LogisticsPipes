package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.minecraft.item.ArmorItem;
import net.modificationstation.stationapi.api.client.item.ArmorTextureProvider;
import net.modificationstation.stationapi.api.template.item.TemplateArmorItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class HudGlassesItem extends TemplateArmorItem implements ArmorTextureProvider {
    public HudGlassesItem(Identifier identifier) {
        super(identifier, 2, 0, 0);
        setMaxDamage(0);
    }

    @Override
    public Identifier getTexture(ArmorItem armorItem) {
        return LogisticsPipes.NAMESPACE.id("logistics_hud");
    }
}
