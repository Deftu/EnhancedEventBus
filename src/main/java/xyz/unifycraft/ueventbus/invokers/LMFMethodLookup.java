package xyz.unifycraft.ueventbus.invokers;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public enum LMFMethodLookup {
    JAVA_8 {
        protected MethodHandles.Lookup privateLookup(
                Class<?> clazz
        ) throws Exception {
            MethodHandles.Lookup lookupIn = MethodHandles.lookup().in(clazz);

            // And then we mark it as trusted for private lookup via reflection on private field
            Field modes = lookupIn.getClass().getDeclaredField("allowedModes");
            modes.setAccessible(true);
            modes.setInt(lookupIn, -1); // -1 == TRUSTED
            return lookupIn;
        }
    },
    JAVA_9 {
        protected MethodHandles.Lookup privateLookup(Class clazz) throws Exception {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);

            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.publicLookup();
            MethodHandles.Lookup lookup = (MethodHandles.Lookup)
                    unsafe.getObject(unsafe.staticFieldBase(implLookup), unsafe.staticFieldOffset(implLookup));

            return lookup.in(clazz);
        }
    };

    protected abstract MethodHandles.Lookup privateLookup(
            Class<?> clazz
    ) throws Exception;
}
