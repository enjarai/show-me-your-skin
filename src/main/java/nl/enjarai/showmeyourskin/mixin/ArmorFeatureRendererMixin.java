package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.CombatLogger;
import nl.enjarai.showmeyourskin.util.MixinHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorFeatureRenderer.class, priority = 999)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Shadow protected abstract Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay);

    @Inject(
            method = "renderArmor",
            at = @At(value = "HEAD")
    )
    private void captureContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        MixinHooks.setContext(new ArmorContext(armorSlot, entity));
    }

    @ModifyVariable(
            method = "renderArmor",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"),
            index = 10
    )
    private boolean toggleGlint(boolean original) {
        var ctx = MixinHooks.getContext();

        if (ctx != null && ctx.getEntity() instanceof PlayerEntity) {
            return original && ctx.getApplicableGlint();
        }
        return original;
    }

    @Inject(
            method = "renderArmorParts",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void armorTransparency(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, String overlay, CallbackInfo ci) {
        var ctx = MixinHooks.getAndClearContext();

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicableTransparency();

            if (t < 1) {
                if (t > 0) {
                    VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(
                            vertexConsumers, RenderLayer.getEntityTranslucent(getArmorTexture(item, legs, overlay)),
                            false, usesSecondLayer
                    );
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, t);
                }

                ci.cancel();
            }
        }
    }
}
