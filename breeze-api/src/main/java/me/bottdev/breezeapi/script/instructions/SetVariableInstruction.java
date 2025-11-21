package me.bottdev.breezeapi.script.instructions;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.script.FrameStack;
import me.bottdev.breezeapi.script.Instruction;
import me.bottdev.breezeapi.script.Value;

@RequiredArgsConstructor
public class SetVariableInstruction implements Instruction {

    private final String id;
    private final Value value;

    @Override
    public void execute(FrameStack frameStack) {
//        frameStack.getLast().getLocalVariables().put(id, value);
    }

}
