package nl.enjarai.showmeyourskin.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface SyncedModConfig {
    Codec<SyncedModConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("allowNotShowInCombat", true).forGetter(SyncedModConfig::allowNotShowInCombat),
            Codec.BOOL.optionalFieldOf("allowNotShowNameTag", true).forGetter(SyncedModConfig::allowNotShowNameTag)
    ).apply(instance, SyncedModConfigClient::new));

    boolean allowNotShowInCombat();

    boolean allowNotShowNameTag();
}
