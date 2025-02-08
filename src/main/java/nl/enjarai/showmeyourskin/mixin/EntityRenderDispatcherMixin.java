package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(
            method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At("HEAD")
    )
    private <E extends Entity, S extends EntityRenderState> void captureEntityContext(E entity, double x, double y, double z,
                                                                                      float tickDelta, MatrixStack matrices,
                                                                                      VertexConsumerProvider vertexConsumers,
                                                                                      int light, EntityRenderer<? super E, S> renderer,
                                                                                      CallbackInfo ci) {
        if(entity instanceof PlayerEntity livingEntity) {
            MixinContext.ENTITY.setContext(livingEntity);
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At("RETURN")
    )
    private <E extends Entity, S extends EntityRenderState> void clearEntityContext(E entity, double x, double y, double z,
                                                                                      float tickDelta, MatrixStack matrices,
                                                                                      VertexConsumerProvider vertexConsumers,
                                                                                      int light, EntityRenderer<? super E, S> renderer,
                                                                                      CallbackInfo ci) {
        MixinContext.ENTITY.clearContext();
    }
}
