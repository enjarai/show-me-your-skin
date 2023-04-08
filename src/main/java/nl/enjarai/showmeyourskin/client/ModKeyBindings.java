package nl.enjarai.showmeyourskin.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.ClientOnlyConfigScreen;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding OPEN_SETTINGS;
    public static KeyBinding GLOBAL_TOGGLE;

    public static void register() {
        OPEN_SETTINGS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.showmeyourskin.open_settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.showmeyourskin.showmeyourskin"
        ));
        GLOBAL_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.showmeyourskin.global_toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.showmeyourskin.showmeyourskin"
        ));
    }

    public static void tick(MinecraftClient client) {
        while (OPEN_SETTINGS.wasPressed()) {
            client.setScreen(ShowMeYourSkinClient.createConfigScreen(null));
        }
        while (GLOBAL_TOGGLE.wasPressed()) {
            ModConfig.INSTANCE.globalEnabled = !ModConfig.INSTANCE.globalEnabled;
            ModConfig.INSTANCE.save();
            if (client.player != null) {
                client.player.sendMessage(
                        Text.translatable(
                                "key.showmeyourskin." +
                                        (ModConfig.INSTANCE.globalEnabled ? "global_toggle.enable" : "global_toggle.disable")
                        ),
                        true
                );
            }
        }
    }
}
