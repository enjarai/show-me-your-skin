package nl.enjarai.showmeyourskin.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SliderSetTab extends AbstractSprucePressableButtonWidget {
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
    private final Identifier icon;
    private final SliderSet container;
    private @Nullable SliderSetManager manager;
    private boolean active;

    public SliderSetTab(Position position, Identifier icon, SliderSet container) {
        super(position, 32, 28, Text.empty());
        this.icon = icon;
        this.container = container;
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        int v = active ? 64 + 28 : 64;
        graphics.drawTexture(TABS_TEXTURE, position.getX(), position.getY(), 100, 32, v, 32, 28, 256, 256);
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        graphics.drawTexture(icon, position.getX() + (active ? 6 : 10), position.getY() + 5, 100, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
    }

    @Override
    public void onPress() {
        if (manager != null) {
            manager.setActiveTab(this);
        }
    }

    public void setContainerActive(boolean active) {
        this.active = active;
        this.container.setVisible(active);
    }

    public SliderSet getContainer() {
        return container;
    }

    public void setManager(@Nullable SliderSetManager manager) {
        this.manager = manager;
    }
}
