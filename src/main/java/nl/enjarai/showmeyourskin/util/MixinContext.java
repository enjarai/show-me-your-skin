package nl.enjarai.showmeyourskin.util;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MixinContext<T> {

    public static final MixinContext<ArmorContext> ARMOR = new MixinContext<>();
    public static final MixinContext<LivingEntity> HELD_ITEM = new MixinContext<>();

    private T currentContext;

    public void setContext(T context) {
        currentContext = context;
    }

    @Nullable
    public T getContext() {
        return currentContext;
    }

    public void clearContext() {
        currentContext = null;
    }

    @Nullable
    public T getAndClearContext() {
        T context = currentContext;
        clearContext();
        return context;
    }
}
