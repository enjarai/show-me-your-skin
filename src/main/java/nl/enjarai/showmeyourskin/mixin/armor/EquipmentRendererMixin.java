package nl.enjarai.showmeyourskin.mixin.armor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.client.ModRenderLayers;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
    @Inject(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dontRender(EquipmentModel.LayerType layerType, Identifier modelId, Model model,
                            ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                            int light, Identifier texture, CallbackInfo ci) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify() && ctx.getApplicablePieceTransparency() <= 0) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"
            )
    )
    private boolean toggleGlint(boolean original) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify()) {
            return original && ctx.getApplicableGlintTransparency() > 0;
        }
        return original;
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer modifyRenderLayer(Identifier texture, Operation<RenderLayer> original) {
        var ctx = MixinContext.ARMOR.getContext();
        if (ctx != null && ctx.shouldModify() && ctx.getApplicablePieceTransparency() < 1) {
            return ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(texture);
        }
        return original.call(texture);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
            ),
            index = 4
    )
    private int modifyAlpha(int color) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify() && ctx.getApplicablePieceTransparency() < 1) {
            return ColorHelper.withAlpha(ColorHelper.channelFromFloat(ctx.getApplicablePieceTransparency()), color);
        }
        return color;
    }

    @Inject(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At("RETURN")
    )
    private void resetContext(EquipmentModel.LayerType layerType, Identifier modelId, Model model,
                              ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              int light, Identifier texture, CallbackInfo ci) {

        MixinContext.ARMOR.clearContext();
        MixinContext.TRIM_RENDER.clearContext();
    }

    @Inject(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void setTrimRenderLocal(EquipmentModel.LayerType layerType, Identifier modelId, Model model,
                              ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              int light, Identifier texture, CallbackInfo ci) {
        MixinContext.TRIM_RENDER.setContext(true);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/TexturedRenderLayers;getArmorTrims(Z)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer modifyTrimRenderLayer(boolean decal, Operation<RenderLayer> original) {
        var ctx = MixinContext.ARMOR.getContext();
        if (ctx != null && ctx.shouldModify() && ctx.getApplicableTrimTransparency() < 1) {
            if(decal) {
                return ModRenderLayers.ARMOR_TRANSLUCENT_DECAL_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
            } else {
                return ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
            }
        }
        return original.call(decal);
    }
}
