package nl.enjarai.showmeyourskin.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfigServer;
import nl.enjarai.showmeyourskin.net.HandshakeServer;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ArmorConfigComponent implements ComponentV3, AutoSyncedComponent {
    private ArmorConfig config;

    public ArmorConfigComponent(ArmorConfig config) {
        this.config = config;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("config")) {
            ArmorConfig.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("config")).result().ifPresent(pair -> config = pair.getFirst());
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        ArmorConfig.CODEC.encodeStart(NbtOps.INSTANCE, config).result().ifPresent(nbt -> tag.put("config", nbt));
    }

    public ArmorConfig getConfig() {
        return config;
    }

    public void setConfig(ArmorConfig config) {
        this.config = config;
    }

    public void setFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        readFromNbt(tag, registryLookup);
    }

    public void ensureValid() {
        if (!SyncedModConfigServer.INSTANCE.allowNotShowInCombat()) getConfig().showInCombat = true;
        if (!SyncedModConfigServer.INSTANCE.allowNotShowNameTag()) getConfig().showNameTag = true;
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
