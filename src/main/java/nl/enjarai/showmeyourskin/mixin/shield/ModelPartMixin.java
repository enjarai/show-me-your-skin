package nl.enjarai.showmeyourskin.mixin.shield;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ModelPart.class)
public class ModelPartMixin {
    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
            ),
            index = 4
    )
    private int modifyAlphaForShield(int color) {
        var ctx = MixinContext.ENTITY.getContext();
        var needRender = MixinContext.SHIELD_PLATE_RENDER.getAndClearContext();

        if (ctx != null && needRender) {
            float t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);
            if (t < 1) {
                return ColorHelper.withAlpha(ColorHelper.channelFromFloat(t), -1);

            }
        }
        return color;
    }
}
