package nl.enjarai.showmeyourskin.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.ConfigScreen;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding OPEN_SETTINGS;

    public static void register() {
        OPEN_SETTINGS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.showmeyourskin.open_settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.showmeyourskin.showmeyourskin"
        ));
    }

    public static void tick(MinecraftClient client) {
        while (OPEN_SETTINGS.wasPressed()) {
            client.setScreen(new ConfigScreen(null));
//            client.setScreen(ModConfig.INSTANCE.getGlobalScreen(null));
        }
    }
}
