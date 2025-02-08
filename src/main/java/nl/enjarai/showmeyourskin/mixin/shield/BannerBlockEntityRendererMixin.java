package nl.enjarai.showmeyourskin.mixin.shield;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(BannerBlockEntityRenderer.class)
public abstract class BannerBlockEntityRendererMixin {
    private static final ThreadLocal<Boolean> showmeyourskin$isShield = ThreadLocal.withInitial(() -> false);

    @Shadow
    private static void renderLayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier textureId, DyeColor color) {}

    @Inject(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V",
            at = @At("HEAD")
    )
    private static void showmeyourskin$captureBannerCanvasContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, DyeColor color, BannerPatternsComponent patterns, boolean glint, boolean solid, CallbackInfo ci) {
        showmeyourskin$isShield.set(!isBanner);
    }


    @Inject(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V",
            at = @At("RETURN")
    )
    private static void clearBannerCanvasContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, DyeColor color, BannerPatternsComponent patterns, boolean glint, boolean solid, CallbackInfo ci) {
        showmeyourskin$isShield.remove();
    }

    @ModifyArg(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 1
    )
    // FIXME: Needs to be always translucent, even when there is a GUI render, or else transparency on the model won't work
    private static Function<Identifier, RenderLayer> changeRenderLayer(Function<Identifier, RenderLayer> layerFactory) {
        return RenderLayer::getEntityTranslucent;
    }

    @Inject(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private static void triggerShieldRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                            int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner,
                                            DyeColor color, BannerPatternsComponent patterns, boolean glint, boolean solid, CallbackInfo ci) {
        MixinContext.SHIELD_PLATE_RENDER.setContext(true);
    }

    @ModifyArg(
            method = "renderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
            ),
            index = 4
    )
    private static int showmeyourskin$modifyBannerPatternTransparency(int original) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx instanceof PlayerEntity) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    return ColorHelper.withAlpha(ColorHelper.channelFromFloat(t), original);
                }
            }
        }

        return original;
    }
}
