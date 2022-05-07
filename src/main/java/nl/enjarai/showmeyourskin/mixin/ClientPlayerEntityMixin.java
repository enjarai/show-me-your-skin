package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import nl.enjarai.showmeyourskin.util.CombatLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(
            method = "damage",
            at = @At(value = "HEAD")
    )
    private void triggerCombat(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        CombatLogger.INSTANCE.triggerCombat(((ClientPlayerEntity) (Object) this).getUuid());
    }
}
