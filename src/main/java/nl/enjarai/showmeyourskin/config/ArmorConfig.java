package nl.enjarai.showmeyourskin.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArmorConfig {
    public static final ArmorConfig VANILLA_VALUES = new ArmorConfig();
    public static final Codec<ArmorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("showInCombat").forGetter(config -> config.showInCombat),
            Codec.BOOL.fieldOf("showNameTag").forGetter(config -> config.showNameTag),
            Codec.BOOL.fieldOf("forceElytraWhenFlying").forGetter(config -> config.forceElytraWhenFlying),
            Codec.unboundedMap(HideableEquipment.getCodec(), ArmorPieceConfig.CODEC).fieldOf("pieces").forGetter(config -> config.pieces),
            Codec.unboundedMap(HideableEquipment.getSlotCodec(), ArmorPieceConfig.CODEC).fieldOf("trims").forGetter(config -> config.trims),
            Codec.unboundedMap(HideableEquipment.getCodec(), ArmorPieceConfig.CODEC).fieldOf("glints").forGetter(config -> config.glints)
    ).apply(instance, ArmorConfig::new));
    public static final PacketCodec<ByteBuf, ArmorConfig> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, c -> c.showInCombat,
            PacketCodecs.BOOLEAN, c -> c.showNameTag,
            PacketCodecs.BOOLEAN, c -> c.forceElytraWhenFlying,
            PacketCodecs.map(HashMap::new, HideableEquipment.getPacketCodec(), ArmorPieceConfig.PACKET_CODEC, VANILLA_VALUES.pieces.size()), ArmorConfig::getPieces,
            PacketCodecs.map(HashMap::new, HideableEquipment.getPacketSlotCodec(), ArmorPieceConfig.PACKET_CODEC, VANILLA_VALUES.trims.size()), ArmorConfig::getTrims,
            PacketCodecs.map(HashMap::new, HideableEquipment.getPacketCodec(), ArmorPieceConfig.PACKET_CODEC, VANILLA_VALUES.glints.size()), ArmorConfig::getGlints,
            ArmorConfig::new
    );

    public final HashMap<HideableEquipment, ArmorPieceConfig> pieces = new HashMap<>();
    public final HashMap<EquipmentSlot, ArmorPieceConfig> trims = new HashMap<>();
    public final HashMap<HideableEquipment, ArmorPieceConfig> glints = new HashMap<>();
    public boolean showInCombat = true;
    public boolean showNameTag = true;
    public boolean forceElytraWhenFlying = true;

    public ArmorConfig() {
        pieces.put(HideableEquipment.HEAD, new ArmorPieceConfig());
        pieces.put(HideableEquipment.CHEST, new ArmorPieceConfig());
        pieces.put(HideableEquipment.LEGS, new ArmorPieceConfig());
        pieces.put(HideableEquipment.FEET, new ArmorPieceConfig());
        pieces.put(HideableEquipment.ELYTRA, new ArmorPieceConfig());
        pieces.put(HideableEquipment.SHIELD, new ArmorPieceConfig());
        pieces.put(HideableEquipment.HAT, new ArmorPieceConfig());

        trims.put(EquipmentSlot.HEAD, new ArmorPieceConfig());
        trims.put(EquipmentSlot.CHEST, new ArmorPieceConfig());
        trims.put(EquipmentSlot.LEGS, new ArmorPieceConfig());
        trims.put(EquipmentSlot.FEET, new ArmorPieceConfig());

        glints.put(HideableEquipment.HEAD, new ArmorPieceConfig());
        glints.put(HideableEquipment.CHEST, new ArmorPieceConfig());
        glints.put(HideableEquipment.LEGS, new ArmorPieceConfig());
        glints.put(HideableEquipment.FEET, new ArmorPieceConfig());
        glints.put(HideableEquipment.ELYTRA, new ArmorPieceConfig());
        glints.put(HideableEquipment.SHIELD, new ArmorPieceConfig());
        glints.put(HideableEquipment.HAT, new ArmorPieceConfig());
    }

    public ArmorConfig(boolean showInCombat, boolean showNameTag, boolean forceElytraWhenFlying, Map<HideableEquipment, ArmorPieceConfig> pieces, Map<EquipmentSlot, ArmorPieceConfig> trims, Map<HideableEquipment, ArmorPieceConfig> glints) {
        this();
        this.showInCombat = showInCombat;
        this.showNameTag = showNameTag;
        this.forceElytraWhenFlying = forceElytraWhenFlying;
        this.pieces.putAll(pieces);
        this.trims.putAll(trims);
        this.glints.putAll(glints);
    }

    /**
     * Use only for modifying values, when applying the transparency,
     * use {@link ModConfig#getApplicablePieceTransparency(UUID, HideableEquipment)}
     */
    public Map<HideableEquipment, ArmorPieceConfig> getPieces() {
        return pieces;
    }

    /**
     * Use only for modifying values, when applying the transparency,
     * use {@link ModConfig#getApplicableTrimTransparency(UUID, EquipmentSlot)}
     */
    public Map<EquipmentSlot, ArmorPieceConfig> getTrims() {
        return trims;
    }

    /**
     * Use only for modifying values, when applying the transparency,
     * use {@link ModConfig#getApplicableGlintTransparency(UUID, HideableEquipment)}
     */
    public Map<HideableEquipment, ArmorPieceConfig> getGlints() {
        return glints;
    }

    public boolean shouldTransformCape(PlayerEntity player) {
        return (getPieces().get(HideableEquipment.CHEST).getTransparency() > 0 ||
                getTrims().get(EquipmentSlot.CHEST).getTransparency() > 0) &&
                !(player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)
                        && getPieces().get(HideableEquipment.ELYTRA).getTransparency() <= 0);
    }

    public void ensureValid() {
        for (HideableEquipment slot : HideableEquipment.values()) {
            if (!pieces.containsKey(slot)) {
                pieces.put(slot, new ArmorPieceConfig());
            }
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!trims.containsKey(slot)) {
                trims.put(slot, new ArmorPieceConfig());
            }
        }

        for (HideableEquipment slot : HideableEquipment.values()) {
            if (!glints.containsKey(slot)) {
                glints.put(slot, new ArmorPieceConfig());
            }
        }
    }

    public ArmorConfig copy() {
        return new ArmorConfig(
                showInCombat,
                showNameTag,
                forceElytraWhenFlying,
                pieces.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().copy())),
                trims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().copy())),
                glints.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().copy()))
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArmorConfig that = (ArmorConfig) o;

        if (showInCombat != that.showInCombat) return false;
        if (showNameTag != that.showNameTag) return false;
        if (forceElytraWhenFlying != that.forceElytraWhenFlying) return false;
        if (!pieces.equals(that.pieces)) return false;
        if (!trims.equals(that.trims)) return false;
        return glints.equals(that.glints);
    }

    @Override
    public int hashCode() {
        int result = pieces.hashCode();
        result = 31 * result + trims.hashCode();
        result = 31 * result + glints.hashCode();
        result = 31 * result + (showInCombat ? 1 : 0);
        result = 31 * result + (showNameTag ? 1 : 0);
        result = 31 * result + (forceElytraWhenFlying ? 1 : 0);
        return result;
    }

    public static class ArmorPieceConfig {
        public static final Codec<ArmorPieceConfig> CODEC = Codec.BYTE.fieldOf("transparency")
                .codec().xmap(ArmorPieceConfig::new, ArmorPieceConfig::getTransparency);
        public static final PacketCodec<ByteBuf, ArmorPieceConfig> PACKET_CODEC =
                PacketCodecs.BYTE.xmap(ArmorPieceConfig::new, ArmorPieceConfig::getTransparency);
        public static final ArmorPieceConfig VANILLA_VALUES = new ArmorPieceConfig();

        public byte transparency = 100;

        public ArmorPieceConfig() {
        }

        public ArmorPieceConfig(byte transparency) {
            this.transparency = transparency;
        }

        public byte getTransparency() {
            return transparency;
        }

        public void setTransparency(byte transparency) {
            this.transparency = transparency;
        }

        public ArmorPieceConfig copy() {
            return new ArmorPieceConfig(transparency);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArmorPieceConfig that = (ArmorPieceConfig) o;

            return transparency == that.transparency;
        }

        @Override
        public int hashCode() {
            return transparency;
        }
    }
}
