package nl.enjarai.showmeyourskin.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfigServer;
import nl.enjarai.showmeyourskin.net.HandshakeServer;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ArmorConfigComponent implements ComponentV3, AutoSyncedComponent {
    private ArmorConfig config;

    public ArmorConfigComponent(ArmorConfig config) {
        this.config = config;
    }

    @Override
    public void readData(ReadView readView){
        readView.read("config",ArmorConfig.CODEC).ifPresent(this::setConfig);
    }

    @Override
    public void writeData(WriteView writeView){
        writeView.put("config", ArmorConfig.CODEC, config);
    }

    public ArmorConfig getConfig() {
        return config;
    }

    public void setConfig(ArmorConfig config) {
        this.config = config;
    }

    public void ensureValid() {
        if (!SyncedModConfigServer.INSTANCE.allowNotShowInCombat()) getConfig().showInCombat = true;
        if (!SyncedModConfigServer.INSTANCE.allowNotShowNameTag()) getConfig().showNameTag = true;
        if (!SyncedModConfigServer.INSTANCE.allowNotForceElytraWhenFlying()) getConfig().forceElytraWhenFlying = true;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return ShowMeYourSkin.HANDSHAKE_SERVER.getHandshakeState(player) != HandshakeServer.HandshakeState.FAILED;
    }

    @Override
    public boolean isRequiredOnClient() {
        return false;
    }
}
