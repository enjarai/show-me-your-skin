package nl.enjarai.showmeyourskin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import nl.enjarai.cicada.api.conversation.ConversationManager;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import nl.enjarai.cicada.api.util.CicadaEntrypoint;
import nl.enjarai.cicada.api.util.JsonSource;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.ClientConfigScreen;
import nl.enjarai.showmeyourskin.gui.ConfigScreen;
import nl.enjarai.showmeyourskin.gui.ServerIntegratedConfigScreen;
import nl.enjarai.showmeyourskin.net.ConfigSyncPacket;
import nl.enjarai.showmeyourskin.net.HandshakeClient;
import nl.enjarai.showmeyourskin.net.SettingsUpdatePacket;
import org.jetbrains.annotations.Nullable;

public class ShowMeYourSkinClient implements ClientModInitializer, CicadaEntrypoint {
	public static final HandshakeClient HANDSHAKE_CLIENT =
			new HandshakeClient(config -> {});

	@Override
	public void onInitializeClient() {
		ModConfig.INSTANCE.ensureValid();

		ModKeyBindings.register();
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);

		// Preload the dummy player for the config screen to avoid lag spikes later.
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> DummyClientPlayerEntity.getInstance());

		initHandshake();
	}

	private void initHandshake() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(ConfigSyncPacket.PACKET_ID, (packet, ctx) -> {
				ctx.responseSender().sendPacket(HANDSHAKE_CLIENT.handleConfigSync(packet));
			});
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			HANDSHAKE_CLIENT.reset();
		});
	}

	public static void syncToServer(ArmorConfig config) {
		var player = MinecraftClient.getInstance().player;
		if (player != null) {
			var component = player.getComponent(Components.ARMOR_CONFIG);
			component.setConfig(config);

			ClientPlayNetworking.send(new SettingsUpdatePacket(config));
		}
	}

	public static ConfigScreen createConfigScreen(@Nullable Screen parent) {
		if (HANDSHAKE_CLIENT.getConfig().isPresent()) {
			return new ServerIntegratedConfigScreen(parent);
		} else {
			return new ClientConfigScreen(parent);
		}
	}

	public void tick(MinecraftClient client) {
		ModKeyBindings.tick(client);
	}

	@Override
	public void registerConversations(ConversationManager conversationManager) {
		conversationManager.registerSource(
				JsonSource.fromUrl("https://raw.githubusercontent.com/enjarai/show-me-your-skin/master/src/main/resources/cicada/showmeyourskin/conversations.json")
						.or(JsonSource.fromResource("cicada/showmeyourskin/conversations.json")),
				ShowMeYourSkin.LOGGER::info
		);
	}
}
