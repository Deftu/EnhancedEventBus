package xyz.unifycraft.ueventbus.invokers;

import java.lang.invoke.*;
import java.lang.reflect.Method;

public class LMFInvoker implements Invoker {
    private LMFMethodLookup lookup;

    public SubscriberMethod setup(
            Object object,
            Class<?> clazz,
            Class<?> parameterClazz,
            Method method
    ) throws Throwable {
        method.setAccessible(true);
        MethodHandles.Lookup caller = lazyPrivateLookup(clazz);
        MethodType subscription = MethodType.methodType(void.class, parameterClazz);
        MethodHandle target = caller.findVirtual(clazz, method.getName(), subscription);
        CallSite site = LambdaMetafactory.metafactory(
                caller,
                "invoke",
                MethodType.methodType(SubscriberMethod.class, clazz),
                subscription.changeParameterType(0, Object.class),
                target,
                subscription);

        MethodHandle factory = site.getTarget();
        return (SubscriberMethod) factory.bindTo(object).invokeExact();

    }

    private MethodHandles.Lookup lazyPrivateLookup(Class clazz) throws Exception {
        if (lookup == null) {
            try { // First, we'll try the Java 9 lookup.
                lookup = LMFMethodLookup.JAVA_9; // Cache that this is in a Java 9 environment.
                return lookup.privateLookup(clazz);
            } catch (NoSuchMethodException e) { // If we're not in a Java 9 environment, we'll default to the Java 8 lookup method.
                lookup = LMFMethodLookup.JAVA_8; // Cache that this is in a Java 8 environment.
                return lookup.privateLookup(clazz);
            }
        }

        return lookup.privateLookup(clazz);
    }
}
