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
    private boolean hasTheme;
    private boolean isGameStarted;
    private boolean isGameEnded;
    int numOfQuestions;
    public Server() {
        this.players = new CopyOnWriteArrayList<>();
        this.hasTheme = false;
        this.isGameEnded = false;
        this.questions = new Question();
    }

    public void start(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.service = Executors.newCachedThreadPool();
        int numberOfPlayers = 0;
        System.out.printf(Messages.SERVER_STARTED, port);

        while (numberOfPlayers <= MAX_NUM_OF_PLAYERS) {
            acceptConnection(numberOfPlayers);
            numberOfPlayers++;
        }
        while(!checkIfGameCanStart()){
            System.out.println("qq cosia");
            checkIfGameCanStart();
        }
        startGame();
//        while (!isGameEnded) {
//            if (checkIfGameCanStart() && !isGameStarted) {
//
//            }
 //       }
    }
//    @Override
//    public void run() {
//
//            System.out.println("Not");
//        }

    public void acceptConnection(int numberOfConnections) throws IOException {
        Socket clientSocket = serverSocket.accept();
        PlayerHandler playerHandler =
                new PlayerHandler(clientSocket,
                        Messages.DEFAULT_NAME + numberOfConnections);
        service.submit(playerHandler);
    }
    private void addPlayer(PlayerHandler playerHandler) {
        players.add(playerHandler);
        //playerHandler.send(Messages.WELCOME.formatted(playerHandler.getName()));
        playerHandler.send(Messages.COMMANDS_LIST);
        broadcast(playerHandler.getName(), Messages.CLIENT_ENTERED_CHAT);
    }

    public boolean checkIfGameCanStart() {
        return  !isAcceptingPlayers()/* &&
                players.stream().filter(p -> !p.hasLeft)
                .noneMatch(playerHandler -> playerHandler.getName() == "")*/;
    }

    public boolean isAcceptingPlayers() {
        return players.size() == MAX_NUM_OF_PLAYERS && !isGameStarted;
    }
    public void startGame() {
        checkIfGameCanStart();
        themeChooser();
        while (numOfQuestions < 10) {
            broadCast(sendQuestion());
            numOfQuestions++;
        }
    }

    public void themeChooser() {
        int rand = (int) (Math.random() * (4 - 1) +1);
        try {
            switch (rand) {
                case 1 -> questions.createListOfQuestion("SPORTS");
                case 2 -> questions.createListOfQuestion("GEOGRAPHY");
                case 3 -> questions.createListOfQuestion("ART");
                case 4 -> questions.createListOfQuestion("ALL THEMES");
                default -> throw new IllegalStateException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendQuestion() {
        return questions.getQuestion();
    }


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
        broadCast(Messages.NO_MESSAGE_YET);
        players.stream()
                .filter(p -> !p.hasLeft)
                .forEach(PlayerHandler::quit);
        isGameEnded = true;
    }

    public boolean isHasTheme() {
        return hasTheme;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public Question getQuestions() {
        return questions;
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

            addPlayer(this);

            send(Messages.ASK_NAME);
            name = getAnswer();
            while (!name.matches("[a-zA-Z]+")){
                send(Messages.ASK_NAME);
                name = getAnswer();
            }

            broadCast(String.format(Messages.WELCOME, name));
            send("Waiting players");
            while (!isGameEnded) {
                if (Thread.interrupted()) {
                    return;
                }
            }
            quit();

        }

        public String getAnswer() {
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
                broadCast(Messages.NO_MESSAGE_YET);//Player x left the game
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