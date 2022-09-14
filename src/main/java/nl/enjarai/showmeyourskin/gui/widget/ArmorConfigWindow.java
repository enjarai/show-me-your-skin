package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorConfigWindow extends AbstractParentElement implements Drawable, Element, Selectable {
    public static final Identifier TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen.png");
    public static final Identifier OVERLAY_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_disabled.png");
    private static final int TEXT_COLOR = 0x303030;
    private static final Text GLINT_TOOLTIP = new TranslatableText("gui.showmeyourskin.armorScreen.glintTooltip");
    private static final Text COMBAT_TOOLTIP = new TranslatableText("gui.showmeyourskin.armorScreen.combatTooltip");
    private static final Text NAME_TAG_TOOLTIP = new TranslatableText("gui.showmeyourskin.armorScreen.nameTagTooltip");
    private static final Text SHOW_ELYTRA_TOOLTIP = new TranslatableText("gui.showmeyourskin.armorScreen.showElytraTooltip");
    private static final Text SHIELD_GLINT_TOOLTIP = new TranslatableText("gui.showmeyourskin.armorScreen.shieldGlintTooltip");

    private final List<ClickableWidget> buttons = Lists.newArrayList();
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

        buttons.add(getTransparencySlider(EquipmentSlot.HEAD, 14, 11,
                "gui.showmeyourskin.armorScreen.headSlider"));
        buttons.add(getTransparencySlider(EquipmentSlot.CHEST, 14, 35,
                "gui.showmeyourskin.armorScreen.chestSlider"));
        buttons.add(getTransparencySlider(EquipmentSlot.LEGS, 14, 59,
                "gui.showmeyourskin.armorScreen.legsSlider"));
        buttons.add(getTransparencySlider(EquipmentSlot.FEET, 14, 83,
                "gui.showmeyourskin.armorScreen.feetSlider"));

        buttons.add(getGlintButton(EquipmentSlot.HEAD, 94, 11));
        buttons.add(getGlintButton(EquipmentSlot.CHEST, 94, 35));
        buttons.add(getGlintButton(EquipmentSlot.LEGS, 94, 59));
        buttons.add(getGlintButton(EquipmentSlot.FEET, 94, 83));

        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 14, getWindowTop() + 107, 40, 38,
                TEXTURE, armorConfig.showInCombat, b -> armorConfig.showInCombat = b, COMBAT_TOOLTIP));
        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 38, getWindowTop() + 107, 80, 38,
                TEXTURE, armorConfig.showNameTag, b -> armorConfig.showNameTag = b, NAME_TAG_TOOLTIP));
        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 62, getWindowTop() + 107, 120, 38,
                TEXTURE, armorConfig.showElytra, b -> armorConfig.showElytra = b, SHOW_ELYTRA_TOOLTIP));
        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 94, getWindowTop() + 107, 160, 38,
                TEXTURE, armorConfig.showShieldGlint, b -> armorConfig.showShieldGlint = b, SHIELD_GLINT_TOOLTIP));
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

    protected SliderWidget getTransparencySlider(EquipmentSlot slot, int x, int y, String translationKey) {
        var initialValue = armorConfig.getTransparency(slot);

        return new SliderWidget(getWindowLeft() + x, getWindowTop() + y,
                77, 20, new TranslatableText(translationKey, initialValue), initialValue / 100f) {
            @Override
            protected void updateMessage() {
                setMessage(new TranslatableText(translationKey, (byte) (this.value * 100)));
            }

            @Override
            protected void applyValue() {
                armorConfig.setTransparency(slot, (byte) (this.value * 100));
            }

            @Override
            public void playDownSound(SoundManager soundManager) {
                soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            @Override
            public void onRelease(double mouseX, double mouseY) {
            }
        };
    }

    protected TexturedButtonWidget getGlintButton(EquipmentSlot slot, int x, int y) {
        return new ToggleButtonWidget(parent, getWindowLeft() + x, getWindowTop() + y, 0, 38,
                TEXTURE, armorConfig.getGlint(slot), b -> armorConfig.setGlint(slot, b), GLINT_TOOLTIP);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices, TEXTURE, -999);
        for (var drawable : buttons) {
            drawable.render(
                    matrices,
                    isEditable() ? mouseX : -1,
                    isEditable() ? mouseY : -1,
                    delta
            );
        }

        var playerX = getWindowRight() - 59;
        var playerY = getWindowTop() + 155;

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(
                matrices, name,
                getWindowRight() - 110, getWindowTop() + 10, TEXT_COLOR
        );

        InventoryScreen.drawEntity(playerX, playerY, 70, -mouseX + playerX, -mouseY + playerY - 110, player);

        if (!isEditable()) {
            renderBackground(matrices, OVERLAY_TEXTURE, 999);
        }
    }

    public void renderBackground(MatrixStack matrices, Identifier backgroundTexture, int zIndex) {
        int leftSide = getWindowLeft() + 3;
        int topSide = getWindowTop();
        int iterationsNeeded = getHeightIterations();

        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();

        matrices.push();
        matrices.translate(0, 0, zIndex);
        drawTexture(matrices, leftSide, topSide, 1, 1, 236, 8);
        for(int i = 0; i < iterationsNeeded; ++i) {
            drawTexture(matrices, leftSide, topSide + 8 + 16 * i, 1, 10, 236, 16);
        }
        drawTexture(matrices, leftSide, topSide + 8 + 16 * iterationsNeeded, 1, 27, 236, 8);
        matrices.pop();

        RenderSystem.disableBlend();
    }

    private boolean isEditable() {
        return ModConfig.INSTANCE.globalEnabled;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public List<? extends Element> children() {
        return buttons;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isEditable() && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
