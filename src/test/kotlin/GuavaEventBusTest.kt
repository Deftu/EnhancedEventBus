import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class GuavaEventBusTest {
    private val eventBus = EventBus()

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
        repeat(50) {
            eventBus.post(MessageReceivedEvent("Hello, World!"))
        }
    }


    @Test
    @Order(2)
    fun `removing class`() {
        eventBus.unregister(this)
    }
}
