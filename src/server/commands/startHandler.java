package server.commands;

import server.Server;
import server.Question;
import server.messages.Messages;

public class startHandler implements CommandHandler {
    private Server server;
    private Server.PlayerHandler playerHandler;
    @Override
    public void execute(Server server, Server.PlayerHandler playerHandler) {
        this.server = server;
        this.playerHandler = playerHandler;
        String optionsRegex = "[abcd]";
        String message = Messages.THEME_CHOOSER;
        String option;
        if (server.isHasTheme()) {
            optionsRegex = "[abc]";
        }
        server.broadCast(server.sendQuestion());
        option = getPlayerAnswer(message, optionsRegex, playerHandler.getName() + Messages.CHOOSE_ANSWER);//choose a, b or c
    }

    private String getMessageFromBuffer(){
        String answer = playerHandler.getValidName();
        return answer != null ? answer.toLowerCase(): null;
    }

    private String getPlayerAnswer(String messageToSend, String regex, String invalidMessage){
        playerHandler.send(messageToSend);
        String answer;
        answer = getMessageFromBuffer();
        while (!validateAnswer(answer, regex)  &&  answer!=null) {
            playerHandler.send(playerHandler.getName() + invalidMessage);
            answer = getMessageFromBuffer();
        }
        return answer;
    }

    private boolean validateAnswer(String playerAnswer, String regex) {
        if(playerAnswer==null){ //occurs when suddenly a player closes client
            return false;
        }
        if (playerAnswer.length() != 1) {
            return false;
        }
        return playerAnswer.toLowerCase().matches(regex);
    }

    private boolean verifyAnswer(String message) {
        String correctAnswer = server.getQuestions().getCorrectAnswer();
        return correctAnswer.equalsIgnoreCase(message);
    }
}
