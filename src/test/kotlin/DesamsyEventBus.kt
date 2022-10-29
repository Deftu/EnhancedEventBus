import dev.deamsy.eventbus.impl.asm.ASMEventBus
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DesamsyEventBusEventBusTest {
    private val eventBus = ASMEventBus()

    @Test
    @Order(0)
    fun `subscribing class`() {
        eventBus.registerLambda(MessageReceivedEvent::class.java) { event ->
            println("message: ${event.message}")
        }
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
