package nl.enjarai.showmeyourskin.net;

import com.mojang.serialization.Codec;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class HandshakeServer<T> {
    private final Codec<T> transferCodec;
    private final Supplier<T> configSupplier;
    private final Map<ServerPlayerEntity, HandshakeState> syncStates = new WeakHashMap<>();

    public HandshakeServer(Codec<T> transferCodec, Supplier<T> configSupplier) {
        this.transferCodec = transferCodec;
        this.configSupplier = configSupplier;
    }

    public HandshakeState getHandshakeState(ServerPlayerEntity player) {
        return syncStates.getOrDefault(player, HandshakeState.NOT_SENT);
    }

    public PacketByteBuf getConfigSyncBuf(ServerPlayerEntity player) {
        var buf = new PacketByteBuf(Unpooled.buffer());

        var config = configSupplier.get();
        var data = transferCodec.encodeStart(NbtOps.INSTANCE, config);
        try {
            buf.writeNbt((NbtCompound) data.getOrThrow(false, ShowMeYourSkin.LOGGER::error));
        } catch (RuntimeException e) {
            ShowMeYourSkin.LOGGER.error("Failed to encode config", e);
            buf.writeNbt(new NbtCompound());
        }

        return buf;
    }

    public void configSentToClient(ServerPlayerEntity player) {
        syncStates.put(player, HandshakeState.SENT);
    }

    public HandshakeState clientReplied(ServerPlayerEntity player, PacketByteBuf buf) {
        var state = getHandshakeState(player);

        if (state == HandshakeState.SENT) {
            if (buf.readBoolean()) {
                syncStates.put(player, HandshakeState.ACCEPTED);
                ShowMeYourSkin.LOGGER.info("Client of {} accepted server config.", player.getName().getString());
                return HandshakeState.ACCEPTED;
            } else {
                syncStates.put(player, HandshakeState.FAILED);
                ShowMeYourSkin.LOGGER.warn(
                        "Client of {} failed to process server config, check client logs find what went wrong.",
                        player.getName().getString());
                return HandshakeState.FAILED;
            }
        }

        return state;
    }

    public void playerDisconnected(ServerPlayerEntity player) {
        syncStates.remove(player);
    }

    public enum HandshakeState {
        NOT_SENT,
        SENT,
        ACCEPTED,
        FAILED
    }
}
