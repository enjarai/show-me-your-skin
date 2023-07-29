package nl.enjarai.showmeyourskin.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SliderSetTab extends AbstractSprucePressableButtonWidget {
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
    private final Identifier icon;
    public final SpruceContainerWidget container;

    public SliderSetTab(Position position, Identifier icon, SpruceContainerWidget container) {
        super(position, 32, 28, Text.empty());
        this.icon = icon;
        this.container = container;
    }

    public SliderSetTab(Position position, Identifier icon, int width, int height, WidgetAdder adder) {
        this(position, icon, new SpruceContainerWidget(x + 32, y, 200, 28));
        adder.addWidgets(container);
    }

    public void render(DrawContext context, int mouseX, int mouseY, boolean selected) {
        int v = selected ? 64 + 28 : 64;
        context.drawTexture(TABS_TEXTURE, x, y, 32, v, 32, 28);
        RenderSystem.enableBlend();
        context.drawTexture(icon, x + (selected ? 6 : 10), y + 5, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
    }

    @Override
    public void onPress() {

    }

    public interface WidgetAdder {
        void addWidgets(SpruceContainerWidget container);
    }
}
