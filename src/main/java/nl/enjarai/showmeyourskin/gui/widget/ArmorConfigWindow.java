package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import nl.enjarai.cicada.api.screen.DrawUtils;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.client.cursed.AlwaysGlintingStack;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.Consumer;

public class ArmorConfigWindow extends AbstractParentElement implements Drawable, Element, Selectable {
    public static final Identifier TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen.png");
    public static final Identifier BACKGROUND_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_background.png");
    public static final Identifier OVERLAY_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_disabled.png");
    private static final int TEXT_COLOR = 0x505050;
    private static final int TEXT_COLOR_RED = 0x880000;
    private static final Text GLINT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.glintTooltip");
    private static final Text COMBAT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.combatTooltip");
    private static final Text NAME_TAG_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.nameTagTooltip");
    protected static final ButtonTextures TOGGLE_GLINT_BUTTON_TEXTURES = ToggleButtonWidget.createTextures("show_glint");
    protected static final ButtonTextures SHOW_IN_COMBAT_BUTTON_TEXTURES = ToggleButtonWidget.createTextures("show_in_combat");
    protected static final ButtonTextures SHOW_NAME_TAG_BUTTON_TEXTURES = ToggleButtonWidget.createTextures("show_nametag");
    protected static final ButtonTextures FORCE_ELYTRA_WHEN_FLYING_BUTTON_TEXTURES = ToggleButtonWidget.createTextures("force_elytra_when_flying");

