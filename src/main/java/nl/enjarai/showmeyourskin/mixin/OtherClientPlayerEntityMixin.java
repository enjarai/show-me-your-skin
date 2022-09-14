package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import nl.enjarai.showmeyourskin.util.CombatLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OtherClientPlayerEntity.class)
public abstract class OtherClientPlayerEntityMixin {
    @Inject(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At(value = "HEAD")
    )
    private void showmeyourskin$triggerCombat(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        CombatLogger.INSTANCE.triggerCombat(((OtherClientPlayerEntity) (Object) this).getUuid());
    }
}
