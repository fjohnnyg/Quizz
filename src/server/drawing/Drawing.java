package server.drawing;

import server.messages.Messages;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Drawing {
    int width = 150;
    int height = 20;

    public void createLogo(String message, String color) {
        BufferedImage logo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = logo.getGraphics();
        graphics.setFont(new Font("SansSerif", Font.BOLD, 15));

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.drawString(message, 10, 20);

        for (int y = 0; y < height; y++) {
            StringBuilder builder = new StringBuilder();
            for (int x = 0; x < width; x++) {
                //builder.append(logo.getRGB(x,y) == -16777216? " " : "$");
                builder.append(logo.getRGB(x,y) == -16777216? " " : color + "$" + "\u001B[0m");
            }
            if (builder.toString().trim().isEmpty()){
                continue;
            }
            System.out.println(builder);
        }
    }
}

