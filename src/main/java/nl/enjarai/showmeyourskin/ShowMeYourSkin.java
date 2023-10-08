package nl.enjarai.showmeyourskin;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import nl.enjarai.cicada.api.util.ProperLogger;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfigServer;
import nl.enjarai.showmeyourskin.net.HandshakeServer;
import org.slf4j.Logger;

public class ShowMeYourSkin implements ModInitializer {
    public static final String MODID = "showmeyourskin";
    public static final Logger LOGGER = ProperLogger.getLogger(MODID);

    public static final Identifier UPDATE_C2S_CHANNEL = id("update");
    public static final Identifier CONFIG_SYNC_CHANNEL = id("config_sync");
    public static final HandshakeServer<SyncedModConfig> HANDSHAKE_SERVER =
            new HandshakeServer<>(SyncedModConfig.CODEC, () -> SyncedModConfigServer.INSTANCE);

    @Override
    public void onInitialize() {
        SyncedModConfigServer.load();

        initHandshake();
    }

    private static void initHandshake() {
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayNetworking.registerReceiver(handler, CONFIG_SYNC_CHANNEL, (server1, player, handler1, buf, responseSender) -> {
                if (HANDSHAKE_SERVER.clientReplied(player, buf) == HandshakeServer.HandshakeState.ACCEPTED) {
                    startListeningForUpdates(handler1);

                    for (var playerEntity : server1.getPlayerManager().getPlayerList()) {
                        Components.ARMOR_CONFIG.sync(playerEntity);
                    }
                }
            });

            ServerPlayNetworking.send(handler.getPlayer(), CONFIG_SYNC_CHANNEL,
                    HANDSHAKE_SERVER.getConfigSyncBuf(handler.getPlayer()));

            HANDSHAKE_SERVER.configSentToClient(handler.getPlayer());

            handler.getPlayer().getComponent(Components.ARMOR_CONFIG).ensureValid();
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            HANDSHAKE_SERVER.playerDisconnected(handler.getPlayer());
        });
    }

    private static void startListeningForUpdates(ServerPlayNetworkHandler handler) {
        ServerPlayNetworking.registerReceiver(handler, UPDATE_C2S_CHANNEL, (server, player, handler1, buf, responseSender) -> {
            var nbt = buf.readNbt();
            if (nbt != null) {
                var component = player.getComponent(Components.ARMOR_CONFIG);
                component.setFromNbt(nbt);
                component.ensureValid();
                Components.ARMOR_CONFIG.sync(player);
            }
        });
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
}
