package nl.enjarai.showmeyourskin.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;

public record ArmorContext(HideableEquipment slot, LivingEntity entity) {

    public boolean shouldModify() {
        return entity() instanceof PlayerEntity;
    }

    public float getApplicablePieceTransparency() {
        return ModConfig.INSTANCE.getApplicablePieceTransparency(entity().getUuid(), slot());
    }

    public float getApplicableTrimTransparency() {
        return ModConfig.INSTANCE.getApplicableTrimTransparency(entity().getUuid(), slot().toSlot());
    }

    public float getApplicableGlintTransparency() {
        return ModConfig.INSTANCE.getApplicableGlintTransparency(entity().getUuid(), slot());
    }
}
