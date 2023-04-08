package nl.enjarai.showmeyourskin.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
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
        var ctx = MixinContext.HELD_ITEM.getContext();

        if (ctx instanceof PlayerEntity) {
            return ModConfig.INSTANCE.getApplicable(ctx.getUuid()).getGlints().get(HideableEquipment.SHIELD).getTransparency() > 0;
        }

        return true;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getDirectItemGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
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
                    target = "Lnet/minecraft/client/render/block/entity/BannerBlockEntityRenderer;renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V"
            ),
            index = 8
    )
    private boolean showmeyourskin$modifyGlint2(boolean original) {
        return original && showmeyourskin$getShieldGlint();
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/BlockItem;getBlockEntityNbt(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NbtCompound;"
            ),
            cancellable = true
    )
    private void showmeyourskin$cancelShieldRender(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        var ctx = MixinContext.HELD_ITEM.getContext();

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
    private RenderLayer showmeyourskin$enableShieldTransparency(ShieldEntityModel model, Identifier texture, Operation<RenderLayer> original) {
        var ctx = MixinContext.HELD_ITEM.getContext();

        if (ctx instanceof PlayerEntity) {
            if (ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD) < 1) {
                return RenderLayer.getEntityTranslucent(texture);
            }
        }

        return original.call(model, texture);
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 7
    )
    private float showmeyourskin$modifyShieldTransparency(float original) {
        var ctx = MixinContext.HELD_ITEM.getContext();

        if (ctx instanceof PlayerEntity) {
            return ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);
        }

        return original;
    }
}
