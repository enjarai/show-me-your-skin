package nl.enjarai.showmeyourskin.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorFeatureRenderer.class, priority = 999)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Shadow protected abstract Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay);

    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    @Inject(
            method = "renderArmor",
            at = @At(value = "HEAD")
    )
    private void showmeyourskin$captureContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        MixinContext.ARMOR.setContext(new ArmorContext(armorSlot, entity));
    }

    @ModifyVariable(
            method = "renderArmor",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"),
            index = 10
    )
    private boolean showmeyourskin$toggleGlint(boolean original) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.getEntity() instanceof PlayerEntity) {
            return original && ctx.getApplicableGlintTransparency() > 0;
        }
        return original;
    }

    @Inject(
            method = "renderArmorParts",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void showmeyourskin$armorTransparency(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, String overlay, CallbackInfo ci) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicablePieceTransparency();
            var gt = ctx.getApplicableGlintTransparency();

            if (t < 1) {
                if (t > 0) {
                    var defaultGt = RenderSystem.getShaderGlintAlpha();
                    RenderSystem.setShaderGlintAlpha(gt);

                    VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(
                            vertexConsumers, RenderLayer.getEntityTranslucent(getArmorTexture(item, legs, overlay)),
                            false, usesSecondLayer
                    );
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, t);

                    RenderSystem.setShaderGlintAlpha(defaultGt);
                }

                ci.cancel();
            }
        }
    }

    @Inject(
            method = "renderTrim",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void showmeyourskin$trimTransparency(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, boolean glint, A model, boolean leggings, float red, float green, float blue, CallbackInfo ci) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicableTrimTransparency();

            if (t < 1) {
                if (t > 0) {
                    Sprite sprite = armorTrimsAtlas.getSprite(
                            leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
                    VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                            ItemRenderer.getDirectItemGlintConsumer(
                                    vertexConsumers, TexturedRenderLayers.getArmorTrims(), true, glint
                            )
                    );
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, t);
                }

                ci.cancel();
            }
        }
    }
}
