package nl.enjarai.showmeyourskin.client;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

public class DummyClientPlayNetworkHandler extends ClientPlayNetworkHandler {

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
    private final DynamicRegistryManager cursedRegistryManager = new DynamicRegistryManager() {
        @SuppressWarnings("unchecked")
        @Override
        public <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key) {
            try {
                return Optional.of((Registry<E>) cursedBiomeRegistry);
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }

        @Override
        public Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries() {
            return null;
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
