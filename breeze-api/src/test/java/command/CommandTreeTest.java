package command;

import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandTreeParser;
import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CommandTreeTest {

    public static class TestCommand implements Command {

        @Override
        public String getName() {
            return "players";
        }

        @SubCommand()
        private void root() {
            System.out.println("this is main root");
        }

        @SubCommand(path = "online")
        private void online() {
            System.out.println("this is online root");
        }

        @SubCommand(path = "invite")
        private void invite() {
            System.out.println("this is invite root");
        }

        @SubCommand(path = "invite <name>")
        private void inviteExact(
                @Argument(name = "name") String playerName
        ) {
            System.out.println("this is invite exact root");
        }

        @SubCommand(path = "kick")
        private void kick() {
            System.out.println("this is kick root");
        }

        @SubCommand(path = "kick all")
        private void kickAll() {
            System.out.println("this is kick all root");
        }

        @SubCommand(path = "kick <name>")
        private void kickExact(
                @Argument(name = "name") String playerName
        ) {
            System.out.println("this is kick exact root");
        }

        @SubCommand(path = "ban <name> <period> <ip>")
        private void banExact(
                @Argument(name = "name") String playerName,
                @Argument(name = "period") int period,
                @Argument(name = "ip") boolean byIp
        ) {
            System.out.println("this is ban exact root");
        }

    }

    static CommandTreeParser parser;

    @BeforeAll
    static void setup() {
        parser = new CommandTreeParser();
    }

    @Test
    void shouldCreateCommandTree() {
        CommandRootNode rootNode = parser.parse(new TestCommand());
    }

}
