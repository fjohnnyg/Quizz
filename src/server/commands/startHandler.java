package server.commands;

import server.Server;
import server.Question;

public class startHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
            //themeChooser();
            playerHandler.send(server.sendQuestion(playerHandler.getTotalOfQuestions()));
    }
}
