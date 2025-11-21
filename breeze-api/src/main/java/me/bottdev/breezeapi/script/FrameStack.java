package me.bottdev.breezeapi.script;

import java.util.Stack;

public class FrameStack {
    private final Stack<Frame> callStack = new Stack<>();

    public Frame getLast() {
        return callStack.getLast();
    }

    public void addFrame(Frame frame) {
        callStack.push(frame);
    }

    public void process() {
        while (!callStack.isEmpty()) {
            Frame frame = getLast();

            if (frame.getPointer() >= frame.getInstructions().size()) {
                System.out.println("Pointer is higher than instructions size, going to next frame.");
                System.out.println("Final frame local variables:");
//                frame.getLocalVariables().forEach((id, value) -> {
//                    System.out.println("- " + id + ": " + value.getClass().getSimpleName() + " = " + value);
//                });
                callStack.removeLast();
                continue;
            }

            Instruction instruction = frame.getInstructions().get(frame.getPointer());

            instruction.execute(this);
            frame.setPointer(frame.getPointer() + 1);

        }
        System.out.println("All frames processed.");

    }

}
