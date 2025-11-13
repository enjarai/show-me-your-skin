package nl.enjarai.showmeyourskin.client;

import jdk.jfr.Category;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding OPEN_SETTINGS;
    public static KeyBinding GLOBAL_TOGGLE;
    
    private static KeyBinding.Category KEYBIND_CATEGORY = new KeyBinding.Category(Identifier.of("category.showmeyourskin.showmeyourskin"));

    public static void register() {
        OPEN_SETTINGS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                KEYBIND_CATEGORY
        ));
        
        GLOBAL_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Global Toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                KEYBIND_CATEGORY
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
