package xyz.unifycraft.ueventbus

import xyz.unifycraft.ueventbus.collection.ConcurrentSubscriberArrayList
import xyz.unifycraft.ueventbus.collection.SubscriberArrayList
import xyz.unifycraft.ueventbus.invokers.Invoker
import xyz.unifycraft.ueventbus.invokers.ReflectionInvoker
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Subscribe(
    val priority: Int = 0
)

class EventBus @JvmOverloads constructor(
    private val invoker: Invoker = ReflectionInvoker(),
    private val exceptionHandler: Consumer<Exception> = Consumer {
        throw it
    },
    private val threadSafety: Boolean = true
) {

    class Subscriber(
        val obj: Any,
        val priority: Int,
        private val invoker: Invoker.SubscriberMethod?
    ) {
        @Throws(Exception::class)
        operator fun invoke(arg: Any?) =
            invoker!!.invoke(arg)
        override fun equals(other: Any?): Boolean =
            other.hashCode() == this.hashCode()
        override fun hashCode() =
            obj.hashCode()
    }

    private val subscribers: AbstractMap<Class<*>, MutableList<Subscriber>> =
        if (threadSafety) ConcurrentHashMap() else HashMap()

    /**
     * Subscribes all the methods marked with the [Subscribe] annotation as
     * event listeners inside the `obj` instance parameter.
     */
    fun register(obj: Any) {
        for (method in obj.javaClass.declaredMethods) {
            val sub: Subscribe = method.getAnnotation(Subscribe::class.java) ?: continue

            // Verification
            val parameterClazz = method.parameterTypes[0]
            when {
                method.parameterCount != 1 -> throw IllegalArgumentException("Subscribed method cannot have more than one parameter.")
                method.returnType != Void.TYPE -> throw IllegalArgumentException("Subscribed method must be of type 'Void'. ")
                parameterClazz.isPrimitive -> throw IllegalArgumentException("Cannot subscribe method to a primitive.")
                parameterClazz.modifiers and (Modifier.ABSTRACT or Modifier.INTERFACE) != 0 -> throw IllegalArgumentException("Cannot subscribe method to a polymorphic class.")
            }

            val subscriberMethod = invoker.setup(obj, obj.javaClass, parameterClazz, method)

            val subscriber = Subscriber(obj, sub.priority, subscriberMethod)
            subscribers.putIfAbsent(parameterClazz, if(threadSafety) ConcurrentSubscriberArrayList() else SubscriberArrayList())
            subscribers[parameterClazz]!!.add(subscriber)
        }
    }

    /**
     * Unsubscribes all registered events inside an instance.
     */
    fun unregister(obj: Any) {
        for (method in obj.javaClass.declaredMethods) {
            if (method.getAnnotation(Subscribe::class.java) == null)
                continue
            subscribers[method.parameterTypes[0]]?.remove(Subscriber(obj, -1, null))
        }
    }

    /**
     * Posts the event instance given to all the subscribers
     * that are subscribed to the event's class.
     */
    fun post(event: Any) {
        val events = subscribers[event.javaClass] ?: return
        // Executed in descending order
        for (i in (events.size-1) downTo 0) {
            try {
                events[i].invoke(event)
            } catch (e: Exception) {
                exceptionHandler.accept(e)
            }
        }
    }

    /**
     * Supplier is only used if there are subscribers listening to
     * the event.
     *
     * Example usage: EventBus#post { ComputationallyHeavyEvent() }
     *
     * This allows events to only be constructed if needed.
     */
    inline fun <reified T> post(supplier: () -> T) {
        val events = getSubscribedEvents(T::class.java) ?: return
        val event = supplier()
        // executed in descending order
        for (i in (events.size-1) downTo 0) {
            events[i].invoke(event)
        }
    }

    fun getSubscribedEvents(clazz: Class<*>) =
        subscribers[clazz]
}
