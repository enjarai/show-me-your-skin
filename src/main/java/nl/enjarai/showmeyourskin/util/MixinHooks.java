package nl.enjarai.showmeyourskin.util;

public class MixinHooks {
    private static ArmorContext currentContext;

    public static void setContext(ArmorContext context) {
        currentContext = context;
    }

    public static ArmorContext getContext() {
        return currentContext;
    }

    public static ArmorContext getAndClearContext() {
        ArmorContext context = currentContext;
        currentContext = null;
        return context;
    }
}
