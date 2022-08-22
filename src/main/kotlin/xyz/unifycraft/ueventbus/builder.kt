package xyz.unifycraft.ueventbus

import xyz.unifycraft.ueventbus.invokers.Invoker
import xyz.unifycraft.ueventbus.invokers.ReflectionInvoker

fun eventBus(lambda: EventBusBuilder.() -> Unit): EventBus {
    return EventBusBuilder().apply(lambda).build()
}

class EventBusBuilder {
    /**
     * Default: [ReflectionInvoker].
     */
    private var invoker: Invoker = ReflectionInvoker()

    /**
     * Default: Re-throws exception.
     */
    private var exceptionHandler: (Exception) -> Unit = {
        throw it
    }

    var threadSafety = false

    fun invoker(lambda: () -> Invoker) = invoker(lambda())
    fun invoker(invoker: Invoker) {
        this.invoker = this.invoker
    }

    fun threadSafety(lambda: () -> Boolean) = threadSafety(lambda())
    fun threadSafety(value: Boolean) {
        this.threadSafety = value
    }

    fun exceptionHandler(handler: (Exception) -> Unit) {
        this.exceptionHandler = handler
    }

    fun build() =
        EventBus(invoker, exceptionHandler, threadSafety)
}