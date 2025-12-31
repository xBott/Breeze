package me.bottdev.breezepaper.autoloaders;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.autoload.AutoLoader;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandTreeParser;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezepaper.command.PaperCommandRegistrar;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class PaperCommandAutoloader implements AutoLoader {

    private final SimpleLogger logger = new SimpleLogger("PaperCommandAutoloader");
    private final CommandTreeParser parser = new CommandTreeParser();

    private final JavaPlugin plugin;

    @Override
    public void load(Object object) {
        if (object instanceof Command command) {

            try {

                CommandRootNode rootNode = parser.parse(command);
                PaperCommandRegistrar.register(rootNode, plugin);

            } catch (IllegalArgumentException ex) {
                logger.error("Failed to parse command {}", ex, command.getName());

            }

        }
    }

}
