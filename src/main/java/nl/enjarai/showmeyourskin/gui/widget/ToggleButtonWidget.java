package nl.enjarai.showmeyourskin.gui.widget;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ToggleButtonWidget extends TexturedButtonWidget {
    private final Screen parent;
    @Nullable
    private final Text tooltip;
    protected boolean enabled;
    protected final BooleanConsumer toggleAction;

    public ToggleButtonWidget(Screen parent, int x, int y, int u, int v, Identifier texture, boolean initial, BooleanConsumer toggleAction, @Nullable Text tooltip) {
        super(x, y, 20, 20, u + (initial ? 0 : 20), v, texture, null);
        this.parent = parent;
        this.tooltip = tooltip;
        this.enabled = initial;
        this.toggleAction = toggleAction;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        setTooltip(Tooltip.of(tooltip));
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void onPress() {
        toggle();
    }

    public void toggle() {
        u += enabled ? 20 : -20;
        enabled = !enabled;

        toggleAction.accept(enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            toggle();
        }
    }
}
