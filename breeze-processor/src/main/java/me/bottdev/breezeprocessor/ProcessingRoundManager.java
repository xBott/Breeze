package me.bottdev.breezeprocessor;

import lombok.Getter;
import me.bottdev.breezeprocessor.rounds.DynamicProcessingRound;
import me.bottdev.breezeprocessor.rounds.StaticProcessingRound;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProcessingRoundManager {

    @Getter
    private final List<ProcessingRound> rounds = new ArrayList<>();

    public void add(ProcessingRound round) {
        rounds.add(round);
    }

    public void executeRounds(Set<? extends Element> elements) {
        for (ProcessingRound round : rounds) {

            if (round instanceof DynamicProcessingRound dynamicRound) {
                dynamicRound.process(elements);

            } else if (round instanceof StaticProcessingRound staticRound) {
                staticRound.process();

            }

        }
    }

}
