package nl.enjarai.showmeyourskin.mixin.elytra;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.fake.FakePlayerEntityRendererState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeFeatureRenderer.class)
public abstract class CapeFeatureRendererMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/render/entity/feature/CapeFeatureRenderer.hasCustomModelForLayer (Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;)Z",ordinal = 0)
    )
    private boolean showmeyourskin$renderCapeIfElytraHidden(boolean original,@Local(argsOnly = true) PlayerEntityRenderState playerEntityRenderState) {
        AbstractClientPlayerEntity player = ((FakePlayerEntityRendererState)playerEntityRenderState).show_me_your_skin$getPlayer();
        if (player != null) {
            if (!player.isGliding() || !ModConfig.INSTANCE.getApplicable(player.getUuid()).forceElytraWhenFlying) {
                var uuid = player.getUuid();
                return original && ModConfig.INSTANCE.getApplicablePieceTransparency(uuid, HideableEquipment.ELYTRA) > 0;
            }
        }
        return original;
    }

}
