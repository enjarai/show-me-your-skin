package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.AbstractSpruceParentWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;
import java.util.function.Supplier;

public class ConfigEntryWidget extends AbstractSpruceParentWidget<SpruceWidget> {
    protected static final int WHITE = 0xEEEEEEEE;
    protected static final int GRAY = 0xCCCCCCCC;
    protected static final Identifier SELECTION_TEXTURE = ShowMeYourSkin.id("textures/gui/selection.png");

    public final PlayerSelectorWidget parent;
    private final Text name;
    public final Supplier<Identifier> texture;
    public final Supplier<String> model;
    protected ArmorConfig armorConfig;
    public boolean selected = false;

    public ConfigEntryWidget(PlayerSelectorWidget parent, Text name, Supplier<Identifier> texture, Supplier<String> model) {
        super(Position.origin(), SpruceWidget.class);
        this.parent = parent;
        this.name = name;
        this.texture = texture;
        this.model = model;
        this.armorConfig = ModConfig.INSTANCE.global;
        this.width = 30;
        this.height = 30;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            if (!super.mouseClicked(mouseX, mouseY, button)) {
                playDownSound(client.getSoundManager());
                parent.setSelected(this);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        var x = getX();
        var y = getY();

        RenderSystem.enableBlend();
        if (selected) {
            graphics.drawTexture(SELECTION_TEXTURE, x - 1, y - 1, 0, 0, 32, 32, 128, 128);
        } else if (isMouseHovered() && children().stream().noneMatch(SpruceWidget::isMouseHovered)) {
            graphics.drawTexture(SELECTION_TEXTURE, x - 1, y - 1, 32, 0, 32, 32, 128, 128);
        }
        renderIcon(graphics, mouseX, mouseY, delta);
        RenderSystem.disableBlend();

        children().forEach(e -> e.render(graphics, mouseX, mouseY, delta));
    }

    protected void renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(texture.get(), getX() + 3, getY() + 3, 24, 24, 0, 0, 24, 24, 24, 24);
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 0.4F));
    }

    @Override
    public List<SpruceWidget> children() {
        return ImmutableList.of();
    }

    public ArmorConfig getArmorConfig() {
        return armorConfig;
    }

    public DummyClientPlayerEntity getDummyPlayer() {
        return DummyClientPlayerEntity.getInstance();
    }

    public Text getName() {
        return name;
    }
}
