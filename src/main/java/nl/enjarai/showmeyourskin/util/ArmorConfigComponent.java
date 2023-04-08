package nl.enjarai.showmeyourskin.util;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfigServer;
import nl.enjarai.showmeyourskin.net.HandshakeServer;
import org.jetbrains.annotations.NotNull;

public class ArmorConfigComponent implements ComponentV3, AutoSyncedComponent {
    private ArmorConfig config;

    public ArmorConfigComponent(ArmorConfig config) {
        this.config = config;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        ArmorConfig.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("config")).result().ifPresent(pair -> config = pair.getFirst());
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        ArmorConfig.CODEC.encodeStart(NbtOps.INSTANCE, config).result().ifPresent(nbt -> tag.put("config", nbt));
    }

    public ArmorConfig getConfig() {
        return config;
    }

    public void setConfig(ArmorConfig config) {
        this.config = config;

        ensureValid();
        sync();
    }

    public void setFromNbt(NbtCompound tag) {
        readFromNbt(tag);

        ensureValid();
        sync();
    }

    public void ensureValid() {
        if (!SyncedModConfigServer.INSTANCE.allowNotShowInCombat()) getConfig().showInCombat = true;
        if (!SyncedModConfigServer.INSTANCE.allowNotShowNameTag()) getConfig().showNameTag = true;
    }

    public void sync() {
        Components.ARMOR_CONFIG.sync(this);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return ShowMeYourSkin.HANDSHAKE_SERVER.getHandshakeState(player) == HandshakeServer.HandshakeState.ACCEPTED;
    }
}
