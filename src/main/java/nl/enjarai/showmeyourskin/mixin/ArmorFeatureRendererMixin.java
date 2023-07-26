package nl.enjarai.showmeyourskin.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.client.ModRenderLayers;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.MixinContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.Queue;

@Mixin(value = ArmorFeatureRenderer.class, priority = 500)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Shadow protected abstract Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay);

    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    @Unique
    private final LinkedList<ArmorContext> trimContextQueue = new LinkedList<>();

    @Inject(
            method = "renderArmor",
            at = @At(value = "HEAD")
    )
    private void showmeyourskin$captureContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        MixinContext.ARMOR.setContext(new ArmorContext(HideableEquipment.fromSlot(armorSlot), entity));
    }

    @ModifyExpressionValue(
            method = "renderArmor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z")
    )
    private boolean showmeyourskin$toggleGlint(boolean original) {
        var ctx = MixinContext.ARMOR.getContext();

        if (ctx != null && ctx.shouldModify()) {
            return original && ctx.getApplicableGlintTransparency() > 0;
        }
        return original;
    }

    @Inject(
            method = "renderArmorParts",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void showmeyourskin$armorTransparency(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, A model, boolean secondTextureLayer, float red, float green, float blue, @Nullable String overlay, CallbackInfo ci) {
        var ctx = MixinContext.ARMOR.getContext();

        if (overlay == null) {
            trimContextQueue.offer(ctx);
        }

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicablePieceTransparency();

            if (t < 1) {
                if (t > 0) {
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                            ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(getArmorTexture(item, secondTextureLayer, overlay)));
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, t);
                }

                ci.cancel();
            }
        }
    }

    @Inject(
            method = "renderTrim",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void showmeyourskin$trimTransparency(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("allthetrims")) return;

        var ctx = trimContextQueue.poll();

        if (ctx != null && ctx.shouldModify()) {
            var t = ctx.getApplicableTrimTransparency();

            if (t < 1) {
                if (t > 0) {
                    Sprite sprite = armorTrimsAtlas.getSprite(
                            leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
                    VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers
                            .getBuffer(ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE)));
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, t);
                }

                ci.cancel();
            }
        }
    }
}
