package server.commands;

import server.Server;
import server.messages.Messages;

import java.util.Optional;

public class WhisperHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
        String message = playerHandler.getMessage();

        if (message.split(" ").length < 3) {
            playerHandler.send(Messages.WHISPER_INSTRUCTIONS);
            return;
        }

        Optional<Server.PlayerHandler> receiverPlayer = server.getPlayerByName(message.split(" ")[1]);

        if (receiverPlayer.isEmpty()) {
            playerHandler.send(Messages.NO_SUCH_CLIENT);
            return;
        }

        String messageToSend = message.substring(message.indexOf(" ") + 1).substring(message.indexOf(" ") + 1);
        receiverPlayer.get().send(playerHandler.getName() + Messages.WHISPER + ": " + messageToSend);
    }
}
