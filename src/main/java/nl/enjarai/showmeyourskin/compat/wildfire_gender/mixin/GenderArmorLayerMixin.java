package nl.enjarai.showmeyourskin.compat.wildfire_gender.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.client.ModRenderLayers;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.wildfire.render.GenderArmorLayer")
public abstract class GenderArmorLayerMixin {
    @Dynamic
    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void showmeyourskin$cancelBreastArmorRendering(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                                           int light, LivingEntity entity, float limbAngle, float limbDistance,
                                                           float partialTicks, float animationProgress, float headYaw, float headPitch,
                                                           CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            if (ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST) <= 0) {
                ci.cancel();
            } else {
                MixinContext.ARMOR.setContext(new ArmorContext(HideableEquipment.CHEST, player));
            }
        }
    }

    @Dynamic
    @ModifyExpressionValue(
            method = "renderBreastArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"
            )
    )
    private boolean showmeyourskin$modifyBreastArmorGlint(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            return original && ModConfig.INSTANCE.getApplicableGlintTransparency(player.getUuid(), HideableEquipment.CHEST) > 0;
        }
        return original;
    }

    @Dynamic
    @WrapOperation(
            method = "renderBreastArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private VertexConsumer showmeyourskin$enableBreastArmorTransparency1(VertexConsumerProvider vertexConsumerProvider,
                                                                         RenderLayer renderLayer, boolean solid,
                                                                         boolean hasGlint, Operation<VertexConsumer> original,
                                                                         @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST);
            if (transparency < 1) {
                return ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, renderLayer, solid, hasGlint);
            }
        }

        return original.call(vertexConsumerProvider, renderLayer, solid, hasGlint);
    }

    @Dynamic
    @WrapOperation(
            method = "renderBreastArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer showmeyourskin$enableBreastArmorTransparency2(Identifier texture, Operation<RenderLayer> original,
                                                                      @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST);
            if (transparency < 1) {
                return RenderLayer.getEntityTranslucent(texture);
            }
        }

        return original.call(texture);
    }

    @Dynamic
    @ModifyArg(
            method = "renderBreastArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderArmorLayer;renderBox(Lcom/wildfire/render/WildfireModelRenderer$ModelBox;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 8
    )
    private float showmeyourskin$applyBreastArmorTransparency(float original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST);
            if (transparency < 1) {
                return transparency;
            }
        }

        return original;
    }

    @Dynamic
    @ModifyArg(
            method = "renderArmorTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/Sprite;getTextureSpecificVertexConsumer(Lnet/minecraft/client/render/VertexConsumer;)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private VertexConsumer showmeyourskin$enableBreastArmorTrimTransparency(VertexConsumer original, @Local(argsOnly = true) VertexConsumerProvider vertexConsumers) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicableTrimTransparency();

            if (t < 1) {
                return vertexConsumers.getBuffer(ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE));
            }
        }

        return original;
    }

    @Dynamic
    @ModifyArg(
            method = "renderArmorTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderArmorLayer;renderBox(Lcom/wildfire/render/WildfireModelRenderer$ModelBox;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 8
    )
    private float showmeyourskin$applyBreastArmorTrimTransparency(float original) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicableTrimTransparency();

            if (t < 1) {
                return t;
            }
        }

        return original;
    }
}
