package nl.enjarai.showmeyourskin.gui.widget;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

public abstract class AbstractIconButtonWidget extends AbstractSprucePressableButtonWidget {
    public static final Identifier BACKGROUND_TEXTURE = ShowMeYourSkin.id("textures/gui/button/background.png");

    private Identifier texture;
    private int u;
    private int v;

    public AbstractIconButtonWidget(Position position, Identifier texture, int u, int v) {
        super(position, 20, 20, Text.empty());
        this.texture = texture;
        this.u = u;
        this.v = v;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isMouseOver(mouseX, mouseY)) {
            getTooltip().ifPresent(tooltip -> {
                var wrappedTooltipText = MinecraftClient.getInstance().textRenderer
                        .wrapLines(tooltip, Math.max(getWidth() * 2 / 3, 200));
                Tooltip.create(mouseX, mouseY, wrappedTooltipText).queue();
            });
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(
                BACKGROUND_TEXTURE,
                getX(), getY(),
                isDisabled() ? getWidth() : 0, isFocusedOrHovered() ? getHeight() : 0,
                getWidth(), getHeight(),
                64, 64
        );
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(
                texture,
                getX(), getY(),
                isDisabled() ? getWidth() : 0, isFocusedOrHovered() ? getHeight() : 0,
                getWidth(), getHeight(),
                64, 64
        );
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected abstract boolean isDisabled();
}
