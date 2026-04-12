package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.network.DefaultRouteToggleC2SPacket;
import net.danygames2014.logisticspipes.network.OpenModuleScreenC2SPacket;
import net.danygames2014.logisticspipes.network.RequestPartialToggleC2SPacket;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;

public class PacketListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerPackets(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("open_module"), OpenModuleScreenC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("default_route_toggle"), DefaultRouteToggleC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("request_partial_toggle"), RequestPartialToggleC2SPacket.TYPE);
    }
}
