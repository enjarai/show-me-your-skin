package nl.enjarai.showmeyourskin.util;

import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.HashMap;
import java.util.UUID;

public class CombatLogger {
    public static final CombatLogger INSTANCE = new CombatLogger();

    private final HashMap<UUID, Long> inCombat = new HashMap<>();

    public boolean isInCombat(UUID uuid) {
        var lastTrigger = inCombat.get(uuid);
        if (lastTrigger != null && lastTrigger > System.currentTimeMillis() - ModConfig.INSTANCE.combatCooldown * 1000) {
            return true;
        }
        inCombat.remove(uuid);
        return false;
    }

    public void triggerCombat(UUID uuid) {
        inCombat.put(uuid, System.currentTimeMillis());
    }
}
