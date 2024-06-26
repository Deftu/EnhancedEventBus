package dev.deftu.eventbus

import dev.deftu.eventbus.invokers.Invoker

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
