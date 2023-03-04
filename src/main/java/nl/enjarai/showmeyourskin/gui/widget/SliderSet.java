package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.config.ArmorConfig;

import java.util.List;
import java.util.Map;

public class SliderSet extends AbstractParentElement implements Drawable, Element, Selectable {
    private final ArmorConfigWindow parent;
    private final int x;
    private final int y;
    private final List<ClickableWidget> sliders = Lists.newArrayList();

    public SliderSet(ArmorConfigWindow parent, int x, int y, String translationKeyPrefix,
                     Map<EquipmentSlot, ArmorConfig.ArmorPieceConfig> configMap) {
        super();
        this.parent = parent;
        this.x = x;
        this.y = y;

        sliders.add(getSlider(configMap.get(EquipmentSlot.HEAD),
                14, 11, translationKeyPrefix + ".head"));
        sliders.add(getSlider(configMap.get(EquipmentSlot.CHEST),
                14, 35, translationKeyPrefix + ".chest"));
        sliders.add(getSlider(configMap.get(EquipmentSlot.LEGS),
                14, 59, translationKeyPrefix + ".legs"));
        sliders.add(getSlider(configMap.get(EquipmentSlot.FEET),
                14, 83, translationKeyPrefix + ".feet"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (var drawable : sliders) {
            drawable.render(
                    matrices,
                    parent.isEditable() ? mouseX : -1,
                    parent.isEditable() ? mouseY : -1,
                    delta
            );
        }
    }

    protected SliderWidget getSlider(ArmorConfig.ArmorPieceConfig config, int x, int y, String translationKey) {
        var initialValue = config.getTransparency();

        return new SliderWidget(getLeft() + x, getTop() + y,
                101, 20, Text.translatable(translationKey, initialValue), initialValue / 100f) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable(translationKey, (byte) (this.value * 100)));
            }

            @Override
            protected void applyValue() {
                config.setTransparency((byte) (this.value * 100));
            }

            @Override
            public void playDownSound(SoundManager soundManager) {
                soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            @Override
            public void onRelease(double mouseX, double mouseY) {
            }
        };
    }

    public int getLeft() {
        return x;
    }

    public int getTop() {
        return y;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public List<? extends Element> children() {
        return sliders;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
