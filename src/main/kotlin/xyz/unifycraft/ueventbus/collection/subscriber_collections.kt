package xyz.unifycraft.ueventbus.collection

import xyz.unifycraft.ueventbus.EventBus
import java.util.Comparator
import java.util.concurrent.CopyOnWriteArrayList

class ConcurrentSubscriberArrayList : CopyOnWriteArrayList<EventBus.Subscriber>() {
    override fun add(element: EventBus.Subscriber): Boolean {
        if (size == 0) {
            super.add(element)
        } else {
            var index = binarySearch(element, Comparator.comparingInt { obj: EventBus.Subscriber -> obj.priority })
            if (index < 0) index = -(index + 1)
            super.add(index, element)
        }

        return true
    }
}

class SubscriberArrayList : ArrayList<EventBus.Subscriber>() {
    override fun add(element: EventBus.Subscriber): Boolean {
        if (size == 0) {
            super.add(element)
        } else {
            var index = binarySearch(element, Comparator.comparingInt { obj: EventBus.Subscriber -> obj.priority })
            if (index < 0) index = -(index + 1)
            super.add(index, element)
        }

        return true
    }
}
