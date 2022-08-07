package nl.enjarai.showmeyourskin.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;

public class ArmorContext {
    private final EquipmentSlot slot;
    private final LivingEntity entity;

    public ArmorContext(EquipmentSlot slot, LivingEntity entity) {
        this.slot = slot;
        this.entity = entity;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public boolean shouldModify() {
        return getEntity() instanceof PlayerEntity;
    }

    public float getApplicableTransparency() {
        return ModConfig.INSTANCE.getApplicableTransparency(getEntity().getUuid(), getSlot());
    }

    public boolean getApplicableGlint() {
        return ModConfig.INSTANCE.getApplicable(getEntity().getUuid()).getGlint(getSlot());
    }
}
