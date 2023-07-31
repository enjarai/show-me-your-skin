package nl.enjarai.showmeyourskin.gui.widget;

import com.google.common.collect.Lists;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.util.ScissorManager;
import dev.lambdaurora.spruceui.widget.container.AbstractSpruceParentWidget;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import nl.enjarai.showmeyourskin.client.cursed.DummyClientPlayerEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerSelectorWidget extends AbstractSpruceParentWidget<ConfigEntryWidget> {
    protected Consumer<ConfigEntryWidget> onSelect;
    private final List<ConfigEntryWidget> entries = Lists.newArrayList();
    private final List<ConfigEntryWidget> allEntries = Lists.newArrayList();
    private Position scrollOffset = Position.origin();
    private ConfigEntryWidget selected;
    private ConfigEntryWidget defaultSelected;
    protected int childSize = 30;

    public PlayerSelectorWidget(Position position, int width, Consumer<ConfigEntryWidget> onSelect) {
        super(position, ConfigEntryWidget.class);
        this.width = width;
        this.height = childSize;
        this.onSelect = onSelect;
        scrollOffset.setAnchor(this);
    }

    public void linkDefault(ConfigEntryWidget defaultSelected) {
        this.defaultSelected = defaultSelected;
    }

    public void updateEntries() {
        entries.clear();
        allEntries.clear();

        if (client.player == null) {
            var dummyPlayer = DummyClientPlayerEntity.getInstance();
            var profile = dummyPlayer.getGameProfile();
            entries.add(new PlayerSelectorEntry(
                    this, profile.getId(),
                    Text.translatable("gui.showmeyourskin.armorScreen.playerName", profile.getName()), dummyPlayer::getSkinTexture,
                    dummyPlayer::getModel
            ));
        } else {
            for (UUID uuid : client.player.networkHandler.getPlayerUuids()) {
                PlayerListEntry playerListEntry = client.player.networkHandler.getPlayerListEntry(uuid);
                if (playerListEntry != null) {
                    UUID playerUuid = playerListEntry.getProfile().getId();
                    String playerName = playerListEntry.getProfile().getName();
                    entries.add(new PlayerSelectorEntry(
                            this, playerUuid,
                            Text.translatable("gui.showmeyourskin.armorScreen.playerName", playerName), playerListEntry::getSkinTexture,
                            playerListEntry::getModel
                    ));
                }
            }
        }

        entries.sort((player1, player2) -> player1.getName().getString().compareToIgnoreCase(player2.getName().getString()));
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            var position = entry.getPosition();
            position.setAnchor(scrollOffset);
            position.setRelativeX(getEntryX(i));
            position.setRelativeY(getEntryY(i));
        }

        allEntries.addAll(this.entries);
        if (defaultSelected != null && !allEntries.contains(defaultSelected)) {
            allEntries.add(defaultSelected);
        }

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

    @Nullable
    public ConfigEntryWidget getHovered(int mouseX, int mouseY) {
        for (ConfigEntryWidget entry : allEntries) {
            var x = entry.getX();
            var y = entry.getY();
            if (mouseX >= x && mouseX < x + entry.getWidth() && mouseY >= y && mouseY < y + entry.getHeight()) {
                return entry;
            }
        }
        return null;
    }

    protected int getEntryX(int index) {
        return index * childSize;
    }

    protected int getEntryY(int index) {
        return 0;
    }

    private int getMaxScroll() {
        return Math.max(0, getEntries().size() * childSize - width);
    }

    public void setScroll(int scroll) {
        this.scrollOffset.setRelativeX(scroll);
    }

    public int getScroll() {
        return scrollOffset.getRelativeX();
    }

    public void scroll(int amount) {
        setScroll(MathHelper.clamp(getScroll() - amount, 0, getMaxScroll()));
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        ScissorManager.push(getX(), getY(), getWidth(), getHeight());
        for (ConfigEntryWidget entry : children()) {
            entry.render(context, mouseX, mouseY, delta);
        }
        context.disableScissor();
    }

    public List<ConfigEntryWidget> getEntries() {
        return entries;
    }

    @Override
    public List<ConfigEntryWidget> children() {
        return getEntries();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll(((int) amount) * childSize);
        return true;
    }
}
