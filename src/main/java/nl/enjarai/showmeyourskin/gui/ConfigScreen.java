package nl.enjarai.showmeyourskin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.widget.ArmorConfigWindow;
import nl.enjarai.showmeyourskin.gui.widget.ConfigEntryWidget;
import nl.enjarai.showmeyourskin.gui.widget.PlayerSelectorWidget;
import nl.enjarai.showmeyourskin.gui.widget.ToggleButtonWidget;

public class ConfigScreen extends Screen {
    public static final Identifier SELECTOR_TEXTURE = ShowMeYourSkin.id("textures/gui/config_screen.png");
    public static final Identifier GLOBAL_ICON = ShowMeYourSkin.id("textures/gui/global_icon.png");
    public static final int TEXT_COLOR = 0x404040;

    private final Screen parent;
    private ConfigEntryWidget globalConfig;
    private PlayerSelectorWidget playerSelector;
    private ArmorConfigWindow armorConfigWindow;
    private ButtonWidget backButton;
    private ButtonWidget globalToggle;
    private boolean initialized = false;

    public ConfigScreen(Screen parent) {
        super(new TranslatableText("gui.showmeyourskin.armorScreen.title"));
        this.parent = parent;
    }

    protected int getSelectorLeft() {
        return (this.width - 238) / 2;
    }

    protected int getSelectorTop() {
        return -49;
    }

    @Override
    protected void init() {
        playerSelector = new PlayerSelectorWidget(
                client, width, height, getSelectorLeft() + 44, getSelectorTop() + 63,
                187, this::loadArmorConfigWindow
        );
        globalConfig = new ConfigEntryWidget(
                client, playerSelector, getSelectorLeft() + 11, getSelectorTop() + 63,
                new TranslatableText("gui.showmeyourskin.armorScreen.global"), () -> GLOBAL_ICON
        );
        playerSelector.linkDefault(globalConfig);

        backButton = new TexturedButtonWidget(
                getSelectorLeft() - 20, getSelectorTop() + 52, 20, 20,
                0, 78, ArmorConfigWindow.TEXTURE, button -> close()
        );
        globalToggle = new ToggleButtonWidget(
                this, getSelectorLeft() - 20, getSelectorTop() + 76,
                0, 160, SELECTOR_TEXTURE, ModConfig.INSTANCE.globalEnabled,
                (enabled) -> ModConfig.INSTANCE.globalEnabled = enabled,
                Text.translatable("gui.showmeyourskin.armorScreen.globalToggleTooltip",
                        KeyBindingHelper.getBoundKeyOf(ModKeyBindings.GLOBAL_TOGGLE).getLocalizedText())
        );

        playerSelector.updateEntries();
    }

    private void fixChildren() {
        clearChildren();
        addDrawableChild(globalConfig);
        addDrawableChild(playerSelector);
        addDrawableChild(backButton);
        addDrawableChild(globalToggle);
        addDrawableChild(armorConfigWindow);
    }

    private void loadArmorConfigWindow(ConfigEntryWidget entry) {
        armorConfigWindow = new ArmorConfigWindow(
                this, getSelectorLeft(), Math.max(getSelectorTop() + 100, (height - 160) / 2),
                entry.getName(), entry.getDummyPlayer(), entry.getArmorConfig()
        );

        fixChildren();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        var hovered = playerSelector.getHovered(mouseX, mouseY);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(
                matrices, hovered == null ? new TranslatableText("gui.showmeyourskin.armorScreen.playerSelector") : hovered.getName(),
                getSelectorLeft() + 11, getSelectorTop() + 52, TEXT_COLOR
        );

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        matrices.push();
        matrices.translate(0, 0, -999);

        super.renderBackground(matrices);
        int leftSide = getSelectorLeft() + 3;
        int topSide = getSelectorTop();

        RenderSystem.setShaderTexture(0, SELECTOR_TEXTURE);

        this.drawTexture(matrices, leftSide, topSide, 1, 1, 236, 127);

        matrices.pop();
    }

    @Override
    public void close() {
        assert this.client != null;
        ModConfig.INSTANCE.save();
        this.client.setScreen(this.parent);
    }
}
