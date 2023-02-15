package server.commands;

import server.Server;

public interface CommandHandler {
    void execute(Server server, Server.PlayerHandler playerHandler);
}
