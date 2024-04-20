package nl.enjarai.showmeyourskin.compat.wildfire_gender;

import nl.enjarai.cicada.api.compat.CompatMixinPlugin;

import java.util.Set;

public class WildfireGenderMixinPlugin implements CompatMixinPlugin {
    @Override
    public Set<String> getRequiredMods() {
        return Set.of("wildfire_gender");
    }
}
