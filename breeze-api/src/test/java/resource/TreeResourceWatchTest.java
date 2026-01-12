package resource;

import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.annotations.Listen;
import me.bottdev.breezeapi.lifecycle.SimpleLifecycleManager;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.platforms.SL4JLogPlatform;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcherBuilder;
import me.bottdev.breezeapi.resource.watcher.WatchEventType;
import me.bottdev.breezeapi.resource.watcher.events.ResourceWatchEvent;
import me.bottdev.breezeapi.resource.watcher.subjects.TreeWatchSubject;
import me.bottdev.breezeapi.resource.watcher.types.TreeResourceWatcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class TreeResourceWatchTest {

    public static class TestEventBus extends EventBus {

        public TestEventBus(BreezeLogger mainLogger) {
            super(mainLogger);
        }

        @Override
        protected void onStart() {}

    }

    public static class TestListener implements Listener {

        @Listen
        private void onWatch(ResourceWatchEvent event) {

            WatchEventType type = event.getType();
            ResourceWatchSubject<?> watchSubject = event.getWatchSubject();
            String registrationKey = watchSubject.getRegistrationKey().orElse("Not valid key");

            System.out.printf("%s has been watched for %s\n", type, registrationKey);

        }

    }

    static final BreezeLogger logger = SL4JLogPlatform.getFactory().simple("ResourceWatchTest");

    static EventBus eventBus;
    static SimpleLifecycleManager lifecycleManager;
    static TreeResourceWatcher treeResourceWatcher;

    @BeforeAll
    static void setUp() {
        eventBus = new TestEventBus(SL4JLogPlatform.getFactory().simple("EventBus"));
        eventBus.registerListeners(new TestListener());

        lifecycleManager = new SimpleLifecycleManager(SL4JLogPlatform.getFactory().simple("LifecycleManager"));
        treeResourceWatcher = lifecycleManager.create(new ResourceWatcherBuilder.Tree(eventBus));
    }

    @AfterAll
    static void tearDown() {
        lifecycleManager.shutdownAll();
        eventBus.unregisterAllListeners();
    }

    @Test
    void testDirectory() throws InterruptedException {

        logger.info("Test single watch service:");

        Path parent = Path.of("/Users/romanplakhotniuk/watcher_test");

        ResourceTree<FileResource> resourceTree = new ResourceTree<>(parent);
        treeResourceWatcher.register(new TreeWatchSubject(resourceTree, "test"));

        Thread.sleep(30_000);

    }

}
