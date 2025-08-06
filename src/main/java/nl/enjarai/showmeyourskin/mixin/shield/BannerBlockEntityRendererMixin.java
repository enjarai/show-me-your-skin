package nl.enjarai.showmeyourskin.mixin.shield;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(BannerBlockEntityRenderer.class)
public abstract class BannerBlockEntityRendererMixin {
    @Unique
    private static final ThreadLocal<Boolean> showmeyourskin$isShield = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V", at = @At("HEAD"),
            cancellable = true
    )
    private static void captureBannerCanvasContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, DyeColor color, BannerPatternsComponent patterns, boolean glint, boolean solid, CallbackInfo ci) {
        showmeyourskin$isShield.set(!isBanner);
        if (!isBanner) {
            var ctx = MixinContext.ENTITY.getContext();
            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);
                if (t <= 0) {
                    ci.cancel();
                }
            }
        }
    }

    @ModifyArg(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/util/SpriteIdentifier.getVertexConsumer (Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 1
    )
    private static Function<Identifier, RenderLayer> modifyBannerCanvasRenderLayer(Function<Identifier, RenderLayer> layerFactory) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);
                if (t < 1) {
                    return RenderLayer::getEntityTranslucent;
                }
            }
        }

        return layerFactory;
    }

    @WrapWithCondition(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLnet/minecraft/util/DyeColor;Lnet/minecraft/component/type/BannerPatternsComponent;ZZ)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
    )
    )
    private static boolean showmeyourskin$applyShieldCanvasTransparency(ModelPart canvas, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    if (t > 0) {
                        canvas.render(
                                matrices, vertexConsumer,
                                light, overlay,
                                ColorHelper.fromFloats(t, 1.0f, 1.0f, 1.0f)
                        );
                    }

                    return false;
                }
            }
        }

        return true;
    }

    @ModifyArg(
            method = "renderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 1
    )
    private static Function<Identifier, RenderLayer> showmeyourskin$modifyBannerPatternRenderLayer(Function<Identifier, RenderLayer> original) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    return RenderLayer::getEntityTranslucent;
                }
            }
        }

        return original;
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

            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    return ColorHelper.withAlpha(ColorHelper.channelFromFloat(t), original);
                }
            }
        }

        return original;
    }
}