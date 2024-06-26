import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.eventbus.invokers.LMFInvoker
import org.junit.jupiter.api.*
import dev.deftu.eventbus.EventPriority
import dev.deftu.eventbus.bus
import dev.deftu.eventbus.on

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EventBusTest {
    private val eventBus = bus {
        setInvoker(LMFInvoker())
        setExceptionHandler { exception ->
            println("Error occurred in method: ${exception.message}")
            exception.printStackTrace()
        }
        setThreadSafety(false)
    }

    @Test
    @Order(0)
    fun `subscribing class`() {
        eventBus.register(this)
        eventBus.on<MessageReceivedEvent> {
            println("message lambda: $message")
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun `subscribed method`(event: MessageReceivedEvent) {
        println("message: ${event.message}")
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun `subscribed method 2`(event: MessageReceivedEvent) {
        println("message 2: ${event.message}")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun `subscribed method 3`(event: MessageReceivedEvent) {
        println("message 3: ${event.message}")
    }

    @Test
    @Order(1)
    fun `posting event`() {
        repeat(50) {
            eventBus.post { MessageReceivedEvent("Hello, World!") }
        }
    }

    @Test
    @Order(2)
    fun `removing class`() {
        eventBus.unregister(this)
    }
}
