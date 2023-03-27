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
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.config.ArmorConfig;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class SliderSet extends AbstractParentElement implements Drawable, Element, Selectable {
    private final ArmorConfigWindow parent;
    private final int x;
    private final int y;
    public final Function<EquipmentSlot, ItemStack> dummyEquipmentGetter;
    private final List<ClickableWidget> sliders = Lists.newArrayList();

    public SliderSet(ArmorConfigWindow parent, int x, int y, Consumer<List<ClickableWidget>> widgetAdder, Function<EquipmentSlot, ItemStack> dummyEquipmentGetter) {
        super();
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.dummyEquipmentGetter = dummyEquipmentGetter;

        widgetAdder.accept(sliders);
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
