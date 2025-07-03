package nl.enjarai.showmeyourskin.mixin.hat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.IWishMixinAllowedForPublicStaticFields;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SkullBlockEntityRenderer.class)
public abstract class SkullBlockEntityRendererMixin {
    @Shadow @Final private static Map<SkullBlock.SkullType, Identifier> TEXTURES;
    @WrapOperation(method="renderSkull",at= @At(value = "INVOKE", target = "net/minecraft/client/render/block/entity/SkullBlockEntityModel.render (Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
    private static void modifySkullColor(SkullBlockEntityModel instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int i1, int i2, Operation<Void> original) {
        var ctx= MixinContext.ENTITY.getContext();
        var percentage=1F;
        if (ctx instanceof PlayerEntity) {
            percentage= ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(),HideableEquipment.HAT);
            if (percentage<=0.1){
                return;
            }
        }
        instance.render( matrixStack, vertexConsumer, i1, i2,ColorHelper.getWhite(percentage));
    }
    @WrapOperation(method = "getRenderLayer",at = @At(value = "INVOKE", target = "net/minecraft/client/render/block/entity/SkullBlockEntityRenderer.getCutoutRenderLayer (Lnet/minecraft/block/SkullBlock$SkullType;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer modifySkullTransparency(SkullBlock.SkullType type, Identifier texture, Operation<RenderLayer> original) {
        var ctx = MixinContext.ENTITY.getContext();
        if (ctx instanceof PlayerEntity) {
            var percentage= ModConfig.INSTANCE.getApplicablePieceTransparency(ctx.getUuid(),HideableEquipment.HAT);
            if(percentage>0){
                return RenderLayer.getEntityTranslucent(TEXTURES.get(type));
            }
        }
        return original.call(type, texture);
    }
}
