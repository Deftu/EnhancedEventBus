package xyz.enhancedpixel.enhancedeventbus.invokers;

import java.lang.reflect.Method;

public class ReflectionInvoker implements Invoker {
    public SubscriberMethod setup(
            Object instance,
            Class<?> clazz,
            Class<?> parameterClazz,
            Method method
    ) {
        CachedJavaVersion.getInstance().trySetAccessible(method);
        return (listener) -> method.invoke(instance, listener);
    }
}
