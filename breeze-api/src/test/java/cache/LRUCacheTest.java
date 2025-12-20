package cache;

import me.bottdev.breezeapi.cache.CacheStats;
import me.bottdev.breezeapi.cache.LRUCache;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LRUCacheTest {

    private LRUCache<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>(3);
    }

    @AfterEach
    void tearDown() {
        cache.shutdown();
    }

    @Test
    void shouldReturnEmptyOnMiss() {
        Optional<String> value = cache.get("missing");

        assertTrue(value.isEmpty());
        assertEquals(0, cache.getStats().getHits());
        assertEquals(1, cache.getStats().getMisses());
    }

    @Test
    void shouldEvictLeastRecentlyUsed() {
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        cache.get("a");

        cache.put("d", "D");

        assertTrue(cache.get("a").isPresent());
        assertTrue(cache.get("c").isPresent());
        assertTrue(cache.get("d").isPresent());
        assertTrue(cache.get("b").isEmpty());

    }

    @Test
    void shouldExpireEntryByTTL() throws InterruptedException {
        cache.put("temp", "value", Duration.ofMillis(200));

        Thread.sleep(100);
        assertTrue(cache.get("temp").isPresent());

        Thread.sleep(150);
        assertTrue(cache.get("temp").isEmpty());
    }

    @Test
    void shouldNotExceedMaxSize() {
        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");

        assertEquals(3, cache.size());
    }

    @Test
    void shouldCollectStatsCorrectly() {
        cache.put("a", "A");

        cache.get("a"); // hit
        cache.get("a"); // hit
        cache.get("b"); // miss

        CacheStats stats = cache.getStats();
        System.out.println(stats);

        assertEquals(2, stats.getHits());
        assertEquals(1, stats.getMisses());
        assertEquals(2.0 / 3.0, stats.getHitRate(), 0.0001);
    }

    @Test
    void performanceTest() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            cache.put("key" + i, "value" + i);
        }
        for (int i = 0; i < 10000; i++) {
            cache.get("key" + i);
        }
        long end = System.currentTimeMillis();

        System.out.println("\nInvocation time: " + (end - start) + "ms");
        System.out.println("Cache size: " + cache.size());
        System.out.println("Cache data: ");
        cache.getData().forEach((key, entry) ->
                System.out.printf("- key: %s, value: %s%n", key, entry.getValue())
        );
        System.out.println("Statistics: " + cache.getStats());
    }


}
