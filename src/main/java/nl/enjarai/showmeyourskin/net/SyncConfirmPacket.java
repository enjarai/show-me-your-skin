package nl.enjarai.showmeyourskin.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

public record SyncConfirmPacket(boolean success) implements CustomPayload {
    public static final Id<SyncConfirmPacket> PACKET_ID = new Id<>(ShowMeYourSkin.id("sync_confirm"));
    public static final PacketCodec<ByteBuf, SyncConfirmPacket> PACKET_CODEC =
            PacketCodecs.BOOL.xmap(SyncConfirmPacket::new, SyncConfirmPacket::success);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
