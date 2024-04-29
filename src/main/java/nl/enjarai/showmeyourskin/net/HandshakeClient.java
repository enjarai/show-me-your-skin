package nl.enjarai.showmeyourskin.net;

import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;

import java.util.Optional;
import java.util.function.Consumer;

public class HandshakeClient {
    private final Consumer<SyncedModConfig> updateCallback;
    private SyncedModConfig serverConfig = null;

    public HandshakeClient(Consumer<SyncedModConfig> updateCallback) {
        this.updateCallback = updateCallback;
    }

    /**
     * Returns the server config if the client has received one for this server,
     * returns an empty optional in any other case.
     */
    public Optional<SyncedModConfig> getConfig() {
        return Optional.ofNullable(serverConfig);
    }

    public SyncConfirmPacket handleConfigSync(ConfigSyncPacket packet) {
        serverConfig = packet.config();

        if (serverConfig != null) {
            updateCallback.accept(serverConfig);
            ShowMeYourSkin.LOGGER.info("Received config from server");
        }

        return new SyncConfirmPacket(serverConfig != null);
    }

    public void reset() {
        serverConfig = null;
    }
}
