import me.bottdev.breezeapi.log.BreezeLogPlatform;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;
import me.bottdev.breezeapi.log.platforms.SL4JLogPlatform;
import me.bottdev.breezeapi.log.trace.LogTrace;
import me.bottdev.breezeapi.log.trace.TraceScope;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogTest {

    static BreezeLogPlatform platform;
    static BreezeLoggerFactory loggerFactory;

    BreezeLogger logger;

    @BeforeAll
    static void setup() {
        platform = new SL4JLogPlatform();
        loggerFactory = new BreezeLoggerFactory(platform);
    }

    @BeforeEach
    void setupLogger() {
        logger = loggerFactory.simple("LogTest");
    }

    @Test
    void shouldTrace() {

        LogTrace trace = new LogTrace();
        trace.addListener(logger);

        try (TraceScope root = new TraceScope(trace, "root", 0)) {

            try (TraceScope db = new TraceScope(trace, "data loading", root.nextDepth())) {
                db.info("<blue>Loading data from database");
            }

            root.info("Everything is fine");
        }

    }

}
