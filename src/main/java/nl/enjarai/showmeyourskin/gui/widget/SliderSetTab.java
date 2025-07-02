package nl.enjarai.showmeyourskin.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

public class SliderSetTab {
    public static final Identifier ICON_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen.png");
    private final int x;
    private final int y;
    private final int iconX;
    private final int iconY;
    public final SliderSet sliderSet;

    public SliderSetTab(int x, int y, int iconX, int iconY, SliderSet sliderSet) {
        this.x = x;
        this.y = y;
        this.iconX = iconX;
        this.iconY = iconY;
        this.sliderSet = sliderSet;
    }

    public void render(DrawContext context, int mouseX, int mouseY, boolean selected) {
        int v = selected ? 64 + 28 : 64;
        AdvancementTabType.LEFT.drawBackground(context, x + 28, y - 29, selected, 1); // index 1 always renders the "middle" texture
        context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, x + (selected ? 6 : 10), y + 5, iconX, iconY, 16, 16, 256, 256);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return (mouseX >= x && mouseY >= y && mouseX < x + 32 && mouseY < y + 28)||sliderSet.isMouseOver(mouseX, mouseY);
    }
    public boolean isMouseOver2(double mouseX, double mouseY) {
        return (mouseX >= x && mouseY >= y && mouseX < x + 32 && mouseY < y + 28);
    }
}
