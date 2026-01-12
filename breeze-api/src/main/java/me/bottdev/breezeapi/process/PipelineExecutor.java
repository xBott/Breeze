package me.bottdev.breezeapi.process;

public interface PipelineExecutor {

    void execute(ProcessPipeline pipeline, PipelineContext context);

}
