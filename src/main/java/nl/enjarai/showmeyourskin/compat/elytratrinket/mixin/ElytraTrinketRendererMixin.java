package nl.enjarai.showmeyourskin.compat.elytratrinket.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "top.theillusivec4.elytratrinket.client.ElytraTrinketRenderer")
public abstract class ElytraTrinketRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends ElytraFeatureRenderer<T, M> {
    @Unique
    private final ThreadLocal<PlayerEntity> showmeyourskin$player = new ThreadLocal<>();

    public ElytraTrinketRendererMixin(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context, loader);
    }

    @Dynamic
    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void showmeyourskin$hideElytra(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
//            if (!ModConfig.INSTANCE.getApplicable(player.getUuid()).showElytra) {
//                ci.cancel();
//            } else {
//                showmeyourskin$player.set(player);
//            } TODO
        }
    }

    @Dynamic
    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
            ),
            index = 3
    )
    private boolean showmeyourskin$hideElytraGlint(boolean original) {
        var player = showmeyourskin$player.get();
        if (player != null) { // TODO
            return original && ModConfig.INSTANCE.getApplicableGlintTransparency(player.getUuid(), HideableEquipment.ELYTRA) > 0;
        }

        return original;
    }
}
