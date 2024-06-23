package nl.enjarai.showmeyourskin.mixin.elytra;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraFeatureRenderer.class)
public abstract class ElytraFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public ElytraFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void showmeyourskin$hideElytra(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f,
            float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
            if (!player.isFallFlying() || !ModConfig.INSTANCE.getApplicable(player.getUuid()).forceElytraWhenFlying) {
                if (ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA) <= 0) {
                    ci.cancel();
                }
            }
        }
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;Z)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 2
    )
    private boolean showmeyourskin$hideElytraGlint(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            return original && ModConfig.INSTANCE.getApplicableGlintTransparency(player.getUuid(), HideableEquipment.ELYTRA) > 0;
        }

        return original;
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;Z)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private VertexConsumer showmeyourskin$enableElytraTransparency1(
            VertexConsumerProvider vertexConsumerProvider, RenderLayer renderLayer, boolean solid, Operation<VertexConsumer> original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            if (!player.isFallFlying() || !ModConfig.INSTANCE.getApplicable(player.getUuid()).forceElytraWhenFlying) {
                var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA);
                if (transparency < 1) {
                    return ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, renderLayer, solid, solid);
                }
            }
        }

        return original.call(vertexConsumerProvider, renderLayer, solid);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer showmeyourskin$enableElytraTransparency2(
            Identifier texture, Operation<RenderLayer> original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            if (!player.isFallFlying() || !ModConfig.INSTANCE.getApplicable(player.getUuid()).forceElytraWhenFlying) {
                var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA);
                if (transparency < 1) {
                    return RenderLayer.getEntityTranslucent(texture);
                }
            }
        }

        return original.call(texture);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void showmeyourskin$applyElytraTransparency(ElytraEntityModel instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int uv, Operation<Void> original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            if (!player.isFallFlying() || !ModConfig.INSTANCE.getApplicable(player.getUuid()).forceElytraWhenFlying) {
                var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA);
                if (transparency < 1) {
                    instance.render(matrixStack, vertexConsumer, i, uv,
                            ColorHelper.Argb.withAlpha(ColorHelper.channelFromFloat(transparency), -1)
                    );
                    return;
                }
            }
        }

        original.call(instance, matrixStack, vertexConsumer, i, uv);
    }
}
