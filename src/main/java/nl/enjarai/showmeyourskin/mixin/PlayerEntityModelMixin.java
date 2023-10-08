package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.CombatLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin<T extends LivingEntity> {
    @Shadow @Final private ModelPart cloak;

    @Inject(
            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At(value = "TAIL")
    )
    private void showmeyourskin$fixCapeAngle(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        var applicable = ModConfig.INSTANCE.getApplicable(livingEntity.getUuid());
        if (livingEntity instanceof PlayerEntity player &&
                !applicable.shouldTransformCape(player) &&
                !CombatLogger.INSTANCE.isInCombat(livingEntity.getUuid())) {
            if (livingEntity.isInSneakingPose()) {
                cloak.pivotZ = 1.4F;
                cloak.pivotY = 1.85F;
            } else {
                cloak.pivotZ = 0.0F;
                cloak.pivotY = 0.0F;
            }
        }
    }
}
