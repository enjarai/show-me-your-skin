package nl.enjarai.showmeyourskin;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowMeYourSkin implements ModInitializer {
	public static final String MODID = "showmeyourskin";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModConfig.load();

		ModKeyBindings.register();
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	public void tick(MinecraftClient client) {
		ModKeyBindings.tick(client);
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
}
