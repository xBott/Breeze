package command;

import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.CommandTreeBuilder;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandTreeBuilderTest {

    @Test
    void shouldBuildCommandWithBuilder() {

        CommandRootNode rootNode = CommandTreeBuilder.named("test")
                .next(
                        CommandTreeBuilder.literal("test")
                )
                .next(
                        CommandTreeBuilder.literal("test2")
                                .next(CommandTreeBuilder.executes(context -> {
                                    context.getSender().send("Executed test2!");
                                })
                        )
                )
                .next(
                        CommandTreeBuilder.executes(context -> {
                            context.getSender().send("Executed!");
                        })
                )
                .build();


        assertEquals(3, rootNode.getChildren().size());
        assertEquals(1, rootNode.getChild("test2")
                .map(CommandNode::getChildren)
                .map(Map::size)
                .orElse(0)
        );


    }

}
