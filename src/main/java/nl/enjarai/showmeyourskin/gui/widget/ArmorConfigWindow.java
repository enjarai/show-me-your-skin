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
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.client.cursed.AlwaysGlintingStack;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;

public class ArmorConfigWindow extends AbstractParentElement implements Drawable, Element, Selectable {
    public static final Identifier TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen.png");
    public static final Identifier BACKGROUND_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_background.png");
    public static final Identifier OVERLAY_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_disabled.png");
    private static final int TEXT_COLOR = 0x303030;
    private static final Text GLINT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.glintTooltip");
    private static final Text COMBAT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.combatTooltip");
    private static final Text NAME_TAG_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.nameTagTooltip");
    private static final Text SHOW_ELYTRA_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.showElytraTooltip");
    private static final Text SHIELD_GLINT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.shieldGlintTooltip");

    private static final ItemStack HEAD_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_HELMET);
    private static final ItemStack CHEST_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_CHESTPLATE);
    private static final ItemStack LEGS_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_LEGGINGS);
    private static final ItemStack FEET_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_BOOTS);
    private static final ItemStack SHIELD = new AlwaysGlintingStack(Items.SHIELD);
    private static final ItemStack ELYTRA = new AlwaysGlintingStack(Items.ELYTRA);

    private final List<ClickableWidget> buttons = Lists.newArrayList();
    private final List<Element> children = Lists.newArrayList();
    private final List<SliderSetTab> sliderSetTabs = Lists.newArrayList();
    private SliderSetTab selectedSliderSetTab;
    private final Screen parent;
    public int x;
    public int y;
    private final Text name;
    private final DummyClientPlayerEntity player;
    private final ArmorConfig armorConfig;

    public ArmorConfigWindow(Screen parent, int x, int y, Text name, DummyClientPlayerEntity player, ArmorConfig armorConfig) {
        super();
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.name = name;
        this.player = player;
        this.armorConfig = armorConfig;

        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 12, 0, 240,
                new SliderSet(this, getWindowLeft(), getWindowTop(), sliders -> {
                    sliders.add(getSlider(HideableEquipment.HEAD,
                            14, 11, "gui.showmeyourskin.armorScreen.piece.head"));
                    sliders.add(getSlider(HideableEquipment.CHEST,
                            14, 35, "gui.showmeyourskin.armorScreen.piece.chest"));
                    sliders.add(getSlider(HideableEquipment.LEGS,
                            14, 59, "gui.showmeyourskin.armorScreen.piece.legs"));
                    sliders.add(getSlider(HideableEquipment.FEET,
                            14, 83, "gui.showmeyourskin.armorScreen.piece.feet"));

                    sliders.add(getGlintButton(HideableEquipment.HEAD, 94, 11));
                    sliders.add(getGlintButton(HideableEquipment.CHEST, 94, 35));
                    sliders.add(getGlintButton(HideableEquipment.LEGS, 94, 59));
                    sliders.add(getGlintButton(HideableEquipment.FEET, 94, 83));
                }, ArmorConfigWindow::getDummyArmor))
        );
        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 42, 16, 240,
                new SliderSet(this, getWindowLeft(), getWindowTop(), sliders -> {
                    sliders.add(getSlider(HideableEquipment.ELYTRA,
                            14, 11, "gui.showmeyourskin.armorScreen.piece.elytra"));
                    sliders.add(getSlider(HideableEquipment.SHIELD,
                            14, 35, "gui.showmeyourskin.armorScreen.piece.shield"));

                    sliders.add(getGlintButton(HideableEquipment.ELYTRA, 94, 11));
                    sliders.add(getGlintButton(HideableEquipment.SHIELD, 94, 35));
                }, ArmorConfigWindow::getDummyEquipment))
        );

        selectTab(sliderSetTabs.get(0));

        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 14, getWindowTop() + 115, 40, 38,
                TEXTURE, armorConfig.showInCombat, b -> armorConfig.showInCombat = b, COMBAT_TOOLTIP));
        buttons.add(new ToggleButtonWidget(parent, getWindowLeft() + 40, getWindowTop() + 115, 80, 38,
                TEXTURE, armorConfig.showNameTag, b -> armorConfig.showNameTag = b, NAME_TAG_TOOLTIP));

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

    protected SliderWidget getSlider(HideableEquipment slot, int x, int y, String translationKey) {
        var pieceConfig = armorConfig.getPieces().get(slot);
        var trimConfig = armorConfig.getTrims().get(slot.toSlot());
        var initialValue = pieceConfig.getTransparency();

        return new SliderWidget(getWindowLeft() + x, getWindowTop() + y,
                77, 20, Text.translatable(translationKey, initialValue), initialValue / 100f) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable(translationKey, (byte) (this.value * 100)));
            }

            @Override
            protected void applyValue() {
                pieceConfig.setTransparency((byte) (this.value * 100));
                if (trimConfig != null) trimConfig.setTransparency((byte) (this.value * 100));
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

    protected TexturedButtonWidget getGlintButton(HideableEquipment slot, int x, int y) {
        return new ToggleButtonWidget(
                parent, getWindowLeft() + x, getWindowTop() + y, 0, 38,
                TEXTURE, armorConfig.getGlints().get(slot).getTransparency() > 0,
                b -> armorConfig.getGlints().get(slot).setTransparency((byte) (b ? 100 : 0)), GLINT_TOOLTIP
        );
    }

    public boolean isEditable() {
        return ModConfig.INSTANCE.globalEnabled;
    }

    public void selectTab(SliderSetTab sliderSetTab) {
        selectedSliderSetTab = sliderSetTab;
        player.equippedStackSupplier = sliderSetTab.sliderSet.dummyEquipmentGetter;
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

    private static ItemStack getDummyArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> HEAD_ARMOR;
            case CHEST -> CHEST_ARMOR;
            case LEGS -> LEGS_ARMOR;
            case FEET -> FEET_ARMOR;
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getDummyEquipment(EquipmentSlot slot) {
        return switch (slot) {
            case OFFHAND -> SHIELD;
            case CHEST -> ELYTRA;
            default -> ItemStack.EMPTY;
        };
    }
}
