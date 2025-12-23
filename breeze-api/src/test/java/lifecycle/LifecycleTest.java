package lifecycle;

import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcherBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class LifecycleTest {

    static LifecycleManager lifecycleManager;

    @BeforeAll
    static void setUp() {
        lifecycleManager = new LifecycleManager();
    }

    @AfterAll
    static void shutdown() {
        lifecycleManager.shutdownAll();
    }

    public Path getRunningJarPath() {
        try {
            return Paths.get(
                    this.getClass().getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<FileResource> getFileResource() throws IOException {

        Path runningJarPath = getRunningJarPath();
        Path watchSubject = Path.of("watch_subject.txt");
        Path absolutePath = runningJarPath.resolve(watchSubject.toString());
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
    void shouldCreateResourceWatcherLifecycleAndSleep() throws IOException, InterruptedException {

        ResourceWatcher resourceWatcher = lifecycleManager.create(new ResourceWatcherBuilder());

        getFileResource().ifPresent(resource -> {
            resourceWatcher.registerResource(resource);
            resourceWatcher.getHookContainer(resource).add(changedResource ->
                    changedResource.readTrimmed().ifPresent(System.out::println)
            );
        });

        Thread.sleep(50000);

    }

}
