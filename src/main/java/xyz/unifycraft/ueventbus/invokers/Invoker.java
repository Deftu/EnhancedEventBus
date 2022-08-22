package xyz.unifycraft.ueventbus.invokers;

import java.lang.reflect.Method;

public interface Invoker {
    SubscriberMethod setup(
            Object object,
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
