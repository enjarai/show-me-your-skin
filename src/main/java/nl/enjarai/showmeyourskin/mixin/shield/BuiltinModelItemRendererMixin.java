package nl.enjarai.showmeyourskin.mixin.shield;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {

    private boolean showmeyourskin$getShieldGlint() {
        var ctx = MixinContext.ENTITY.getContext();

        if (ctx instanceof PlayerEntity) {
            return ModConfig.INSTANCE.getApplicable(ctx.getUuid()).getGlints().get(HideableEquipment.SHIELD).getTransparency() > 0;
        }

        return true;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getItemGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;",
                    ordinal = 0
            ),
            index = 3
    )
    private boolean showmeyourskin$modifyGlint1(boolean original) {
        return original && showmeyourskin$getShieldGlint();
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/BannerBlockEntityRenderer;renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V"
            ),
            index = 9
    )
    private boolean showmeyourskin$modifyGlint2(boolean original) {
        return original && showmeyourskin$getShieldGlint();
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            cancellable = true
    )
    private void showmeyourskin$cancelShieldRender(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        var ctx = MixinContext.ENTITY.getContext();

        if (ctx instanceof PlayerEntity) {
            if (ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD) <= 0) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ShieldEntityModel;getLayer(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;",
                    ordinal = 0
            )
    )
    private RenderLayer showmeyourskin$enableShieldTransparency(ShieldEntityModel instance, Identifier identifier, Operation<RenderLayer> original) {
        var ctx = MixinContext.ENTITY.getContext();

        if (ctx instanceof PlayerEntity) {
            if (ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD) < 1) {
                return RenderLayer.getEntityTranslucent(identifier);
            }
        }

        return original.call(instance, identifier);
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void showmeyourskin$modifyShieldTransparency(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original) {
        var ctx = MixinContext.ENTITY.getContext();

        if (ctx instanceof PlayerEntity) {
            instance.render(matrices, vertices, light, overlay, ColorHelper.withAlpha(ColorHelper.channelFromFloat(ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD)), -1));
            return;
        }

        original.call(instance, matrices, vertices, light, overlay);
    }
}
