package nl.enjarai.showmeyourskin.gui.widget;

import dev.lambdaurora.spruceui.Position;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IconToggleButtonWidget extends AbstractIconButtonWidget {
    private boolean value;
    private @Nullable ToggleCallback callback;

    public IconToggleButtonWidget(Position position, Identifier texture, int u, int v, boolean initial) {
        super(position, texture, u, v);
        this.value = initial;
    }

    @Override
    protected boolean isDisabled() {
        return !value;
    }

    @Override
    public void onPress() {
        value = !value;
        if (callback != null) {
            callback.onToggle(this, value);
        }
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setCallback(@Nullable ToggleCallback callback) {
        this.callback = callback;
    }

    public interface ToggleCallback {
        void onToggle(IconToggleButtonWidget button, boolean value);
    }
}
