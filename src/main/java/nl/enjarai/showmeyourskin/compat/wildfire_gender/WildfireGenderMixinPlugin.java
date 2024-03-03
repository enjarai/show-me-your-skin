package nl.enjarai.showmeyourskin.compat.wildfire_gender;

import nl.enjarai.cicada.api.compat.CompatMixinPlugin;

import java.util.Set;

public class WildfireGenderMixinPlugin implements CompatMixinPlugin {
    @Override
    public Set<String> getRequiredMods() {
        // This compat module is disabled for now since gender mod is not available for 1.20.4 yet.
        return Set.of("wildfire_gender", "dont-use-right-now-alr-thx");
    }
}
