package nl.enjarai.showmeyourskin.config;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorConfig {
    public static final ArmorConfig VANILLA_VALUES = new ArmorConfig();

    public final HashMap<HideableEquipment, ArmorPieceConfig> pieces = new HashMap<>();
    public final HashMap<EquipmentSlot, ArmorPieceConfig> trims = new HashMap<>();
    public final HashMap<HideableEquipment, ArmorPieceConfig> glints = new HashMap<>();
    public boolean showInCombat = true;
    public boolean showNameTag = true;

    public ArmorConfig() {
        pieces.put(HideableEquipment.HEAD, new ArmorPieceConfig());
        pieces.put(HideableEquipment.CHEST, new ArmorPieceConfig());
        pieces.put(HideableEquipment.LEGS, new ArmorPieceConfig());
        pieces.put(HideableEquipment.FEET, new ArmorPieceConfig());
        pieces.put(HideableEquipment.ELYTRA, new ArmorPieceConfig());
        pieces.put(HideableEquipment.SHIELD, new ArmorPieceConfig());

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

    public static class ArmorPieceConfig {
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
    }
}
