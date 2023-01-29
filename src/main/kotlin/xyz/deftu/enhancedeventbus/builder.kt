package xyz.deftu.enhancedeventbus

import xyz.deftu.enhancedeventbus.invokers.Invoker
import xyz.deftu.enhancedeventbus.invokers.ReflectionInvoker
import java.util.function.Consumer

fun bus(lambda: EventBusBuilder.() -> Unit) = EventBusBuilder().apply(lambda).build()

class EventBusBuilder {
    /**
     * The default invoker is the [ReflectionInvoker].
     */
    var invoker: Invoker =
        ReflectionInvoker()

    /**
     * By default, the handler will re-throw the exception.
     */
    private var exceptionHandler: (Exception) -> Unit = {
        throw it
    }

    var threadSafety = false

    fun setInvoker(block: () -> Invoker) = setInvoker(block())
    fun setInvoker(invoker: Invoker) = apply {
        this.invoker = this.invoker
    }

    fun setThreadSafety(lambda: () -> Boolean) = setThreadSafety(lambda())
    fun setThreadSafety(value: Boolean) = apply {
        this.threadSafety = value
    }

    fun setExceptionHandler(handler: (Exception) -> Unit) = apply {
        this.exceptionHandler = handler
    }

    fun setExceptionHandler(consumer: Consumer<Exception>) = apply {
        this.exceptionHandler = consumer::accept
    }

    fun build() = EventBus(invoker, exceptionHandler, threadSafety)
}
