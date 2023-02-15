package server.commands;

import server.Server;

public class ListHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
        playerHandler.send(server.listPlayers());
    }
}
