package xyz.enhancedpixel.enhancedeventbus.invokers;

import java.lang.reflect.Method;

public interface Invoker {
    SubscriberMethod setup(
            Object instance,
            Class<?> clazz,
            Class<?> parameterClazz,
            Method method
    ) throws Throwable;

    @FunctionalInterface
    interface SubscriberMethod {
        void invoke(
                Object event
        ) throws Exception;
    }
}
