package me.bottdev.breezeapi.script.instructions;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.script.FrameStack;
import me.bottdev.breezeapi.script.Instruction;

@RequiredArgsConstructor
public class WhileInstruction implements Instruction {

    private final String value;

    @Override
    public void execute(FrameStack frameStack) {
        System.out.println(value);
    }

}
