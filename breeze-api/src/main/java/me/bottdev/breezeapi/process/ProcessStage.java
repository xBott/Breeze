package me.bottdev.breezeapi.process;

@FunctionalInterface
public interface ProcessStage {

    void apply(PipelineNodeScope scope);

}
