package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.IWishMixinAllowedForPublicStaticFields;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {
    
    @Inject(
            method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void showmeyourskin$removeLabel(T livingEntity, double d, CallbackInfoReturnable<Boolean> cir) {
        if (
                livingEntity instanceof DummyClientPlayerEntity ||
                !ModConfig.INSTANCE.getApplicable(livingEntity.getUuid()).showNameTag
        ) cir.setReturnValue(false);
    }
    
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At("HEAD")
    )
    private static <T extends LivingEntity, S extends LivingEntityRenderState> void showmeyourskin$captureEntity(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci){
        var entity = ShowMeYourSkinClient.ENTITY_RENDER_STATE_KEY.get(livingEntityRenderState);
        if (entity != null) {
            IWishMixinAllowedForPublicStaticFields.currentEntity = entity;
        }
    }

    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void showmeyourskin$updateRenderState(LivingEntity livingEntity, LivingEntityRenderState livingEntityRenderState, float f, CallbackInfo ci) {
        ShowMeYourSkinClient.ENTITY_RENDER_STATE_KEY.put(livingEntityRenderState, livingEntity);
    }
}
