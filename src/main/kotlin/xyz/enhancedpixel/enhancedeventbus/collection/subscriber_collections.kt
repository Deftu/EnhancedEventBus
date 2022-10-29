package xyz.enhancedpixel.enhancedeventbus.collection

import xyz.enhancedpixel.enhancedeventbus.EventBus
import java.util.Comparator
import java.util.concurrent.CopyOnWriteArrayList

class ConcurrentSubscriberArrayList : CopyOnWriteArrayList<EventBus.EventSubscriber>() {
    override fun add(element: EventBus.EventSubscriber): Boolean {
        if (size == 0) {
            super.add(element)
        } else {
            var index = binarySearch(element, Comparator.comparingInt { it.priority.ordinal })
            if (index < 0) index = -(index + 1)
            super.add(index, element)
        }

        return true
    }
}

class SubscriberArrayList : ArrayList<EventBus.EventSubscriber>() {
    override fun add(element: EventBus.EventSubscriber): Boolean {
        if (size == 0) {
            super.add(element)
        } else {
            var index = binarySearch(element, Comparator.comparingInt { it.priority.ordinal })
            if (index < 0) index = -(index + 1)
            super.add(index, element)
        }

        return true
    }
}
