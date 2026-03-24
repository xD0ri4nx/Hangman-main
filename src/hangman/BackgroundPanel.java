package hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BackgroundPanel extends JPanel {
    private BufferedImage blurredImage;
    private BufferedImage scaledImage;
    private int lastWidth = -1;
    private int lastHeight = -1;
    private static final String BACKGROUND_PATH = "./src/hangman/images/background.png";
    
    private static final Color FROSTED_OVERLAY = new Color(200, 200, 220, 60);
    private static final Color VIGNETTE_COLOR = new Color(0, 0, 0, 80);

    public BackgroundPanel() {
        setOpaque(false);
        loadAndBlurImage();
    }

    private void loadAndBlurImage() {
        try {
            BufferedImage original = javax.imageio.ImageIO.read(new File(BACKGROUND_PATH));
            blurredImage = fastBlur(original, 3);
        } catch (IOException e) {
            System.out.println("ERROR! Could not load background image");
        }
    }

    private BufferedImage fastBlur(BufferedImage image, int radius) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);
        int[] blurred = new int[pixels.length];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = 0, g = 0, b = 0, count = 0;
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int px = Math.min(Math.max(x + kx, 0), w - 1);
                        int py = Math.min(Math.max(y + ky, 0), h - 1);
                        int pixel = pixels[py * w + px];
                        r += (pixel >> 16) & 0xff;
                        g += (pixel >> 8) & 0xff;
                        b += pixel & 0xff;
                        count++;
                    }
                }
                blurred[y * w + x] = (0xff << 24) | ((r / count) << 16) | ((g / count) << 8) | (b / count);
            }
        }

        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, w, h, blurred, 0, w);
        return result;
    }

    private void scaleAndRender() {
        if (blurredImage == null) return;

        int currentWidth = getWidth();
        int currentHeight = getHeight();

        if (currentWidth <= 0 || currentHeight <= 0) return;
        if (currentWidth == lastWidth && currentHeight == lastHeight) return;

        lastWidth = currentWidth;
        lastHeight = currentHeight;

        double widthScale = (double) currentWidth / blurredImage.getWidth();
        double heightScale = (double) currentHeight / blurredImage.getHeight();
        double scale = Math.max(widthScale, heightScale);

        int newWidth = (int) (blurredImage.getWidth() * scale);
        int newHeight = (int) (blurredImage.getHeight() * scale);
        int x = (currentWidth - newWidth) / 2;
        int y = (currentHeight - newHeight) / 2;

        scaledImage = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Image scaled = blurredImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
        g2d.drawImage(scaled, x, y, null);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
        g2d.setColor(FROSTED_OVERLAY);
        g2d.fillRect(0, 0, currentWidth, currentHeight);

        GradientPaint vignette = new GradientPaint(
            currentWidth / 2, 0, new Color(0, 0, 0, 0),
            currentWidth / 2, currentHeight, new Color(0, 0, 0, 100)
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, currentWidth, currentHeight);

        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        scaleAndRender();
        if (scaledImage != null) {
            g.drawImage(scaledImage, 0, 0, this);
        }
    }
}
