package server.commands;

import server.Server;
import server.messages.Messages;

public class HelpHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
        playerHandler.send(Messages.GAME_INSTRUCTIONS);
    }
}
