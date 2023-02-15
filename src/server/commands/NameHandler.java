package server.commands;

import server.Server;
import server.messages.Messages;

public class NameHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
        String message = playerHandler.getMessage();
        String name = message.substring(6);
        String oldName = playerHandler.getName();
        server.getPlayerByName(name).ifPresentOrElse(
                player -> playerHandler.send(Messages.CLIENT_ALREADY_EXISTS),
                () -> {
                    playerHandler.setName(name);
                    playerHandler.send(Messages.SELF_NAME_CHANGED.formatted(name));
                    server.broadcast(name, Messages.NAME_CHANGED.formatted(oldName, name));
                }
        );
    }
}
