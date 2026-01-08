package me.bottdev.breezepaper.command.context.resolvers;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezemc.entity.player.BreezeOnlinePlayer;
import me.bottdev.breezepaper.command.context.PaperContextArgumentResolver;
import me.bottdev.breezepaper.components.PaperPlayerManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaperPlayerArgumentResolver implements PaperContextArgumentResolver<BreezeOnlinePlayer> {

    private final PaperPlayerManager paperPlayerManager;

    @Inject
    public PaperPlayerArgumentResolver(PaperPlayerManager paperPlayerManager) {
        this.paperPlayerManager = paperPlayerManager;
    }

    @Override
    public Optional<BreezeOnlinePlayer> resolve(String name, CommandContext<CommandSourceStack> paperContext) {
        PlayerSelectorArgumentResolver resolver = paperContext.getArgument(name, PlayerSelectorArgumentResolver.class);

        try {

            List<Player> players = resolver.resolve(paperContext.getSource());
            if (players.isEmpty()) return Optional.empty();

            List<BreezeOnlinePlayer> breezePlayers = players.stream().map(paperPlayerManager::getPlayerByBukkit).collect(Collectors.toList());
            BreezeOnlinePlayer player = breezePlayers.getFirst();

            return Optional.of(player);

        } catch (Exception ex) {
            return Optional.empty();
        }
    }

}
