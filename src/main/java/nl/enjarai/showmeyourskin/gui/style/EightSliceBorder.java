package nl.enjarai.showmeyourskin.gui.style;

import dev.lambdaurora.spruceui.border.Border;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

public class EightSliceBorder implements Border {
    public static final EightSliceBorder WINDOW = new EightSliceBorder(ShowMeYourSkin.id("textures/gui/border/window.png"), 16, 4);
    public static final EightSliceBorder INDENT = new EightSliceBorder(ShowMeYourSkin.id("textures/gui/border/indent.png"), 16, 1);
    public static final EightSliceBorder DARK_INDENT = new EightSliceBorder(ShowMeYourSkin.id("textures/gui/border/dark_indent.png"), 16, 1);

    private final Identifier texture;
    private final int textureScale;
    private final int thickness;

    public EightSliceBorder(Identifier texture, int textureScale, int thickness) {
        this.texture = texture;
        this.textureScale = textureScale;
        this.thickness = thickness;
    }

    @Override
    public void render(DrawContext graphics, SpruceWidget widget, int mouseX, int mouseY, float delta) {
        int left = widget.getX();
        int top = widget.getY();
        int xShift = widget.getWidth() - getThickness();
        int yShift = widget.getHeight() - getThickness();
        int textureShift = textureScale - getThickness();

        // Corners
        for (int i = 0; i < 4; i++) {
            int x = i % 2;
            int y = i / 2;

            graphics.drawTexture(
                    texture,
                    left + xShift * x, top + yShift * y,
                    textureShift * x, textureShift * y,
                    getThickness(), getThickness(),
                    textureScale, textureScale
            );
        }

        // Edges
        int cornerSize = getThickness() * 2;
        int blockSize = textureScale - cornerSize;
        for (int i = 0; i < 4; i++) {
            int isX = (i + 1) % 2;
            int isY = i % 2;
            int isShift = i / 2;

            int localXShift = xShift * isX * isShift;
            int localYShift = yShift * isY * isShift;
            int sideLength = (widget.getWidth() - cornerSize) * isY + (widget.getHeight() - cornerSize) * isX;
            for (int j = 0; j < sideLength; j += blockSize) {
                int endCutoff = Math.min(sideLength - j, blockSize);
                graphics.drawTexture(
                        texture,
                        left + getThickness() * isY + localXShift + j * isY, top + getThickness() * isX + localYShift + j * isX,
                        getThickness() * isY + textureShift * isX * isShift, getThickness() * isX + textureShift * isY * isShift,
                        getThickness() * isX + endCutoff * isY, getThickness() * isY + endCutoff * isX,
                        textureScale, textureScale
                );
            }
        }
    }

    @Override
    public int getThickness() {
        return thickness;
    }
}
