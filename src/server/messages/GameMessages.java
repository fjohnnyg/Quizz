package server.messages;

/**
 * This class is final - can't extend other class or be extended.
 * The variables are final and static because:
 * a) they won't change (final);
 * b) they could be accessed through a class without creating objects (static).
 * @author Lina Balciunate
 */
public final class GameMessages {

    public static final String WELCOME = """
            Welcome to the quiz!
            You get a question from the topics you choose and possible answers.
            Choose the right answer pressing the key A, B or C.
            For the right answer you'll get 1 point. The winner is who has the most points in the end of the game.
            This quiz has X questions.
            Choose your topic:
            /geography -> gets the questions randomly from GEOGRAPHY.
            /arts -> gets the questions randomly from ARTS.
            /sports -> gets the questions randomly from SPORTS.
            /quit -> exits the server.""";

    public static final String NO_SUCH_COMMAND = "\u001B[31m" + "Invalid command!" + "\u001B[0m";

    public static final String WRONG_ANSWER = "\u001B[31m" + "WRONG. " + "\u001B[0m" + "The right answer is: X";

    public static final String RIGHT_ANSWER = "\u001B[32m" + "RIGHT. " + "\u001B[0m" + "You won 1 point for this question!";

    public static final String WARNING = "If you leave a game now you'll loose all your points!";

    public static final String GAME_OVER = "\u001B[34m" + """
         
 +-+-+-+-+ +-+-+-+-+
 |G|a|m|e| |o|v|e|r|
 +-+-+-+-+ +-+-+-+-+""" + "\u001B[0m";

    // For testing purposes, delete it after
    public static void main(String[] args) {
        System.out.println(GameMessages.WRONG_ANSWER);
        System.out.println(GameMessages.RIGHT_ANSWER);
        System.out.println(GameMessages.GAME_OVER);
        System.out.println(GameMessages.WELCOME);
        System.out.println(GameMessages.NO_SUCH_COMMAND);
        System.out.println(GameMessages.WARNING);

    }
}
