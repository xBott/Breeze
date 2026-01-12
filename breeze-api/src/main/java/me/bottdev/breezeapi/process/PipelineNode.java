package me.bottdev.breezeapi.process;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class PipelineNode {

    public static PipelineNode of(ProcessStage stage) {
        String name = stage.getClass().getSimpleName();
        return new PipelineNode(name, stage);
    }

    private final List<PipelineNode> thenNodes = new ArrayList<>();

    private String name;
    private ProcessStage stage;

    public PipelineNode then(PipelineNode node) {
        thenNodes.add(node);
        return this;
    }

    public PipelineNode then(ProcessStage stage) {
        String name = stage.getClass().getSimpleName();
        PipelineNode node = new PipelineNode(name, stage);
        return then(node);
    }

    public PipelineNode then(ProcessStage stage, Consumer<PipelineNode> nodeConsumer) {
        String name = stage.getClass().getSimpleName();
        PipelineNode node = new PipelineNode(name, stage);
        nodeConsumer.accept(node);
        return then(node);
    }

}
