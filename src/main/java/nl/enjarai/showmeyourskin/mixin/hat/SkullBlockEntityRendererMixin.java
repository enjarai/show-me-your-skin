package nl.enjarai.showmeyourskin.mixin.hat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkullBlockEntityRenderer.class)
public abstract class SkullBlockEntityRendererMixin {
    @WrapOperation(
            method = "renderSkull",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private static void modifySkullTransparency(SkullBlockEntityModel instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original) {
        var wearingEntity = MixinContext.ENTITY.getContext();

        if (wearingEntity != null) {
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(wearingEntity.getUuid(), HideableEquipment.HAT);

            if (transparency < 1) {
                if (transparency > 0) {
                    // If transparency is below one but above 0, we *should* call the original but modify the alpha
                    instance.render(matrixStack, vertexConsumer, light, uv,
                            ColorHelper.withAlpha(ColorHelper.channelFromFloat(transparency), -1)
                    );
                }

                // If transparency is below 1, we potentially don't want to call the original
                return;
            }
        }

        // Continue as usual
        original.call(instance, matrixStack, vertexConsumer, light, uv);
    }

    @WrapOperation(
            method = "getRenderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutoutNoCullZOffset(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private static RenderLayer modifySkullRenderLayer(Identifier texture, Operation<RenderLayer> original) {
        var wearingEntity = MixinContext.ENTITY.getContext();

        if (wearingEntity != null) {
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(wearingEntity.getUuid(), HideableEquipment.HAT);

            if (transparency < 1) {
                // If the transparency is below 1, use a translucent render layer anyway
                return RenderLayer.getEntityTranslucent(texture);
            }
        }

        return original.call(texture);
    }
}