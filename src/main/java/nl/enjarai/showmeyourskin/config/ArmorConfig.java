package nl.enjarai.showmeyourskin.config;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorConfig {
    public static final ArmorConfig VANILLA_VALUES = new ArmorConfig();

    public final HashMap<EquipmentSlot, ArmorPieceConfig> pieces = new HashMap<>();
    public final HashMap<EquipmentSlot, ArmorPieceConfig> trims = new HashMap<>();
    public final HashMap<EquipmentSlot, ArmorPieceConfig> glints = new HashMap<>();
    public boolean showInCombat = true;
    public boolean showNameTag = true;
    public boolean showElytra = true;
    public boolean showShieldGlint = true;

    public ArmorConfig() {
        pieces.put(EquipmentSlot.HEAD, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.CHEST, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.LEGS, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.FEET, new ArmorPieceConfig());

        trims.put(EquipmentSlot.HEAD, new ArmorPieceConfig());
        trims.put(EquipmentSlot.CHEST, new ArmorPieceConfig());
        trims.put(EquipmentSlot.LEGS, new ArmorPieceConfig());
        trims.put(EquipmentSlot.FEET, new ArmorPieceConfig());

        glints.put(EquipmentSlot.HEAD, new ArmorPieceConfig((byte) 75));
        glints.put(EquipmentSlot.CHEST, new ArmorPieceConfig((byte) 75));
        glints.put(EquipmentSlot.LEGS, new ArmorPieceConfig((byte) 75));
        glints.put(EquipmentSlot.FEET, new ArmorPieceConfig((byte) 75));
    }

    /**
     * Use only for modifying values, when applying the transparency,
     * use {@link ModConfig#getApplicablePieceTransparency(UUID, EquipmentSlot)}
     */
    public Map<EquipmentSlot, ArmorPieceConfig> getPieces() {
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
     * use {@link ModConfig#getApplicableGlintTransparency(UUID, EquipmentSlot)}
     */
    public Map<EquipmentSlot, ArmorPieceConfig> getGlints() {
        return glints;
    }

    public boolean shouldTransformCape(PlayerEntity player) {
        return (getPieces().get(EquipmentSlot.CHEST).getTransparency() > 0 ||
                getTrims().get(EquipmentSlot.CHEST).getTransparency() > 0) &&
                !(player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && !showElytra);
    }

    public static class ArmorPieceConfig {
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
