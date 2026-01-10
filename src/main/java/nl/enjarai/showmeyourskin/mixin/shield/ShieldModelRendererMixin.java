package nl.enjarai.showmeyourskin.mixin.shield;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShieldModelRenderer.class)
public abstract class ShieldModelRendererMixin {
    @ModifyArg(
            method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModelPart(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IILnet/minecraft/client/texture/Sprite;ZZILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;I)V"
            ),
            index = 7
    )
    private boolean modifyGlint(boolean glint) {
        var ctx = MixinContext.ENTITY.getContext();
        if (ctx != null) {
            return glint && ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD) > 0;
        }
        return glint;
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ShieldEntityModel;getLayer(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer modifyTransparency(ShieldEntityModel model, Identifier texture, Operation<RenderLayer> original) {
        var ctx = MixinContext.ENTITY.getContext();
        if (ctx != null) {
            if (ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD) < 1) {
                return RenderLayer.getEntityTranslucent(texture);
            }
        }
        return original.call(model, texture);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModelPart(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IILnet/minecraft/client/texture/Sprite;ZZILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;I)V"
            )
    )
    private void modifyColor(OrderedRenderCommandQueue instance, ModelPart modelPart, MatrixStack matrixStack, RenderLayer renderLayer, int light, int overlay, Sprite sprite, boolean bl1, boolean bl2, int k, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, int l, Operation<Void> original) {
        var ctx = MixinContext.ENTITY.getContext();
        var percentage = 1F;
        if (ctx != null) {
            percentage = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);
            if (percentage <= 0.1) {
                return;
            }
        }
        original.call(instance, modelPart, matrixStack, renderLayer, light, overlay, sprite, bl1, bl2, ColorHelper.getWhite(percentage), crumblingOverlayCommand, l);
    }
}