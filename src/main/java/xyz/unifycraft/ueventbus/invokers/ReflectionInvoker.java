package xyz.unifycraft.ueventbus.invokers;

import java.lang.reflect.Method;

public class ReflectionInvoker implements Invoker {
    public SubscriberMethod setup(
            Object object,
            Class<?> clazz,
            Class<?> parameterClazz,
            Method method
    ) {
        method.setAccessible(true);
        return (obj) -> method.invoke(object, obj);
    }
}
