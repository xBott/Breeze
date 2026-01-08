package me.bottdev.breezepaper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezepaper.BreezePaper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PaperCommandRegistrar {

    private final BreezeLogger logger = new SimpleLogger("PaperCommandRegistrar");
    private final Map<Class<? extends CommandNode>, PaperCommandNodeFactory> factories = new HashMap<>();

    private final BreezePaper breezePaper;
    @Getter
    private final PaperCommandContextFactory contextFactory;

    @Inject
    public PaperCommandRegistrar(BreezePaper breezePaper, PaperCommandContextFactory paperCommandContextFactory) {
        this.breezePaper = breezePaper;
        this.contextFactory = paperCommandContextFactory;
    }

    public PaperCommandRegistrar addFactory(
            Class<? extends CommandNode> nodeClass,
            PaperCommandNodeFactory builder
    ) {
        factories.put(nodeClass, builder);
        return this;
    }

    private Optional<PaperCommandNodeFactory> getFactory(
            Class<? extends CommandNode> nodeClass
    ) {
        return Optional.ofNullable(factories.get(nodeClass));
    }

    public void register(CommandRootNode rootNode) {
        breezePaper.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralCommandNode<CommandSourceStack> paperRoot = convert(rootNode).build();
            commands.registrar().register(paperRoot);
        });
    }

    private LiteralArgumentBuilder<CommandSourceStack> convert(CommandRootNode rootNode) {
        LiteralArgumentBuilder<CommandSourceStack> paperRoot = Commands.literal(rootNode.getName());

        rootNode.getChildren().values().forEach(child ->
                registerSubCommand(child, paperRoot)
        );

        return paperRoot;
    }

    private void registerSubCommand(
            CommandNode node,
            ArgumentBuilder<CommandSourceStack, ?> parent
    ) {

        Class<? extends CommandNode> nodeClass = node.getClass();
        logger.info("registering node of class {} and name {}", nodeClass, node.getDisplayName());

        Optional<PaperCommandNodeFactory> factoryOptional = getFactory(nodeClass);
        if (factoryOptional.isEmpty()) {
            logger.warn("No command factory found for class {}", nodeClass);
            return;
        }

        PaperCommandNodeFactory factory = factoryOptional.get();

        Optional<ArgumentBuilder<CommandSourceStack, ?> > paperNodeOptional = factory.create(parent, node);
        if (paperNodeOptional.isEmpty()) return;

        ArgumentBuilder<CommandSourceStack, ?> paperNode = paperNodeOptional.get();

        if (!node.hasChildren()) return;
        node.getChildren().values().forEach(child ->
                registerSubCommand(child, paperNode)
        );

        parent.then(paperNode);
    }

}
