package nl.enjarai.showmeyourskin.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.gui.widget.ArmorConfigWindow;

public class ServerIntegratedConfigScreen extends ConfigScreen {
    public ServerIntegratedConfigScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected void init() {
        super.init();

        // When the server is available, we don't show options for any other players
        assert client != null;
        var player = client.player;
        assert player != null;
        var config = player.getComponent(Components.ARMOR_CONFIG).getConfig();

        var dummyPlayer = new DummyClientPlayerEntity(
                player, player.getUuid(), player.getSkinTexture(), player.getModel(),
                client.world, client.getNetworkHandler()
        );

        loadArmorConfigWindow(new ArmorConfigWindow(
                this, getWindowLeft(), getWindowTop(),
                Text.translatable("gui.showmeyourskin.armorScreen.global"),
                dummyPlayer, config.copy(), 0
        ));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, delta);
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
