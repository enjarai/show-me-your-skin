package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import nl.enjarai.showmeyourskin.client.DummyClientPlayerEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerSelectorWidget extends AbstractParentElement implements Drawable, Element, Selectable {
    protected final MinecraftClient client;
    protected int screenWidth;
    protected int screenHeight;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Consumer<ConfigEntryWidget> onSelect;
    private final List<ConfigEntryWidget> entries = Lists.newArrayList();
    private int scroll = 0;
    private ConfigEntryWidget selected;
    private ConfigEntryWidget defaultSelected;

    public PlayerSelectorWidget(MinecraftClient client, int screenWidth, int screenHeight, int x, int y, int width, Consumer<ConfigEntryWidget> onSelect) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 30;
        this.client = client;
        this.onSelect = onSelect;
    }

    public void linkDefault(ConfigEntryWidget defaultSelected) {
        this.defaultSelected = defaultSelected;
    }

    public void updatePosition(int screenWidth, int screenHeight, int x, int y) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = x;
        this.y = y;
    }

    public void updateEntries() {
        entries.clear();

        if (client.player == null) {
            var dummyPlayer = DummyClientPlayerEntity.getInstance();
            var profile = dummyPlayer.getGameProfile();
            entries.add(new PlayerSelectorEntry(
                    client, this, profile.getId(),
                    profile.getName(), dummyPlayer::getSkinTexture)
            );
        } else {
            for (UUID uuid : client.player.networkHandler.getPlayerUuids()) {
                PlayerListEntry playerListEntry = client.player.networkHandler.getPlayerListEntry(uuid);
                if (playerListEntry != null) {
                    UUID playerUuid = playerListEntry.getProfile().getId();
                    String playerName = playerListEntry.getProfile().getName();
                    entries.add(new PlayerSelectorEntry(client, this, playerUuid, playerName, playerListEntry::getSkinTexture));
                }
            }
        }

        this.entries.sort((player1, player2) -> player1.name.compareToIgnoreCase(player2.name));
        setSelected(null);
    }

    public void setSelected(@Nullable ConfigEntryWidget player) {
        for (ConfigEntryWidget entry : entries) {
            entry.setSelected(false);
        }
        defaultSelected.setSelected(false);

        var selected = player == null ? defaultSelected : player;
        selected.setSelected(true);
        this.selected = selected;
        onSelect.accept(selected);
    }

    public ConfigEntryWidget getSelected() {
        return selected;
    }

    private int getChildX(ConfigEntryWidget child) {
        return x + getEntries().indexOf(child) * 30 - scroll;
    }

    private int getChildY(ConfigEntryWidget child) {
        return y;
    }

    private int getMaxScroll() {
        return Math.max(0, getEntries().size() * 30 - width);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double scaleFactor = client.getWindow().getScaleFactor();
        RenderSystem.enableScissor(
                (int) ((double) x * scaleFactor),
                0,
                (int) ((double) width * scaleFactor),
                Integer.MAX_VALUE
        );
        var players = getEntries();
        for (int i = 0; i < players.size(); i++) {
            var child = players.get(i);
            var x = getChildX(child);
            var y = getChildY(child);
            child.directRender(
                    matrices, i, x, y, mouseX, mouseY,
                    mouseX >= x && mouseX < x + child.getWidth() && mouseY >= y && mouseY < y + child.getHeight(),
                    delta
            );
        }
        RenderSystem.disableScissor();
    }

    public List<ConfigEntryWidget> getEntries() {
        return entries;
    }

    @Override
    public List<? extends Element> children() {
        return getEntries();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            for (var child : getEntries()) {
                var childX = getChildX(child);
                var childY = getChildY(child);
                if (mouseX >= childX && mouseX < childX + child.getWidth() && mouseY >= childY && mouseY < childY + child.getHeight()) {
                    return child.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll += amount * 30;
        if (scroll < 0) {
            scroll = 0;
        } else if (scroll > getMaxScroll()) {
            scroll = getMaxScroll();
        }
        return true;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
