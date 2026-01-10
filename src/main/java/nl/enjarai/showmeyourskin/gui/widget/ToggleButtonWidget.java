package nl.enjarai.showmeyourskin.gui.widget;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ToggleButtonWidget extends PressButtonWidget {
    protected boolean enabled;
    protected final BiConsumer<ToggleButtonWidget, Boolean> toggleAction;

    public ToggleButtonWidget(int x, int y, int width, int height, ButtonTextures textures,
                              boolean initial, BiConsumer<ToggleButtonWidget, Boolean> toggleAction,
                              @Nullable Text tooltip) {
        super(x, y, width, height, textures, btn -> {}, tooltip);
        this.enabled = initial;
        this.toggleAction = toggleAction;
    }

    @Override
    public void onPress(AbstractInput input) {
        toggle();
    }

    public void toggle() {
        enabled = !enabled;

        toggleAction.accept(this, enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            toggle();
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static ButtonTextures createTextures(String path) {
        return new ButtonTextures(
                ShowMeYourSkin.id("button/" + path), ShowMeYourSkin.id("button/" + path + "_disabled"),
                ShowMeYourSkin.id("button/" + path)
        );
    }
}
