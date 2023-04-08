package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
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
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.client.cursed.AlwaysGlintingStack;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ArmorConfigWindow extends AbstractParentElement implements Drawable, Element, Selectable {
    public static final Identifier TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen.png");
    public static final Identifier BACKGROUND_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_background.png");
    public static final Identifier OVERLAY_TEXTURE = ShowMeYourSkin.id("textures/gui/armor_screen_disabled.png");
    private static final int TEXT_COLOR = 0x303030;
    private static final Text GLINT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.glintTooltip");
    private static final Text COMBAT_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.combatTooltip");
    private static final Text NAME_TAG_TOOLTIP = Text.translatable("gui.showmeyourskin.armorScreen.nameTagTooltip");

    private static final ItemStack HEAD_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_HELMET);
    private static final ItemStack CHEST_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_CHESTPLATE);
    private static final ItemStack LEGS_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_LEGGINGS);
    private static final ItemStack FEET_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_BOOTS);
    private static final ItemStack SHIELD = new AlwaysGlintingStack(Items.SHIELD);
    private static final ItemStack ELYTRA = new AlwaysGlintingStack(Items.ELYTRA);

    static {
        var shieldNbt = new NbtCompound();
        NbtList nbtList = new BannerPattern.Patterns().add(BannerPatterns.RHOMBUS, DyeColor.CYAN).toNbt();
        shieldNbt.put("Patterns", nbtList);
        shieldNbt.putInt("Base", DyeColor.WHITE.getId());
        BlockItem.setBlockEntityNbt(SHIELD, BlockEntityType.BANNER, shieldNbt);
    }

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

    public ArmorConfigWindow(Screen parent, int x, int y, Text name, DummyClientPlayerEntity player, ArmorConfig armorConfig, int tabIndex) {
        super();
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.name = name;
        this.player = player;
        this.armorConfig = armorConfig;

        var serverConfig = ShowMeYourSkinClient.HANDSHAKE_CLIENT.getConfig();
        var serverAvailable = serverConfig.isPresent();

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
                }, ArmorConfigWindow::getDummyArmor, 0, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE))
        );
        sliderSetTabs.add(new SliderSetTab(getWindowLeft() - 25, getWindowTop() + 42, 16, 240,
                new SliderSet(this, getWindowLeft(), getWindowTop(), sliders -> {
                    sliders.add(getOpacitySlider(HideableEquipment.ELYTRA,
                            14, 11, "gui.showmeyourskin.armorScreen.piece.elytra"));
                    sliders.add(getOpacitySlider(HideableEquipment.SHIELD,
                            14, 35, "gui.showmeyourskin.armorScreen.piece.shield"));

                    sliders.add(getGlintButton(HideableEquipment.ELYTRA, 94, 11));
                    sliders.add(getGlintButton(HideableEquipment.SHIELD, 94, 35));
                }, ArmorConfigWindow::getDummyEquipment, -180, SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA))
        );

        selectTab(sliderSetTabs.get(tabIndex));
        lastPlayerRotation = selectedSliderSetTab.sliderSet.rotatedBy;

        if (!serverAvailable || serverConfig.get().allowNotShowInCombat()) {
            buttons.add(new ToggleButtonWidget(
                    parent, getWindowLeft() + 14, getWindowTop() + 115, 40, 38,
                    TEXTURE, armorConfig.showInCombat, b -> armorConfig.showInCombat = b, COMBAT_TOOLTIP
            ));
        }

        if (!serverAvailable || serverConfig.get().allowNotShowNameTag()) {
            buttons.add(new ToggleButtonWidget(
                    parent, getWindowLeft() + 14, getWindowTop() + 141, 80, 38,
                    TEXTURE, armorConfig.showNameTag, b -> armorConfig.showNameTag = b, NAME_TAG_TOOLTIP
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
        var playerRotation = getCurrentPlayerRotation();

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(
                matrices, name,
                getWindowRight() - 110, getWindowTop() + 10, TEXT_COLOR
        );

        matrices.push();
        matrices.translate(playerX, playerY, -950);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(playerRotation));
        matrices.translate(0, 0, 950.0);
        DrawableHelper.enableScissor(
                getWindowRight() - 112, getWindowTop() + 8,
                getWindowRight() - 5, getWindowTop() + 168
        );
        drawEntity(matrices, 0, 0, 70, -mouseX + playerX, -mouseY + playerY - 110, player);
        DrawableHelper.disableScissor();
        matrices.pop();

        if (!isEditable()) {
            renderBackground(matrices, OVERLAY_TEXTURE, 999);
        }
    }

    private float getPlayerRotationDelta() {
        var delta = MathHelper.clamp((System.currentTimeMillis() - lastTabSwitchTime) / 500.0, 0, 1);
        return (float) Math.sin(delta * Math.PI / 2);
    }

    private float getCurrentPlayerRotation() {
        return MathHelper.lerp(getPlayerRotationDelta(), lastPlayerRotation, selectedSliderSetTab.sliderSet.rotatedBy);
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

    public void drawEntity(MatrixStack matrices, int x, int y, int size, double mouseX, double mouseY, LivingEntity entity) {
        float f = (float) (Math.atan(mouseX / 40.0F) * Math.sin((getCurrentPlayerRotation() / 180.0 + 0.5) * Math.PI));
        float g = (float)Math.atan(mouseY / 40.0F);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        drawEntity(matrices, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    @SuppressWarnings("deprecation")
    public static void drawEntity(MatrixStack matrices, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(0.0, 0.0, 1000.0);
        RenderSystem.applyModelViewMatrix();
        matrices.push();
        matrices.translate(x, y, -950.0);
        matrices.multiplyPositionMatrix(new Matrix4f().scaling(size, size, -size));
        matrices.translate(0, -1, 0);
        matrices.multiply(quaternionf);
        matrices.translate(0, -1, 0);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.setRotation(quaternionf2);
        }
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrices, immediate, 0xF000F0));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
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
