package nl.enjarai.showmeyourskin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.MathHelper;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.util.CombatLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class ModConfig {
    public static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir() + "/" + ShowMeYourSkin.MODID + ".json");
    public static ModConfig INSTANCE;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // Makes the json use new lines instead of being a "one-liner"
            .serializeNulls() // Makes fields with `null` value to be written as well.
            .disableHtmlEscaping() // We'll be able to use custom chars without them being saved differently
            .create();

    public boolean globalEnabled = true;
    public boolean overridesEnabledInServerMode = false;
    public float combatCooldown = 16;
    public float fadeOutTime = 2;
    public final ArmorConfig global = new ArmorConfig();
    public final HashMap<UUID, ArmorConfig> overrides = new HashMap<>();


    public ArmorConfig getOverrideOrGlobal(UUID uuid) {
        var client = MinecraftClient.getInstance();

        if (ShowMeYourSkinClient.HANDSHAKE_CLIENT.getConfig().isPresent() && client.world != null) {
            var player = client.world.getPlayerByUuid(uuid);

            return player != null ? player.getComponent(Components.ARMOR_CONFIG).getConfig() : ArmorConfig.VANILLA_VALUES;
        }

        return overrides.getOrDefault(uuid, global);
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



    public static void load() {
        INSTANCE = loadConfigFile(CONFIG_FILE);
    }

    public void save() {
        saveConfigFile(CONFIG_FILE);
    }

    /**
     * Loads config file.
     *
     * @param file file to load the config file from.
     * @return ConfigManager object
     */
    private static ModConfig loadConfigFile(File file) {
        ModConfig config = null;

        if (file.exists()) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = GSON.fromJson(fileReader, ModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Problem occurred when trying to load config: ", e);
            }
        }
        // gson.fromJson() can return null if file is empty
        if (config == null) {
            config = new ModConfig();
        }

        // Saves the file in order to write new fields if they were added
        config.saveConfigFile(file);
        return config;
    }

    /**
     * Saves the config to the given file.
     *
     * @param file file to save config to
     */
    private void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
