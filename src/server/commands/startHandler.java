package server.commands;

import server.Server;
import server.Question;
import server.messages.Messages;

public class startHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
        playerHandler.send(Messages.THEME_CHOOSER);
    }
}
