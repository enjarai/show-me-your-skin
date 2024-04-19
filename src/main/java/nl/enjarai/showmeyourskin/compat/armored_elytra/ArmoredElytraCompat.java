package nl.enjarai.showmeyourskin.compat.armored_elytra;

import net.fabricmc.loader.api.FabricLoader;

public class ArmoredElytraCompat {
    public static final boolean IS_LOADED = FabricLoader.getInstance().isModLoaded("armored_elytra");
}
