package nl.enjarai.showmeyourskin.config;

import net.minecraft.entity.EquipmentSlot;

import java.util.HashMap;

public class ArmorConfig {
    public static final ArmorConfig VANILLA_VALUES = new ArmorConfig();

    public final HashMap<EquipmentSlot, ArmorPieceConfig> pieces = new HashMap<>();
    public boolean showInCombat = true;

    public ArmorConfig() {
        pieces.put(EquipmentSlot.HEAD, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.CHEST, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.LEGS, new ArmorPieceConfig());
        pieces.put(EquipmentSlot.FEET, new ArmorPieceConfig());
    }

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

    public static class ArmorPieceConfig {
        public byte transparency = 100;
        public boolean enchantGlint = true;
    }
}
