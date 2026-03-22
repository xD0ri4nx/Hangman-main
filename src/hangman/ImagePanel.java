package hangman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel
{
    private static ImagePanel instance;
    private BufferedImage image;
    private Image scaledImage;
    private final String[] imagePaths = {
            "./src/hangman/images/default.jpg",
            "./src/hangman/images/mistake1.jpg",
            "./src/hangman/images/mistake2.jpg",
            "./src/hangman/images/mistake3.jpg",
            "./src/hangman/images/mistake4.jpg",
            "./src/hangman/images/mistake5.jpg",
            "./src/hangman/images/mistake6.jpg",
            "./src/hangman/images/mistake7.jpg",
            "./src/hangman/images/mistake8.jpg",
            "./src/hangman/images/lose.jpg" };

    private ImagePanel() {
        setBackground(Color.BLACK);
        try {
            image = ImageIO.read(new File(imagePaths[0]));
        } catch (IOException e) {
            System.out.println("ERROR! COULD NOT OPEN IMAGE");
        }
    }

    public static ImagePanel getInstance() {
        if(instance == null) {
            instance = new ImagePanel();
        }
        return instance;
    }

    public void setImage(String imagePath)
    {
        try {
            image = ImageIO.read(new File(imagePath));
            repaint();
        } catch (IOException e) {
            System.out.println("ERROR! COULD NOT OPEN IMAGE");
        }
    }

    private void resizeImage() {
        if(image != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            double widthScale = (double) panelWidth / image.getWidth(this);
            double heightScale = (double) panelHeight / image.getHeight(this);

            double scale = Math.min(widthScale, heightScale);

            int newWidth = (int) (image.getWidth(this) * scale);
            int newHeight = (int) (image.getHeight(this) * scale);

            scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        }
    }

    public void updateImagePanel() {

        if(Game.getInstance().isWon()) {
            instance.setImage("./src/hangman/images/win.png");
        } else {
            instance.setImage(imagePaths[Game.getInstance().getGuesses()]);
        }
    }

    public void resetToDefault() {
        setImage(imagePaths[0]);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        resizeImage();
        if (scaledImage != null)
        {
            g.drawImage(scaledImage, (getWidth() - scaledImage.getWidth(this)) / 2, (getHeight() - scaledImage.getHeight(this)) / 2, this);
        }
    }
}