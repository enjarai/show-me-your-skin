package nl.enjarai.showmeyourskin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import nl.enjarai.cicada.api.conversation.ConversationManager;
import nl.enjarai.cicada.api.util.CicadaEntrypoint;
import nl.enjarai.cicada.api.util.JsonSource;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;
import nl.enjarai.showmeyourskin.gui.ClientConfigScreen;
import nl.enjarai.showmeyourskin.gui.OverrideableConfigScreen;
import nl.enjarai.showmeyourskin.gui.ConfigScreen;
import nl.enjarai.showmeyourskin.gui.ServerIntegratedConfigScreen;
import nl.enjarai.showmeyourskin.net.HandshakeClient;
import nl.enjarai.showmeyourskin.util.ArmorConfigComponent;
import org.jetbrains.annotations.Nullable;

public class ShowMeYourSkinClient implements ClientModInitializer, CicadaEntrypoint {
	public static final HandshakeClient<SyncedModConfig> HANDSHAKE_CLIENT =
			new HandshakeClient<>(SyncedModConfig.CODEC, config -> {});

	@Override
	public void onInitializeClient() {
		ModConfig.load();
		ModConfig.INSTANCE.ensureValid();

		ModKeyBindings.register();
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);

		// Preload the dummy player for the config screen to avoid lag spikes later.
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> DummyClientPlayerEntity.getInstance());

		initHandshake();
	}

	private void initHandshake() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(ShowMeYourSkin.CONFIG_SYNC_CHANNEL, (client1, handler1, buf, responseSender) -> {
				var returnBuf = HANDSHAKE_CLIENT.handleConfigSync(buf);
				responseSender.sendPacket(ShowMeYourSkin.CONFIG_SYNC_CHANNEL, returnBuf);
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

			var buf = PacketByteBufs.create();
			var nbt = new NbtCompound();
			component.writeToNbt(nbt);
			buf.writeNbt(nbt);
			ClientPlayNetworking.send(ShowMeYourSkin.UPDATE_C2S_CHANNEL, buf);
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
