package nl.enjarai.showmeyourskin.util;

import net.minecraft.util.math.MathHelper;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.HashMap;
import java.util.UUID;

public class CombatLogger {
    public static final CombatLogger INSTANCE = new CombatLogger();

    private final HashMap<UUID, Long> inCombat = new HashMap<>();

    public boolean isInCombat(UUID uuid) {
        var lastTrigger = inCombat.get(uuid);
        if (lastTrigger != null && lastTrigger > System.currentTimeMillis() - (long) (ModConfig.INSTANCE.combatCooldown * 1000)) {
            return true;
        }
        inCombat.remove(uuid);
        return false;
    }

    public float getFade(UUID uuid) {
        var lastTrigger = inCombat.get(uuid);
        if (lastTrigger != null) {
            var fadeStart = lastTrigger + (long) (ModConfig.INSTANCE.combatCooldown * 1000) - (long) (ModConfig.INSTANCE.fadeOutTime * 1000);
            var timeSinceCombat = System.currentTimeMillis() - fadeStart;

            // Calculate the current transparency using the time since combat ended and the fade out duration,
            // then clamp between 0 and 1 to ensure the final transparency is calculated properly.
            return MathHelper.clamp(1 - (timeSinceCombat / 1000f) / ModConfig.INSTANCE.fadeOutTime, 0, 1);
        }
        return 1;
    }

    public void triggerCombat(UUID uuid) {
        inCombat.put(uuid, System.currentTimeMillis());
    }
}
