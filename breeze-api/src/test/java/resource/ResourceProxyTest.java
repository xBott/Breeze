package resource;

import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.proxy.CacheProxyHandlerFactory;
import me.bottdev.breezeapi.cache.proxy.Cacheable;
import me.bottdev.breezeapi.cache.proxy.annotations.CachePut;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.sources.DummySource;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.annotations.sources.JarSource;
import me.bottdev.breezeapi.resource.proxy.ResourceProvider;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandlerFactory;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.source.types.DummyResourceSource;
import me.bottdev.breezeapi.resource.source.types.JarResourceSource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceProxyTest {

    @Proxy
    public interface SomeResourceProvider extends ResourceProvider, Cacheable {

        @CachePut
        @ProvideResource
        @DummySource(value = "Version is 0.2!")
        Optional<SingleFileResource> getVersion();

        @CachePut
        @ProvideResource
        @DummySource(value = "Message to edit")
        Optional<SingleFileResource> getMutableResource();

        @CachePut
        @ProvideResource
        @JarSource(path = "test_resource.txt")
        Optional<SingleFileResource> getTestResource();

        @CachePut
        @ProvideResource
        @JarSource(path = "not_existing.txt")
        @DummySource(value = "Dummy")
        Optional<SingleFileResource> getChainedResource();

        @CachePut
        @ProvideResource(isTree = true)
        @JarSource(path = "tree")
        ResourceTree<SingleFileResource> getTree();

    }

    static final SimpleTreeLogger logger = new SimpleTreeLogger("ResourceProxyTest");

    static ProxyFactoryRegistry proxyFactory;
    static CacheManager cacheManager;
    SomeResourceProvider provider;

    @BeforeAll
    static void createProxyFactory() {

        cacheManager = new CacheManager();

        ResourceSourceRegistry resourceSourceRegistry = new ResourceSourceRegistry()
                .register(SourceType.JAR, new JarResourceSource())
                .register(SourceType.DUMMY, new DummyResourceSource());

        proxyFactory = new ProxyFactoryRegistry()
                .register(new CacheProxyHandlerFactory(cacheManager), 0)
                .register(new ResourceProxyHandlerFactory(resourceSourceRegistry), 1);
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

            Optional<SingleFileResource> versionOptional = provider.getMutableResource();
            if (versionOptional.isEmpty()) return;

            SingleFileResource resource = versionOptional.get();

            String contentBefore = resource.readTrimmed().orElse("Resource is empty!");
            logger.info(" (Before) Read content: {}", contentBefore);

            assertEquals("Message to edit", contentBefore);

            resource.write("Updated data");

            String contentAfter = resource.readTrimmed().orElse("Resource is empty!");
            logger.info(" (After) Read content: {}", contentAfter);

            assertEquals("Updated data", contentAfter);

            resource.save();

        });

    }

    @Test
    void shouldReadJarResource() {

        logger.withSection("Test jar read:", "", () -> {
            Optional<SingleFileResource> versionOptional = provider.getTestResource();

            String content = versionOptional
                    .flatMap(SingleFileResource::readTrimmed)
                    .orElse("Resource is empty!");

            logger.info(" Read content: {}", content);

            assertEquals("BreezeEngine is the best!", content);
        });

    }

    @Test
    void shouldReadJarAndDummy() {

        logger.withSection("Test jar and dummy read:", "", () -> {
            Optional<SingleFileResource> versionOptional = provider.getChainedResource();

            String content = versionOptional
                    .flatMap(SingleFileResource::readTrimmed)
                    .orElse("Resource is empty!");

            logger.info(" Read content: {}", content);

            assertEquals("Dummy", content);
        });

    }

    @Test
    void shouldReadJarTree() {

        logger.withSection("Test jar tree read:", "", () -> {
            ResourceTree<SingleFileResource> tree = provider.getTree();

            tree.getData().forEach((key, resource) ->
                    logger.info(key + ": " + resource.readTrimmed().orElse("Resource is empty!"))
            );

            int size = tree.getSize();
            logger.info("Read {} resources", size);

            assertEquals(3, size);
        });

    }

}
