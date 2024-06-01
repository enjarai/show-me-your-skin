package nl.enjarai.showmeyourskin.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface SyncedModConfig {
    Codec<SyncedModConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("allowNotShowInCombat", true).forGetter(SyncedModConfig::allowNotShowInCombat),
            Codec.BOOL.optionalFieldOf("allowNotShowNameTag", true).forGetter(SyncedModConfig::allowNotShowNameTag),
            Codec.BOOL.optionalFieldOf("allowNotForceElytraWhenFlying", true).forGetter(SyncedModConfig::allowNotForceElytraWhenFlying)
    ).apply(instance, SyncedModConfigClient::new));
    PacketCodec<RegistryByteBuf, SyncedModConfig> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, SyncedModConfig::allowNotShowInCombat,
            PacketCodecs.BOOL, SyncedModConfig::allowNotShowNameTag,
            PacketCodecs.BOOL, SyncedModConfig::allowNotForceElytraWhenFlying,
            SyncedModConfigClient::new
    );

    boolean allowNotShowInCombat();

    boolean allowNotShowNameTag();

    boolean allowNotForceElytraWhenFlying();
}
