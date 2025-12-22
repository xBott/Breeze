package resource;

import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.annotations.DummySource;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.proxy.ResourceProvider;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandlerFactory;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.source.types.DummyResourceSource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceProxyTest {

    @Proxy
    public interface SomeResourceProvider extends ResourceProvider {

        @ProvideResource
        @DummySource(value = "Version is 0.2!")
        Optional<SingleFileResource> getVersion();

    }

    static final SimpleTreeLogger logger = new SimpleTreeLogger("ResourceProxyTest");

    static ProxyFactoryRegistry proxyFactory;
    SomeResourceProvider provider;

    @BeforeAll
    static void createProxyFactory() {

        ResourceSourceRegistry resourceSourceRegistry = new ResourceSourceRegistry()
                .register(SourceType.DUMMY, new DummyResourceSource());

        proxyFactory = new ProxyFactoryRegistry()
                .register(new ResourceProxyHandlerFactory(resourceSourceRegistry), 0);
    }

    @BeforeEach
    void setup() {
        provider = proxyFactory.createObject(SomeResourceProvider.class).orElse(null);
    }

    @Test
    void shouldSetup() {
        assertNotNull(provider);
    }

    @Test
    void shouldReturnCorrectVersion() {

        logger.withSection("Test read:", "", () -> {

            Optional<SingleFileResource> versionOptional = provider.getVersion();

            String content = versionOptional
                    .flatMap(SingleFileResource::readTrimmed)
                    .orElse("Resource is empty!");

            logger.info(" Read content: {}", content);

            assertEquals("Version is 0.2!", content);

        });

    }

    @Test
    void shouldReadAndWrite() {

        logger.withSection("Test read and write:", "", () -> {

            Optional<SingleFileResource> versionOptional = provider.getVersion();
            if (versionOptional.isEmpty()) return;

            SingleFileResource resource = versionOptional.get();

            String contentBefore = resource.readTrimmed().orElse("Resource is empty!");
            logger.info(" (Before) Read content: {}", contentBefore);

            assertEquals("Version is 0.2!", contentBefore);

            resource.write("Updated data");
            String contentAfter = resource.readTrimmed().orElse("Resource is empty!");
            logger.info(" (After) Read content: {}", contentAfter);

            assertEquals("Updated data", contentAfter);

            resource.save();

        });

    }

}
