package nl.enjarai.showmeyourskin.config;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.UUID;

public class ArmorConfig {
    public static final ArmorConfig VANILLA_VALUES = new ArmorConfig();

    public final HashMap<EquipmentSlot, ArmorPieceConfig> pieces = new HashMap<>();
    public boolean showInCombat = true;
    public boolean showNameTag = true;
    public boolean showElytra = true;
    public boolean showShieldGlint = true;

    public ArmorConfig() {
        pieces.put(EquipmentSlot.HEAD, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.CHEST, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.LEGS, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.FEET, new ArmorPieceConfig());
    }

    /**
     * Use only for modifying values, when applying the transparency,
     * use {@link ModConfig#getApplicableTransparency(UUID, EquipmentSlot)}
     */
    public byte getTransparency(EquipmentSlot slot) {
        return pieces.get(slot).transparency;
    }

    public void setTransparency(EquipmentSlot slot, byte transparency) {
        pieces.get(slot).transparency = transparency;
    }

    public boolean getGlint(EquipmentSlot slot) {
        return pieces.get(slot).enchantGlint;
    }

    public void setGlint(EquipmentSlot slot, boolean glint) {
        pieces.get(slot).enchantGlint = glint;
    }

    public boolean shouldTransformCape(PlayerEntity player) {
        return getTransparency(EquipmentSlot.CHEST) > 0 && !(player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && !showElytra);
    }

    public static class ArmorPieceConfig {
        public byte transparency = 100;
        public boolean enchantGlint = true;
    }
}
