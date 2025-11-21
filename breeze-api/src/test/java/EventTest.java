import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.Cancellable;
import me.bottdev.breezeapi.events.Event;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.annotations.Listener;
import org.junit.jupiter.api.Test;

public class EventTest {


    @Getter
    @RequiredArgsConstructor
    public static class JoinEvent implements Event, Cancellable {

        private final String name;
        private boolean cancelled;

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            cancelled = cancel;
        }

    }

    public static class JoinListener {

        @Listener(priority = 2)
        public void onJoin(JoinEvent event) {
            if (event.isCancelled()) return;
            System.out.println("Joined: " + event.getName());
        }

        @Listener(priority = 1)
        public void onJoinMorePriority(JoinEvent event) {
            System.out.println("NO, NOT JOINED: " + event.getName());
            event.setCancelled(true);
        }

    }

    @Test
    public void testEventBus() {

        EventBus eventBus = new EventBus();

        JoinListener listener = new JoinListener();
        eventBus.registerListeners(listener);

        JoinEvent event = new JoinEvent("Alice");
        eventBus.call(event);

        eventBus.unregisterListeners(listener);

    }

}
