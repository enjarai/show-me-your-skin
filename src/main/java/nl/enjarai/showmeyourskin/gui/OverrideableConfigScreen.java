package nl.enjarai.showmeyourskin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.widget.ConfigEntryWidget;
import nl.enjarai.showmeyourskin.gui.widget.PlayerSelectorWidget;

public abstract class OverrideableConfigScreen extends ConfigScreen {
    public static final Identifier SELECTOR_TEXTURE = ShowMeYourSkin.id("textures/gui/config_screen.png");
    public static final Identifier GLOBAL_ICON = ShowMeYourSkin.id("textures/gui/global_icon.png");

    private ConfigEntryWidget globalConfig;
    private PlayerSelectorWidget playerSelector;

    public OverrideableConfigScreen(Screen parent, Text title) {
        super(parent, title);
    }

    @Override
    protected void init() {
        super.init();

        // When the server either doesn't exist or doesn't have the mod, all players will be configurable
        playerSelector = new PlayerSelectorWidget(
                client, width, height, getWindowLeft() + 44, getSelectorTop() + 63,
                187, this::loadConfigEntry
        );
        globalConfig = new ConfigEntryWidget(
                client, playerSelector, getWindowLeft() + 11, getSelectorTop() + 63,
                Text.translatable("gui.showmeyourskin.armorScreen.global"),
                () -> OverrideableConfigScreen.GLOBAL_ICON, () -> null
        );
        playerSelector.linkDefault(globalConfig);
        playerSelector.updateEntries();
    }

    @Override
    protected void renderBackgroundTextures(DrawContext context) {
        int leftSide = getWindowLeft() + 3;
        int topSide = getSelectorTop();

        context.drawTexture(OverrideableConfigScreen.SELECTOR_TEXTURE, leftSide, topSide, 1, 1, 236, 127);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        var hovered = playerSelector.getHovered(mouseX, mouseY);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawText(
                textRenderer, hovered == null ? title : hovered.getName(),
                getWindowLeft() + 11, getSelectorTop() + 52, TEXT_COLOR, true
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void fixChildren() {
        super.fixChildren();
        addDrawableChild(globalConfig);
        addDrawableChild(playerSelector);
    }

    protected int getSelectorTop() {
        return -49;
    }

    @Override
    protected int getWindowTop() {
        return Math.max(getSelectorTop() + 100, super.getWindowTop());
    }

    @Override
    protected int getBackButtonX() {
        return getWindowLeft() - 20;
    }

    @Override
    protected int getBackButtonY() {
        return getSelectorTop() + 52;
    }

    @Override
    protected int getGlobalToggleX() {
        return getWindowLeft() - 20;
    }

    @Override
    protected int getGlobalToggleY() {
        return getSelectorTop() + 76;
    }

    @Override
    public void close() {
        super.close();
        ModConfig.INSTANCE.save();
    }
}
