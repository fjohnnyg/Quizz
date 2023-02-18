package server;

import server.drawing.Drawing;
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
    private int numberOfPlayers;
    public Server() {
        this.players = new CopyOnWriteArrayList<>();
        this.hasTheme = false;
        this.isGameEnded = false;
        this.questions = new Question();
    }

    public void start(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.service = Executors.newCachedThreadPool();

        System.out.printf(Messages.SERVER_STARTED, port);

        while (players.size() != MAX_NUM_OF_PLAYERS) {
            acceptConnection(this.numberOfPlayers);
            this.numberOfPlayers++;
        }

        checkIfGameCanStart();

        while(!checkIfGameCanStart()){
            checkIfGameCanStart();
        }
        startGame();
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
        players.add(playerHandler);
    }
    private void addPlayer(PlayerHandler playerHandler) {
        //players.add(playerHandler);
        //playerHandler.send(Messages.WELCOME.formatted(playerHandler.getName()));
        //playerHandler.send(Messages.COMMANDS_LIST);
        playerHandler.send(Messages.GAME_INSTRUCTIONS);
        broadcast(playerHandler.getName(), Messages.PLAYER_ENTERED_GAME);
    }

    public boolean checkIfGameCanStart() {
        return !players.get(0).getName().equals("PLAYER 0") && !players.get(1).getName().equals("PLAYER 1");
    }

    public void startGame() {
        themeChooser();
        //while (numOfQuestions < 10) {
            broadCast(sendQuestion());
            //numOfQuestions++;
        //}
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

        /*Drawing welcome = new Drawing();
        welcome.createLogo(Messages.GAME_OVER, "\u001B[36m");*/

        broadCast(Messages.GAME_OVER);

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
            this.name = getValidName();
            while (!name.matches("[a-zA-Z]+")){
                send(Messages.INVALID_NAME);
                this.name = getValidName();
            }

            broadCast(String.format(Messages.WELCOME, name));

            if (checkIfGameCanStart()) {
                send(Messages.START_GAME);
            } else {
                send(Messages.WAITING_FOR_PLAYERS);
            }

            while (!isGameEnded) {
                if (Thread.interrupted()) {
                    return;
                }
            }
            quit();
        }

        public String getValidName() {
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
                //broadCast(Messages.PLAYER_LEFT_GAME);//Player x left the game

                String name = players.get(0).getName();// É PRECISO CORRIGIR
                broadcast(name, Messages.PLAYER_LEFT_GAME);
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