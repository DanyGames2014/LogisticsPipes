package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.network.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;

@SuppressWarnings("unused")
public class PacketListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerPackets(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("open_module"), OpenModuleScreenC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("default_route_toggle"), DefaultRouteToggleC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("request_partial_toggle"), RequestPartialToggleC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("provider_module_command"), ProviderModuleCommandC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("advanced_extractor_module_command"), AdvancedExtractorModuleCommandC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("extractor_module_direction"), ExtractorModuleDirectionC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("pipe_particle"), PipeParticleS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("satellite_id"), SatelliteIdC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("crafting_pipe_command"), CraftingPipeCommandC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("submit_request"), SubmitRequestC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("item_message"), ItemMessageS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("request_screen_content"), RequestScreenContentC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("send_screen_content"), SendScreenContentS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("request_disk_content"), RequestDiskContentC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("send_disk_content"), SendDiskContentS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("drop_disk"), DropDiskC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("set_disk_name"), SetDiskNameC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("request_disk_macro"), RequestDiskMacroC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("set_disk_content"), SetDiskContentC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_player_watching_status"), UpdatePlayerWatchingStatusC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_player_module_watching_status"), UpdatePlayerModuleWatchingStatusC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_module_inventory_content"), UpdateModuleInventoryContentS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_chassis_content"), UpdateChassisInventoryContentS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_module_data"), UpdateModuleDataS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_pipe_inventory_content"), UpdatePipeInventoryContentS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_pipe_data"), UpdatePipeDataS2CPacket.TYPE);
    }
}
