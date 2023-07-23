package nl.enjarai.showmeyourskin.compat.wildfire_gender.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
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
@Mixin(targets = "com.wildfire.render.GenderLayer")
public abstract class GenderLayerMixin {
    @Dynamic
    @Inject(
            method = "Lcom/wildfire/render/GenderLayer;renderBreast(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;IIFFFFZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderLayer;getArmorResource(Lnet/minecraft/item/ArmorItem;ZLjava/lang/String;)Lnet/minecraft/util/Identifier;"
            ),
            cancellable = true
    )
    private void showmeyourskin$cancelBreastArmorRendering(AbstractClientPlayerEntity player, ItemStack armorStack,
                                                           MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                                           RenderLayer breastRenderType, int packedLightIn,
                                                           int packedOverlayIn, float red, float green,
                                                           float blue, float alpha, boolean left, CallbackInfo ci) {
        if (ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST) <= 0) {
            ci.cancel();
        } else {
            MixinContext.ARMOR.setContext(new ArmorContext(HideableEquipment.CHEST, player));
        }
    }

    @Dynamic
    @ModifyExpressionValue(
            method = "Lcom/wildfire/render/GenderLayer;renderBreast(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;IIFFFFZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"
            ),
            require = 2
    )
    private boolean showmeyourskin$modifyBreastArmorGlint(boolean original, @Local(argsOnly = true) AbstractClientPlayerEntity player) {
        return original && ModConfig.INSTANCE.getApplicableGlintTransparency(player.getUuid(), HideableEquipment.CHEST) > 0;
    }

    @Dynamic
    @WrapOperation(
            method = "Lcom/wildfire/render/GenderLayer;renderBreast(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;IIFFFFZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            require = 2
    )
    private VertexConsumer showmeyourskin$enableBreastArmorTransparency1(VertexConsumerProvider vertexConsumerProvider,
                                                                         RenderLayer renderLayer, boolean solid,
                                                                         boolean hasGlint, Operation<VertexConsumer> original,
                                                                         @Local(argsOnly = true) AbstractClientPlayerEntity player) {
        var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST);
        if (transparency < 1) {
            return ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, renderLayer, solid, hasGlint);
        }

        return original.call(vertexConsumerProvider, renderLayer, solid, hasGlint);
    }

    @Dynamic
    @WrapOperation(
            method = "Lcom/wildfire/render/GenderLayer;renderBreast(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;IIFFFFZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            ),
            require = 2
    )
    private RenderLayer showmeyourskin$enableBreastArmorTransparency2(Identifier texture, Operation<RenderLayer> original,
                                                                      @Local(argsOnly = true) AbstractClientPlayerEntity player) {
        var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST);
        if (transparency < 1) {
            return RenderLayer.getEntityTranslucent(texture);
        }

        return original.call(texture);
    }

    @Dynamic
    @ModifyArg(
            method = "Lcom/wildfire/render/GenderLayer;renderBreast(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;IIFFFFZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderLayer;renderBox(Lcom/wildfire/render/WildfireModelRenderer$ModelBox;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 8,
            require = 2
    )
    private float showmeyourskin$applyBreastArmorTransparency(float original, @Local(argsOnly = true) AbstractClientPlayerEntity player) {
        var transparency = ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST);
        if (transparency < 1) {
            return transparency;
        }

        return original;
    }
}
