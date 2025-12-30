package resource;

import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.annotations.Listen;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcherBuilder;
import me.bottdev.breezeapi.resource.watcher.WatchEventType;
import me.bottdev.breezeapi.resource.watcher.events.ResourceWatchEvent;
import me.bottdev.breezeapi.resource.watcher.subjects.SingleWatchSubject;
import me.bottdev.breezeapi.resource.watcher.types.SingleResourceWatcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class SingleResourceWatchTest {

    public static class TestListener implements Listener {

        @Listen
        private void onWatch(ResourceWatchEvent event) {

            WatchEventType type = event.getType();
            ResourceWatchSubject<?> watchSubject = event.getWatchSubject();
            String registrationKey = watchSubject.getRegistrationKey().orElse("Not valid key");

            System.out.printf("%s has been watched for %s\n", type, registrationKey);

        }

    }

    static final BreezeLogger logger = new SimpleLogger("ResourceWatchTest");

    static EventBus eventBus;
    static LifecycleManager lifecycleManager;
    static SingleResourceWatcher singleResourceWatcher;

    @BeforeAll
    static void setUp() {
        eventBus = new EventBus(new SimpleTreeLogger("EventBus"));
        eventBus.registerListeners(new TestListener());

        lifecycleManager = new LifecycleManager(new SimpleLogger("LifecycleManager"));
        singleResourceWatcher = lifecycleManager.create(new ResourceWatcherBuilder.Single(eventBus));
    }

    @AfterAll
    static void tearDown() {
        lifecycleManager.shutdownAll();
        eventBus.unregisterAllListeners();
    }

    public Optional<FileResource> getFileResource(Path parent) throws IOException {

        Path watchSubject = Path.of("watch_subject.txt");
        Path absolutePath = parent.resolve(watchSubject.toString());
        File file = absolutePath.toFile();

        if (file.createNewFile()) {
            file.deleteOnExit();
        }

        return TempFiles.create(watchSubject)
                .map(tempFile -> {
                    tempFile.setSourcePath(absolutePath);
                    return new SingleFileResource(tempFile, SourceType.DRIVE);
                });

    }

    @Test
    void testDirectory() throws InterruptedException, IOException {

        logger.info("Test single watch service:");

        Path parent = Path.of("/Users/romanplakhotniuk/watcher_test");
        Optional<FileResource> resourceOptional = getFileResource(parent);
        if (resourceOptional.isEmpty()) return;

        FileResource resource = resourceOptional.get();
        singleResourceWatcher.register(new SingleWatchSubject(resource, "test"));

        Thread.sleep(30_000);

    }

}
