package nl.enjarai.showmeyourskin.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private static final ItemStack HEAD_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_HELMET);
    private static final ItemStack CHEST_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_CHESTPLATE);
    private static final ItemStack LEGS_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_LEGGINGS);
    private static final ItemStack FEET_ARMOR = new AlwaysGlintingStack(Items.NETHERITE_BOOTS);
    private static final ItemStack OFF_HAND = new AlwaysGlintingStack(Items.SHIELD);

    private static DummyClientPlayerEntity instance;
    private Identifier skinIdentifier = null;
    private String model = null;
    private PlayerEntity player = null;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity() {
            @Override
            public Text getName() {
                return Text.translatable("gui.showmeyourskin.armorScreen.global");
            }
        };
        return instance;
    }

    private DummyClientPlayerEntity() {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        setUuid(UUID.randomUUID());
        MinecraftClient.getInstance().getSkinProvider().loadSkin(getGameProfile(), (type, identifier, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                skinIdentifier = identifier;
                model = texture.getMetadata("model");
                if (model == null) {
                    model = "default";
                }
            }
        }, true);
    }

    public DummyClientPlayerEntity(@Nullable PlayerEntity player, UUID uuid, Identifier skinIdentifier) {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        this.player = player;
        setUuid(uuid);
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
        return skinIdentifier == null ? DefaultSkinHelper.getTexture(getUuid()) : skinIdentifier;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public String getModel() {
        return model == null ? DefaultSkinHelper.getModel(getUuid()) : model;
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
        if (player != null) {
            return player.getEquippedStack(slot);
        }
        return switch (slot) {
            case HEAD -> HEAD_ARMOR;
            case CHEST -> CHEST_ARMOR;
            case LEGS -> LEGS_ARMOR;
            case FEET -> FEET_ARMOR;
            case OFFHAND -> OFF_HAND;
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
