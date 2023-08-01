package nl.enjarai.showmeyourskin.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.border.Border;
import dev.lambdaurora.spruceui.widget.SpruceSliderWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.client.cursed.AlwaysGlintingStack;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.style.EightSliceBorder;
import nl.enjarai.showmeyourskin.gui.style.OneSliceBackground;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ArmorConfigWindow extends SpruceContainerWidget {
    private static final int TEXT_COLOR = 0x505050;
    private static final int TEXT_COLOR_RED = 0x880000;
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

    private final SliderSetManager tabManager;
    private float lastPlayerRotation;
    private long lastTabSwitchTime;
    private final Screen parent;
    private final Text name;
    private final DummyClientPlayerEntity player;
    private final ArmorConfig armorConfig;
    private final SpruceContainerWidget rightArea;

    public ArmorConfigWindow(Position position, Screen parent, Text name, DummyClientPlayerEntity player, ArmorConfig armorConfig, int tabIndex, boolean allowAllOptions) {
        super(position, 236, 173);
        this.parent = parent;
        this.name = name;
        this.player = player;
        this.armorConfig = armorConfig;

        var serverConfig = ShowMeYourSkinClient.HANDSHAKE_CLIENT.getConfig();
        var hideOptions = serverConfig.isPresent() && !allowAllOptions;

        this.tabManager = new SliderSetManager(tabIndex,
                new SliderSetTab(Position.of(-28, 12), ShowMeYourSkin.id("textures/gui/tab/armor.png"),
                        new SliderSet(Position.of(7, 7), 109, 101, con -> {
                            con.setBackground(OneSliceBackground.INDENT);
                            con.setBorder(EightSliceBorder.INDENT);
                            
                            con.addChild(getOpacitySlider(HideableEquipment.HEAD,
                                    4, 4, "gui.showmeyourskin.armorScreen.piece.head"));
                            con.addChild(getOpacitySlider(HideableEquipment.CHEST,
                                    4, 28, "gui.showmeyourskin.armorScreen.piece.chest"));
                            con.addChild(getOpacitySlider(HideableEquipment.LEGS,
                                    4, 52, "gui.showmeyourskin.armorScreen.piece.legs"));
                            con.addChild(getOpacitySlider(HideableEquipment.FEET,
                                    4, 76, "gui.showmeyourskin.armorScreen.piece.feet"));
        
                            con.addChild(getGlintButton(HideableEquipment.HEAD, 84, 4));
                            con.addChild(getGlintButton(HideableEquipment.CHEST, 84, 28));
                            con.addChild(getGlintButton(HideableEquipment.LEGS, 84, 52));
                            con.addChild(getGlintButton(HideableEquipment.FEET, 84, 76));
                        }, ArmorConfigWindow::getDummyArmor, 0, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE)
                ),
                new SliderSetTab(Position.of(-28, 42), ShowMeYourSkin.id("textures/gui/tab/equipment.png"),
                        new SliderSet(Position.of(7, 7), 109, 101, con -> {
                            con.setBackground(OneSliceBackground.INDENT);
                            con.setBorder(EightSliceBorder.INDENT);
                            
                            con.addChild(getOpacitySlider(HideableEquipment.ELYTRA,
                                    4, 4, "gui.showmeyourskin.armorScreen.piece.elytra"));
                            con.addChild(getOpacitySlider(HideableEquipment.SHIELD,
                                    4, 28, "gui.showmeyourskin.armorScreen.piece.shield"));
        
                            con.addChild(getGlintButton(HideableEquipment.ELYTRA, 84, 4));
                            con.addChild(getGlintButton(HideableEquipment.SHIELD, 84, 28));
                        }, ArmorConfigWindow::getDummyEquipment, -180, SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA)
                )
        );
        lastPlayerRotation = tabManager.getActiveTab().getContainer().rotatedBy;
        tabManager.setTabSwitchCallback(tab -> {
            lastPlayerRotation = getCurrentPlayerRotation();
            lastTabSwitchTime = System.currentTimeMillis();
            MinecraftClient.getInstance().getSoundManager()
                    .play(PositionedSoundInstance.master(tab.getContainer().sound, 1.0F));

            player.equippedStackSupplier = tab.getContainer().getDummyEquipmentGetter();
        });
        player.equippedStackSupplier = tabManager.getActiveTab().getContainer().getDummyEquipmentGetter();

        var lowerArea = new SpruceContainerWidget(Position.of(7, 113), 109, 53);
        lowerArea.setBackground(OneSliceBackground.INDENT);
        lowerArea.setBorder(EightSliceBorder.INDENT);

        if (!hideOptions || serverConfig.get().allowNotShowInCombat()) {
            var showInCombat = new IconToggleButtonWidget(
                    Position.of(4, 4), ShowMeYourSkin.id("textures/gui/button/show_in_combat.png"),
                    0, 0, armorConfig.showInCombat
            );
            showInCombat.setCallback((btn, b) -> armorConfig.showInCombat = b);
            showInCombat.setTooltip(COMBAT_TOOLTIP);
            lowerArea.addChild(showInCombat);
        }

        if (!hideOptions || serverConfig.get().allowNotShowNameTag()) {
            var showNameTag = new IconToggleButtonWidget(
                    Position.of(4, 28), ShowMeYourSkin.id("textures/gui/button/show_nametag.png"),
                    0, 0, armorConfig.showNameTag
            );
            showNameTag.setCallback((btn, b) -> armorConfig.showNameTag = b);
            showNameTag.setTooltip(NAME_TAG_TOOLTIP);
            lowerArea.addChild(showNameTag);
        }

        rightArea = new SpruceContainerWidget(Position.of(120, 7), 109, 159);
        rightArea.setBackground(OneSliceBackground.DARK_INDENT);
        rightArea.setBorder(EightSliceBorder.DARK_INDENT);

        var label = new SuperiorLabelWidget(Position.of(4, 4), name, 200);
        label.setColor(TEXT_COLOR);
        label.setShadow(false);
        rightArea.addChild(label);

        this.tabManager.addChildrenTo(this);
        this.addChild(lowerArea);
        this.addChild(rightArea);
    }

    @Override
    public Border getBorder() {
        return EightSliceBorder.WINDOW;
    }

    @Override
    public Background getBackground() {
        return OneSliceBackground.WINDOW;
    }

    public ArmorConfig getArmorConfig() {
        return armorConfig;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        var matrices = context.getMatrices();

        super.renderWidget(context, mouseX, mouseY, delta);

        var playerX = rightArea.getX() + rightArea.getWidth() / 2;
        var playerY = rightArea.getY() + rightArea.getHeight() / 10 * 10;
        var playerRotation = getCurrentPlayerRotation();
        var playerBorder = rightArea.getBorder().getThickness();

//        var textRenderer = MinecraftClient.getInstance().textRenderer;
//        context.drawText(
//                textRenderer, name,
//                getWindowRight() - 110, getWindowTop() + 10, TEXT_COLOR, false
//        );
//        if (isOverridden()) {
//            var text = Text.translatable("gui.showmeyourskin.armorScreen.overridden");
//            context.drawText(
//                    textRenderer, text,
//                    getWindowRight() - 7 - textRenderer.getWidth(text), getWindowTop() + 10, TEXT_COLOR_RED, false
//            );
//        }


        matrices.push();
        matrices.translate(playerX, playerY, -950);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(playerRotation));
        matrices.translate(0, 0, 950.0);
        context.enableScissor(
                rightArea.getX() + playerBorder, rightArea.getY() + playerBorder,
                rightArea.getX() + rightArea.getWidth() - playerBorder, rightArea.getY() + rightArea.getHeight() - playerBorder
        );
        drawEntity(matrices, 0, 0, 70, -mouseX + playerX, -mouseY + playerY - 110, player);
        context.disableScissor();
        matrices.pop();
    }

    private float getPlayerRotationDelta() {
        var delta = MathHelper.clamp((System.currentTimeMillis() - lastTabSwitchTime) / 500.0, 0, 1);
        return (float) Math.sin(delta * Math.PI / 2);
    }

    private float getCurrentPlayerRotation() {
        return MathHelper.lerp(getPlayerRotationDelta(), lastPlayerRotation, tabManager.getActiveTab().getContainer().rotatedBy);
    }

    protected SpruceSliderWidget getOpacitySlider(HideableEquipment slot, int x, int y, String translationKey) {
        var pieceConfig = armorConfig.getPieces().get(slot);
        var trimConfig = armorConfig.getTrims().get(slot.toSlot());
        var initialValue = pieceConfig.getTransparency();

        return new SpruceSliderWidget(
                Position.of(x, y), 77, 20,
                Text.translatable(translationKey), initialValue / 100.0,
                sld -> {
                    pieceConfig.setTransparency((byte) sld.getIntValue());
                    if (trimConfig != null) trimConfig.setTransparency((byte) sld.getIntValue());
                },
                100, "%"
        );
    }

    protected IconToggleButtonWidget getGlintButton(HideableEquipment slot, int x, int y) {
        var button = new IconToggleButtonWidget(
                Position.of(x, y), ShowMeYourSkin.id("textures/gui/button/show_glint.png"), 0, 0,
                armorConfig.getGlints().get(slot).getTransparency() > 0
        );
        button.setTooltip(GLINT_TOOLTIP);
        button.setCallback((btn, b) -> armorConfig.getGlints().get(slot).setTransparency((byte) (b ? 100 : 0)));
        return button;
    }

    public boolean isEditable() {
        return ModConfig.INSTANCE.globalEnabled;
    }

    public boolean isOverridden() {
        return false; // !armorConfig.equals(ModConfig.INSTANCE.getApplicable(player.getUuid()));
    }

    public SliderSetManager getTabManager() {
        return tabManager;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isActive() || !this.isVisible())
            return false;

        return this.onMouseClick(mouseX, mouseY, button);
    }

    public void drawEntity(MatrixStack matrices, int x, int y, int size, double mouseX, double mouseY, LivingEntity entity) {
        float f = (float) (Math.atan(mouseX / 40.0F) * Math.sin((getCurrentPlayerRotation() / 180.0 + 0.5) * Math.PI));
        float g = (float) Math.atan(mouseY / 40.0F);
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
