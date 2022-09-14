package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {

    private boolean showmeyourskin$getShieldGlint() {
        var ctx = MixinContext.HELD_ITEM.getContext();

        if (ctx instanceof PlayerEntity) {
            return ModConfig.INSTANCE.getApplicable(ctx.getUuid()).showShieldGlint;
        }

        return true;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
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
            method = "render(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/BannerBlockEntityRenderer;renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V"
            ),
            index = 8
    )
    private boolean showmeyourskin$modifyGlint2(boolean original) {
        return original && showmeyourskin$getShieldGlint();
    }
}
