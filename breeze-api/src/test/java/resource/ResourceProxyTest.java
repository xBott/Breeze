package resource;

import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceProxyTest {

    @Proxy
    interface SomeResourceProvider extends ResourceProvider {

        @ProvideResource
        @DummySource(value = "Version is 0.2!")
        Optional<SingleFileResource> getVersion();

    }

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
        Optional<SingleFileResource> versionOptional = provider.getVersion();
        versionOptional
                .flatMap(SingleFileResource::read)
                .ifPresent(System.out::println);
    }

}
