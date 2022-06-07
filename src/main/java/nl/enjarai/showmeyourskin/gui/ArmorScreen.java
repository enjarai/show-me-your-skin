package nl.enjarai.showmeyourskin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.jetbrains.annotations.Nullable;

public class ArmorScreen extends Screen {
    public static final Identifier TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen.png");
    private static final Text GLINT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.glintTooltip");
    private static final Text COMBAT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.combatTooltip");
    private static final Text NAME_TAG_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.nameTagTooltip");
    private static final Text SHOW_ELYTRA_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.showElytraTooltip");

    private final Screen parent;
    private final PlayerEntity player;
    private final ArmorConfig armorConfig;

    public ArmorScreen(PlayerEntity player, ArmorConfig armorConfig, Screen parent) {
        super(Text.translatable("gui.showmeyourskin.armorScreen.title"));
        this.player = player;
        this.armorConfig = armorConfig;
        this.parent = parent;
    }

    protected int getWindowLeft() {
        return (this.width - 238) / 2;
    }

    protected int getWindowRight() {
        return getWindowLeft() + 236;
    }

    protected int getWindowTop() {
        return (this.height - (16 + 16 * getHeightIterations())) / 2;
    }

    protected int getWindowBottom() {
        return getWindowTop() + 16 + 16 * getHeightIterations();
    }

    protected int getHeightIterations() {
        return 10;
    }

    protected TexturedButtonWidget getButton(int x, int y, int u, int v, ButtonWidget.PressAction pressAction, @Nullable Text tooltip) {
        return new TexturedButtonWidget(x, y, 20, 20, u, v, TEXTURE, pressAction) {
            @Override
            public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
                if (tooltip != null) {
                    ArmorScreen.this.renderTooltip(matrices, tooltip, mouseX, mouseY);
                }
            }
        };
    }

    protected TexturedButtonWidget getToggleButton(int x, int y, int u, int v, boolean initial, BooleanConsumer toggleAction, @Nullable Text tooltip) {
        var state = new Object() {
            boolean enabled = initial;
        };
        return getButton(x, y, u + (initial ? 0 : 20), v, button -> {
            ((TexturedButtonWidget) button).u += state.enabled ? 20 : -20;
            state.enabled = !state.enabled;

            toggleAction.accept(state.enabled);
        }, tooltip);
    }

    protected SliderWidget getTransparencySlider(EquipmentSlot slot, int x, int y, String translationKey) {
        var initialValue = armorConfig.getTransparency(slot);

        return new SliderWidget(getWindowLeft() + x, getWindowTop() + y,
                77, 20, Text.translatable(translationKey, initialValue), initialValue / 100f) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable(translationKey, (byte) (this.value * 100)));
            }

            @Override
            protected void applyValue() {
                armorConfig.setTransparency(slot, (byte) (this.value * 100));
            }
        };
    }

    protected TexturedButtonWidget getGlintButton(EquipmentSlot slot, int x, int y) {
        return getToggleButton(getWindowLeft() + x, getWindowTop() + y, 0, 38,
                armorConfig.getGlint(slot), b -> armorConfig.setGlint(slot, b), GLINT_TOOLTIP);
    }

    @Override
    protected void init() {
        addDrawableChild(getTransparencySlider(EquipmentSlot.HEAD, 14, 11,
                "gui.showmeyourskin.armorScreen.headSlider"));
        addDrawableChild(getTransparencySlider(EquipmentSlot.CHEST, 14, 35,
                "gui.showmeyourskin.armorScreen.chestSlider"));
        addDrawableChild(getTransparencySlider(EquipmentSlot.LEGS, 14, 59,
                "gui.showmeyourskin.armorScreen.legsSlider"));
        addDrawableChild(getTransparencySlider(EquipmentSlot.FEET, 14, 83,
                "gui.showmeyourskin.armorScreen.feetSlider"));

        addDrawableChild(getGlintButton(EquipmentSlot.HEAD, 94, 11));
        addDrawableChild(getGlintButton(EquipmentSlot.CHEST, 94, 35));
        addDrawableChild(getGlintButton(EquipmentSlot.LEGS, 94, 59));
        addDrawableChild(getGlintButton(EquipmentSlot.FEET, 94, 83));

        addDrawableChild(getToggleButton(getWindowLeft() + 14, getWindowTop() + 107, 40, 38,
                armorConfig.showInCombat, b -> armorConfig.showInCombat = b, COMBAT_TOOLTIP));
        addDrawableChild(getToggleButton(getWindowLeft() + 38, getWindowTop() + 107, 80, 38,
                armorConfig.showNameTag, b -> armorConfig.showNameTag = b, NAME_TAG_TOOLTIP));
        addDrawableChild(getToggleButton(getWindowLeft() + 62, getWindowTop() + 107, 120, 38,
                armorConfig.showElytra, b -> armorConfig.showElytra = b, SHOW_ELYTRA_TOOLTIP));

        addDrawableChild(getButton(getWindowLeft() + 14, getWindowBottom() - 31, 0, 78, button -> close(), null));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        var playerX = getWindowRight() - 59;
        var playerY = getWindowTop() + 155;

        InventoryScreen.drawEntity(playerX, playerY, 70, -mouseX + playerX, -mouseY + playerY - 110, player);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        super.renderBackground(matrices);
        int leftSide = getWindowLeft() + 3;
        int topSide = getWindowTop();
//        int iterationsNeeded = Math.max(52, this.height - 128 - 16) / 16;
        int iterationsNeeded = getHeightIterations();

        RenderSystem.setShaderTexture(0, TEXTURE);

        this.drawTexture(matrices, leftSide, topSide, 1, 1, 236, 8);
        for(int i = 0; i < iterationsNeeded; ++i) {
            this.drawTexture(matrices, leftSide, topSide + 8 + 16 * i, 1, 10, 236, 16);
        }
        this.drawTexture(matrices, leftSide, topSide + 8 + 16 * iterationsNeeded, 1, 27, 236, 8);
    }

    @Override
    public void close() {
        assert this.client != null;
        ModConfig.INSTANCE.save();
        this.client.setScreen(this.parent);
    }
}
