package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerSelectorEntry extends ConfigEntryWidget {
    public final UUID uuid;
    public final IconPressButtonWidget clearButton;

    public PlayerSelectorEntry(PlayerSelectorWidget parent, UUID uuid, Text name, Supplier<Identifier> skinTexture, Supplier<String> model) {
        super(parent, name, skinTexture, model);
        this.armorConfig = ModConfig.INSTANCE.getOverride(uuid);
        this.uuid = uuid;
        this.clearButton = new IconPressButtonWidget(Position.origin(), ShowMeYourSkin.id("textures/gui/button/delete_override.png"), 0, 0) {
            @Override
            protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
            }
        };
        this.clearButton.setCallback(button -> clearConfig());
        this.clearButton.getPosition().setAnchor(this);
        this.clearButton.setVisible(armorConfig != null);
        this.clearButton.setWidth(11);
        this.clearButton.setHeight(11);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected && armorConfig == null) {
            armorConfig = ModConfig.INSTANCE.getOrCreateOverride(uuid);
            clearButton.setVisible(true);
            ModConfig.INSTANCE.save();
        }
    }

    public void clearConfig() {
        if (selected) {
            parent.setSelected(null);
        }
        armorConfig = null;
        clearButton.setVisible(false);
        ModConfig.INSTANCE.deleteOverride(uuid);
        ModConfig.INSTANCE.save();
    }

    @Override
    protected void renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(texture.get(), getX() + 3, getY() + 3, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.enableBlend();
        graphics.drawTexture(texture.get(), getX() + 3, getY() + 3, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.disableBlend();
    }

    @Override
    public DummyClientPlayerEntity getDummyPlayer() {
        if (client.world != null && client.getNetworkHandler() != null) {
            return new DummyClientPlayerEntity(
                    client.world.getPlayerByUuid(uuid), uuid, texture.get(), model.get(),
                    client.world, client.getNetworkHandler()
            );
        } else {
            return new DummyClientPlayerEntity(null, uuid, texture.get(), model.get());
        }
    }

    @Override
    public List<SpruceWidget> children() {
        return ImmutableList.of(clearButton);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 0.6F));
    }
}
