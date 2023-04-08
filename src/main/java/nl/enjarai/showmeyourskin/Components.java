package nl.enjarai.showmeyourskin;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.util.ArmorConfigComponent;

public class Components implements EntityComponentInitializer {
    public static final ComponentKey<ArmorConfigComponent> ARMOR_CONFIG =
            ComponentRegistry.getOrCreate(ShowMeYourSkin.id("armor_config"), ArmorConfigComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                ARMOR_CONFIG,
                player -> new ArmorConfigComponent(ArmorConfig.VANILLA_VALUES),
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}
