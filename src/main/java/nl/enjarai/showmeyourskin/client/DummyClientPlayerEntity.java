package nl.enjarai.showmeyourskin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private static final ItemStack HEAD_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_HELMET);
    private static final ItemStack CHEST_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_CHESTPLATE);
    private static final ItemStack LEGS_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_LEGGINGS);
    private static final ItemStack FEET_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_BOOTS);

    private static DummyClientPlayerEntity instance;
    private Identifier skinIdentifier = null;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity();
        return instance;
    }

    private DummyClientPlayerEntity() {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        MinecraftClient.getInstance().getSkinProvider().loadSkin(getGameProfile(), (type, identifier, texture) -> {
            skinIdentifier = identifier;
        }, true);
    }

    public DummyClientPlayerEntity(Identifier skinIdentifier) {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        this.skinIdentifier = skinIdentifier;
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public boolean hasSkinTexture() {
        return true;
    }

    @Override
    public Identifier getSkinTexture() {
        return skinIdentifier == null ? DefaultSkinHelper.getTexture(this.getUuid()) : skinIdentifier;
    }

    @Override
    public UUID getUuid() {
        return getGameProfile().getId();
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> HEAD_ARMOR;
            case CHEST -> CHEST_ARMOR;
            case LEGS -> LEGS_ARMOR;
            case FEET -> FEET_ARMOR;
            default -> ItemStack.EMPTY;
        };
    }

    private static class AlwaysGlintingStack extends ItemStack {
        public AlwaysGlintingStack(ItemConvertible item) {
            super(item);
        }

        @Override
        public boolean hasGlint() {
            return true;
        }
    }
}
