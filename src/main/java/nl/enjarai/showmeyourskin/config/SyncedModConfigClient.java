package nl.enjarai.showmeyourskin.config;

public record SyncedModConfigClient(boolean allowNotShowInCombat, boolean allowNotShowNameTag, boolean allowNotForceElytraWhenFlying) implements SyncedModConfig {
}
