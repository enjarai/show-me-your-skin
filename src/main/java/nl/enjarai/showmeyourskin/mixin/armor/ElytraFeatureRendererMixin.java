package nl.enjarai.showmeyourskin.mixin.armor;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin<S extends BipedEntityRenderState, M extends EntityModel<S>> {
    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
            at = @At("HEAD")
    )
    private void setArmorContext(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                 int i, S bipedEntityRenderState, float f, float g, CallbackInfo ci) {
        var ctx = MixinContext.ENTITY.getContext();

        if (ctx != null) {
            MixinContext.ARMOR.setContext(new ArmorContext(HideableEquipment.ELYTRA, ctx));
        }
    }
}
