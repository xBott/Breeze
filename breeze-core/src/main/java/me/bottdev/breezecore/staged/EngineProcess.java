package me.bottdev.breezecore.staged;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.structures.priority.PriorityList;
import me.bottdev.breezecore.StagedBreezeEngine;

@Getter
@RequiredArgsConstructor
public class EngineProcess {

    private final PriorityList<ProcessStage> stages = new PriorityList<>();

    private final String name;

    public EngineProcess addStage(ProcessStage stage, int priority) {
        stages.add(stage, priority);
        return this;
    }

    public EngineProcess addStage(ProcessStage stage, StagePriority priority) {
        return addStage(stage, priority.getPriority());
    }

    public void process(StagedBreezeEngine engine) {
        stages.forEach(processStage -> processStage.process(engine));
    }

}
