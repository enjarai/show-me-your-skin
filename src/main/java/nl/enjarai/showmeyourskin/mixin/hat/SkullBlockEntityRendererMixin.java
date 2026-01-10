package nl.enjarai.showmeyourskin.mixin.hat;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(SkullBlockEntityRenderer.class)
public abstract class SkullBlockEntityRendererMixin {
    @Shadow
    @Final
    private static Map<SkullBlock.SkullType, Identifier> TEXTURES;

    @WrapOperation(
            method = "render(Lnet/minecraft/client/render/block/entity/state/SkullBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;render(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
            )
    )
    private static void modifySkullColor(Direction facing, float yaw, float poweredTicks, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, SkullBlockEntityModel model, RenderLayer renderLayer, int outlineColor, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, Operation<Void> original) {
        var ctx = MixinContext.ENTITY.getContext();
        var percentage = 1F;
        if (ctx instanceof PlayerEntity) {
            percentage = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.HAT);
            if (percentage <= 0.1) {
                return;
            }
        }
        original.call(facing, yaw, poweredTicks, matrices, queue, light, model, renderLayer, outlineColor, crumblingOverlay);
    }

    @WrapOperation(
            method = "renderSkull",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/render/block/entity/SkullBlockEntityRenderer.getCutoutRenderLayer(Lnet/minecraft/block/SkullBlock$SkullType;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private static RenderLayer modifySkullTransparency(SkullBlock.SkullType type, Identifier texture, Operation<RenderLayer> original) {
        var ctx = MixinContext.ENTITY.getContext();
        if (ctx instanceof PlayerEntity) {
            var percentage = ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(), HideableEquipment.HAT);
            if (percentage > 0) {
                return RenderLayer.getEntityTranslucent(TEXTURES.get(type));
            }
        }
        return original.call(type, texture);
    }
}