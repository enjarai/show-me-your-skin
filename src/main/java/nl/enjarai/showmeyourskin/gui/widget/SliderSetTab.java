package nl.enjarai.showmeyourskin.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

public class SliderSetTab extends DrawableHelper {
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
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

    public void render(MatrixStack matrices, int mouseX, int mouseY, boolean selected) {
        int v = selected ? 64 + 28 : 64;
        RenderSystem.setShaderTexture(0, TABS_TEXTURE);
        DrawableHelper.drawTexture(matrices, x, y, 32, v, 32, 28);
        RenderSystem.setShaderTexture(0, ICON_TEXTURE);
        DrawableHelper.drawTexture(matrices, x + (selected ? 6 : 10), y + 5, iconX, iconY, 16, 16);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + 32 && mouseY < y + 28;
    }
}
