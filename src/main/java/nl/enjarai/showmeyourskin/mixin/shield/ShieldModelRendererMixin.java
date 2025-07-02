package nl.enjarai.showmeyourskin.mixin.shield;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.IWishMixinAllowedForPublicStaticFields;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShieldModelRenderer.class)
public abstract class ShieldModelRendererMixin {
    @Inject(method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IIZ)V",at=@At("HEAD"))
    private void showmeyourskin$shieldGlint(ComponentMap componentMap, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl, CallbackInfo ci) {
        IWishMixinAllowedForPublicStaticFields.currentArmorContext=new ArmorContext(HideableEquipment.SHIELD, (LivingEntity) IWishMixinAllowedForPublicStaticFields.currentEntity);
    }

    @WrapOperation(method="render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IIZ)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/render/item/ItemRenderer.getItemGlintConsumer (Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;")
    )
    private VertexConsumer showmeyourskin$modifyGlint1(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint, Operation<VertexConsumer> original) {
        var ctx=IWishMixinAllowedForPublicStaticFields.currentArmorContext;

        return glint&& ctx.getApplicablePieceTransparency()>0 ? VertexConsumers.union(vertexConsumers.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer)) : vertexConsumers.getBuffer(layer);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IIZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ShieldEntityModel;getLayer(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;",
                    ordinal = 0
            )
    )
    private RenderLayer showmeyourskin$enableShieldTransparency(ShieldEntityModel model, Identifier texture, Operation<RenderLayer> original) {
        var ctx = IWishMixinAllowedForPublicStaticFields.currentArmorContext;

        if (ctx != null && ctx.getEntity() != null) {
            if(ctx.getApplicablePieceTransparency()<1){
                return RenderLayer.getEntityTranslucent(texture);
            }
        }

        return original.call(model, texture);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IIZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void showmeyourskin$modifyShieldTransparency(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original) {
        var ctx = IWishMixinAllowedForPublicStaticFields.currentArmorContext;
        var percentage=1F;
        var color= 0;
        if (ctx != null && ctx.getEntity() != null) {
            percentage=ctx.getApplicablePieceTransparency();
            if (percentage==0){
                return;
            }
            if (percentage <= 1) {
                color=ColorHelper.withAlpha(ColorHelper.channelFromFloat(percentage), -1);
            }
            instance.render(matrices, vertices, light, overlay, color);
        }
        else {
            original.call(instance, matrices, vertices, light, overlay);
        }
    }
}
