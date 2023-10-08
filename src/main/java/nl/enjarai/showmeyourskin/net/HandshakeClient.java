package nl.enjarai.showmeyourskin.net;

import com.mojang.serialization.Codec;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

import java.util.Optional;
import java.util.function.Consumer;

public class HandshakeClient<T> {
    private final Codec<T> transferCodec;
    private final Consumer<T> updateCallback;
    private T serverConfig = null;

    public HandshakeClient(Codec<T> transferCodec, Consumer<T> updateCallback) {
        this.transferCodec = transferCodec;
        this.updateCallback = updateCallback;
    }

    /**
     * Returns the server config if the client has received one for this server,
     * returns an empty optional in any other case.
     */
    public Optional<T> getConfig() {
        return Optional.ofNullable(serverConfig);
    }

    public PacketByteBuf handleConfigSync(PacketByteBuf buf) {
        var data = buf.readNbt();
        try {
            serverConfig = transferCodec.parse(NbtOps.INSTANCE, data)
                    .getOrThrow(false, ShowMeYourSkin.LOGGER::error);
        } catch (RuntimeException e) {
            serverConfig = null;
            ShowMeYourSkin.LOGGER.error("Failed to parse config from server", e);
        }

        if (serverConfig != null) {
            updateCallback.accept(serverConfig);
            ShowMeYourSkin.LOGGER.info("Received config from server");
        }

        var returnBuf = new PacketByteBuf(Unpooled.buffer());
        returnBuf.writeBoolean(serverConfig != null);
        return returnBuf;
    }

    public void reset() {
        serverConfig = null;
    }
}
