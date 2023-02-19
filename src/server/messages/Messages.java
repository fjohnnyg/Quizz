package server.messages;

public final class Messages {
    public static final String SERVER_STARTED = "Server started on port: %s";
    public static final String DEFAULT_NAME = "PLAYER ";
    public static final String PLAYER_ENTERED_GAME = " entered the game.";
    public static final String PLAYER_LEFT_GAME = " left the game.";
    public static final String NO_SUCH_COMMAND = "⚠️" + "\u001B[31m" + "Invalid command!" + "\u001B[0m";
    public static final String GAME_INSTRUCTIONS = """
           You will play against another player.
           You'll get to choose the theme of your questions:
           SPORTS - ART - GEOGRAPHY - ALL THEMES
           This quiz has 10 questions.
           Choose the right answer pressing the key A, B or C.
           For the right answer you'll get 1 point. The winner is who has the most points in the end of the game.
           Good luck :)
           """;
    public static final String CLIENT_DISCONNECTED = " left the chat.";
    public static final String WHISPER_INSTRUCTIONS = "Invalid whisper use. Correct use: '/whisper <username> <message>";
    public static final String NO_SUCH_CLIENT = "The client you want to whisper to doesn't exists.";
    public static final String WHISPER = "(whisper)";
    public static final String WELCOME = "Welcome to Quizz! %s";
    public static final String CLIENT_ERROR = "Something went wrong with this client's connection. Error: ";
    public static final String CLIENT_ALREADY_EXISTS = "A client with this name already exists. Please choose another one.";

    public static final String SELF_NAME_CHANGED = "You changed your name to: %s";
    public static final String NAME_CHANGED = "%s changed name to: %s";


    //MESSAGES BEFORE GAME START
    public static final String WAITING_FOR_PLAYERS = "Waiting for another player to start the game.";
    public static final String ASK_NAME = "What's your name?";
    public static final String INVALID_NAME = "\u001B[31m" + "Put a valid name." + "\u001B[0m";
    public static final String INVALID_ANSWER = "\u001B[31m" + "Invalid input." + "\u001B[0m" + "\nChoose answer A, B or C.";
    public static final String START_GAME = "LET'S START THIS GAME!";
    public static final String THEME_CHOOSER = "Please choose a theme: \n 1 - Sports \n 2 - Geography \n 3 - Arts";
    public static final String ART = "The theme you'll play is: ART\n";
    public static final String SPORTS = "The theme you'll play is: SPORTS\n";
    public static final String GEOGRAPHY = "The theme you'll play is: GEOGRAPHY\n";
    public static final String ALL_THEMES = "You'll be playing ALL THEMES\n";
    public static final String CHOOSE_ANSWER = "Choose the answer A, B or C.";


    //MESSAGES DURING THE GAME

    public static final String WRONG_ANSWER = "\u001B[31m" + "WRONG. " + "\u001B[0m" + "The right answer is: X";
    public static final String RIGHT_ANSWER = "\u001B[32m" + "RIGHT. " + "\u001B[0m" + "You won 1 point for this question!";
    public static final String WARNING = "If you leave a game now you'll loose all your points!";
    public static final String GAME_OVER = "\u001B[31m" + ":'######::::::'###::::'##::::'##:'########:::::'#######::'##::::'##:'########:'########::\n" +
            "'##... ##::::'## ##::: ###::'###: ##.....:::::'##.... ##: ##:::: ##: ##.....:: ##.... ##:\n" +
            " ##:::..::::'##:. ##:: ####'####: ##:::::::::: ##:::: ##: ##:::: ##: ##::::::: ##:::: ##:\n" +
            " ##::'####:'##:::. ##: ## ### ##: ######:::::: ##:::: ##: ##:::: ##: ######::: ########::\n" +
            " ##::: ##:: #########: ##. #: ##: ##...::::::: ##:::: ##:. ##:: ##:: ##...:::: ##.. ##:::\n" +
            " ##::: ##:: ##.... ##: ##:.:: ##: ##:::::::::: ##:::: ##::. ## ##::: ##::::::: ##::. ##::\n" +
            ". ######::: ##:::: ##: ##:::: ##: ########::::. #######::::. ###:::: ########: ##:::. ##:\n" +
            ":......::::..:::::..::..:::::..::........::::::.......::::::...:::::........::..:::::..::" + "\u001B[0m";
}