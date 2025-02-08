package nl.enjarai.showmeyourskin.mixin.armor;

import net.minecraft.client.model.Model;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Model.class)
public class ModelMixin {

    // The alternative to this is to @Redirect the render() on EquipmentRenderer trim part
    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
            ),
            index = 4
    )
    private int modifyAlphaForTrims(int color) {
        var ctx = MixinContext.ARMOR.getContext();
        var needRender = MixinContext.TRIM_RENDER.getAndClearContext();

        if (ctx != null && needRender != null && needRender && ctx.shouldModify()) {
            float t = ctx.getApplicableTrimTransparency();
            if (t < 1) {
                return ColorHelper.withAlpha(ColorHelper.channelFromFloat(t), color);
            }
        }
        return color;
    }
}
