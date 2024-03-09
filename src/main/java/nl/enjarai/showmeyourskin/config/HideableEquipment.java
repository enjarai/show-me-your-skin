package nl.enjarai.showmeyourskin.config;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public enum HideableEquipment {
    HEAD,
    CHEST,
    LEGS,
    FEET,
    ELYTRA,
    SHIELD,
    HAT;

    private final String id;

    HideableEquipment() {
        this.id = name().toLowerCase();
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static HideableEquipment fromId(String id) {
        for (var i : values()) {
            if (i.id.equals(id)) {
                return i;
            }
        }
        return null;
    }

    @Nullable
    public static HideableEquipment fromSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> HEAD;
            case CHEST -> CHEST;
            case LEGS -> LEGS;
            case FEET -> FEET;
            default -> null;
        };
    }

    @Nullable
    public EquipmentSlot toSlot() {
        return switch (this) {
            case HEAD -> EquipmentSlot.HEAD;
            case CHEST -> EquipmentSlot.CHEST;
            case LEGS -> EquipmentSlot.LEGS;
            case FEET -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    public static Codec<HideableEquipment> getCodec() {
        return Codec.STRING.xmap(HideableEquipment::fromId, HideableEquipment::getId);
    }

    public static Codec<EquipmentSlot> getSlotCodec() {
        return Codec.STRING.xmap(EquipmentSlot::byName, EquipmentSlot::getName);
    }
}
