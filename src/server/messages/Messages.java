package server.messages;

public final class Messages {
    public static final String SERVER_STARTED = "Server started on port: %s";
    public static final String SERVER_CLOSED = "Server is closed.";
    public static final String DEFAULT_NAME = "PLAYER ";
    public static final String PLAYER_ENTERED_GAME = " entered the game.";
    public static final String PLAYER_LEFT_GAME = "%s left the game.";
    public static final String NO_SUCH_COMMAND = "⚠️" + "\u001B[31m" + "Invalid command!" + "\u001B[0m";
    public static final String GAME_INSTRUCTIONS = """
           You will play against another player.
           You'll get questions of one of these themes:
           SPORTS - ART - GEOGRAPHY - ALL THEMES
           This quiz has 10 questions.
           Choose the right answer pressing the key A, B or C.
           For the right answer you'll get 1 point. The winner is who has the most points in the end of the game.
           Good luck :)
           """;
    public static final String WELCOME = "%s Welcome to Quizz! \n";
    public static final String ASCII_WELCOME = "Welcome to Quizz!";

    public static final String SELF_NAME_CHANGED = "You changed your name to: %s";
    public static final String NAME_CHANGED = "%s changed name to: %s";


    //MESSAGES BEFORE GAME START
    public static final String WAITING_FOR_PLAYERS = "Waiting for another player to start the game.";
    public static final String ASK_NAME = "What's your name?";
    public static final String INVALID_NAME = "\u001B[31m" + "Put a valid name." + "\u001B[0m";
    public static final String INVALID_ANSWER = "\u001B[31m" + "Invalid input." + "\u001B[0m" + "\nChoose answer A, B or C.";
    public static final String START_GAME = "LET'S START THIS GAME, %s";
    public static final String THEME_CHOOSER = "Please choose a theme: \n 1 - Sports \n 2 - Geography \n 3 - Arts";
    public static final String ART = "\u001B[43m" + "The theme you'll play is: ART \u001B[0m \n";
    public static final String SPORTS = "\u001B[43m" +"The theme you'll play is: SPORTS \u001B[0m \n";
    public static final String GEOGRAPHY = "\u001B[43m" + "The theme you'll play is: GEOGRAPHY \u001B[0m \n";
    public static final String ALL_THEMES = "\u001B[43m" + "You'll be playing ALL THEMES \u001B[0m \n";
    public static final String CHOOSE_ANSWER = " choose the answer A, B or C.";


    //MESSAGES DURING THE GAME

    public static final String WRONG_ANSWER = "\u001B[31m" + "WRONG. " + "\u001B[0m" + "The right answer is: ";
    public static final String RIGHT_ANSWER = "\u001B[32m" + "RIGHT ANSWER." + "\u001B[0m" + "\nYour score is:  ";
    public static final String WARNING = "If you leave a game now you'll loose all your points!";
    public static final String FINAL_SCORE = " final score is: ";
    public static final String WINNER = "\u001B[33m" + "██╗    ██╗██╗███╗   ██╗███╗   ██╗███████╗██████╗ \n" +
            "██║    ██║██║████╗  ██║████╗  ██║██╔════╝██╔══██╗\n" +
            "██║ █╗ ██║██║██╔██╗ ██║██╔██╗ ██║█████╗  ██████╔╝\n" +
            "██║███╗██║██║██║╚██╗██║██║╚██╗██║██╔══╝  ██╔══██╗\n" +
            "╚███╔███╔╝██║██║ ╚████║██║ ╚████║███████╗██║  ██║\n" +
            " ╚══╝╚══╝ ╚═╝╚═╝  ╚═══╝╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝\n" +
            "                                                 " + "\u001B[0m";
    public static final String GAME_OVER = "\n \u001B[31m" + ":'######::::::'###::::'##::::'##:'########:::::'#######::'##::::'##:'########:'########::\n" +
            "'##... ##::::'## ##::: ###::'###: ##.....:::::'##.... ##: ##:::: ##: ##.....:: ##.... ##:\n" +
            " ##:::..::::'##:. ##:: ####'####: ##:::::::::: ##:::: ##: ##:::: ##: ##::::::: ##:::: ##:\n" +
            " ##::'####:'##:::. ##: ## ### ##: ######:::::: ##:::: ##: ##:::: ##: ######::: ########::\n" +
            " ##::: ##:: #########: ##. #: ##: ##...::::::: ##:::: ##:. ##:: ##:: ##...:::: ##.. ##:::\n" +
            " ##::: ##:: ##.... ##: ##:.:: ##: ##:::::::::: ##:::: ##::. ## ##::: ##::::::: ##::. ##::\n" +
            ". ######::: ##:::: ##: ##:::: ##: ########::::. #######::::. ###:::: ########: ##:::. ##:\n" +
            ":......::::..:::::..::..:::::..::........::::::.......::::::...:::::........::..:::::..::" + "\u001B[0m";
}