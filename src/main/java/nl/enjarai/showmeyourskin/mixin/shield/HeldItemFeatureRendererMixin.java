package nl.enjarai.showmeyourskin.mixin.shield;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import nl.enjarai.showmeyourskin.fake.FakePlayerEntityRendererState;
import nl.enjarai.showmeyourskin.util.IWishMixinAllowedForPublicStaticFields;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin<S extends ArmedEntityRenderState, M extends EntityModel<S> & ModelWithArms> extends FeatureRenderer<S, M> {
    public HeldItemFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method="renderItem",at=@At("HEAD"))
    private void renderItem1(S entityState, ItemRenderState itemState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entityState instanceof PlayerEntityRenderState playerEntityRenderState){
            IWishMixinAllowedForPublicStaticFields.currentEntity=((FakePlayerEntityRendererState)playerEntityRenderState).show_me_your_skin$getPlayer();
        }
    }
    @Inject(method="renderItem",at=@At("RETURN"))
    private void renderItem2(S entityState, ItemRenderState itemState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        IWishMixinAllowedForPublicStaticFields.currentEntity=null;
    }
}
