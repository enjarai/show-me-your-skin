package nl.enjarai.showmeyourskin.mixin.cursed;

import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Supplier;

@Mixin(World.class)
public interface WorldAccessor {
    @Mutable
    @Accessor("thread")
    void showmeyourskin$setThread(Thread thread);

    @Mutable
    @Accessor("debugWorld")
    void showmeyourskin$setDebugWorld(boolean debugWorld);

    @Mutable
    @Accessor("properties")
    void showmeyourskin$setProperties(MutableWorldProperties properties);

    @Mutable
    @Accessor("profiler")
    void showmeyourskin$setProfiler(Supplier<Profiler> profiler);

    @Mutable
    @Accessor("border")
    void showmeyourskin$setBorder(WorldBorder border);

    @Mutable
    @Accessor("biomeAccess")
    void showmeyourskin$setBiomeAccess(BiomeAccess biomeAccess);

    @Mutable
    @Accessor("registryKey")
    void showmeyourskin$setRegistryKey(RegistryKey<World> registryKey);

    @Mutable
    @Accessor("dimension")
    void showmeyourskin$setDimensionKey(RegistryKey<DimensionType> dimension);

    @Mutable
    @Accessor("dimensionEntry")
    void showmeyourskin$setDimensionEntry(RegistryEntry<DimensionType> dimensionEntry);

    @Mutable
    @Accessor("random")
    void showmeyourskin$setRandom(Random random);

    @Mutable
    @Accessor("threadSafeRandom")
    void showmeyourskin$setAsyncRandom(Random random);

    @Mutable
    @Accessor("blockEntityTickers")
    void showmeyourskin$setBlockEntityTickers(List<BlockEntityTickInvoker> list);

    @Mutable
    @Accessor("pendingBlockEntityTickers")
    void showmeyourskin$setPendingBlockEntityTickers(List<BlockEntityTickInvoker> list);

    @Mutable
    @Accessor("damageSources")
    void showmeyourskin$setDamageSources(DamageSources sources);
}