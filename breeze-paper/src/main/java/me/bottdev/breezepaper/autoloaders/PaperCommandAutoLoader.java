package me.bottdev.breezepaper.autoloaders;

import me.bottdev.breezeapi.autoload.AutoLoader;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandTreeParser;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezepaper.command.PaperCommandRegistrar;

public class PaperCommandAutoLoader implements AutoLoader {

    private final SimpleLogger logger = new SimpleLogger("PaperCommandAutoloader");

    private final CommandTreeParser parser;
    private final PaperCommandRegistrar registrar;

    @Inject
    public PaperCommandAutoLoader(
            CommandTreeParser commandTreeParser,
            PaperCommandRegistrar paperCommandRegistrar
    ) {
        this.parser = commandTreeParser;
        this.registrar = paperCommandRegistrar;
    }

    @Override
    public void load(Object object) {
        if (object instanceof Command command) {

            try {
                CommandRootNode rootNode = parser.parse(command);
                registrar.register(rootNode);

            } catch (IllegalArgumentException ex) {
                logger.error("Failed to parse command {}", ex, command.getName());

            }

        }
    }

}
