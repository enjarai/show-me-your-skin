package nl.enjarai.showmeyourskin.net;

import com.mojang.serialization.Codec;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class HandshakeServer {
    private final Supplier<SyncedModConfig> configSupplier;
    private final Map<ServerPlayerEntity, HandshakeState> syncStates = new WeakHashMap<>();

    public HandshakeServer(Supplier<SyncedModConfig> configSupplier) {
        this.configSupplier = configSupplier;
    }

    public HandshakeState getHandshakeState(ServerPlayerEntity player) {
        return syncStates.getOrDefault(player, HandshakeState.NOT_SENT);
    }

    public ConfigSyncPacket getSyncPacket(ServerPlayerEntity player) {
        return new ConfigSyncPacket(configSupplier.get());
    }

    public void configSentToClient(ServerPlayerEntity player) {
        syncStates.put(player, HandshakeState.SENT);
    }

    public HandshakeState clientReplied(ServerPlayerEntity player, SyncConfirmPacket packet) {
        var state = getHandshakeState(player);

        if (state == HandshakeState.SENT) {
            if (packet.success()) {
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
