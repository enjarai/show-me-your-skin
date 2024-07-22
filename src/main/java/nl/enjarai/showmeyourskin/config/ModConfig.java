package nl.enjarai.showmeyourskin.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.MathHelper;
import nl.enjarai.cicada.api.util.AbstractModConfig;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.util.CombatLogger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class ModConfig extends AbstractModConfig {
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(ShowMeYourSkin.MODID + ".json");
    public static ModConfig INSTANCE = loadConfigFile(CONFIG_FILE, new ModConfig());

    public boolean globalEnabled = true;
    public boolean overridesEnabledInServerMode = false;
    public float combatCooldown = 16;
    public float fadeOutTime = 2;
    public final ArmorConfig global = new ArmorConfig();
    public final HashMap<UUID, ArmorConfig> overrides = new HashMap<>();


    public ArmorConfig getOverrideOrGlobal(UUID uuid) {
        var client = MinecraftClient.getInstance();

        boolean serverAvailable = ShowMeYourSkinClient.HANDSHAKE_CLIENT.getConfig().isPresent() && client.world != null;
        boolean useClientValues = overridesEnabledInServerMode || !serverAvailable;

        if (useClientValues) {
            var config = overrides.get(uuid);

            if (config != null) {
                return config;
            }
        }

        if (serverAvailable) {
            var player = client.world.getPlayerByUuid(uuid);

            if (player != null) {
                var config = player.getComponent(Components.ARMOR_CONFIG).getConfig();

                if (!config.equals(ArmorConfig.VANILLA_VALUES)) {
                    return config;
                }
            }
        }

        if (useClientValues) {
            return global;
        }

        return ArmorConfig.VANILLA_VALUES;
    }

    public ArmorConfig getApplicable(UUID uuid) {
        var applicable = getOverrideOrGlobal(uuid);
        return !globalEnabled || applicable.showInCombat && CombatLogger.INSTANCE.isInCombat(uuid) ? ArmorConfig.VANILLA_VALUES : applicable;
    }

    public float getApplicablePieceTransparency(UUID uuid, HideableEquipment slot) {
        return getApplicableTransparency(uuid, applicable -> applicable.getPieces()
                .getOrDefault(slot, ArmorConfig.ArmorPieceConfig.VANILLA_VALUES));
    }

    public float getApplicableTrimTransparency(UUID uuid, EquipmentSlot slot) {
        return getApplicableTransparency(uuid, applicable -> applicable.getTrims().get(slot));
    }

    public float getApplicableGlintTransparency(UUID uuid, HideableEquipment slot) {
        return getApplicableTransparency(uuid, applicable -> applicable.getGlints()
                .getOrDefault(slot, ArmorConfig.ArmorPieceConfig.VANILLA_VALUES));
    }

    private float getApplicableTransparency(UUID uuid, Function<ArmorConfig, ArmorConfig.ArmorPieceConfig> configRetriever) {
        if (!globalEnabled) {
            return configRetriever.apply(ArmorConfig.VANILLA_VALUES).getTransparency() / 100f;
        }

        var applicable = getOverrideOrGlobal(uuid);
        var normal = configRetriever.apply(applicable).getTransparency() / 100f;

        // Only apply modifications if enabled and player is in combat.
        if (applicable.showInCombat && CombatLogger.INSTANCE.isInCombat(uuid)) {
            return MathHelper.clampedLerp(normal, 1f, CombatLogger.INSTANCE.getFade(uuid));
        }
        return normal;
    }

    public ArmorConfig getOverride(UUID uuid) {
        return overrides.get(uuid);
    }

    public ArmorConfig getOrCreateOverride(UUID uuid) {
        var armorConfig = getOverride(uuid);

        if (armorConfig == null) {
            armorConfig = new ArmorConfig();
            overrides.put(uuid, armorConfig);
        }

        return armorConfig;
    }

    public void deleteOverride(UUID uuid) {
        overrides.remove(uuid);
    }

    public void ensureValid() {
        if (combatCooldown < 0) {
            combatCooldown = 0;
        }
        if (fadeOutTime < 0) {
            fadeOutTime = 0;
        }
        if (fadeOutTime > combatCooldown) {
            fadeOutTime = combatCooldown;
        }

        global.ensureValid();
        overrides.values().forEach(ArmorConfig::ensureValid);

        save();
    }
}