    private static final ItemStack HEAD_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_HELMET);
    private static final ItemStack CHEST_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_CHESTPLATE);
    private static final ItemStack LEGS_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_LEGGINGS);
    private static final ItemStack FEET_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_BOOTS);
    private static final ItemStack SHIELD = new AlwaysGlintingStack(Items.SHIELD);
    private static final ItemStack ELYTRA = new AlwaysGlintingStack(Items.ELYTRA);
    private static final ItemStack HAT = new AlwaysGlintingStack(Items.CREEPER_HEAD);

    private final List<ClickableWidget> buttons = Lists.newArrayList();
    private final List<Element> children = Lists.newArrayList();
    private final List<SliderSetTab> sliderSetTabs = Lists.newArrayList();
    private SliderSetTab selectedSliderSetTab;
    private float lastPlayerRotation;
    private long lastTabSwitchTime;
    private final Screen parent;
    public int x;
    public int y;
    private final Text name;
    private final DummyClientPlayerEntity player;
    private final ArmorConfig armorConfig;

    public ArmorConfigWindow(Screen parent, int x, int y, Text name, DummyClientPlayerEntity player, ArmorConfig armorConfig, int tabIndex, boolean allowAllOptions) {
        super();
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.name = name;
        this.player = player;
        this.armorConfig = armorConfig;

        var serverConfig = ShowMeYourSkinClient.HANDSHAKE_CLIENT.getConfig();
        var hideOptions = serverConfig.isPresent() && !allowAllOptions;

        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 12, 0, 240,
                new SliderSet(this, getWindowLeft(), getWindowTop(), sliders -> {
                    sliders.add(getOpacitySlider(HideableEquipment.HEAD,
                            14, 11, "gui.showmeyourskin.armorScreen.piece.head"));
                    sliders.add(getOpacitySlider(HideableEquipment.CHEST,
                            14, 35, "gui.showmeyourskin.armorScreen.piece.chest"));
                    sliders.add(getOpacitySlider(HideableEquipment.LEGS,
                            14, 59, "gui.showmeyourskin.armorScreen.piece.legs"));
                    sliders.add(getOpacitySlider(HideableEquipment.FEET,
                            14, 83, "gui.showmeyourskin.armorScreen.piece.feet"));

                    sliders.add(getGlintButton(HideableEquipment.HEAD, 94, 11));
                    sliders.add(getGlintButton(HideableEquipment.CHEST, 94, 35));
                    sliders.add(getGlintButton(HideableEquipment.LEGS, 94, 59));
                    sliders.add(getGlintButton(HideableEquipment.FEET, 94, 83));
                }, ArmorConfigWindow::getDummyArmor, 0, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE.value()))
        );
        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 42, 16, 240,
                new SliderSet(this, getWindowLeft(), getWindowTop(), sliders -> {
                    sliders.add(getOpacitySlider(HideableEquipment.ELYTRA,
                            14, 11, "gui.showmeyourskin.armorScreen.piece.elytra"));
                    sliders.add(getOpacitySlider(HideableEquipment.SHIELD,
                            14, 35, "gui.showmeyourskin.armorScreen.piece.shield"));
                    sliders.add(getOpacitySlider(HideableEquipment.HAT,
                            14, 59, "gui.showmeyourskin.armorScreen.piece.hat"));

                    sliders.add(getGlintButton(HideableEquipment.ELYTRA, 94, 11));
                    sliders.add(getGlintButton(HideableEquipment.SHIELD, 94, 35));
                    sliders.add(getGlintButton(HideableEquipment.HAT, 94, 59));
                }, ArmorConfigWindow::getDummyEquipment, -180, SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value()))
        );

        selectTab(sliderSetTabs.get(tabIndex));
        lastPlayerRotation = selectedSliderSetTab.sliderSet.rotatedBy;

        if (!hideOptions || serverConfig.get().allowNotShowInCombat()) {
            buttons.add(new ToggleButtonWidget(
                    getWindowLeft() + 14, getWindowTop() + 115, 20, 20,
                    SHOW_IN_COMBAT_BUTTON_TEXTURES,
                    armorConfig.showInCombat, (btn, b) -> armorConfig.showInCombat = b, COMBAT_TOOLTIP
            ));
        }

        if (!hideOptions || serverConfig.get().allowNotShowNameTag()) {
            buttons.add(new ToggleButtonWidget(
                    getWindowLeft() + 40, getWindowTop() + 115, 20, 20,
                    SHOW_NAME_TAG_BUTTON_TEXTURES,
                    armorConfig.showNameTag, (btn, b) -> armorConfig.showNameTag = b, NAME_TAG_TOOLTIP
            ));
        }

        if (!hideOptions || serverConfig.get().allowNotForceElytraWhenFlying()) {
            buttons.add(new ToggleButtonWidget(
                    getWindowLeft() + 66, getWindowTop() + 115, 20, 20,
                    FORCE_ELYTRA_WHEN_FLYING_BUTTON_TEXTURES,
                    armorConfig.forceElytraWhenFlying, (btn, b) -> armorConfig.forceElytraWhenFlying = b, NAME_TAG_TOOLTIP
            ));
        }

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

    public ArmorConfig getArmorConfig() {
        return armorConfig;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (var sliderSetTabs : sliderSetTabs) {
            sliderSetTabs.render(context, mouseX, mouseY, sliderSetTabs == selectedSliderSetTab);
        }

        renderBackground(context, BACKGROUND_TEXTURE, -999);
        for (var drawable : buttons) {
            drawable.render(
                    context,
                    isEditable() ? mouseX : -1,
                    isEditable() ? mouseY : -1,
                    delta
            );
        }
        selectedSliderSetTab.sliderSet.render(
                context,
                isEditable() ? mouseX : -1,
                isEditable() ? mouseY : -1,
                delta
        );

        var playerX = getWindowRight() - 59;
        var playerY = getWindowTop() + 155;
        var playerRotation = getCurrentPlayerRotation();

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawText(
                textRenderer, name,
                getWindowRight() - 110, getWindowTop() + 10, TEXT_COLOR, false
        );
        if (isOverridden()) {
            var text = Text.translatable("gui.showmeyourskin.armorScreen.overridden");
            context.drawText(
                    textRenderer, text,
                    getWindowRight() - 7 - textRenderer.getWidth(text), getWindowTop() + 10, TEXT_COLOR_RED, false
            );
        }

        var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(playerX, playerY, -950);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(playerRotation));
        matrices.translate(0, 0, 950.0);
        context.enableScissor(
                getWindowRight() - 112, getWindowTop() + 8,
                getWindowRight() - 5, getWindowTop() + 168
        );
        DrawUtils.drawEntityFollowingMouse(
                matrices, 0, 0, 70,
                getCurrentPlayerRotation(), mouseX - playerX, mouseY - playerY,
                player
        );
        context.disableScissor();
        matrices.pop();

        if (!isEditable()) {
            renderBackground(context, OVERLAY_TEXTURE, 999);
        }
    }

    private float getPlayerRotationDelta() {
        var delta = MathHelper.clamp((System.currentTimeMillis() - lastTabSwitchTime) / 500.0, 0, 1);
        return (float) Math.sin(delta * Math.PI / 2);
    }

    private float getCurrentPlayerRotation() {
        return MathHelper.lerp(getPlayerRotationDelta(), lastPlayerRotation, selectedSliderSetTab.sliderSet.rotatedBy);
    }

    public void renderBackground(DrawContext context, Identifier backgroundTexture, int zIndex) {
        int leftSide = getWindowLeft() + 3;
        int topSide = getWindowTop();
        var matrices = context.getMatrices();

        RenderSystem.enableBlend();

        matrices.push();
        matrices.translate(0, 0, zIndex);
        context.drawTexture(backgroundTexture, leftSide, topSide, 1, 1, 236, 254);
        matrices.pop();

        RenderSystem.disableBlend();
    }

    protected SliderWidget getOpacitySlider(HideableEquipment slot, int x, int y, String translationKey) {
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

    protected SliderWidget getSlider(int x, int y, int width, int minValue, int maxValue, int initialValue, Consumer<Integer> onValueChange, String translationKey) {
        return new SliderWidget(getWindowLeft() + x, getWindowTop() + y,
                width, 20, Text.translatable(translationKey, initialValue), (initialValue - minValue) / (float) (maxValue - minValue)) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable(translationKey, (int) MathHelper.lerp(this.value, minValue, maxValue)));
            }

            @Override
            protected void applyValue() {
                onValueChange.accept((int) MathHelper.lerp(this.value, minValue, maxValue));
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

    protected ToggleButtonWidget getGlintButton(HideableEquipment slot, int x, int y) {
        return new ToggleButtonWidget(
                getWindowLeft() + x, getWindowTop() + y, 20, 20,
                TOGGLE_GLINT_BUTTON_TEXTURES, armorConfig.getGlints().get(slot).getTransparency() > 0,
                (btn, b) -> armorConfig.getGlints().get(slot).setTransparency((byte) (b ? 100 : 0)), GLINT_TOOLTIP
        );
    }

    public boolean isEditable() {
        return ModConfig.INSTANCE.globalEnabled;
    }
    
    public boolean isOverridden() {
        return false; // !armorConfig.equals(ModConfig.INSTANCE.getApplicable(player.getUuid()));
    }

    public void selectTab(SliderSetTab sliderSetTab) {
        if (selectedSliderSetTab != null) {
            lastPlayerRotation = getCurrentPlayerRotation();
            MinecraftClient.getInstance().getSoundManager()
                    .play(PositionedSoundInstance.master(sliderSetTab.sliderSet.sound, 1.0F));
        }
        lastTabSwitchTime = System.currentTimeMillis();

        selectedSliderSetTab = sliderSetTab;

        player.equippedStackSupplier = sliderSetTab.sliderSet.dummyEquipmentGetter;
    }

    public int getTabIndex() {
        return sliderSetTabs.indexOf(selectedSliderSetTab);
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
            case HEAD -> HAT;
            default -> ItemStack.EMPTY;
        };
    }
}
