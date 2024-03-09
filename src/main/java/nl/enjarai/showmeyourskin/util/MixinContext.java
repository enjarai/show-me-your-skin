package nl.enjarai.showmeyourskin.util;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MixinContext<T> {

    public static final MixinContext<ArmorContext> ARMOR = new MixinContext<>();
    public static final MixinContext<LivingEntity> ENTITY = new MixinContext<>();

    private final ThreadLocal<T> currentContext = new ThreadLocal<>();

    public void setContext(T context) {
        currentContext.set(context);
    }

    @Nullable
    public T getContext() {
        return currentContext.get();
    }

    public void clearContext() {
        currentContext.set(null);
    }

    @Nullable
    public T getAndClearContext() {
        T context = currentContext.get();
        clearContext();
        return context;
    }
}
