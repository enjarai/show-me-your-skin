package nl.enjarai.showmeyourskin.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;

public record SettingsUpdatePacket(ArmorConfig settings) implements CustomPayload {
    public static final Id<SettingsUpdatePacket> PACKET_ID = new Id<>(ShowMeYourSkin.id("settings_update"));
    public static final PacketCodec<ByteBuf, SettingsUpdatePacket> PACKET_CODEC =
            ArmorConfig.PACKET_CODEC.xmap(SettingsUpdatePacket::new, SettingsUpdatePacket::settings);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
