package nl.enjarai.showmeyourskin.util;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MixinContext<T> {

    public static final MixinContext<ArmorContext> ARMOR = new MixinContext<>();
    public static final MixinContext<LivingEntity> ENTITY = new MixinContext<>();
    public static final MixinContext<Boolean> TRIM_RENDER = new MixinContext<>(false);
    public static final MixinContext<Boolean> SHIELD_PLATE_RENDER = new MixinContext<>(false);

    private final ThreadLocal<T> currentContext;

    public MixinContext() {
        currentContext = new ThreadLocal<>();
    }

    public MixinContext(T initialValue) {
        currentContext = ThreadLocal.withInitial(() -> initialValue);
    }

    public void setContext(T context) {
        currentContext.set(context);
    }

    @Nullable
    public T getContext() {
        return currentContext.get();
    }

    public void clearContext() {
        currentContext.remove();
    }

    @Nullable
    public T getAndClearContext() {
        T context = currentContext.get();
        clearContext();
        return context;
    }
}
