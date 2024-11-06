package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import nl.enjarai.showmeyourskin.util.CombatLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(
            method = "onDamaged",
            at = @At(value = "HEAD")
    )
    private void showmeyourskin$triggerCombat(DamageSource damageSource, CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity player) {
            CombatLogger.INSTANCE.triggerCombat(player.getUuid());
        }
    }
}
