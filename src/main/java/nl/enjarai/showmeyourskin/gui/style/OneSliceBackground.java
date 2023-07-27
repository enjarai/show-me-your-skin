package nl.enjarai.showmeyourskin.gui.style;

import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

public class OneSliceBackground implements Background {
    public static final OneSliceBackground WINDOW = new OneSliceBackground(ShowMeYourSkin.id("textures/gui/border/window.png"), 16, 4);
    public static final OneSliceBackground INDENT = new OneSliceBackground(ShowMeYourSkin.id("textures/gui/border/indent.png"), 16, 1);
    public static final OneSliceBackground DARK_INDENT = new OneSliceBackground(ShowMeYourSkin.id("textures/gui/border/dark_indent.png"), 16, 1);

    private final Identifier texture;
    private final int textureScale;
    private final int borderThickness;

    public OneSliceBackground(Identifier texture, int textureScale, int borderThickness) {
        this.texture = texture;
        this.textureScale = textureScale;
        this.borderThickness = borderThickness;
    }

    @Override
    public void render(DrawContext graphics, SpruceWidget widget, int vOffset, int mouseX, int mouseY, float delta) {
        int bgSize = textureScale - borderThickness * 2;
        int coverWidth = widget.getWidth() - borderThickness * 2;
        int coverHeight = widget.getHeight() - borderThickness * 2;
        for (int x = 0; x < coverWidth; x += bgSize) {
            for (int y = 0; y < coverHeight; y += bgSize) {
                graphics.drawTexture(
                        texture,
                        widget.getX() + borderThickness + x, widget.getY() + borderThickness + y,
                        borderThickness, borderThickness,
                        Math.min(bgSize, coverWidth - x), Math.min(bgSize, coverHeight - y),
                        textureScale, textureScale
                );
            }
        }
    }
}
