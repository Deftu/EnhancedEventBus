package xyz.deftu.enhancedeventbus

import xyz.deftu.enhancedeventbus.collection.ConcurrentSubscriberArrayList
import xyz.deftu.enhancedeventbus.collection.SubscriberArrayList
import xyz.deftu.enhancedeventbus.invokers.Invoker
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

enum class EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST;
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class SubscribeEvent(
    val priority: EventPriority = EventPriority.NORMAL
)

class EventBus internal constructor(
    val invoker: Invoker,
    val exceptionHandler: Consumer<Exception>,
    val threadSafety: Boolean
) {
    class EventSubscriber(
        val listener: Any,
        val priority: EventPriority,
        private val invoker: Invoker.SubscriberMethod?
    ) {
        @Throws(Exception::class)
        operator fun invoke(arg: Any?) =
            invoker!!.invoke(arg)

        override fun equals(other: Any?): Boolean =
            other.hashCode() == this.hashCode()

        override fun hashCode() =
            listener.hashCode()
    }

    class PriorityComparator : Comparator<EventSubscriber> {
        override fun compare(o1: EventSubscriber, o2: EventSubscriber): Int {
            return o2.priority.ordinal - o1.priority.ordinal
        }
    }

    val subscribers: AbstractMap<Class<*>, MutableList<EventSubscriber>> =
        if (threadSafety) ConcurrentHashMap() else HashMap()

    private fun Method.checkParameters() {
        if (parameterCount < 1)
            throw IllegalArgumentException("Method $name has no parameters, but it is marked with the @SubscribeEvent annotation. Event listeners must be methods with at least one parameter.")
        if (parameterCount > 1)
            throw IllegalArgumentException("Subscribed method cannot have more than one parameter.")
    }

    /**
     * Subscribes all the methods marked with the [SubscribeEvent] annotation as
     * event listeners inside the object instance parameter.
     */
    fun register(listener: Any) {
        for (method in listener::class.java.declaredMethods) {
            val annotation = method.getAnnotation(SubscribeEvent::class.java) ?: continue
            method.checkParameters()

            // Verification
            val parameterClazz = method.parameterTypes[0]
            when {
                method.returnType != Void.TYPE -> throw IllegalArgumentException("Subscribed method must be of type 'Void'. ")
                parameterClazz.isPrimitive -> throw IllegalArgumentException("Cannot subscribe method to a primitive.")
                parameterClazz.modifiers and (Modifier.ABSTRACT or Modifier.INTERFACE) != 0 -> throw IllegalArgumentException("Cannot subscribe method to a polymorphic class.")
            }

            val subscriber = EventSubscriber(listener, annotation.priority, invoker.setup(listener, listener::class.java, parameterClazz, method))
            subscribers.computeIfAbsent(parameterClazz) {
                if (threadSafety) ConcurrentSubscriberArrayList() else SubscriberArrayList()
            }.add(subscriber)
        }
    }

    /**
     * Unsubscribes all registered event listeners inside an object instance.
     */
    fun unregister(listener: Any) {
        for (method in listener::class.java.declaredMethods) {
            if (method.getAnnotation(SubscribeEvent::class.java) == null) continue

            method.checkParameters()
            val parameterClazz = method.parameterTypes[0]
            subscribers[parameterClazz]?.removeIf {
                it.listener == listener
            }
        }
    }

    /**
     * Allows you to register a lambda as an event listener using generics.
     */
    inline fun <reified T> on(
        priority: EventPriority = EventPriority.NORMAL,
        crossinline listener: (T) -> Unit
    ) {
        val method = Invoker.SubscriberMethod { arg -> listener(arg as T) }
        val subscriber = EventSubscriber(this, priority, method)
        subscribers.computeIfAbsent(T::class.java) {
            if (threadSafety) ConcurrentSubscriberArrayList() else SubscriberArrayList()
        }.add(subscriber)
    }
    /**
     * Posts the event instance given to all the subscribers
     * that are subscribed to the event's class.
     */
    fun post(event: Any) {
        val listeners = subscribers[event.javaClass] ?: return
        listeners.sortWith(PriorityComparator())
        for (listener in listeners) {
            try {
                listener(event)
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
        events.sortWith(PriorityComparator())
        for (listener in events) {
            try {
                listener(event)
            } catch (e: Exception) {
                exceptionHandler.accept(e)
            }
        }
    }

    fun getSubscribedEvents(clazz: Class<*>) = subscribers[clazz]
}
