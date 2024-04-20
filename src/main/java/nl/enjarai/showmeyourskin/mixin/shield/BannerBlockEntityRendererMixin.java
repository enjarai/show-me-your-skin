package nl.enjarai.showmeyourskin.mixin.shield;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(BannerBlockEntityRenderer.class)
public abstract class BannerBlockEntityRendererMixin {
    private static final ThreadLocal<Boolean> showmeyourskin$isShield = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void showmeyourskin$captureBannerCanvasContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns, boolean glint, CallbackInfo ci) {
        showmeyourskin$isShield.set(!isBanner);

        if (!isBanner) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx instanceof PlayerEntity) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t <= 0) {
                    ci.cancel();
                }
            }
        }
    }

    @ModifyArg(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;Z)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 1
    )
    private static Function<Identifier, RenderLayer> showmeyourskin$modifyBannerCanvasRenderLayer(Function<Identifier, RenderLayer> original) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx instanceof PlayerEntity) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    return RenderLayer::getEntityTranslucent;
                }
            }
        }

        return original;
    }

    @WrapWithCondition(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private static boolean showmeyourskin$applyShieldCanvasTransparency(ModelPart canvas, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx instanceof PlayerEntity) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    if (t > 0) {
                        canvas.render(
                                matrices, vertexConsumer,
                                light, overlay,
                                1.0f, 1.0f, 1.0f, t
                        );
                    }

                    return false;
                }
            }
        }

        return true;
    }

    @ModifyArg(
            method = "method_43789",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 1
    )
    private static Function<Identifier, RenderLayer> showmeyourskin$modifyBannerPatternRenderLayer(Function<Identifier, RenderLayer> original) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx instanceof PlayerEntity) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    return RenderLayer::getEntityTranslucent;
                }
            }
        }

        return original;
    }

    @ModifyArg(
            method = "method_43789",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 7
    )
    private static float showmeyourskin$modifyBannerPatternTransparency(float original) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx instanceof PlayerEntity) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    return t;
                }
            }
        }

        return original;
    }
}
