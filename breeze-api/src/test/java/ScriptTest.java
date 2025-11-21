import me.bottdev.breezeapi.script.Frame;
import me.bottdev.breezeapi.script.FrameStack;
import me.bottdev.breezeapi.script.instructions.PrintInstruction;
import me.bottdev.breezeapi.script.instructions.SetVariableInstruction;
import me.bottdev.breezeapi.script.values.IntegerValue;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScriptTest {


    @Test
    public void testSimpleInstructions() {

        /*
        - setVar{id=a;type=Integer;value=1}
        - setVar{id=b;type=Integer;value=10}
        - setVar{id=c;type=Integer;value=100}
        - print{message="test1"}
         */

        FrameStack frameStack = new FrameStack();
        Frame frame = new Frame();
        frame.setInstructions(
                List.of(
                        new SetVariableInstruction("a", new IntegerValue(1)),
                        new SetVariableInstruction("b", new IntegerValue(10)),
                        new SetVariableInstruction("c", new IntegerValue(100)),
                        new PrintInstruction("test 1"),
                        new SetVariableInstruction("a", new IntegerValue(2)),
                        new SetVariableInstruction("b", new IntegerValue(20)),
                        new PrintInstruction("test 2"),
                        new SetVariableInstruction("c", new IntegerValue(200))
                )
        );
        frameStack.addFrame(frame);
        frameStack.process();

    }

}
