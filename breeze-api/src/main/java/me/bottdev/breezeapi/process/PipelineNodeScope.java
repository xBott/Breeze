package me.bottdev.breezeapi.process;

import lombok.Getter;
import me.bottdev.breezeapi.log.trace.TraceScope;

@Getter
public class PipelineNodeScope implements AutoCloseable {

    private final PipelineNode node;
    private final PipelineContext context;
    private final TraceScope trace;

    public PipelineNodeScope(PipelineNode node, PipelineContext context) {
        this.node = node;
        this.context = context;
        this.trace = context.scope(node.getName());
    }

    public void error(String message, boolean critical) {
        PipelineError error = new PipelineError(node, message, critical);
        context.addError(error);
        trace.warn("Pipeline error: {}", message);
        trace.setFailed(true);
    }

    public void fail(Throwable t) {
        trace.error(t);

        context.addError(
                new PipelineError(node, t.getMessage(), true)
        );
    }

    @Override
    public void close() {
        trace.close();
    }

}
