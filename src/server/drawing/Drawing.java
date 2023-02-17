package server.drawing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Drawing {
    public static final int ART_SIZE = 12;
    private static final String DEFAULT_ART_SYMBOL = "*";

    private static int getBaselineFor(Graphics graphics, Font font){
        FontMetrics metrics = graphics.getFontMetrics(font);
        int yPosition = metrics.getAscent() - metrics.getDescent();
        return yPosition;
    }

    private static int findImageWidth(int textHeight, String artText, String fontName){
        BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setFont(new Font(fontName, Font.BOLD, textHeight));
        return graphics.getFontMetrics().stringWidth(artText);
    }

    public static void printTextArt(String artText, int textHeight, DrawingEnum.ASCIIArtFont fontType, String artSymbol)
            throws Exception{
        String fontName = fontType.getValue();
        int imageWidth = findImageWidth (textHeight, artText, fontName);

        BufferedImage image = new BufferedImage(imageWidth, textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        Font font = new Font(fontName, Font.BOLD, textHeight);
        graphics.setFont(font);

        Graphics2D newGraphics = (Graphics2D) graphics;
        newGraphics.drawString(artText, 0, getBaselineFor(graphics, font));

        for (int y=0; y < textHeight; y++){
            StringBuilder builder = new StringBuilder();
            for (int x = 0; x < imageWidth; x++)
                builder.append(image.getRGB(x,y) == Color.WHITE.getRGB() ? artSymbol : " ");
            if (builder.toString().trim().isEmpty())
                continue;System.out.println(builder);
        }
    }

}
