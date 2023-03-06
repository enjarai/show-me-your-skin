package nl.enjarai.showmeyourskin.client.cursed;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DummyClientPlayNetworkHandler extends ClientPlayNetworkHandler {
    public final static ArmorTrimMaterial DUMMY_TRIM_MATERIAL = ArmorTrimMaterial.of(
            "gold",
            Items.GOLD_INGOT,
            0.6f,
            Text.empty(),
            Map.of()
    );
    @SuppressWarnings("deprecation")
    public final static ArmorTrimPattern DUMMY_TRIM_PATTERN = new ArmorTrimPattern(
            new Identifier("coast"),
            Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE.getRegistryEntry(),
            Text.empty()
    );

    private static DummyClientPlayNetworkHandler instance;

    public static DummyClientPlayNetworkHandler getInstance() {
        if (instance == null) instance = new DummyClientPlayNetworkHandler();
        return instance;
    }

    private final Registry<Biome> cursedBiomeRegistry = new SimpleDefaultedRegistry<>("dummy", RegistryKeys.BIOME, Lifecycle.stable(), true) {
        @Override
        public RegistryEntry.Reference<Biome> entryOf(RegistryKey<Biome> key) {
            return null;
        }
    };

    @SuppressWarnings("rawtypes")
    private final RegistryEntryOwner cursedRegistryEntryOwner = new RegistryEntryOwner() {
        @Override
        public boolean ownerEquals(RegistryEntryOwner other) {
            return true;
        }
    };

    private final RegistryWrapper.Impl<ArmorTrimMaterial> cursedTrimMaterialRegistryWrapper = new SimpleRegistry<>(RegistryKeys.TRIM_MATERIAL, Lifecycle.stable(), true) {
        @SuppressWarnings({"deprecation", "unchecked"})
        @Override
        public Optional<RegistryEntry.Reference<ArmorTrimMaterial>> getEntry(RegistryKey<ArmorTrimMaterial> key) {
            return Optional.of(RegistryEntry.Reference.intrusive(cursedRegistryEntryOwner, DUMMY_TRIM_MATERIAL));
        }
    }.getReadOnlyWrapper();

    private final RegistryWrapper.Impl<ArmorTrimPattern> cursedTrimPatternRegistryWrapper = new SimpleRegistry<>(RegistryKeys.TRIM_PATTERN, Lifecycle.stable(), true) {
        @SuppressWarnings({"deprecation", "unchecked"})
        @Override
        public Optional<RegistryEntry.Reference<ArmorTrimPattern>> getEntry(RegistryKey<ArmorTrimPattern> key) {
            return Optional.of(RegistryEntry.Reference.intrusive(cursedRegistryEntryOwner, DUMMY_TRIM_PATTERN));
        }
    }.getReadOnlyWrapper();

    private final DynamicRegistryManager cursedRegistryManager = new DynamicRegistryManager.Immutable() {
        private FakeRegistry<DamageType> damageTypes = new FakeRegistry<>(RegistryKeys.DAMAGE_TYPE, ShowMeYourSkin.id("fake_damage"),
                new DamageType("", DamageScaling.NEVER, 0));

        @Override
        public Optional<Registry> getOptional(RegistryKey key) {
            var x = Registries.REGISTRIES.get(key);
            if (x != null) {
                return Optional.of(x);
            }

            if (RegistryKeys.DAMAGE_TYPE.equals(key)) {
                return Optional.of(damageTypes);
            }

            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Optional<RegistryWrapper.Impl> getOptionalWrapper(RegistryKey registryRef) {
            try {
                return Optional.of(cursedTrimMaterialRegistryWrapper);
            } catch (ClassCastException e) {
                try {
                    return Optional.of(cursedTrimPatternRegistryWrapper);
                } catch (ClassCastException e2) {
                    return Optional.empty();
                }
            }
        }

        @Override
        public Stream<Entry<?>> streamAllRegistries() {
            return Stream.empty();
        }
    };

    private DummyClientPlayNetworkHandler() {
        super(
                MinecraftClient.getInstance(),
                null,
                new ClientConnection(NetworkSide.CLIENTBOUND),
                MinecraftClient.getInstance().getCurrentServerEntry(),
                MinecraftClient.getInstance().getSession().getProfile(),
                MinecraftClient.getInstance().getTelemetryManager().createWorldSession(true, Duration.of(0, ChronoUnit.SECONDS))
        );
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return cursedRegistryManager;
    }
}
