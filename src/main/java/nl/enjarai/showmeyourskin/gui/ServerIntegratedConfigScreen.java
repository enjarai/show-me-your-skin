package nl.enjarai.showmeyourskin.gui;

import dev.lambdaurora.spruceui.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.widget.ArmorConfigWindow;
import nl.enjarai.showmeyourskin.gui.widget.IconPressButtonWidget;
import nl.enjarai.showmeyourskin.gui.widget.IconToggleButtonWidget;

public class ServerIntegratedConfigScreen extends ConfigScreen {
    private IconToggleButtonWidget overridesEnabledButton;
    private IconPressButtonWidget overridesConfigureButton;

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
                player, player.getUuid(), player.getSkinTexture(), player.getModel(),
                client.world, client.getNetworkHandler()
        );

        overridesEnabledButton = new IconToggleButtonWidget(
                getOverridesEnabledPos(), ShowMeYourSkin.id("textures/gui/button/enable_local_overrides.png"),
                0, 0, ModConfig.INSTANCE.overridesEnabledInServerMode
        );
        overridesEnabledButton.setCallback((btn, enabled) -> {
            ModConfig.INSTANCE.overridesEnabledInServerMode = enabled;
            overridesConfigureButton.setVisible(enabled);
        });

        overridesConfigureButton = new IconPressButtonWidget(
                getOverridesConfigurePos(), ShowMeYourSkin.id("textures/gui/button/edit_local_overrides.png"),
                0, 0
        );
        overridesConfigureButton.setCallback(btn -> {
            if (client != null) {
                client.setScreen(new ServerOverridesConfigScreen(ServerIntegratedConfigScreen.this));
            }
        });

        addDrawableChild(overridesEnabledButton);
        addDrawableChild(overridesConfigureButton);

        loadArmorConfigWindow(new ArmorConfigWindow(
                Position.of(getWindowLeft(), getWindowTop()), this,
                Text.translatable("gui.showmeyourskin.armorScreen.synced"),
                dummyPlayer, config.copy(), 0, false
        ));
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

    protected Position getOverridesEnabledPos() {
        return Position.of(windowRelative, 54, -22);
    }

    protected Position getOverridesConfigurePos() {
        return Position.of(windowRelative, 78, -22);
    }

    @Override
    public void close() {
        super.close();
        ShowMeYourSkinClient.syncToServer(armorConfigWindow.getArmorConfig());
    }
}
