package nl.enjarai.showmeyourskin.gui.widget;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

import java.util.function.Function;

public class SliderSet extends DisableableContainerWidget {
    private Function<EquipmentSlot, ItemStack> dummyEquipmentGetter;
    public final float rotatedBy;
    public final SoundEvent sound;

    public SliderSet(Position position, int width, int height, WidgetAdder adder, Function<EquipmentSlot, ItemStack> dummyEquipmentGetter, float rotatedBy, SoundEvent sound) {
        super(position, width, height);
        this.rotatedBy = rotatedBy;
        this.sound = sound;
        this.dummyEquipmentGetter = dummyEquipmentGetter;
        adder.addWidgets(this);
    }

    public void setDummyEquipmentGetter(Function<EquipmentSlot, ItemStack> dummyEquipmentGetter) {
        this.dummyEquipmentGetter = dummyEquipmentGetter;
    }

    public Function<EquipmentSlot, ItemStack> getDummyEquipmentGetter() {
        return dummyEquipmentGetter;
    }

    public interface WidgetAdder {
        void addWidgets(SliderSet container);
    }
}
