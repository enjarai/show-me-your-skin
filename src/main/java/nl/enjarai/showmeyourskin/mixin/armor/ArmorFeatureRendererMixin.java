package nl.enjarai.showmeyourskin.mixin.armor;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.IWishMixinAllowedForPublicStaticFields;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorFeatureRenderer.class, priority = 500)
public abstract class ArmorFeatureRendererMixin<T extends BipedEntityRenderState, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Inject(
            method = "renderArmor",
            at = @At(value = "HEAD")
    )
    private void setArmorContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack,
                                 EquipmentSlot slot, int light, A armorModel, CallbackInfo ci) {
        if (IWishMixinAllowedForPublicStaticFields.currentEntity instanceof LivingEntity livingEntity) {
            IWishMixinAllowedForPublicStaticFields.currentArmorContext = new ArmorContext(HideableEquipment.fromSlot(slot), livingEntity);
        }
    }

//    @ModifyExpressionValue(
//            method = "renderArmor",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z")
//    )
//    private boolean showmeyourskin$toggleGlint(boolean original) {
//        var ctx = MixinContext.ARMOR.getContext();
//
//        if (ctx != null && ctx.shouldModify()) {
//            return original && ctx.getApplicableGlintTransparency() > 0;
//        }
//        return original;
//    }
//
//    @Inject(
//            method = "renderArmorParts",
//            at = @At(value = "HEAD"),
//            cancellable = true
//    )
//    private void showmeyourskin$armorTransparency(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model, int i, Identifier overlay, CallbackInfo ci) {
//        var ctx = MixinContext.ARMOR.getContext();
//        if (ctx == null) throw new IllegalStateException("ArmorContext is null");
//
//        // Some mod is probably up to no good. That's fine, but we should make sure to ignore it.
//        if (ctx.getSlot() == null) return;
//
//        if (ctx.getEntity().getEquippedStack(ctx.getSlot().toSlot()).get(DataComponentTypes.TRIM) != null) {
//            trimContextQueue.offer(ctx);
//        }
//
//        if (ctx.shouldModify()) {
//            var t = ctx.getApplicablePieceTransparency();
//
//            if (t < 1) {
//                if (t > 0) {
//                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
//                            ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(overlay));
//                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV,
//                            ColorHelper.Argb.withAlpha(ColorHelper.channelFromFloat(t), i)
//                    );
//                }
//
//                ci.cancel();
//            }
//        }
//    }
//
//    @Inject(
//            method = "renderTrim",
//            at = @At(value = "HEAD"),
//            cancellable = true
//    )
//    private void showmeyourskin$trimTransparency(RegistryEntry<ArmorMaterial> armorMaterial, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings, CallbackInfo ci) {
//        if (FabricLoader.getInstance().isModLoaded("allthetrims")) return;
//
//        var ctx = trimContextQueue.poll();
//
//        if (ctx != null && ctx.shouldModify()) {
//            var t = ctx.getApplicableTrimTransparency();
//
//            if (t < 1) {
//                if (t > 0) {
//                    Sprite sprite = this.armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(armorMaterial) : trim.getGenericModelId(armorMaterial));
//                    VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers
//                            .getBuffer(ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE)));
//                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, ColorHelper.Argb.fromFloats(t, 1.0f, 1.0f, 1.0f));
//                }
//
//                ci.cancel();
//            }
//        }
//    }
}
