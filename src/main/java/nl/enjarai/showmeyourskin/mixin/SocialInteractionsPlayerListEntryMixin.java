package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.client.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.ArmorScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mixin(SocialInteractionsPlayerListEntry.class)
public abstract class SocialInteractionsPlayerListEntryMixin {
    @Shadow @Nullable private ButtonWidget hideButton;
    @Mutable
    @Shadow @Final private List<ClickableWidget> buttons;
    @Shadow private boolean offline;

    private TexturedButtonWidget armorOverrideButton;
    private TexturedButtonWidget overrideDeleteButton;
    private boolean overrideExists;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void createButtons(MinecraftClient client, SocialInteractionsScreen parent, UUID uuid, String name, Supplier<Identifier> skinTexture, CallbackInfo ci) {
        armorOverrideButton = getButton(parent, 20, 20, 20, 78, button -> {
            PlayerEntity player;
            if (client.world != null) {
                player = client.world.getPlayerByUuid(uuid);
            } else {
                player = new DummyClientPlayerEntity(skinTexture.get());
            }
            client.setScreen(ModConfig.INSTANCE.getScreen(player, parent));
            updateButtonState(true);
        }, new TranslatableText("gui.showmeyourskin.armorScreen.openButtonTooltip"));

        overrideDeleteButton = getButton(parent, 10, 16, 10, 120, button -> {
            ModConfig.INSTANCE.deleteOverride(uuid);
            ModConfig.INSTANCE.save();
            updateButtonState(false);
        }, new TranslatableText("gui.showmeyourskin.armorScreen.deleteButtonTooltip"));

        updateButtonState(ModConfig.INSTANCE.getOverride(uuid) != null);

        // Mojank decided they needed an ImmutableList to store the buttons, because yes.
        buttons = new ArrayList<>(buttons);
        buttons.add(armorOverrideButton);
        buttons.add(overrideDeleteButton);
    }

    @Inject(
            method = "render",
            at = @At(value = "TAIL")
    )
    private void renderButtons(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (offline) return;

        armorOverrideButton.x = x + (entryWidth - armorOverrideButton.getWidth() - (hideButton != null ? 28 : 4));
        armorOverrideButton.y = y + (entryHeight - armorOverrideButton.getHeight()) / 2;
        armorOverrideButton.render(matrices, mouseX, mouseY, tickDelta);

        overrideDeleteButton.x = armorOverrideButton.x - overrideDeleteButton.getWidth();
        overrideDeleteButton.y = armorOverrideButton.y + (armorOverrideButton.getHeight() - overrideDeleteButton.getHeight()) / 2;
        overrideDeleteButton.render(matrices, mouseX, mouseY, tickDelta);
    }

    @Inject(
            method = "setOffline",
            at = @At(value = "HEAD")
    )
    private void disableButtonsWhenOffline(boolean offline, CallbackInfo ci) {
        armorOverrideButton.visible = !offline;
        overrideDeleteButton.visible = (!offline) && overrideExists;
    }

    private void updateButtonState(boolean overrideExists) {
//        overrideExists = ModConfig.INSTANCE.getOverride(this.uuid) != null;
        this.overrideExists = overrideExists;

        armorOverrideButton.u = overrideExists ? 20 : 40;
        overrideDeleteButton.visible = overrideExists;
    }

    private TexturedButtonWidget getButton(SocialInteractionsScreen parent, int width, int height, int u, int v, ButtonWidget.PressAction pressAction, TranslatableText tooltip) {
        var orderedTooltip = List.of(tooltip.asOrderedText());
        return new TexturedButtonWidget(0, 0, width, height, u, v, 20,
                ArmorScreen.TEXTURE, 256, 256, pressAction,
                (button, matrices, mouseX, mouseY) -> parent.setOnRendered(() -> {
                    parent.renderOrderedTooltip(matrices, orderedTooltip, mouseX, mouseY);
                    parent.setOnRendered(null);
                }),
                tooltip
        );
    }
}
