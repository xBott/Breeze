package me.bottdev.breezepaper.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezeapi.command.nodes.CommandExecuteNode;
import me.bottdev.breezepaper.command.senders.PlayerCommandSender;
import me.bottdev.breezepaper.entity.player.BreezeOnlinePlayer;
import me.bottdev.breezepaper.entity.player.PlayerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class PaperCommandContextFactory {

    private final PlayerManager playerManager;

    public Optional<CommandExecutionContext> create(
            CommandExecuteNode node,
            CommandContext<CommandSourceStack> paperContext
    ) {

        Optional<CommandSender> senderOptional = createCommandSender(paperContext);
        if (senderOptional.isEmpty()) {
            return Optional.empty();
        }

        CommandSender sender = senderOptional.get();
        CommandExecutionContext context = new CommandExecutionContext(sender);

        node.getScheme().getResolvers().forEach(resolver -> {

//            if (resolver instanceof ArgumentSchemeResolver argumentResolver) {
//                String name = argumentResolver.getName();
//                Object value = paperContext.getArgument(name, Object.class);
//                context.setArgument(name, value);
//            }

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
