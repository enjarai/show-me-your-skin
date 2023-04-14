package nl.enjarai.showmeyourskin.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClientConfigScreen extends OverrideableConfigScreen {
    public ClientConfigScreen(Screen parent) {
        super(parent, Text.translatable("gui.showmeyourskin.armorScreen.title"));
    }
}
