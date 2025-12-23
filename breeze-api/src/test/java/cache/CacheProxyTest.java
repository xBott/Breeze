package cache;

import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.proxy.CacheProxyHandlerFactory;
import me.bottdev.breezeapi.cache.proxy.Cacheable;
import me.bottdev.breezeapi.cache.proxy.annotations.CacheEvict;
import me.bottdev.breezeapi.cache.proxy.annotations.CachePut;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheProxyTest {


    @Proxy
    public interface Computations extends Cacheable {

        @CachePut(ttl = 500, group = "static")
        default int computeStatic() throws InterruptedException {
            Thread.sleep(100);
            return 1000;
        }

        @CachePut(ttl = 500, group = "dynamic", key = "{a}.{b}")
        default int computeDynamic(int a, int b) throws InterruptedException {
            int sum = a + b;
            for (int i = 0; i < sum; i++) {
                Thread.sleep(50);
            }
            return sum * 500;
        }

        @CachePut(group = "constant", key = "{a}")
        default int computeSquare(int a) throws InterruptedException {
            Thread.sleep(100);
            return a * a;
        }

        @CacheEvict(group = "constant", key = "{a}")
        void resetSquare(int a);

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
        cacheManager.clear();
        computations = proxyFactory.createObject(Computations.class).orElse(null);
    }

    <T> Pair<Long, T> measureTime(TimeMeasureFunction<T> function) {
        long start = System.currentTimeMillis();
        try {
            T result = function.invoke();
            long delta = System.currentTimeMillis() - start;
            return Pair.of(delta, result);

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        long delta = System.currentTimeMillis() - start;
        return Pair.of(delta, null);
    }

    @Test
    void shouldCacheStaticComputation() {
        System.out.println("Test cache without ttl:");

        Pair<Long, Integer> before = measureTime(() -> computations.computeStatic());
        Pair<Long, Integer> after = measureTime(() -> computations.computeStatic());

        System.out.printf("   before cache %dms\n", before.getLeft());
        System.out.printf("   after cache %dms\n", after.getLeft());

        assertEquals(1000, before.getRight());
        assertEquals(before.getRight(), after.getRight());
        assertTrue(after.getLeft() < before.getLeft());
    }

    @Test
    void shouldCacheStaticComputationTTL() throws Throwable {
        System.out.println("Test cache with ttl:");

        Pair<Long, Integer> before = measureTime(() -> computations.computeStatic());
        Pair<Long, Integer> after = measureTime(() -> computations.computeStatic());

        System.out.printf("   before cache %dms\n", before.getLeft());
        System.out.printf("   after cache %dms\n", after.getLeft());
        System.out.println("   delay 1000ms");
        Thread.sleep(1000);

        after = measureTime(() -> computations.computeStatic());
        System.out.printf("   after ttl expire %dms\n", after.getLeft());

        assertEquals(1000, before.getRight());
        assertEquals(before.getRight(), after.getRight());
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCacheStaticComputationAsync() {
        System.out.println("Test cache async (deterministic):");

        CountDownLatch startLatch = new CountDownLatch(1);

        long testStart = System.nanoTime();

        CompletableFuture<Pair<Long, Integer>> futureA =
                CompletableFuture.supplyAsync(() -> {
                    await(startLatch);
                    return measureTime(() -> computations.computeStatic());
                });

        CompletableFuture<Pair<Long, Integer>> futureB =
                CompletableFuture.supplyAsync(() -> {
                    await(startLatch);
                    return measureTime(() -> computations.computeStatic());
                });

        startLatch.countDown();

        CompletableFuture.allOf(futureA, futureB).join();

        Pair<Long, Integer> a = futureA.join();
        Pair<Long, Integer> b = futureB.join();

        System.out.printf("   (async A) took %d ms%n", a.getLeft());
        System.out.printf("   (async B) took %d ms%n", b.getLeft());

        Pair<Long, Integer> sync = measureTime(() -> computations.computeStatic());
        System.out.printf("   (sync) took %d ms%n", sync.getLeft());

        long testEnd = System.nanoTime();
        long totalMs = TimeUnit.NANOSECONDS.toMillis(testEnd - testStart);

        System.out.printf("   total time: %d ms%n", totalMs);

        assertEquals(a.getRight(), b.getRight());
        assertEquals(a.getRight(), sync.getRight());

        long maxAsync = Math.max(a.getLeft(), b.getLeft());

        assertTrue(
                totalMs < maxAsync * 1.5,
                "Async computations were executed more than once"
        );
    }

    @Test
    void shouldCacheDynamicComputation() {
        System.out.println("Test cache parameters:");

        Pair<Long, Integer> beforeCache = measureTime(() -> computations.computeDynamic(1, 1));
        System.out.printf("   before cache %dms\n", beforeCache.getLeft());

        Pair<Long, Integer> afterCache = measureTime(() -> computations.computeDynamic(1, 1));
        System.out.printf("   after cache %dms\n", afterCache.getLeft());

        assertTrue(afterCache.getLeft() < beforeCache.getLeft());
    }


    @Test
    void shouldCacheAndEvictState() {

        System.out.println("Test cache put and evict:");

        int a = 1000;

        Pair<Long, Integer> beforeCache = measureTime(() -> computations.computeSquare(a));
        System.out.printf("   before cache %dms\n", beforeCache.getLeft());

        Pair<Long, Integer> afterCache = measureTime(() -> computations.computeSquare(a));
        System.out.printf("   after cache %dms\n", afterCache.getLeft());

        assertTrue(afterCache.getLeft() < beforeCache.getLeft());

        computations.resetSquare(a);

        Pair<Long, Integer> afterEvict = measureTime(() -> computations.computeSquare(a));
        System.out.printf("   after cache evict %dms\n", afterEvict.getLeft());

        assertTrue(afterCache.getLeft() < afterEvict.getLeft());

    }

}
