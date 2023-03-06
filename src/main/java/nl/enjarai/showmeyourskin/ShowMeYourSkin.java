package nl.enjarai.showmeyourskin;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import nl.enjarai.cicada.api.conversation.ConversationManager;
import nl.enjarai.cicada.api.util.CicadaEntrypoint;
import nl.enjarai.cicada.api.util.JsonSource;
import nl.enjarai.cicada.api.util.ProperLogger;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.slf4j.Logger;

public class ShowMeYourSkin implements ModInitializer, CicadaEntrypoint {
	public static final String MODID = "showmeyourskin";
	public static final Logger LOGGER = ProperLogger.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModConfig.load();

		ModKeyBindings.register();
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);

		// Preload the dummy player for the config screen to avoid lag spikes later.
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> DummyClientPlayerEntity.getInstance());
	}

	public void tick(MinecraftClient client) {
		ModKeyBindings.tick(client);
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}

	@Override
	public void registerConversations(ConversationManager conversationManager) {
		conversationManager.registerSource(
				JsonSource.fromUrl("https://raw.githubusercontent.com/enjarai/show-me-your-skin/master/src/main/resources/cicada/showmeyourskin/conversations.json")
						.or(JsonSource.fromResource("cicada/showmeyourskin/conversations.json")),
				LOGGER::info
		);
	}
}
