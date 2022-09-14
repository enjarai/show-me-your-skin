package nl.enjarai.showmeyourskin.mixin.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorRenderer.class)
public interface ArmorRendererMixin {
    @Inject(
            method = "renderPart",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void showmeyourskin$armorTransparency(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Model model, Identifier texture, CallbackInfo ci) {
        var ctx = MixinContext.ARMOR.getAndClearContext();

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicableTransparency();

            if (t < 1) {
                if (t > 0) {
                    VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(
                            vertexConsumers, RenderLayer.getEntityTranslucent(texture),
                            false, stack.hasGlint() && ctx.getApplicableGlint()
                    );
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, t);
                }

                ci.cancel();
            }
        }
    }
}
