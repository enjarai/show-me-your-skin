package nl.enjarai.showmeyourskin.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

import static net.minecraft.client.gl.RenderPipelines.ENTITY_SNIPPET;
import static net.minecraft.client.render.RenderPhase.*;

public class ModRenderLayers {
    public static final RenderPipeline ARMOR_DECAL_TRANSLUCENT = RenderPipelines.register(
            RenderPipeline.builder(ENTITY_SNIPPET)
                    .withLocation("pipeline/armor_decal_cutout_no_cull")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("NO_OVERLAY")
                    .withCull(false)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .build());

    public static final Function<Identifier, RenderLayer> ARMOR_DECAL_TRANSLUCENT_NO_CULL = Util.memoize(texture -> {
        var params = RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(texture, false))
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(true);
        return RenderLayer.of(
                "armor_cutout_no_cull", 1536, true, false, ARMOR_DECAL_TRANSLUCENT, params);
    });
}
