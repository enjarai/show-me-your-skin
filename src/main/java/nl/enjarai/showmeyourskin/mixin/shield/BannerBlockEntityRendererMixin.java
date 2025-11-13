package nl.enjarai.showmeyourskin.mixin.shield;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BannerBlockEntityRenderer.class)
public abstract class BannerBlockEntityRendererMixin {
    @Unique
    private static final ThreadLocal<Boolean> showmeyourskin$isShield = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "renderCanvas", at = @At("HEAD"),
            cancellable = true
    )
    private static <S> void captureBannerCanvasContext(SpriteHolder materials, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Model<S> model, S state, SpriteIdentifier spriteId, boolean useBannerLayer, DyeColor color, BannerPatternsComponent patterns, boolean isBanner, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, int i, CallbackInfo ci) {
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

    @ModifyArgs(
            method = "renderCanvas",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
            )
    )
    private static void modifyBannerCanvasRenderLayer(Args args){
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);
                if (t < 1) {
                    Sprite sprite = args.get(7);
                    args.set(3, RenderLayer.getEntityTranslucent(sprite.getAtlasId()));
                }
            }
        }
    }

    @WrapOperation(
            method = "renderCanvas", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
    )
    )
    private static void showmeyourskin$applyShieldCanvasTransparency(OrderedRenderCommandQueue instance, Model model, Object o, MatrixStack matrixStack, RenderLayer renderLayer, int i, int j, int k, Sprite sprite, int l, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, Operation<Void> original) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    if (t > 0) {
                        j = ColorHelper.fromFloats(t, 1.0f, 1.0f, 1.0f);
                        
                    }
                    original.call(instance, model, o, matrixStack, renderLayer, i, j, k, sprite, l, crumblingOverlayCommand);
                    return;
                }
            }
        }

        original.call(instance, model, o, matrixStack, renderLayer, i, j, k, sprite, l, crumblingOverlayCommand);
    }

    @WrapOperation(
            method = "renderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
            )
    )
    private static void showmeyourskin$modifyBannerPatternRenderLayer(OrderedRenderCommandQueue instance, Model model, Object o, MatrixStack matrixStack, RenderLayer renderLayer, int i, int j, int k, Sprite sprite, int l, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, Operation<Void> original) {
        if (showmeyourskin$isShield.get()) {
            var ctx = MixinContext.ENTITY.getContext();

            if (ctx != null) {
                var t = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.SHIELD);

                if (t < 1) {
                    o = RenderLayer.getEntityTranslucent(sprite.getAtlasId());
                    original.call(instance, model, o, matrixStack, renderLayer, i, j, k, sprite, l, crumblingOverlayCommand);
                }
            }
        }

    }

    @ModifyArg(
            method = "renderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
            ),
            index = 6
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