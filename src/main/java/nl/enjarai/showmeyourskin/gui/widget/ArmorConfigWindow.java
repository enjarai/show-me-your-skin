package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;

public class ArmorConfigWindow extends AbstractParentElement implements Drawable, Element, Selectable {
    public static final Identifier TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen.png");
    public static final Identifier BACKGROUND_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_background.png");
    public static final Identifier OVERLAY_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_disabled.png");
    private static final int TEXT_COLOR = 0x303030;
    private static final Text COMBAT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.combatTooltip");
    private static final Text NAME_TAG_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.nameTagTooltip");
    private static final Text SHOW_ELYTRA_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.showElytraTooltip");
    private static final Text SHIELD_GLINT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.shieldGlintTooltip");

    private final List<ClickableWidget> buttons = Lists.newArrayList();
    private final List<Element> children = Lists.newArrayList();
    private final List<SliderSetTab> sliderSetTabs = Lists.newArrayList();
    private SliderSetTab selectedSliderSetTab;
    private final Screen parent;
    public int x;
    public int y;
    private final Text name;
    private final PlayerEntity player;
    private final ArmorConfig armorConfig;

    public ArmorConfigWindow(Screen parent, int x, int y, Text name, PlayerEntity player, ArmorConfig armorConfig) {
        super();
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.name = name;
        this.player = player;
        this.armorConfig = armorConfig;

        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 12, 0, 0,
                new SliderSet(this, getWindowLeft(), getWindowTop(),
                        "gui.showmeyourskin.armorScreen.piece", armorConfig.getPieces())));
        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 42, 0, 0,
                new SliderSet(this, getWindowLeft(), getWindowTop(),
                        "gui.showmeyourskin.armorScreen.trim", armorConfig.getTrims())));
        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 72, 0, 0,
                new SliderSet(this, getWindowLeft(), getWindowTop(),
                        "gui.showmeyourskin.armorScreen.glint", armorConfig.getGlints())));

        selectedSliderSetTab = sliderSetTabs.get(0);

        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 14, getWindowTop() + 115, 40, 38,
                TEXTURE, armorConfig.showInCombat, b -> armorConfig.showInCombat = b, COMBAT_TOOLTIP));
        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 40, getWindowTop() + 115, 80, 38,
                TEXTURE, armorConfig.showNameTag, b -> armorConfig.showNameTag = b, NAME_TAG_TOOLTIP));
        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 66, getWindowTop() + 115, 120, 38,
                TEXTURE, armorConfig.showElytra, b -> armorConfig.showElytra = b, SHOW_ELYTRA_TOOLTIP));
        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 92, getWindowTop() + 115, 160, 38,
                TEXTURE, armorConfig.showShieldGlint, b -> armorConfig.showShieldGlint = b, SHIELD_GLINT_TOOLTIP));

        children.addAll(buttons);
    }

    protected int getWindowLeft() {
        return x;
    }

    protected int getWindowRight() {
        return getWindowLeft() + 236;
    }

    protected int getWindowTop() {
        return y;
    }

    protected int getWindowBottom() {
        return getWindowTop() + 16 + 16 * getHeightIterations();
    }

    protected int getHeightIterations() {
        return 10;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (var sliderSetTabs : sliderSetTabs) {
            sliderSetTabs.render(matrices, mouseX, mouseY, sliderSetTabs == selectedSliderSetTab);
        }

        renderBackground(matrices, BACKGROUND_TEXTURE, -999);
        for (var drawable : buttons) {
            drawable.render(
                    matrices,
                    isEditable() ? mouseX : -1,
                    isEditable() ? mouseY : -1,
                    delta
            );
        }
        selectedSliderSetTab.sliderSet.render(
                matrices,
                isEditable() ? mouseX : -1,
                isEditable() ? mouseY : -1,
                delta
        );

        var playerX = getWindowRight() - 59;
        var playerY = getWindowTop() + 155;

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(
                matrices, name,
                getWindowRight() - 110, getWindowTop() + 10, TEXT_COLOR
        );

        InventoryScreen.drawEntity(matrices, playerX, playerY, 70, -mouseX + playerX, -mouseY + playerY - 110, player);

        if (!isEditable()) {
            renderBackground(matrices, OVERLAY_TEXTURE, 999);
        }
    }

    public void renderBackground(MatrixStack matrices, Identifier backgroundTexture, int zIndex) {
        int leftSide = getWindowLeft() + 3;
        int topSide = getWindowTop();

        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();

        matrices.push();
        matrices.translate(0, 0, zIndex);
        drawTexture(matrices, leftSide, topSide, 1, 1, 236, 254);
        matrices.pop();

        RenderSystem.disableBlend();
    }

    public boolean isEditable() {
        return ModConfig.INSTANCE.globalEnabled;
    }

    public void selectTab(SliderSetTab sliderSetTab) {
        selectedSliderSetTab = sliderSetTab;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isEditable()) {
            return false;
        }

        for (var sliderSetTab : sliderSetTabs) {
            if (sliderSetTab.isMouseOver(mouseX, mouseY)) {
                MinecraftClient.getInstance().getSoundManager()
                        .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                selectTab(sliderSetTab);
                return true;
            }
        }

        if (selectedSliderSetTab.sliderSet.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isEditable()) {
            return false;
        }

        if (selectedSliderSetTab.sliderSet.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
