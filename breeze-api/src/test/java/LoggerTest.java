import me.bottdev.breezeapi.log.types.SimpleTreeLogger;
import me.bottdev.breezeapi.log.TreeLogger;
import org.junit.jupiter.api.Test;

public class LoggerTest {

    @Test
    public void testTreeLogger() {

        TreeLogger treeLogger = new SimpleTreeLogger("BreezeEngine");
        treeLogger.info("Breeze Engine Startup", "");
        treeLogger.push("BreezeEngine Initialization", "");
        treeLogger.error("Loaded context", null);
        treeLogger.info("Loaded modules (3x)");
        treeLogger.pop();
        treeLogger.info("Successfully started!");
    }

}
