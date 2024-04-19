package nl.enjarai.showmeyourskin.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.widget.ArmorConfigWindow;
import nl.enjarai.showmeyourskin.gui.widget.ToggleButtonWidget;

public class ServerIntegratedConfigScreen extends ConfigScreen {
    private ButtonWidget overridesEnabledButton;
    private ButtonWidget overridesConfigureButton;

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

        overridesEnabledButton = new ToggleButtonWidget(
                this, getWindowLeft() + 54, getWindowTop() - 22,
                120, 38, ArmorConfigWindow.TEXTURE, ModConfig.INSTANCE.overridesEnabledInServerMode,
                (enabled) -> {
                    ModConfig.INSTANCE.overridesEnabledInServerMode = enabled;
                    fixChildren();
                },
                Text.translatable("gui.showmeyourskin.armorScreen.overridesEnabled")
        );
        overridesConfigureButton = new ToggleButtonWidget(
                this, getWindowLeft() + 78, getWindowTop() - 22,
                20, 78, ArmorConfigWindow.TEXTURE, true,
                button -> {}, Text.translatable("gui.showmeyourskin.armorScreen.overridesConfigure")
        ) {
            @Override
            public void onPress() {
                if (client != null) {
                    client.setScreen(new ServerOverridesConfigScreen(ServerIntegratedConfigScreen.this));
                }
            }
        };

        loadArmorConfigWindow(new ArmorConfigWindow(
                this, getWindowLeft(), getWindowTop(),
                Text.translatable("gui.showmeyourskin.armorScreen.synced"),
                dummyPlayer, config.copy(), 0, false
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

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
