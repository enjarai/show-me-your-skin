package nl.enjarai.showmeyourskin.gui.widget;

import dev.lambdaurora.spruceui.Position;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IconPressButtonWidget extends AbstractIconButtonWidget {
    private @Nullable PressCallback callback;

    public IconPressButtonWidget(Position position, Identifier texture, int u, int v) {
        super(position, texture, u, v);
    }

    @Override
    protected boolean isDisabled() {
        return !isActive();
    }

    @Override
    public void onPress() {
        if (callback != null) {
            callback.onPress(this);
        }
    }

    public void setCallback(@Nullable PressCallback callback) {
        this.callback = callback;
    }

    public interface PressCallback {
        void onPress(IconPressButtonWidget button);
    }
}
