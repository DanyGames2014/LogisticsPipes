package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.network.RequestRoutingStatsC2SPacket;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public class RoutingStatsScreen extends LogisticsBaseScreen{
    private final Router router;
    public RoutingStatsScreen(Router router, PlayerEntity player) {
        super(player, null, new ModuleScreenHandler(player, null));
        this.router = router;
        this.backgroundWidth = 170;
        this.backgroundHeight = 200;
        if(router != null && router.getPipe() != null && router.getPipe().world != null && router.getPipe().world.isRemote) {
            PacketHelper.send(new RequestRoutingStatsC2SPacket(router.getPipe().x, router.getPipe().y, router.getPipe().z));
        }
    }

    @Override
    protected void drawForeground() {
        super.drawForeground();
        if(router == null) return;
        String pipeName = router.getPipe().pipeBlock.asItem().getTranslatedName();
        textRenderer.draw(pipeName, (170 - textRenderer.getWidth(pipeName))/2, 10, 0x83601c);

        int sessionxCenter = 85;
        int lifetimexCenter = 130;

        textRenderer.draw("Session", sessionxCenter - textRenderer.getWidth("Session") / 2, 40, 0x303030);
        textRenderer.draw("Lifetime", lifetimexCenter - textRenderer.getWidth("Lifetime") / 2, 40, 0x303030);
        textRenderer.draw("Sent:", 60 - textRenderer.getWidth("Sent:"), 55, 0x303030);
        textRenderer.draw("Received:", 60 - textRenderer.getWidth("Received:"), 70, 0x303030);
        textRenderer.draw("Relayed:", 60 - textRenderer.getWidth("Relayed:"), 85, 0x303030);
        LogisticPipeBlockEntity pipe = router.getPipe();
        textRenderer.draw(pipe.statSessionSent+"", sessionxCenter - textRenderer.getWidth(pipe.statSessionSent+"")/2, 55, 0x303030);
        textRenderer.draw(pipe.statSessionReceived+"", sessionxCenter - textRenderer.getWidth(pipe.statSessionReceived+"")/2, 70, 0x303030);
        textRenderer.draw(pipe.statSessionRelayed+"", sessionxCenter - textRenderer.getWidth(pipe.statSessionRelayed+"")/2, 85, 0x303030);

        textRenderer.draw(pipe.statLifetimeSent+"", lifetimexCenter - textRenderer.getWidth(pipe.statLifetimeSent+"")/2, 55, 0x303030);
        textRenderer.draw(pipe.statLifetimeReceived+"", lifetimexCenter - textRenderer.getWidth(pipe.statLifetimeReceived+"")/2, 70, 0x303030);
        textRenderer.draw(pipe.statLifetimeRelayed+"", lifetimexCenter - textRenderer.getWidth(pipe.statLifetimeRelayed+"")/2, 85, 0x303030);

        textRenderer.draw("Network Size:", 110 - textRenderer.getWidth("Network Size:"), 120, 0x303030);

        int networkSize = 0;

        if(router.getPipe() != null && router.getPipe().world != null && router.getPipe().world.isRemote) {
            networkSize = pipe.networkSize;
        } else {
            networkSize = pipe.getNetwork().routers.size();
        }

        textRenderer.draw(networkSize+"", 130 - textRenderer.getWidth(networkSize+"")/2, 120, 0x303030);


        String escString = "Press <ESC> to exit";
        textRenderer.draw(escString, (170 - textRenderer.getWidth(escString)) / 2, 180, 0x404040);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        BasicGuiHelper.drawGuiBackGround(minecraft, guiLeft, guiTop, right, bottom, zOffset, true);
    }
}
