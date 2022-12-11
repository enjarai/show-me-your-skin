package nl.enjarai.showmeyourskin.compat.elytratrinket;

import nl.enjarai.cicada.api.compat.CompatMixinPlugin;

import java.util.Set;

/**
 * Non-critical mixin config plugin, just disables mixins if Elytra Trinket isn't present,
 * since otherwise the log gets spammed with warnings.
 */
public class ElytraTrinketMixinPlugin implements CompatMixinPlugin {
    @Override
    public Set<String> getRequiredMods() {
        return Set.of("elytratrinket");
    }
}
