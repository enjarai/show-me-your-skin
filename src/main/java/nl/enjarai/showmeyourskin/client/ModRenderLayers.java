package nl.enjarai.showmeyourskin.client;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

public class ModRenderLayers {
    public static final Function<Identifier, RenderLayer> ARMOR_TRANSLUCENT_NO_CULL = Util.memoize(texture -> {
        var params = RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(texture, false))
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(true);
        return RenderLayer.of(
                "armor_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_CUTOUT_NO_CULL, params);
    });

}
