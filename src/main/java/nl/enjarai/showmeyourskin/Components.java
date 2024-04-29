package nl.enjarai.showmeyourskin;

import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.util.ArmorConfigComponent;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

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
