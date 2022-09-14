package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import nl.enjarai.showmeyourskin.client.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(
            method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void showmeyourskin$removeLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (
                livingEntity instanceof DummyClientPlayerEntity ||
                !ModConfig.INSTANCE.getApplicable(livingEntity.getUuid()).showNameTag
        ) cir.setReturnValue(false);
    }
}
