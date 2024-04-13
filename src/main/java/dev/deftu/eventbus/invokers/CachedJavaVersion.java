package dev.deftu.eventbus.invokers;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

enum CachedJavaVersion {
    JAVA_8 {
        public void trySetAccessible(Method method) {
            method.setAccessible(true);
        }

        public MethodHandles.Lookup privateLookup(Class<?> clz) throws Exception {
            MethodHandles.Lookup lookupIn = MethodHandles.lookup().in(clz);

            // And then we mark it as trusted for private lookup via reflection on private field
            Field modes = lookupIn.getClass().getDeclaredField("allowedModes");
            modes.setAccessible(true);
            modes.setInt(lookupIn, -1); // -1 == TRUSTED
            return lookupIn;
        }
    },
    JAVA_9 {
        public void trySetAccessible(Method method) {
            // Java 9+ has a new method to set accessible
            method.trySetAccessible();
        }

        public MethodHandles.Lookup privateLookup(Class<?> clz) throws Exception {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);

            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.publicLookup();
            MethodHandles.Lookup lookup = (MethodHandles.Lookup)
                    unsafe.getObject(unsafe.staticFieldBase(implLookup), unsafe.staticFieldOffset(implLookup));

            return lookup.in(clz);
        }
    };

    public static CachedJavaVersion CACHED = null;

    public abstract void trySetAccessible(Method method);
    public abstract MethodHandles.Lookup privateLookup(Class<?> clz) throws Exception;

    public static CachedJavaVersion getInstance() {
        if (CACHED == null) {
            try {
                CACHED = JAVA_9;
                CACHED.privateLookup(CachedJavaVersion.class);
            } catch (Exception e) {
                CACHED = JAVA_8;
            }
        }

        return CACHED;
    }
}
