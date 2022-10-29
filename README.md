<div align="center">

# [`EnhancedEventBus`]
JVM event bus focused on thread-safety and performance.

<sup>Based on [keventbus][keventbus] (Kevin Event Bus) by [KevinPriv][kevin]</sup>

</div>

## Registering an event listener

<details>
    <summary>Java</summary>

```java
public class Example {
    // Create our event bus instance.
    private EventBus eventBus = new EventBusBuilder()
            .setInvoker(new LMFInvoker())
            .setExceptionHandler(e -> {
                System.out.println("Error occurred in method: " + e.getMessage());
            }).build();

    /**
     * This is your application's entrypoint, where everything takes place.
     * It's best to register your event listener here.
     */
    public static void main(String[]args) {
        eventBus.register(this);
    }

    /**
     * Methods you'd like to register should be annotated with @SubscribeEvent
     * The first, and only, parameter is a MessageReceivedEvent. Thus, events posted with that class will invoke this method.
     */
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        // Do something with the data from the event.
        System.out.println("Received a message: " + event.getMessage());
    }
}
```

</details>

<details>
    <summary>Kotlin</summary>

```kt
// Create our event bus instance.
private val eventBus = bus {
    invoker = LMFInvoker()
    setExceptionHandler { e ->
        println("Error occurred in method: ${e.message}")
    }
}

/**
 * This is your application's entrypoint, where everything takes place.
 * It's best to register your event listener here.
 */
fun main(args: Array<String>) {
    eventBus.register(this)
}

/**
 * Methods you'd like to register should be annotated with @Subscribe
 * The first, and only, parameter is a MessageReceivedEvent. Thus, events posted with that class will invoke this method.
 */
@Subscribe
fun onMessageReceived(event: MessageReceivedEvent) {
    // Do something with the data from the event.
    println("Received a message: ${event.message}")
}
```

</details>

## Posting events

<details>
    <summary>Java</summary>

```java
public class Example {
    // Create our event bus instance.
    private EventBus eventBus = new EventBusBuilder()
            .setInvoker(new LMFInvoker())
            .setExceptionHandler(e -> {
                System.out.println("Error occurred in method: " + e.getMessage());
            }).build();

    /**
     * This is your application's entrypoint, where everything takes place.
     * It's best to register your event listener here.
     */
    public static void main(String[] args) {
        eventBus.post(new MessageReceivedEvent("Hello, World!"));
    }
}

public class MessageReceivedEvent {
    private final String message;

    public MessageReceivedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

</details>

<details>
    <summary>Kotlin</summary>

```kt
// Create our event bus instance.
private val eventBus = bus {
    invoker = LMFInvoker()
    setExceptionHandler { e ->
        println("Error occurred in method: ${e.message}")
    }
}

/**
 * This is your application's entrypoint, where everything takes place.
 * It's best to register your event listener here.
 */
fun main(args: Array<String>) {
    eventBus.post(MessageReceivedEvent("Hello, World!"))
}

data class MessageReceivedEvent(
    val message: String
)
```

</details>

## Unregistering an event listener

<details>
    <summary>Java</summary>

```java
public class Example {
    // Create our event bus instance.
    private EventBus eventBus = new EventBusBuilder()
            .setInvoker(new LMFInvoker())
            .setExceptionHandler(e -> {
                System.out.println("Error occurred in method: " + e.getMessage());
            }).build();

    /**
     * This is your application's entrypoint, where everything takes place.
     * It's best to register your event listener here.
     */
    public static void main(String[]args) {
        eventBus.unregister(this);
    }

    /**
     * Methods you'd like to register should be annotated with @Subscribe
     * The first, and only, parameter is a MessageReceivedEvent. Thus, events posted with that class will invoke this method.
     */
    @Subscribe
    public void onMessageReceived(MessageReceivedEvent event) {
        // Do something with the data from the event.
        System.out.println("Received a message: " + event.getMessage());
    }
}
```

</details>

<details>
    <summary>Kotlin</summary>

```kt
// Create our event bus instance.
private val eventBus = eventBus {
    invoker = LMFInvoker()
    setExceptionHandler { e ->
        println("Error occurred in method: ${e.message}")
    }
}

/**
 * This is your application's entrypoint, where everything takes place.
 * It's best to register your event listener here.
 */
fun main(args: Array<String>) {
    eventBus.unregister(this)
}

/**
 * Methods you'd like to register should be annotated with @Subscribe
 * The first, and only, parameter is a MessageReceivedEvent. Thus, events posted with that class will invoke this method.
 */
@Subscribe
fun onMessageReceived(event: MessageReceivedEvent) {
    // Do something with the data from the event.
    println("Received a message: ${event.message}")
}
```

</details>

[kevin]: https://github.com/KevinPriv
[keventbus]: https://github.com/KevinPriv/keventbus
