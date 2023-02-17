package server.commands;

import server.Server;
import server.messages.Messages;

public class QuitHandler implements CommandHandler {

    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
        server.removePlayer(playerHandler);
        server.broadcast(playerHandler.getName(), playerHandler.getName() + Messages.CLIENT_DISCONNECTED);
        playerHandler.quit();
    }
}
