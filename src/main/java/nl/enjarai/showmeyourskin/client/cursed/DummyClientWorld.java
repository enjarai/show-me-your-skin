package nl.enjarai.showmeyourskin.client.cursed;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionTypes;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(
                DummyClientPlayNetworkHandler.getInstance(),
                new Properties(Difficulty.EASY, false, true),
                RegistryKey.of(Registry.WORLD_KEY, ShowMeYourSkin.id("dummy")),
                BuiltinRegistries.DIMENSION_TYPE.getEntry(DimensionTypes.OVERWORLD)
                        .orElseThrow(() -> new IllegalStateException("Wait what? Who deleted the overworld?")),
                0,
                0,
                () -> null,
                null,
                false,
                0L
        );
    }
}
