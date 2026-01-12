package me.bottdev.breezeapi.process;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ProcessPipeline {

    public static ProcessPipeline of(PipelineNode... nodes) {
        ProcessPipeline pipeline = new ProcessPipeline();
        pipeline.addNodes(nodes);
        return pipeline;
    }

    @Getter
    private final List<PipelineNode> pipelineNodes = new ArrayList<>();

    public ProcessPipeline addNode(PipelineNode node) {
        pipelineNodes.add(node);
        return this;
    }

    public ProcessPipeline addNodes(PipelineNode... nodes) {
        for (PipelineNode node : nodes) {
            addNode(node);
        }
        return this;
    }

}
