package nl.enjarai.showmeyourskin.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
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
            if (ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA) <= 0) {
                ci.cancel();
            }
        }
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 3
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
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private VertexConsumer showmeyourskin$enableElytraTransparency1(
            VertexConsumerProvider vertexConsumerProvider, RenderLayer renderLayer, boolean solid, boolean hasGlint,
            Operation<VertexConsumer> original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA);
            if (transparency < 1) {
                return ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, renderLayer, solid, hasGlint);
            }
        }

        return original.call(vertexConsumerProvider, renderLayer, solid, hasGlint);
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
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA);
            if (transparency < 1) {
                return RenderLayer.getEntityTranslucent(texture);
            }
        }

        return original.call(texture);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 7
    )
    private float showmeyourskin$applyElytraTransparency(float original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.ELYTRA);
            if (transparency < 1) {
                return transparency;
            }
        }

        return original;
    }
}
