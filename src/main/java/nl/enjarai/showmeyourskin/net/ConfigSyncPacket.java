package nl.enjarai.showmeyourskin.net;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;

public record ConfigSyncPacket(SyncedModConfig config) implements CustomPayload {
    public static final Id<ConfigSyncPacket> PACKET_ID = new Id<>(ShowMeYourSkin.id("config_sync"));
    public static final PacketCodec<RegistryByteBuf, ConfigSyncPacket> PACKET_CODEC =
            SyncedModConfig.PACKET_CODEC.xmap(ConfigSyncPacket::new, ConfigSyncPacket::config);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
