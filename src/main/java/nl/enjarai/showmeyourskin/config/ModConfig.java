package nl.enjarai.showmeyourskin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.client.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.gui.ArmorScreen;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class ModConfig {
    private static final UUID LOCAL_UUID = UUID.nameUUIDFromBytes(new byte[]{});
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // Makes the json use new lines instead of being a "one-liner"
            .serializeNulls() // Makes fields with `null` value to be written as well.
            .disableHtmlEscaping() // We'll be able to use custom chars without them being saved differently
            .create();

    public final ArmorConfig global = new ArmorConfig();
    public final HashMap<UUID, ArmorConfig> overrides = new HashMap<>();


    public ArmorConfig getApplicable(UUID uuid) {
        return overrides.getOrDefault(uuid, global);
    }

    public ArmorConfig getOrCreateOverride(UUID uuid) {
        var armorConfig = overrides.get(uuid);

        if (armorConfig == null) {
            armorConfig = overrides.put(uuid, new ArmorConfig());
        }

        return armorConfig;
    }

    public ArmorScreen getScreen(@Nullable PlayerEntity player, Screen parent) {
        player = player == null ? DummyClientPlayerEntity.getInstance() : player;
        return new ArmorScreen(player, getOrCreateOverride(player.getUuid()), parent);
    }

    public ArmorScreen getScreen(Screen parent) {
        return getScreen(null, parent);
    }

    public ArmorScreen getGlobalScreen(@Nullable PlayerEntity player, Screen parent) {
        player = player == null ? DummyClientPlayerEntity.getInstance() : player;
        return new ArmorScreen(player, global, parent);
    }


    /**
     * Loads config file.
     *
     * @param file file to load the config file from.
     * @return ConfigManager object
     */
    public static ModConfig loadConfigFile(File file) {
        ModConfig config = null;

        if (file.exists()) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = GSON.fromJson(fileReader, ModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("[MultiChat] Problem occurred when trying to load config: ", e);
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
    public void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
