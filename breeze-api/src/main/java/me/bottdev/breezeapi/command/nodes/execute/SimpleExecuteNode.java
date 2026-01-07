package me.bottdev.breezeapi.command.nodes.execute;

import lombok.Getter;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.exceptions.UnsupportedParameterException;
import me.bottdev.breezeapi.command.nodes.CommandExecuteNode;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;

import java.util.function.Consumer;

@Getter
public class SimpleExecuteNode implements CommandExecuteNode {

    private final BreezeLogger logger = new SimpleLogger("CommandExecuteNode");

    private final Consumer<CommandExecutionContext> handler;

    public SimpleExecuteNode(Consumer<CommandExecutionContext> handler) throws UnsupportedParameterException {
        this.handler = handler;
    }

    @Override
    public void execute(CommandExecutionContext context) {
        handler.accept(context);
    }

}
