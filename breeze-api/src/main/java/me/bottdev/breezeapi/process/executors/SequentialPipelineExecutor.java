package me.bottdev.breezeapi.process.executors;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.trace.TraceScope;
import me.bottdev.breezeapi.process.*;

import java.util.List;

@RequiredArgsConstructor
public class SequentialPipelineExecutor implements PipelineExecutor {

    private final BreezeLogger logger;

    @Override
    public void execute(ProcessPipeline pipeline, PipelineContext context) {

        context.getTrace().addListener(logger);

        try (TraceScope scope = context.scope("Pipeline Sequential Execution")) {

            context.setDepthOffset(context.getDepthOffset() + 1);

            for (PipelineNode node : pipeline.getPipelineNodes()) {
                if (context.isFailed()) {
                    scope.warn("Pipeline Sequential Execution was interrupted by a critical error.");
                    break;
                }
                executeNode(node, context);
            }

            context.setDepthOffset(context.getDepthOffset() - 1);

            if (context.hasErrors()) {

                List<PipelineError> errors = context.getErrors();
                scope.info("Pipeline Sequential Execution has {}x errors.", errors.size());

                errors.forEach(error -> {
                    scope.info(" - {} at {}: {}",
                            error.isCritical() ? "Critical" : "Not Critical",
                            error.getNode().getName(),
                            error.getMessage()
                    );
                });
            }

        }
        
    }

    private void executeNode(PipelineNode node, PipelineContext context) {

        PipelineNodeScope scope = new PipelineNodeScope(node, context);

        try {
            node.getStage().apply(scope);

        } catch (Exception ex) {
            scope.fail(ex);

        } finally {
            scope.close();
        }

        if (context.isFailed()) return;

        context.setDepthOffset(context.getDepthOffset() + 1);
        node.getThenNodes().forEach(child -> executeNode(child, context));
        context.setDepthOffset(context.getDepthOffset() - 1);
    }

}
