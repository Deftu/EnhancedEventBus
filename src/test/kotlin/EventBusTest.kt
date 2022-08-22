import xyz.unifycraft.ueventbus.Subscribe
import xyz.unifycraft.ueventbus.invokers.LMFInvoker
import org.junit.jupiter.api.*
import xyz.unifycraft.ueventbus.eventBus

class MessageReceivedEvent(
    val message: String
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EventBusTest {

    private val eventBus = eventBus {
        invoker(LMFInvoker())
        exceptionHandler { exception -> println("Error occurred in method: ${exception.message}")  }
        threadSafety(false)
    }

    @Test
    @Order(0)
    fun `subscribing class`() {
        eventBus.register(this)
    }

    @Subscribe
    fun `subscribed method`(event: MessageReceivedEvent) {
        println("message: ${event.message}")
    }

    @Test
    @Order(1)
    fun `posting event`() {
        repeat(100_000) {
            eventBus.post { MessageReceivedEvent("Hello world") }
        }
    }

    @Test
    @Order(2)
    fun `removing class`() {
        eventBus.unregister(this)
    }

}