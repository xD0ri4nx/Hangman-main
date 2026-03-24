package hangman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImagePanel extends JPanel {
    private static ImagePanel instance;
    private BufferedImage image;
    private BufferedImage scaledImage;
    private int lastWidth = -1;
    private int lastHeight = -1;
    private int lastImageIndex = -1;
    
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

    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    private ImagePanel() {
        setOpaque(false);
        setDoubleBuffered(true);
        preloadImages();
    }

    private void preloadImages() {
        for (String path : imagePaths) {
            try {
                imageCache.put(path, ImageIO.read(new File(path)));
            } catch (IOException e) {
                System.out.println("ERROR! Could not preload: " + path);
            }
        }
        imageCache.put("./src/hangman/images/win.png", loadImage("./src/hangman/images/win.png"));
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static ImagePanel getInstance() {
        if(instance == null) {
            instance = new ImagePanel();
        }
        return instance;
    }

    public void setImage(String imagePath) {
        BufferedImage newImage = imageCache.get(imagePath);
        if (newImage == null) {
            newImage = loadImage(imagePath);
            if (newImage != null) {
                imageCache.put(imagePath, newImage);
            }
        }
        
        if (newImage != image) {
            image = newImage;
            lastWidth = -1;
            lastHeight = -1;
            repaint();
        }
    }

    private void resizeImage() {
        if (image == null) return;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (panelWidth <= 0 || panelHeight <= 0) return;
        if (panelWidth == lastWidth && panelHeight == lastHeight) return;

        lastWidth = panelWidth;
        lastHeight = panelHeight;

        double widthScale = (double) panelWidth / image.getWidth();
        double heightScale = (double) panelHeight / image.getHeight();
        double scale = Math.min(widthScale, heightScale);

        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);

        scaledImage = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST), 
                      (panelWidth - newWidth) / 2, (panelHeight - newHeight) / 2, null);
        g2d.dispose();
    }

    public void updateImagePanel() {
        if (Game.getInstance().isWon()) {
            setImage("./src/hangman/images/win.png");
        } else {
            int guesses = Game.getInstance().getGuesses();
            if (guesses != lastImageIndex) {
                lastImageIndex = guesses;
                setImage(imagePaths[guesses]);
            }
        }
    }

    public void resetToDefault() {
        lastImageIndex = 0;
        setImage(imagePaths[0]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        resizeImage();
        if (scaledImage != null) {
            g.drawImage(scaledImage, 0, 0, this);
        }
    }
}
