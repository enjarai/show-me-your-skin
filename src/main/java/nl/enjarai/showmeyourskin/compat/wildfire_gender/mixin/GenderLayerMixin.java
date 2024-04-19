package nl.enjarai.showmeyourskin.compat.wildfire_gender.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.wildfire.render.GenderLayer")
public abstract class GenderLayerMixin {
    @Dynamic
    @ModifyExpressionValue(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack showmeyourskin$suppressArmorTransform(ItemStack original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            if (ModConfig.INSTANCE.getApplicablePieceTransparency(player.getUuid(), HideableEquipment.CHEST) <= 0) {
                return ItemStack.EMPTY;
            }
        }

        return original;
    }
}
