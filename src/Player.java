import server.drawing.Drawing;
import server.messages.Messages;

import java.io.*;
import java.net.Socket;

import static server.drawing.Drawing.ART_SIZE;
import static server.drawing.DrawingEnum.ASCIIArtFont.ART_FONT_SERIF;

public class Player {
    public static void main(String[] args) {
        Player player = new Player();
        try {
            Drawing.printTextArt (Messages.WELCOME, ART_SIZE, ART_FONT_SERIF,"|");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            player.start("localhost", 8082);
        } catch (IOException e) {
            System.out.println("Connection closed...");
        }
    }

    private void start(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        new Thread(new KeyboardHandler(out, socket)).start();
        String line;

        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        socket.close();
    }

    private class KeyboardHandler implements Runnable {
        private BufferedWriter out;
        private Socket socket;
        private BufferedReader in;

        private KeyboardHandler(BufferedWriter out, Socket socket) {
            this.out = out;
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {

            while (!socket.isClosed()) {
                try{
                    String line = in.readLine();

                    out.write(line);
                    out.newLine();
                    out.flush();

                    if (line.equals("/quit")) {
                        socket.close();
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println("Something went wrong with the server. Connection is closing...");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
