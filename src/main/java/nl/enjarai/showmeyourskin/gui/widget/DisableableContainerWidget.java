package nl.enjarai.showmeyourskin.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.DrawContext;

public class DisableableContainerWidget extends SpruceContainerWidget {
    public DisableableContainerWidget(Position position, int width, int height) {
        super(position, width, height);
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(graphics, mouseX, mouseY, delta);

        if (!isActive()) {
            RenderSystem.enableBlend();
            graphics.fill(
                    getX() + 1, getY() + 1,
                    getX() + getWidth() - 1, getY() + getHeight() - 1,
                    999, 0xB0000000
            );
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        children().forEach(child -> child.setActive(active));
    }
}
