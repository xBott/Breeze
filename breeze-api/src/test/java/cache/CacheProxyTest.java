package cache;

import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.proxy.CacheProxyHandlerFactory;
import me.bottdev.breezeapi.cache.proxy.Cacheable;
import me.bottdev.breezeapi.cache.proxy.annotations.Cached;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheProxyTest {

    @Proxy
    interface Computations extends Cacheable {

        @Cached(ttl = 500)
        default int computeStatic() throws InterruptedException {
            Thread.sleep(1000);
            return 10000;
        }

    }

    @FunctionalInterface
    public interface TimeMeasureFunction<R> extends Cacheable {
        R invoke() throws Throwable;

    }

    static ProxyFactoryRegistry proxyFactory;
    static CacheManager cacheManager;
    Computations computations;

    @BeforeAll
    static void createProxyFactory() {
        cacheManager = new CacheManager();
        proxyFactory = new ProxyFactoryRegistry()
                .register(new CacheProxyHandlerFactory(cacheManager), 0);
    }

    @BeforeEach
    void createComputations() {
        computations = proxyFactory.createObject(Computations.class).orElse(null);
    }

    <T> Pair<Long, T> measureTime(TimeMeasureFunction<T> function) throws Throwable {
        long start = System.currentTimeMillis();
        T result = function.invoke();
        long delta = System.currentTimeMillis() - start;
        return Pair.of(delta, result);
    }

    @Test
    void shouldCacheStaticComputation() throws Throwable {
        System.out.println("Test without ttl:");

        Pair<Long, Integer> before = measureTime(() -> computations.computeStatic());
        Pair<Long, Integer> after = measureTime(() -> computations.computeStatic());

        System.out.printf("- before cache %dms\n", before.getLeft());
        System.out.printf("- after cache %dms\n", after.getLeft());

        assertEquals(10000, before.getRight());
        assertEquals(before.getRight(), after.getRight());
        assertTrue(after.getLeft() < before.getLeft());
    }

    @Test
    void shouldCacheStaticComputationTTL() throws Throwable {
        System.out.println("Test with ttl:");

        Pair<Long, Integer> before = measureTime(() -> computations.computeStatic());
        Pair<Long, Integer> after = measureTime(() -> computations.computeStatic());

        System.out.printf("- before cache %dms\n", before.getLeft());
        System.out.printf("- after cache %dms\n", after.getLeft());
        System.out.println("- delay 1000ms");
        Thread.sleep(1000);

        after = measureTime(() -> computations.computeStatic());
        System.out.printf("- after ttl expire %dms\n", after.getLeft());

        assertEquals(10000, before.getRight());
        assertEquals(before.getRight(), after.getRight());
    }

}
