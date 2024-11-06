package nl.enjarai.showmeyourskin.mixin.armor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"
            )
    )
    private boolean toggleGlint(boolean original,
                                @Share(namespace = ShowMeYourSkin.MODID, value = "renderEquipmentContext") LocalRef<ArmorContext> ctx) {
        if (ctx.get() != null && ctx.get().shouldModify()) {
            return original && ctx.get().getApplicableGlintTransparency() > 0;
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
    private RenderLayer modifyRenderLayer(Identifier texture, Operation<RenderLayer> original,
                                          @Share(namespace = ShowMeYourSkin.MODID, value = "renderEquipmentContext") LocalRef<ArmorContext> ctx) {
        if (ctx.get() != null && ctx.get().getApplicablePieceTransparency() < 1) {
            return RenderLayer.createArmorTranslucent(texture);
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
    private int modifyAlpha(int color,
                            @Share(namespace = ShowMeYourSkin.MODID, value = "renderEquipmentContext") LocalRef<ArmorContext> ctx) {
        if (ctx.get() != null && ctx.get().getApplicablePieceTransparency() < 1) {
            return ColorHelper.withAlpha(ColorHelper.channelFromFloat(ctx.get().getApplicablePieceTransparency()), color);
        }
        return color;
    }

    @Inject(
            method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At("RETURN")
    )
    private void resetContext(EquipmentModel.LayerType layerType, Identifier modelId, Model model,
                              ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              int light, Identifier texture, CallbackInfo ci,
                              @Share(namespace = ShowMeYourSkin.MODID, value = "renderEquipmentContext") LocalRef<ArmorContext> ctx) {
        ctx.set(null);
    }
}
