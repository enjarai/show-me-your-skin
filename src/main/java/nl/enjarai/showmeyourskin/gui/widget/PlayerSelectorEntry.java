package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerSelectorEntry extends ConfigEntryWidget {
    protected static final ButtonTextures CLEAR_BUTTON_TEXTURES = new ButtonTextures(
            ShowMeYourSkin.id("button/delete_override"),
            ShowMeYourSkin.id("button/delete_override_highlighted")
    );

    public final UUID uuid;
    public final ButtonWidget clearButton;
    public final Supplier<SkinTextures> skinTextures;

    public PlayerSelectorEntry(MinecraftClient client, PlayerSelectorWidget parent, UUID uuid, Text name, Supplier<SkinTextures> skinTextures) {
        super(client, parent, -1, -1, name);
        this.armorConfig = ModConfig.INSTANCE.getOverride(uuid);
        this.uuid = uuid;
        this.clearButton = new TexturedButtonWidget(
                0, 0, 11, 11, CLEAR_BUTTON_TEXTURES, button -> clearConfig());
        this.clearButton.visible = armorConfig != null;
        this.skinTextures = skinTextures;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected && armorConfig == null) {
            armorConfig = ModConfig.INSTANCE.getOrCreateOverride(uuid);
            clearButton.visible = true;
            ModConfig.INSTANCE.save();
        }
    }

    public void clearConfig() {
        if (selected) {
            parent.setSelected(null);
        }
        armorConfig = null;
        clearButton.visible = false;
        ModConfig.INSTANCE.deleteOverride(uuid);
        ModConfig.INSTANCE.save();
    }

    @Override
    public void directRender(DrawContext context, int index, int x, int y, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.directRender(context, index, x, y, mouseX, mouseY, hovered, tickDelta);

        if (clearButton != null) {
            clearButton.setX(x);
            clearButton.setY(y);

            clearButton.render(context, mouseX, mouseY, tickDelta);
        }
    }

    @Override
    protected void renderIcon(DrawContext context, int index, int x, int y, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.drawTexture(skinTextures.get().texture(), x + 3, y + 3, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.enableBlend();
        context.drawTexture(skinTextures.get().texture(), x + 3, y + 3, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.disableBlend();
    }

    @Override
    public DummyClientPlayerEntity getDummyPlayer() {
        if (client.world != null && client.getNetworkHandler() != null) {
            return new DummyClientPlayerEntity(
                    client.world.getPlayerByUuid(uuid), uuid, skinTextures.get(),
                    client.world, client.getNetworkHandler()
            );
        } else {
            return new DummyClientPlayerEntity(null, uuid, skinTextures.get());
        }
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(clearButton);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 0.6F));
    }
}
