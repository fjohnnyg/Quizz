package server;

import server.messages.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int MAX_NUM_OF_PLAYERS = 2;
    private ServerSocket serverSocket;
    private ExecutorService service;
    private List<PlayerHandler> players;
    private Question questions;
    private boolean asTheme;
    private static boolean isGameStarted;
    private boolean isGameEnded;
    private int numOfQuestions;

    public Server() {
        this.players = new ArrayList<>();
        this.asTheme = false;
        this.isGameEnded = false;
        this.questions = new Question();
    }


    /**
     * Starts the server
     * Defines the Thread Pool
     * Define the max number of players and wait for them to join the "game"
     * @param port
     * @throws IOException
     */

    public void start(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.service = Executors.newCachedThreadPool();
        int numberOfPlayers = 0;
        System.out.printf(Messages.SERVER_STARTED, port);

        while (numberOfPlayers < MAX_NUM_OF_PLAYERS) {
            acceptConnection();
            numberOfPlayers++;
        }
    }

    /**
     * Server accept players
     * Creates PlayerHandler objects and gives them a new thread
     * @throws IOException
     */

    public void acceptConnection() throws IOException {
        Socket clientSocket = serverSocket.accept();
        PlayerHandler playerHandler =
                new PlayerHandler(clientSocket);
        service.submit(playerHandler);
    }

    /**
     * Add players to a List
     * @param playerHandler
     */
    private void addPlayer(PlayerHandler playerHandler) {
        players.add(playerHandler);
        playerHandler.send(Messages.GAME_INSTRUCTIONS);
        broadcast(playerHandler.getName(), Messages.PLAYER_ENTERED_GAME);
    }

    public void runGame() {
        if (checkIfGameCanStart() && !isGameStarted) {
            try {
                startGame();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check the conditions to start the game:
     * - number of players;
     * - no game ongoing.
     */
    public boolean checkIfGameCanStart() {
        return  !isAcceptingPlayers() &&
                players.stream()
                        .filter(p -> !p.hasLeft)
                        .noneMatch(playerHandler -> "".equals(playerHandler.getName()));
    }

    public boolean isAcceptingPlayers() {
        return players.size() < MAX_NUM_OF_PLAYERS && !isGameStarted;
    }


    /**
     * Where the game loops and runs until the defined number of questions is answered
     * @throws InterruptedException
     */
    public void startGame() throws InterruptedException {
        isGameStarted = true;
        themeChooser();
        String p1Answer;
        String p2Answer;
        String[] option = new String[MAX_NUM_OF_PLAYERS];
        while (numOfQuestions < 10) {
            String optionsRegex = "[abc]";
            broadCast(sendQuestion());
            for (int i = 0; i <= option.length - 1; i++) {
                option[i] = getPlayerAnswer(
                        players.get(i),
                        optionsRegex);
            }
            p1Answer = option[0];
            p2Answer = option[1];
            dealWithAnswer(p1Answer, p2Answer);
            numOfQuestions++;
        }
        gamePlayersResults();
        endGame();
    }

    public void themeChooser() {
        int rand = (int) (Math.random() * (4 - 1) +1);
        try {
            switch (rand) {
                case 1 -> {questions.createListOfQuestion("SPORTS"); broadCast(Messages.SPORTS);}
                case 2 -> {questions.createListOfQuestion("GEOGRAPHY");broadCast(Messages.GEOGRAPHY);}
                case 3 -> {questions.createListOfQuestion("ART");broadCast(Messages.ART);}
                case 4 -> {questions.createListOfQuestion("ALL THEMES");broadCast(Messages.ALL_THEMES);}
                default -> throw new IllegalStateException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendQuestion() {

        return "\u001B[44m" + (numOfQuestions+1) + ". " + questions.getQuestion() + "\u001B[0m";
    }

    /**
     * Gets the player's input and formats it.
     * @param playerHandler
     * @return
     */
    private String getAnswerFromBuffer(PlayerHandler playerHandler){
        String answer = playerHandler.getInput();
        return answer!=null? answer.toLowerCase(): null;
    }

    /**
     * This block of methods handles the players answers and validates it.
     * Check if they are correct or wrong and gives tem feedback.
     * @param playerHandler
     * @param regex
     * @return
     */
    private String getPlayerAnswer(PlayerHandler playerHandler, String regex){
        String answer;
        answer = getAnswerFromBuffer(playerHandler);
        while (!validateAnswer(answer, regex)  &&  answer!=null) {
            playerHandler.send(Messages.INVALID_ANSWER);
            answer = getAnswerFromBuffer(playerHandler);
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

    private void dealWithAnswer(String p1Answer, String p2Answer) {

        if (verifyAnswer(p1Answer)) {
            players.get(0).setScore();
            players.get(0).send(Messages.RIGHT_ANSWER + players.get(0).getScore() + "\n");
        } else {
            players.get(0).send(Messages.WRONG_ANSWER + questions.getCorrectAnswerValue() + "\n");
        }

        if (verifyAnswer(p2Answer)) {
            players.get(1).setScore();
            players.get(1).send(Messages.RIGHT_ANSWER + players.get(1).getScore() + "\n");
        } else {
            players.get(1).send(Messages.WRONG_ANSWER + questions.getCorrectAnswerValue() + "\n");
        }
    }

    private boolean verifyAnswer(String answer) {
        String correctAnswer = questions.getCorrectAnswer();
        return correctAnswer.equalsIgnoreCase(answer);
    }

    /**
     * Verify the players' score and defines if there´s a winner or if it's a draw
     * @return
     */
    public String checkWinner(){

        int maxScore = players.stream()
                .mapToInt(player -> player.score)
                .max()
                .getAsInt();

        long checkIfDraw = players.stream()
                .filter(player -> player.getScore() == maxScore)
                .count();

        String winnerPlayerName = players.stream()
                .sorted(Comparator.comparing(PlayerHandler::getScore).reversed())
                .toList()
                .get(0)
                .getName();

        if(checkIfDraw > 1){
            return "It's a draw!";
        }
        return winnerPlayerName;
    }
    public void gamePlayersResults(){

        for (PlayerHandler player: players) {
            broadCast("-".repeat(30) + "\n" + player.getName() + Messages.FINAL_SCORE + player.getScore());
        }

        String winner = checkWinner();
        if(winner != "It's a draw!") {
            broadCast("\n" + Messages.WINNER + "is: " + winner.toUpperCase());
            return;
        }

        broadCast(winner.toUpperCase());
        broadCast(Messages.GAME_OVER);
    }


    /**
     * Server send messages to all players'
     * @param message
     */
    public void broadCast(String message) {
        players.stream()
                .filter(p -> !p.hasLeft)
                .forEach(player -> player.send(message));
    }
    public void broadcast(String name, String message) {
        players.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public void removePlayer(PlayerHandler playerHandler) {
        players.remove(playerHandler);
    }

    public void areStillPlayersPlaying() {
        if (players == null) {
            endGame();
        }
    }

    public void endGame() {
        players.stream()
                .filter(p -> !p.hasLeft)
                .forEach(PlayerHandler::quit);
        isGameEnded = true;
    }

    public class PlayerHandler implements Runnable {
        private String name = "";
        private Socket playerSocket;
        private BufferedWriter out;
        private boolean hasLeft;
        private BufferedReader in;
        private int score = 0;


        public PlayerHandler(Socket playerSocket) {
            this.playerSocket = playerSocket;
            try {
                this.out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
                this.in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            } catch (IOException e) {
                quit();
            }
        }

        /**
         * Ask the players' to input a valid name
         * After this verification starts the game
         * Checks if the game is running otherwise closes players' socket
         */
        @Override
        public void run() {

            addPlayer(this);

            while (players.size() < 2) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    quit();
                }
            }

            send(Messages.ASK_NAME);

            String input = getInput();
            //while (!input.matches("[a-zA-Z]+")) {
            while (!input.matches("[a-zA-Z\\u00C0-\\u00ff]+")) {
                send(Messages.INVALID_NAME);
                input = getInput();
            }
            name = input;

            //send(String.format(Messages.WELCOME, name));
            send(String.format(Messages.START_GAME, name.toUpperCase()));
            runGame();
            while (!isGameEnded) {
                if (Thread.interrupted()) {
                    return;
                }
            }
            quit();
        }

        public String getInput() {
            while (true) {
                String message = null;
                try {
                    message = in.readLine();
                } catch (IOException | NullPointerException e) {
                    quit();
                } finally {
                    if (message == null) {
                        quit();
                    }
                }
                return message;
            }
        }

        public void send(String message) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                removePlayer(this);
                e.printStackTrace();
            }
        }

        public void quit() {
            hasLeft = true;
            try {
                playerSocket.close();
            } catch (IOException e) {
                System.out.println("Couldn't closer player socket");
            } finally {
                areStillPlayersPlaying();
                broadCast(Messages.PLAYER_LEFT_GAME);
            }
        }

        public String getName() {
            return name;
        }

        public void setScore() {
            this.score++;
        }

        public int getScore() {
            return this.score;
        }
    }
}