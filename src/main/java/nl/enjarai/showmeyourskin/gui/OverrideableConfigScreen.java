package nl.enjarai.showmeyourskin.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.style.EightSliceBorder;
import nl.enjarai.showmeyourskin.gui.style.OneSliceBackground;
import nl.enjarai.showmeyourskin.gui.widget.ConfigEntryWidget;
import nl.enjarai.showmeyourskin.gui.widget.PlayerSelectorWidget;

public abstract class OverrideableConfigScreen extends ConfigScreen {
    public static final Identifier GLOBAL_ICON = ShowMeYourSkin.id("textures/gui/global_icon.png");

    private Position selectorRelative = Position.of(0, 0);
    private SpruceContainerWidget selectorContainer;
    private SpruceContainerWidget listContainer;
    private ConfigEntryWidget globalConfig;
    private PlayerSelectorWidget playerSelector;

    public OverrideableConfigScreen(Screen parent, Text title) {
        super(parent, title);
    }

    @Override
    protected void init() {
        super.init();

        selectorContainer = new SpruceContainerWidget(Position.of(getWindowLeft(), getSelectorTop()), 236, 58);
        selectorContainer.setBackground(OneSliceBackground.WINDOW);
        selectorContainer.setBorder(EightSliceBorder.WINDOW);
        selectorRelative.setAnchor(selectorContainer);

        listContainer = new SpruceContainerWidget(Position.of(7, 19), 222, 32);
        listContainer.setBackground(OneSliceBackground.DARK_INDENT);
        listContainer.setBorder(EightSliceBorder.DARK_INDENT);

        playerSelector = new PlayerSelectorWidget(Position.of(34, 1), 187, this::loadConfigEntry);
        globalConfig = new ConfigEntryWidget(
                playerSelector, Text.translatable("gui.showmeyourskin.armorScreen.global"),
                () -> OverrideableConfigScreen.GLOBAL_ICON, () -> null
        );
        var globalConfigPosition = globalConfig.getPosition();
        globalConfigPosition.setRelativeX(1);
        globalConfigPosition.setRelativeY(1);
        playerSelector.linkDefault(globalConfig);
        playerSelector.updateEntries();

        listContainer.addChild(playerSelector);
        listContainer.addChild(globalConfig);

        selectorContainer.addChild(listContainer);

        addDrawableChild(selectorContainer);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Screen title/hovered player name
        var hovered = playerSelector.getHovered(mouseX, mouseY);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawText(
                textRenderer, hovered == null ? title : hovered.getName(),
                selectorContainer.getX() + 8, selectorContainer.getY() + 8, TEXT_COLOR, false
        );

        // Player list/global config divider texture
        context.drawTexture(
                ShowMeYourSkin.id("textures/gui/divider.png"),
                listContainer.getX() + 31, listContainer.getY() + 1,
                0, 0, 3, 30, 32, 32
        );
    }

    protected int getSelectorTop() {
        return -4;
    }

    @Override
    protected int getWindowTop() {
        return Math.max(getSelectorTop() + 57, super.getWindowTop());
    }

    @Override
    protected Position getBackButtonPos() {
        return Position.of(selectorRelative, -24, 8);
    }

    @Override
    protected Position getGlobalTogglePos() {
        return Position.of(selectorRelative, -24, 32);
    }

    @Override
    public void close() {
        super.close();
        ModConfig.INSTANCE.save();
    }
}
