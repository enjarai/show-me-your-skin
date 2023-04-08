package nl.enjarai.showmeyourskin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
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
    private boolean serverAvailable = false;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("gui.showmeyourskin.armorScreen.title"));
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
        serverAvailable = ShowMeYourSkinClient.HANDSHAKE_CLIENT.getConfig().isPresent() &&
                client != null && client.player != null && client.getNetworkHandler() != null;

        if (!serverAvailable) {
            // When the server either doesn't exist or doesn't have the mod,
            // load client-mode, making all players configurable
            playerSelector = new PlayerSelectorWidget(
                    client, width, height, getSelectorLeft() + 44, getSelectorTop() + 63,
                    187, this::loadConfigEntry
            );
            globalConfig = new ConfigEntryWidget(
                    client, playerSelector, getSelectorLeft() + 11, getSelectorTop() + 63,
                    Text.translatable("gui.showmeyourskin.armorScreen.global"),
                    () -> GLOBAL_ICON, () -> null
            );
            playerSelector.linkDefault(globalConfig);
        } else {
            // When the server *is* available, we don't show options for any other players
            var player = client.player;
            var config = player.getComponent(Components.ARMOR_CONFIG).getConfig();

            var dummyPlayer = new DummyClientPlayerEntity(
                    player, player.getUuid(), player.getSkinTexture(), player.getModel(),
                    client.world, client.getNetworkHandler()
            );

            loadArmorConfigWindow(new ArmorConfigWindow(
                    this, getSelectorLeft(), Math.max(getSelectorTop() + 100, (height - 160) / 2),
                    Text.translatable("gui.showmeyourskin.armorScreen.global"),
                    dummyPlayer, config.copy(), 0
            ));
        }

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

    @Override
    public void tick() {
        if (serverAvailable && client != null && client.player != null) {
            // Sync config to server when it changes and server is available
            var player = client.player;
            var remoteConfig = player.getComponent(Components.ARMOR_CONFIG).getConfig();
            var config = armorConfigWindow.getArmorConfig();
            if (!remoteConfig.equals(config)) {
                ShowMeYourSkinClient.syncToServer(config);
            }
        }
    }

    private void fixChildren() {
        clearChildren();
        addDrawableChild(globalConfig);
        addDrawableChild(playerSelector);
        addDrawableChild(backButton);
        addDrawableChild(globalToggle);
        addDrawableChild(armorConfigWindow);
    }

    private void loadConfigEntry(ConfigEntryWidget entry) {
        var tabIndex = 0;
        if (armorConfigWindow != null) {
            tabIndex = armorConfigWindow.getTabIndex();
        }

        loadArmorConfigWindow(new ArmorConfigWindow(
                this, getSelectorLeft(), Math.max(getSelectorTop() + 100, (height - 160) / 2),
                entry.getName(), entry.getDummyPlayer(), entry.getArmorConfig(), tabIndex
        ));
    }

    private void loadArmorConfigWindow(ArmorConfigWindow window) {
        armorConfigWindow = window;

        fixChildren();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        var hovered = playerSelector.getHovered(mouseX, mouseY);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(
                matrices, hovered == null ? Text.translatable("gui.showmeyourskin.armorScreen.playerSelector") : hovered.getName(),
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

        if (!serverAvailable) {
            ModConfig.INSTANCE.save();
        } else {
            ShowMeYourSkinClient.syncToServer(armorConfigWindow.getArmorConfig());
        }

        this.client.setScreen(this.parent);
    }
}
