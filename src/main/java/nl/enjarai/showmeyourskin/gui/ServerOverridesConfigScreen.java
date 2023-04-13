package nl.enjarai.showmeyourskin.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ServerOverridesConfigScreen extends OverrideableConfigScreen {
    public ServerOverridesConfigScreen(Screen parent) {
        super(parent, Text.translatable("gui.showmeyourskin.armorScreen.title.serverOverrides"));
    }
}
