package nl.enjarai.showmeyourskin.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ShowMeYourSkin.CONFIG.getGlobalScreen(MinecraftClient.getInstance().player, parent);
    }
}
