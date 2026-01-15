package me.bottdev.breezepaper.stages;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.process.PipelineNodeScope;
import me.bottdev.breezecore.stages.EngineProcessStage;

@RequiredArgsConstructor
public class PaperSupplierRegistrationStage implements EngineProcessStage {

    @Override
    public void apply(PipelineNodeScope scope) {

    }

//    private final BreezePaper breezePaper;
//
//    @Override
//    public String getName() {
//        return "Paper supplier registration";
//    }
//
//    @Override
//    public void process(StagedBreezeEngine engine) {
//        BreezeContext context = engine.getContext();
//
//        registerBreezePaper(context);
//
//        registerAdventureText(context);
//        registerPlayerManager(context);
//
//        registerCommandContextFactory(context);
//        registerCommandRegistrar(context);
//        registerCommandArgumentFactory(context);
//        registerCommandTreeParser(context);
//
//    }
//
//    private void registerBreezePaper(BreezeContext context) {
//        context.addObjectSupplier("breezePaper", new SingletonSupplier(breezePaper));
//    }
//
//    private void registerAdventureText(BreezeContext context) {
//        context.injectConstructor(BreezeAdventureText.class).ifPresent(adventureText ->
//                context.addObjectSupplier("adventureText", new SingletonSupplier(adventureText))
//        );
//    }
//
//    private void registerPlayerManager(BreezeContext context) {
//        context.injectConstructor(PaperPlayerManager.class).ifPresent(paperPlayerManager ->
//                context.addObjectSupplier("paperPlayerManager", new SingletonSupplier(paperPlayerManager))
//        );
//    }
//
//    private void registerCommandContextFactory(BreezeContext context) {
//        context.injectConstructor(PaperCommandContextFactory.class).ifPresent(contextFactory -> {
//            context.addObjectSupplier("paperCommandContextFactory", new SingletonSupplier(contextFactory));
//
//            context.injectConstructor(PaperPlayerArgumentResolver.class).ifPresent(resolver ->
//                    contextFactory.addArgumentResolver(BreezeOnlinePlayer.class, resolver)
//            );
//
//        });
//    }
//
//    private void registerCommandRegistrar(BreezeContext context) {
//        context.injectConstructor(PaperCommandRegistrar.class).ifPresent(registrar -> {
//
//            context.addObjectSupplier("paperCommandRegistrar", new SingletonSupplier(registrar));
//
//            registrar
//                    .addFactory(CommandLiteralNode.class, new PaperLiteralNodeFactory())
//                    .addFactory(CommandArgumentNode.class, new PaperArgumentNodeFactory()
//                            .addFactory(String.class, new PaperArgumentNodeFactory.Factory.Str())
//                            .addFactory(Boolean.class, new PaperArgumentNodeFactory.Factory.Bool())
//                            .addFactory(Integer.class, new PaperArgumentNodeFactory.Factory.Int())
//                            .addFactory(Float.class, new PaperArgumentNodeFactory.Factory.Float())
//                            .addFactory(BreezeOnlinePlayer.class, new PaperArgumentNodeFactory.Factory.Player())
//                    )
//                    .addFactory(MethodExecuteNode.class, new PaperExecuteNodeFactory(registrar.getContextFactory()));
//
//        });
//    }
//
//    private void registerCommandArgumentFactory(BreezeContext context) {
//        CommandArgumentFactory argumentFactory = CommandArgumentFactory.defaultFactory()
//                        .registerArgumentFactory(BreezeOnlinePlayer.class, (name, parameter) ->
//                                new PlayerArgument(name)
//                        );
//        context.addObjectSupplier("commandArgumentFactory", new SingletonSupplier(argumentFactory));
//        context.injectConstructor(ModuleSuggestionFactory.class).ifPresent(argumentFactory::registerSuggestionFactory);
//    }
//
//    private void registerCommandTreeParser(BreezeContext context) {
//        context.injectConstructor(CommandTreeParser.class).ifPresent(parser ->
//                context.addObjectSupplier("commandTreeParser", new SingletonSupplier(parser))
//        );
//    }

}
