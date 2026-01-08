package me.bottdev.breezepaper.command.context;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezeapi.command.nodes.execute.MethodExecuteNode;
import me.bottdev.breezeapi.command.scheme.resolvers.ArgumentSchemeResolver;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezemc.entity.player.BreezeOnlinePlayer;
import me.bottdev.breezepaper.command.senders.PlayerCommandSender;
import me.bottdev.breezepaper.components.PaperPlayerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class PaperCommandContextFactory {

    private final Map<Class<?>, PaperContextArgumentResolver<?>> argumentResolvers = new HashMap<>();

    private final PaperPlayerManager playerManager;

    @Inject
    public PaperCommandContextFactory(PaperPlayerManager paperPlayerManager) {
        this.playerManager = paperPlayerManager;
    }

    public <T> PaperCommandContextFactory addArgumentResolver(Class<T> clazz, PaperContextArgumentResolver<T> argumentResolver) {
        argumentResolvers.put(clazz, argumentResolver);
        return this;
    }

    private Optional<PaperContextArgumentResolver<?>> getArgumentResolver(Class<?> clazz) {
        return Optional.ofNullable(argumentResolvers.get(clazz));
    }

    public Optional<CommandExecutionContext> create(
            MethodExecuteNode node,
            CommandContext<CommandSourceStack> paperContext
    ) {

        Optional<CommandSender> senderOptional = createCommandSender(paperContext);
        if (senderOptional.isEmpty()) {
            return Optional.empty();
        }

        CommandSender sender = senderOptional.get();
        CommandExecutionContext context = new CommandExecutionContext(sender);

        node.getScheme().getResolvers().stream()
                .filter(schemeResolver -> schemeResolver instanceof ArgumentSchemeResolver)
                .map(schemeResolver -> (ArgumentSchemeResolver) schemeResolver)
                .forEach(schemeResolver -> {

                    Class<?> type = schemeResolver.getType();
                    String name = schemeResolver.getName();

                    getArgumentResolver(type)
                            .flatMap(argumentResolver -> argumentResolver.resolve(name, paperContext))
                            .ifPresent(object -> context.setArgument(name, object));

                });

        return Optional.of(context);
    }

    private Optional<CommandSender> createCommandSender(CommandContext<CommandSourceStack> paperContext) {

        Entity executor = paperContext.getSource().getExecutor();

        if (executor instanceof Player bukkitPlayer) {
            BreezeOnlinePlayer player = playerManager.getPlayerByBukkit(bukkitPlayer);
            CommandSender sender = new PlayerCommandSender(player);
            return Optional.of(sender);
        }

        return Optional.empty();
    }

}
