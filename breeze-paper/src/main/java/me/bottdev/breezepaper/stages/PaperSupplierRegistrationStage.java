package me.bottdev.breezepaper.stages;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandTreeParser;
import me.bottdev.breezeapi.command.argument.CommandArgumentFactory;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezeapi.command.nodes.execute.MethodExecuteNode;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;
import me.bottdev.breezepaper.BreezePaper;
import me.bottdev.breezepaper.ModuleSuggestionFactory;
import me.bottdev.breezepaper.command.PaperCommandContextFactory;
import me.bottdev.breezepaper.command.PaperCommandRegistrar;
import me.bottdev.breezepaper.command.nodes.PaperArgumentNodeFactory;
import me.bottdev.breezepaper.command.nodes.PaperExecuteNodeFactory;
import me.bottdev.breezepaper.command.nodes.PaperLiteralNodeFactory;
import me.bottdev.breezepaper.components.PaperPlayerManager;
import me.bottdev.breezepaper.text.BreezeAdventureText;

@RequiredArgsConstructor
public class PaperSupplierRegistrationStage implements ProcessStage {

    private final BreezePaper breezePaper;

    @Override
    public String getName() {
        return "Paper supplier registration";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        BreezeContext context = engine.getContext();

        registerBreezePaper(context);

        registerAdventureText(context);
        registerPlayerManager(context);

        registerCommandContextFactory(context);
        registerCommandRegistrar(context);
        registerCommandArgumentFactory(context);
        registerCommandTreeParser(context);

    }

    private void registerBreezePaper(BreezeContext context) {
        context.addObjectSupplier("breezePaper", new SingletonSupplier(breezePaper));
    }

    private void registerAdventureText(BreezeContext context) {
        context.injectConstructor(BreezeAdventureText.class).ifPresent(adventureText ->
                context.addObjectSupplier("adventureText", new SingletonSupplier(adventureText))
        );
    }

    private void registerPlayerManager(BreezeContext context) {
        context.injectConstructor(PaperPlayerManager.class).ifPresent(paperPlayerManager ->
                context.addObjectSupplier("paperPlayerManager", new SingletonSupplier(paperPlayerManager))
        );
    }

    private void registerCommandContextFactory(BreezeContext context) {
        context.injectConstructor(PaperCommandContextFactory.class).ifPresent(contextFactory ->
                context.addObjectSupplier("paperCommandContextFactory", new SingletonSupplier(contextFactory))
        );
    }

    private void registerCommandRegistrar(BreezeContext context) {
        context.injectConstructor(PaperCommandRegistrar.class).ifPresent(registrar -> {

            context.addObjectSupplier("paperCommandRegistrar", new SingletonSupplier(registrar));

            registrar
                    .addFactory(CommandLiteralNode.class, new PaperLiteralNodeFactory())
                    .addFactory(CommandArgumentNode.class, new PaperArgumentNodeFactory()
                            .addFactory(String.class, new PaperArgumentNodeFactory.Factory.Str())
                            .addFactory(Boolean.class, new PaperArgumentNodeFactory.Factory.Bool())
                            .addFactory(Integer.class, new PaperArgumentNodeFactory.Factory.Int())
                            .addFactory(Float.class, new PaperArgumentNodeFactory.Factory.Float())
                    )
                    .addFactory(MethodExecuteNode.class, new PaperExecuteNodeFactory(registrar.getContextFactory()));

        });
    }

    private void registerCommandArgumentFactory(BreezeContext context) {
        CommandArgumentFactory argumentFactory = CommandArgumentFactory.defaultFactory();
        context.addObjectSupplier("commandArgumentFactory", new SingletonSupplier(argumentFactory));
        context.injectConstructor(ModuleSuggestionFactory.class).ifPresent(argumentFactory::registerSuggestionFactory);
    }

    private void registerCommandTreeParser(BreezeContext context) {
        context.injectConstructor(CommandTreeParser.class).ifPresent(parser ->
                context.addObjectSupplier("commandTreeParser", new SingletonSupplier(parser))
        );
    }

}
