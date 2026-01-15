package me.bottdev.breezeprocessor.rounds;

import lombok.Getter;
import me.bottdev.breezeprocessor.ProcessingRound;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class StaticProcessingRound extends ProcessingRound {

    @Getter
    private final String roundName;
    private final Runnable runnable;

    public StaticProcessingRound(Messager messager, String roundName, Runnable runnable) {
        super(messager);
        this.roundName = roundName;
        this.runnable = runnable;
    }

    public void process() {
        logRoundStart();
        runnable.run();
        logRoundEnd();
    }

    protected void logRoundStart() {
        messager.printMessage(Diagnostic.Kind.NOTE,
                "[" + getRoundName() + "] Starting processing");
    }

    protected void logRoundEnd() {
        messager.printMessage(Diagnostic.Kind.NOTE,
                "[" + getRoundName() + "] Finished processing");
    }

}