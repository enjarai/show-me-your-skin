package nl.enjarai.showmeyourskin.gui;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.widget.ArmorConfigWindow;
import nl.enjarai.showmeyourskin.gui.widget.ConfigEntryWidget;
import nl.enjarai.showmeyourskin.gui.widget.PressButtonWidget;
import nl.enjarai.showmeyourskin.gui.widget.ToggleButtonWidget;

public abstract class ConfigScreen extends Screen {
    protected static final ButtonTextures BACK_BUTTON_TEXTURES = PressButtonWidget.createTextures("back");
    protected static final ButtonTextures GLOBAL_TOGGLE_TEXTURES = ToggleButtonWidget.createTextures("global_toggle");
    public static final int TEXT_COLOR = 0xff404040;

    protected final Screen parent;
    protected ArmorConfigWindow armorConfigWindow;
    private PressButtonWidget backButton;
    private ToggleButtonWidget globalToggle;

    public ConfigScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        backButton = new PressButtonWidget(
                getBackButtonX(), getBackButtonY(), 20, 20,
                BACK_BUTTON_TEXTURES, button -> close(), null
        );
        globalToggle = new ToggleButtonWidget(
                getGlobalToggleX(), getGlobalToggleY(), 20, 20,
                GLOBAL_TOGGLE_TEXTURES, ModConfig.INSTANCE.globalEnabled,
                (btn, enabled) -> ModConfig.INSTANCE.globalEnabled = enabled,
                Text.translatable("gui.showmeyourskin.armorScreen.globalToggleTooltip",
                        KeyBindingHelper.getBoundKeyOf(ModKeyBindings.GLOBAL_TOGGLE).getLocalizedText())
        );
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        renderBackgroundTextures(context);
    }

    protected void renderBackgroundTextures(DrawContext context) {
    }

    protected void fixChildren() {
        clearChildren();
        addDrawableChild(backButton);
        addDrawableChild(globalToggle);
        addDrawableChild(armorConfigWindow);
    }

    protected void loadConfigEntry(ConfigEntryWidget entry) {
        var tabIndex = 0;
        if (armorConfigWindow != null) {
            tabIndex = armorConfigWindow.getTabIndex();
        }

        loadArmorConfigWindow(new ArmorConfigWindow(
                this, getWindowLeft(), getWindowTop(),
                entry.getName(), entry.getDummyPlayer(), entry.getArmorConfig(), tabIndex, true
        ));
    }

    protected void loadArmorConfigWindow(ArmorConfigWindow window) {
        armorConfigWindow = window;

        fixChildren();
    }

    protected int getWindowLeft() {
        return (this.width - 238) / 2;
    }

    protected int getWindowTop() {
        return (this.height - 180) / 2;
    }

    protected int getBackButtonX() {
        return getWindowLeft() + 6;
    }

    protected int getBackButtonY() {
        return getWindowTop() - 22;
    }

    protected int getGlobalToggleX() {
        return getWindowLeft() + 30;
    }

    protected int getGlobalToggleY() {
        return getWindowTop() - 22;
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }
}
