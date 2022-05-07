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
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Shadow protected abstract Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay);

    private EquipmentSlot slot;
    private T entity;

    @Inject(
            method = "renderArmor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getSlotType()Lnet/minecraft/entity/EquipmentSlot;")
    )
    private void captureContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        this.slot = armorSlot;
        this.entity = entity;
    }

    @ModifyVariable(
            method = "renderArmor",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z"),
            index = 10
    )
    private boolean toggleGlint(boolean original) {
        if (entity instanceof PlayerEntity player) {
            return original && ModConfig.INSTANCE.getApplicable(player.getUuid()).getGlint(slot);
        }
        return original;
    }

    @Inject(
            method = "renderArmorParts",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void armorTransparency(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, String overlay, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            var t = ModConfig.INSTANCE.getApplicable(player.getUuid()).getTransparency(slot);
            if (t < 100) {
                if (t > 0) {
                    VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, RenderLayer.getEntityTranslucent(getArmorTexture(item, legs, overlay)), false, usesSecondLayer);
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, t / 100f);
                }

                ci.cancel();
            }
        }
    }
}
