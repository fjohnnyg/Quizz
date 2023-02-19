package server;

import server.messages.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private static final int MAX_NUM_OF_PLAYERS = 2;
    private ServerSocket serverSocket;
    private ExecutorService service;
    private List<PlayerHandler> players;
    private Question questions;
    private boolean asTheme;
    private boolean isGameStarted;
    private boolean isGameEnded;
    int numOfQuestions;
    int playersInput = 0;
    private int valideName = 0;
    private int nrOfAnswers;
    private boolean nextQuestion;
    public Server() {
        this.players = new CopyOnWriteArrayList<>();
        this.asTheme = false;
        this.isGameEnded = false;
        this.questions = new Question();
    }

    public void start(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.service = Executors.newCachedThreadPool();
        int numberOfPlayers = 0;
        System.out.printf(Messages.SERVER_STARTED, port);

        while (numberOfPlayers < MAX_NUM_OF_PLAYERS) {
            acceptConnection(numberOfPlayers);
            ++numberOfPlayers;
        }

        checkIfGameCanStart();

        while(!checkIfGameCanStart()){
            checkIfGameCanStart();
        }

        startGame();
    }

    public void acceptConnection(int numberOfConnections) throws IOException {
        Socket clientSocket = serverSocket.accept();
        PlayerHandler playerHandler =
                new PlayerHandler(clientSocket,
                        Messages.DEFAULT_NAME + numberOfConnections);
        service.submit(playerHandler);
        addPlayer(playerHandler);
    }
    private void addPlayer(PlayerHandler playerHandler) {
        players.add(playerHandler);
        //playerHandler.send(Messages.WELCOME.formatted(playerHandler.getName()));
        playerHandler.send(Messages.GAME_INSTRUCTIONS);
        broadcast(playerHandler.getName(), Messages.PLAYER_ENTERED_GAME);
    }

    public boolean isAcceptingPlayers() {
        return players.size() < MAX_NUM_OF_PLAYERS && !isGameStarted;
    }

    public boolean checkIfGameCanStart() {
        //return !players.get(0).getName().equals("PLAYER 0") && !players.get(1).getName().equals("PLAYER 1");
        System.out.println(valideName);
        return valideName == 2;
    }

    /**
     * the game begins after all players define their name
     * choose the theme and send the questions to the players
     */
    public synchronized void startGame() {

        //isGameStarted = true;
        System.out.println("começou o jogo");
        themeChooser();

        while(numOfQuestions < 10){

            sendQuestionToPlayers();
            waitForAnswer();
        }
        isGameEnded = true;

        //sendPlayersResults();
        //gameOver();

        /*while (numOfQuestions < 10) {
            broadCast(sendQuestion());

            //WAIT FOR PLAYERS VALID ANSWERS
            //waitForPlayersInput();

            while (playersInput != MAX_NUM_OF_PLAYERS) {

                synchronized (this){
                    for (PlayerHandler player : players) {
                        String playerAnswer = getPlayerAnswer(player);
                        dealWithAnswer(player, playerAnswer);
                    }
                }


            }
            playersInput = 0;

            System.out.println("saiu");

            //WHEN BOTH HAVE PLAYED VERIFY ANSWERS

            numOfQuestions++;
        }*/
    }

    public void sendQuestionToPlayers(){
        broadCast(sendQuestion());
        numOfQuestions++;
    }

    public synchronized void waitForAnswer() {
        while (!nextQuestion) {
            try {
                System.out.println("Waiting for both players!");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
        System.out.println("next question");
    }


    //private String getPlayerAnswer(PlayerHandler playerHandler, String regex, String invalidMessage){
    private synchronized String getPlayerAnswer(PlayerHandler playerHandler){
        String answer;
        String optionsRegex = "[abc]";
        answer = playerHandler.getPlayerInput();
        //answer = getMessageFromBuffer(playerHandler);
        while (!validateAnswer(answer, optionsRegex) && answer != null) {
            //playerHandler.send(playerHandler.getName() + Messages.INVALID_ANSWER);
            playerHandler.send(Messages.INVALID_ANSWER);
            answer = playerHandler.getPlayerInput();
        }

        return answer;
    }

    private String getMessageFromBuffer(PlayerHandler playerHandler){
        String answer = playerHandler.getPlayerInput();
        return answer != null ? answer.toLowerCase() : null;
    }

    private boolean validateAnswer(String playerAnswer, String regex) {
        if(playerAnswer == null){ //occurs when suddenly a player closes client
            return false;
        }
        if (playerAnswer.length() != 1) {
            return false;
        }
        return playerAnswer.toLowerCase().matches(regex);
    }

    private void dealWithAnswer(PlayerHandler playerHandler, String message) {
        if (verifyAnswer(message))
            playerHandler.send("Your answer is correct!");
        if (!verifyAnswer(message))
            playerHandler.send("Wrong answer. Correct answer is " + questions.getCorrectAnswer());
    }

    private boolean verifyAnswer(String message) {
        String correctAnswer = questions.getCorrectAnswer();
        return correctAnswer.equalsIgnoreCase(message);
    }

    public void themeChooser() {
        int rand = (int) (Math.random() * (4 - 1) +1);
        try {
            switch (rand) {
                case 1 -> {questions.createListOfQuestion("SPORTS"); broadCast(Messages.SPORTS);}
                case 2 -> {questions.createListOfQuestion("GEOGRAPHY");broadCast(Messages.GEOGRAPHY);}
                case 3 -> {questions.createListOfQuestion("ART"); broadCast(Messages.ART);}
                case 4 -> {questions.createListOfQuestion("ALL THEMES"); broadCast(Messages.ALL_THEMES);}
                default -> throw new IllegalStateException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendQuestion() {
        return questions.getQuestion();
    }


    public synchronized void broadCast(String message) {
        players.stream()
                .filter(p -> !p.hasLeft)
                .forEach(player -> player.send(message));
    }

    public synchronized void broadcast(String name, String message) {
        players.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public String listPlayers() {
        StringBuffer buffer = new StringBuffer();
        players.forEach(player -> buffer.append(player.getName()).append("\n"));
        return buffer.toString();
    }

    public void removePlayer(PlayerHandler playerHandler) {
        players.remove(playerHandler);
    }

    public Optional<PlayerHandler> getPlayerByName(String name) {
        return players.stream()
                .filter(playerHandler -> playerHandler.getName().equals(name))
                .findFirst();
    }

    public void areStillPlayersPlaying() {
        if (players == null) {
            endGame();
        }
    }

    public void endGame() {
        broadCast(Messages.GAME_OVER);
        players.stream()
                .filter(p -> !p.hasLeft)
                .forEach(PlayerHandler::quit);
        isGameEnded = true;
    }

    public boolean isAsTheme() {
        return asTheme;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public Question getQuestions() {
        return questions;
    }

    public synchronized void playerWaits() {
        while (valideName < 2) {
            try {
                System.out.println("Waiting for players valid names!");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
        System.out.println("players can star playing!");
    }


    public class PlayerHandler implements Runnable {

        private String name;
        private Socket playerSocket;
        private BufferedWriter out;
        private String message;
        private boolean hasLeft;
        private BufferedReader in;

        public PlayerHandler(Socket playerSocket, String name) {
            this.playerSocket = playerSocket;
            this.name = name;
            try {
                this.out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
                this.in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            } catch (IOException e) {
                quit();
            }
        }

        @Override
        public void run() {

            System.out.println("criou jogador");
            //addPlayer(this);

            //ASK PLAYER TO INPUT A NAME
            send(Messages.ASK_NAME);
            name = getPlayerInput();
            while (!name.matches("[a-zA-Z]+")){
                send(Messages.INVALID_NAME);
                name = getPlayerInput();
            }
            valideName++;
            send(String.format(Messages.WELCOME, name));

            playerWaits();

            //ASK PLAYER ANSWERS

            //if(isGameStarted){
                //startGame();
                while (!isGameEnded){
                    //startGame(players);
                    String playerAnswer = getPlayerAnswer(this);
                    dealWithAnswer(this, playerAnswer);
                    System.out.println("respondeu");
                    nrOfAnswers++;

                    System.out.println(nrOfAnswers);
                    if(nrOfAnswers == 2){
                        System.out.println("responderam os 2");
                        nextQuestion = true;
                    }
                }
            //}

            //runGame();

            while (!isGameEnded) {
                if (Thread.interrupted()) {
                    return;
                }
            }
            quit();
        }

        /**
         * method use to read what the players input in terminal
         * @return
         */

        public String getPlayerInput() {
            String message = null;
            try {
                message = this.in.readLine();
            } catch (IOException | NullPointerException e) {
                quit();
            } finally {
                if (message == null) {
                    quit();
                }
            }
            return message;
        }
/*
        private boolean isTheme(String message) {
            return message.equals("1") ||
                    message.equals("2") ||
                    message.equals("3");
        }

        private void dealWithTheme(String message) {
            themeChooser(message);
            this.send(sendQuestion());
        }

        private boolean isAnswer(String message) {
            return (message.equalsIgnoreCase("a") ||
                    message.equalsIgnoreCase("b") ||
                    message.equalsIgnoreCase("c") ||
                    message.equalsIgnoreCase("d"));
        }

        private void dealWithAnswer(String message) {
            if (verifyAnswer(message))
                this.send("Your answer is correct!");
            if (!verifyAnswer(message))
                this.send("Wrong answer. Correct answer is " + questions.getCorrectAnswer());
        }

        private boolean isCommand(String message) {
            return message.startsWith("/");
        }

        private void dealWithCommand(String message) throws IOException {
            String description = message.split(" ")[0];
            Command command = Command.getCommandFromDescription(description);

            if (command == null) {
                out.write(Messages.NO_SUCH_COMMAND);
                out.newLine();
                out.flush();
                return;
            }

            command.getHandler().execute(Server.this, this);
        }*/

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
                broadCast(Messages.PLAYER_LEFT_GAME);//Player x left the game
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }
    }

}
