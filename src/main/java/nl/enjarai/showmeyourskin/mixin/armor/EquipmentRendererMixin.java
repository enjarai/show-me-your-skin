package nl.enjarai.showmeyourskin.mixin.armor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.util.IWishMixinAllowedForPublicStaticFields;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"
            )
    )
    private boolean toggleGlint(boolean original) {
        var ctx = IWishMixinAllowedForPublicStaticFields.currentArmorContext;
        if (ctx != null && ctx.shouldModify()) {
            return original && ctx.getApplicableGlintTransparency() > 0;
        }
        return original;
    }
    @WrapOperation(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target="net/minecraft/client/render/item/ItemRenderer.getArmorGlintConsumer (Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;Z)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private VertexConsumer modifyGlint123(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean glint, Operation<VertexConsumer> original) {
        var ctx=IWishMixinAllowedForPublicStaticFields.currentArmorContext;
        if(ctx!=null&&ctx.shouldModify()) {
            return glint&& ctx.getApplicablePieceTransparency()>0 ? VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getGlint()), vertexConsumers.getBuffer(layer)) : vertexConsumers.getBuffer(layer);
        }
        return original.call(vertexConsumers,layer,glint);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer modifyRenderLayer(Identifier texture, Operation<RenderLayer> original) {
        var ctx = IWishMixinAllowedForPublicStaticFields.currentArmorContext;
        if (ctx!= null && ctx.getApplicablePieceTransparency() < 1) {
            return RenderLayer.createArmorTranslucent(texture);
        }
        return original.call(texture);
    }
    @WrapOperation(method="render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at=@At(value="INVOKE",target="net/minecraft/client/model/Model.render (Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V")
    )
    private void modifyRenderLayer(Model instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color, Operation<Void> original) {
        var ctx = IWishMixinAllowedForPublicStaticFields.currentArmorContext;
        var percentage=1F;
        if (ctx != null) {
            percentage=ctx.getApplicablePieceTransparency();
            if (percentage==0){
                return;
            }
            if (percentage < 1) {
                color= ColorHelper.withAlpha(ColorHelper.channelFromFloat(percentage), color);
            }
        }
        original.call(instance, matrices, vertices, light, overlay, color);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At("RETURN")
    )
    private void resetContext(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture, CallbackInfo ci) {

        IWishMixinAllowedForPublicStaticFields.currentArmorContext = null;
    }
}
