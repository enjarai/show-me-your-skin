package nl.enjarai.showmeyourskin;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import nl.enjarai.cicada.api.util.ProperLogger;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfigServer;
import nl.enjarai.showmeyourskin.net.ConfigSyncPacket;
import nl.enjarai.showmeyourskin.net.HandshakeServer;
import nl.enjarai.showmeyourskin.net.SettingsUpdatePacket;
import nl.enjarai.showmeyourskin.net.SyncConfirmPacket;
import org.slf4j.Logger;

public class ShowMeYourSkin implements ModInitializer {
    public static final String MODID = "showmeyourskin";
    public static final Logger LOGGER = ProperLogger.getLogger(MODID);

    public static final Identifier UPDATE_C2S_CHANNEL = id("update");
    public static final Identifier CONFIG_SYNC_CHANNEL = id("config_sync");
    public static final HandshakeServer HANDSHAKE_SERVER =
            new HandshakeServer(() -> SyncedModConfigServer.INSTANCE);

    @Override
    public void onInitialize() {
        SyncedModConfigServer.load();

        initHandshake();
    }

    private static void initHandshake() {
        PayloadTypeRegistry.playS2C().register(ConfigSyncPacket.PACKET_ID, ConfigSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SyncConfirmPacket.PACKET_ID, SyncConfirmPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SettingsUpdatePacket.PACKET_ID, SettingsUpdatePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SettingsUpdatePacket.PACKET_ID, SettingsUpdatePacket.PACKET_CODEC);

        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayNetworking.registerReceiver(handler, SyncConfirmPacket.PACKET_ID, (packet, ctx) -> {
                if (HANDSHAKE_SERVER.clientReplied(ctx.player(), packet) == HandshakeServer.HandshakeState.ACCEPTED) {
                    startListeningForUpdates(handler);

                    for (var playerEntity : server.getPlayerManager().getPlayerList()) {
                        Components.ARMOR_CONFIG.sync(playerEntity);
                    }
                }
            });

            ServerPlayNetworking.send(handler.getPlayer(), HANDSHAKE_SERVER.getSyncPacket(handler.getPlayer()));

            HANDSHAKE_SERVER.configSentToClient(handler.getPlayer());

            handler.getPlayer().getComponent(Components.ARMOR_CONFIG).ensureValid();
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            HANDSHAKE_SERVER.playerDisconnected(handler.getPlayer());
        });
    }

    private static void startListeningForUpdates(ServerPlayNetworkHandler handler) {
        ServerPlayNetworking.registerReceiver(handler, SettingsUpdatePacket.PACKET_ID, (packet, ctx) -> {
            var component = Components.ARMOR_CONFIG.get(ctx.player());
            component.setConfig(packet.settings());
            component.ensureValid();
            Components.ARMOR_CONFIG.sync(ctx.player());
        });
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
}
