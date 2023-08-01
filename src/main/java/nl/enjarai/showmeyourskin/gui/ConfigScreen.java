package nl.enjarai.showmeyourskin.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.widget.ArmorConfigWindow;
import nl.enjarai.showmeyourskin.gui.widget.ConfigEntryWidget;
import nl.enjarai.showmeyourskin.gui.widget.IconPressButtonWidget;
import nl.enjarai.showmeyourskin.gui.widget.IconToggleButtonWidget;

public abstract class ConfigScreen extends SpruceScreen {
    public static final int TEXT_COLOR = 0x404040;
    protected final Screen parent;
    protected ArmorConfigWindow armorConfigWindow;
    protected final Position windowRelative = Position.of(0, 0);
    private IconPressButtonWidget backButton;
    private IconToggleButtonWidget globalToggle;

    public ConfigScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        backButton = new IconPressButtonWidget(
                getBackButtonPos(), ShowMeYourSkin.id("textures/gui/button/back.png"),
                0, 0
        );
        backButton.setCallback(btn -> close());

        globalToggle = new IconToggleButtonWidget(
                getGlobalTogglePos(), ShowMeYourSkin.id("textures/gui/button/global_toggle.png"),
                0, 0, ModConfig.INSTANCE.globalEnabled
        );
        globalToggle.setCallback((btn, enabled) -> ModConfig.INSTANCE.globalEnabled = enabled);
        globalToggle.setTooltip(Text.translatable("gui.showmeyourskin.armorScreen.globalToggleTooltip",
                KeyBindingHelper.getBoundKeyOf(ModKeyBindings.GLOBAL_TOGGLE).getLocalizedText()));

        addDrawableChild(backButton);
        addDrawableChild(globalToggle);
    }

    @Override
    public void renderBackground(DrawContext context) {
        var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(0, 0, -999);

        super.renderBackground(context);

        matrices.pop();
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        ScissorManager.pushScaleFactor(this.scaleFactor);
        this.renderBackground(graphics);
        this.renderWidgets(graphics, mouseX, mouseY, delta);
        this.renderTitle(graphics, mouseX, mouseY, delta);
        Tooltip.renderAll(graphics);
        ScissorManager.popScaleFactor();
    }

    protected void loadConfigEntry(ConfigEntryWidget entry) {
        var tabIndex = 0;
        if (armorConfigWindow != null) {
            tabIndex = armorConfigWindow.getTabManager().getActiveIndex();
        }

        loadArmorConfigWindow(new ArmorConfigWindow(
                Position.of(getWindowLeft(), getWindowTop()), this,
                entry.getName(), entry.getDummyPlayer(), entry.getArmorConfig(), tabIndex, true
        ));
    }

    protected void loadArmorConfigWindow(ArmorConfigWindow window) {
        remove(armorConfigWindow);
        armorConfigWindow = window;
        windowRelative.setAnchor(window);
        addDrawableChild(armorConfigWindow);
    }

    protected int getWindowLeft() {
        return (this.width - 236) / 2;
    }

    protected int getWindowTop() {
        return (this.height - 180) / 2;
    }

    protected Position getBackButtonPos() {
        return Position.of(windowRelative, 6, -22);
    }

    protected Position getGlobalTogglePos() {
        return Position.of(windowRelative, 30, -22);
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }
}
