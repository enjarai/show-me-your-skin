package nl.enjarai.showmeyourskin.gui;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.widget.ArmorConfigWindow;
import nl.enjarai.showmeyourskin.gui.widget.PressButtonWidget;
import nl.enjarai.showmeyourskin.gui.widget.ToggleButtonWidget;

public class ServerIntegratedConfigScreen extends ConfigScreen {
    protected static final ButtonTextures OVERRIDES_ENABLED_BUTTON_TEXTURES = ToggleButtonWidget.createTextures("enable_local_overrides");
    protected static final ButtonTextures OVERRIDES_CONFIGURE_BUTTON_TEXTURES = PressButtonWidget.createTextures("edit_local_overrides");

    private ToggleButtonWidget overridesEnabledButton;
    private PressButtonWidget overridesConfigureButton;

    public ServerIntegratedConfigScreen(Screen parent) {
        super(parent, Text.translatable("gui.showmeyourskin.armorScreen.title.serverIntegrated"));
    }

    @Override
    protected void init() {
        super.init();

        // When the server is available, we don't show options for any other players by default
        assert client != null;
        var player = client.player;
        assert player != null;
        var config = player.getComponent(Components.ARMOR_CONFIG).getConfig();

        var dummyPlayer = new DummyClientPlayerEntity(
                player, player.getUuid(), player.getSkinTextures(),
                client.world, client.getNetworkHandler()
        );

        overridesEnabledButton = new ToggleButtonWidget(
                getWindowLeft() + 54, getWindowTop() - 22, 20, 20,
                OVERRIDES_ENABLED_BUTTON_TEXTURES, ModConfig.INSTANCE.overridesEnabledInServerMode,
                (btn, enabled) -> {
                    ModConfig.INSTANCE.overridesEnabledInServerMode = enabled;
                    fixChildren();
                },
                Text.translatable("gui.showmeyourskin.armorScreen.overridesEnabled")
        );
        overridesConfigureButton = new PressButtonWidget(
                getWindowLeft() + 78, getWindowTop() - 22, 20, 20,
                OVERRIDES_CONFIGURE_BUTTON_TEXTURES,
                button -> {
                    if (client != null) {
                        client.setScreen(new ServerOverridesConfigScreen(ServerIntegratedConfigScreen.this));
                    }
                }, Text.translatable("gui.showmeyourskin.armorScreen.overridesConfigure")
        );

        loadArmorConfigWindow(new ArmorConfigWindow(
                this, getWindowLeft(), getWindowTop(),
                Text.translatable("gui.showmeyourskin.armorScreen.synced"),
                dummyPlayer, config.copy(), 0, false
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void fixChildren() {
        super.fixChildren();
        addDrawableChild(overridesEnabledButton);
        if (ModConfig.INSTANCE.overridesEnabledInServerMode) {
            addDrawableChild(overridesConfigureButton);
        }
    }

    @Override
    public void tick() {
        if (client != null && client.player != null) {
            // Sync config to server when it changes
            var player = client.player;
            var remoteConfig = player.getComponent(Components.ARMOR_CONFIG).getConfig();
            var config = armorConfigWindow.getArmorConfig();
            if (!remoteConfig.equals(config)) {
                ShowMeYourSkinClient.syncToServer(config);
            }
        }
    }

    @Override
    public void close() {
        super.close();
        ShowMeYourSkinClient.syncToServer(armorConfigWindow.getArmorConfig());
    }
}
