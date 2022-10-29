package xyz.enhancedpixel.enhancedeventbus.invokers;

import java.lang.invoke.*;
import java.lang.reflect.Method;

public class LMFInvoker implements Invoker {
    public SubscriberMethod setup(
            Object instance,
            Class<?> clazz,
            Class<?> parameterClazz,
            Method method
    ) throws Throwable {
        method.setAccessible(true);
        MethodHandles.Lookup caller = CachedJavaVersion.getInstance().privateLookup(clazz);
        MethodType subscription = MethodType.methodType(void.class, parameterClazz);
        MethodHandle target = caller.findVirtual(clazz, method.getName(), subscription);
        CallSite site = LambdaMetafactory.metafactory(
                caller,
                "invoke",
                MethodType.methodType(SubscriberMethod.class, clazz),
                subscription.changeParameterType(0, Object.class),
                target,
                subscription
        );

        MethodHandle factory = site.getTarget();
        return (SubscriberMethod) factory.bindTo(instance).invokeExact();
    }
}
