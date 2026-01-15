import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.platforms.SLF4JLogPlatform;
import me.bottdev.breezeapi.process.*;
import me.bottdev.breezeapi.process.executors.SequentialPipelineExecutor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessTest {

    @RequiredArgsConstructor
    public static class CreationStage implements ProcessStage {

        private final String name;

        @Override
        public void apply(PipelineNodeScope scope) {
            PipelineContext context = scope.getContext();
            scope.getTrace().info("Creating user {}...", name);
            context.put(PipelineContextKey.of("name", String.class), name);
            context.put(PipelineContextKey.of("role", String.class), "Developer");
            scope.getTrace().info("Successfully created user {}!", name);
        }

    }

    @RequiredArgsConstructor
    public static class StartStage implements ProcessStage {

        private final String requiredName;

        @Override
        public void apply(PipelineNodeScope scope) {
            PipelineContext context = scope.getContext();

            scope.getTrace().info("Starting user {}...", requiredName);

            String name = context.get(PipelineContextKey.of("name", String.class)).orElseThrow();
            String role = context.get(PipelineContextKey.of("role", String.class)).orElseThrow();

            if (!name.equals(requiredName)) {
                scope.error(
                        "Incorrect name. Expected \"" + requiredName + "\" instead of \"" + name + "\"",
                        true
                );
                return;
            }

            scope.getTrace().info("Successfully started user {} {}!", name, role);
        }

    }

    static BreezeLogger logger;
    static PipelineExecutor pipelineExecutor;

    @BeforeAll
    static void setup() {
        logger = SLF4JLogPlatform.getFactory().simple("ProcessTest");
        pipelineExecutor = new SequentialPipelineExecutor(logger);
    }

    @Test
    void shouldCreateBranchedProcess() {

        ProcessPipeline pipeline = ProcessPipeline.of(
            PipelineNode.of(new CreationStage("Bob"))
                    .then(PipelineNode.of(new StartStage("Bob"))),
            PipelineNode.of(new CreationStage("Alice"))
                    .then(PipelineNode.of(new StartStage("Alice2")))
        );

        PipelineContext context = new PipelineContext();
        pipelineExecutor.execute(pipeline, context);

        assertTrue(context.isFailed());

    }


}
