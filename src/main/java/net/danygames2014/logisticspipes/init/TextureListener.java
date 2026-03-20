package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

public class TextureListener {
    public static final Identifier notRoutedStatus = LogisticsPipes.NAMESPACE.id("block/pipe/status/not_routed");
    public static final Identifier poweredStatus = LogisticsPipes.NAMESPACE.id("block/pipe/status/powered");
    public static final Identifier routedStatus = LogisticsPipes.NAMESPACE.id("block/pipe/status/routed");

    public static final Identifier basicPipe = LogisticsPipes.NAMESPACE.id("block/pipe/basic_pipe");

    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        Atlases.getTerrain().addTexture(notRoutedStatus);
        Atlases.getTerrain().addTexture(poweredStatus);
        Atlases.getTerrain().addTexture(routedStatus);

        Atlases.getTerrain().addTexture(basicPipe);
    }
}
