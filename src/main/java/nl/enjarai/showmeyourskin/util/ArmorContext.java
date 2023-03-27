package nl.enjarai.showmeyourskin.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;

public class ArmorContext {
    private final HideableEquipment slot;
    private final LivingEntity entity;

    public ArmorContext(HideableEquipment slot, LivingEntity entity) {
        this.slot = slot;
        this.entity = entity;
    }

    public HideableEquipment getSlot() {
        return slot;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public boolean shouldModify() {
        return getEntity() instanceof PlayerEntity;
    }

    public float getApplicablePieceTransparency() {
        return ModConfig.INSTANCE.getApplicablePieceTransparency(getEntity().getUuid(), getSlot());
    }

    public float getApplicableTrimTransparency() {
        return ModConfig.INSTANCE.getApplicableTrimTransparency(getEntity().getUuid(), getSlot().toSlot());
    }

    public float getApplicableGlintTransparency() {
        return ModConfig.INSTANCE.getApplicableGlintTransparency(getEntity().getUuid(), getSlot());
    }
}
