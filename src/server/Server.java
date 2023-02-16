package server;

import server.commands.Command;
import server.messages.Messages;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private ServerSocket serverSocket;
    private ExecutorService service;
    private List<PlayerHandler> players;
    private Question questions;
    private int numberOfQuestions = 10;
    private int totalOfQuestions;
    public Server() {
        players = new CopyOnWriteArrayList<>();
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        service = Executors.newCachedThreadPool();
        int numberOfConnections = 0;
        System.out.printf(Messages.SERVER_STARTED, port);

        while (true) {
            acceptConnection(numberOfConnections);
            numberOfConnections++;
        }
    }

    public void acceptConnection(int numberOfConnections) throws IOException {
        Socket clientSocket = serverSocket.accept();
        PlayerHandler playerHandler =
                new PlayerHandler(clientSocket,
                        Messages.DEFAULT_NAME + numberOfConnections);
        service.submit(playerHandler);
    }
    private void addPlayer(PlayerHandler playerHandler) {
        players.add(playerHandler);
        playerHandler.send(Messages.WELCOME.formatted(playerHandler.getName()));
        playerHandler.send(Messages.COMMANDS_LIST);
        broadcast(playerHandler.getName(), Messages.CLIENT_ENTERED_CHAT);
    }

/*    public void startGame(PlayerHandler playerHandler) {
        int questionNumber = 0;
        int gameSize = 10;
        while (questionNumber < gameSize) {
            //themeChooser();
            playerHandler.send(sendQuestion(questionNumber));
            playerHandler.run();
            questionNumber++;

        }
    }*/

    public String sendQuestion(int totalOfQuestions) {
        questions = new Question();
        String string = questions.getQuestions().toString().split("00")[totalOfQuestions];
        String question = string.split("::")[0];
        String answerA = string.split("::")[1];
        String answerB = string.split("::")[2];
        String answerC = string.split("::")[3];
        String answerD = string.split("::")[4];

        return question + "\n" + answerA + "\n" + answerB + "\n" + answerC + "\n" + answerD;
    }

    //private boolean verifyAnswer(String message) {
       // String correctAnswer = questions.getCorrectAnswer();
  //      return correctAnswer.equals(message);
    //}
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

    public class PlayerHandler implements Runnable {

        private String name;
        private Socket playerSocket;
        private BufferedWriter out;
        private String message;

        public PlayerHandler(Socket playerSocket, String name) throws IOException {
            this.playerSocket = playerSocket;
            this.name = name;
            this.out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
        }

        @Override
        public void run() {
            addPlayer(this);
            /*int questionNumber = 0;
            int gameSize = 10;
            while (questionNumber < gameSize) {*/
                try {
                    Scanner in = new Scanner(playerSocket.getInputStream());
                    while (in.hasNext()) {
                        message = in.nextLine();
                        if (isCommand(message)) {
                            dealWithCommand(message);
                        }
                        if (isAnswer(message)) {
                            //dealWithAnswer(message);
                            //questionNumber++;
                            //this.send(sendQuestion(questionNumber));
                        }
                        /*else {
                            this.send(Messages.NO_SUCH_COMMAND + "\n" + Messages.COMMANDS_LIST);
                        }*/
                        //broadcast(name, message);
                        //this.send(Messages.NO_SUCH_COMMAND + "\n" + Messages.COMMANDS_LIST);
                    }
                } catch (IOException e) {
                    System.err.println(Messages.CLIENT_ERROR + e.getMessage());
                } finally {
                    removePlayer(this);
                }
            //}
        }

        private boolean isAnswer(String message) {
            return (message.equalsIgnoreCase("a") ||
                    message.equalsIgnoreCase("b") ||
                    message.equalsIgnoreCase("c") ||
                    message.equalsIgnoreCase("d"));
        }

/*        private void dealWithAnswer(String message) {
            if (verifyAnswer(message))
                this.send("Your answer is correct!");
            if (!verifyAnswer(message))
//                this.send("Wrong answer. Correct answer is " + questions.getCorrectAnswer());
            totalOfQuestions++;
            this.send(sendQuestion(totalOfQuestions));


        }*/

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

        public  void close() {
            try {
                playerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
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

        public int getNumberOfQuestions() {
            return numberOfQuestions;
        }

        public int getTotalOfQuestions() {
            return totalOfQuestions;
        }

    }

}
