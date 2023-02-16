package server.messages;

public final class GameMessages {

    public static final String WELCOME = "Welcome to quiz!";

    public static final String WRONG_ANSWER = "\u001B[31m" + "WRONG." + "\u001B[0m" + "The right answer is: X";

    // For testing purposes, delete it after
    public static void main(String[] args) {
        System.out.println(GameMessages.WRONG_ANSWER);
    }
}
