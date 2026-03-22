package hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.concurrent.TimeUnit;

public class GameOverOverlay extends JPanel {
    private static GameOverOverlay instance;
    private BufferedImage blurredBackground;
    private boolean isWin;
    private JButton playAgainButton;
    private JPanel card;
    private JLabel titleLabel;
    private JLabel messageLabel;

    private GameOverOverlay() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 0, 0, 0));
        createUI();
    }

    private void createUI() {
        card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient;
                if (isWin) {
                    gradient = new GradientPaint(0, 0, new Color(34, 139, 34, 220), 0, getHeight(), new Color(0, 100, 0, 220));
                } else {
                    gradient = new GradientPaint(0, 0, new Color(139, 0, 0, 220), 0, getHeight(), new Color(80, 0, 0, 220));
                }
                
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setPaint(gradient);
                g2.fill(roundedRectangle);
                
                g2.setStroke(new BasicStroke(2f));
                g2.setPaint(new Color(255, 255, 255, 80));
                g2.draw(roundedRectangle);
                
                g2.dispose();
            }
        };

        card.setPreferredSize(new Dimension(400, 250));
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        messageLabel.setForeground(new Color(220, 220, 220));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 18));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setContentAreaFilled(false);
        playAgainButton.setOpaque(false);
        playAgainButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 2, true),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        playAgainButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playAgainButton.addActionListener(e -> restartGame());
        playAgainButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playAgainButton.setForeground(new Color(200, 255, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                playAgainButton.setForeground(Color.WHITE);
            }
        });

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(25));
        card.add(playAgainButton);
        card.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(card, gbc);
    }

    public static GameOverOverlay getInstance() {
        if (instance == null) {
            instance = new GameOverOverlay();
        }
        return instance;
    }

    public void showOverlay(boolean won) {
        this.isWin = won;
        updateCardContent();
        
        GameFrame frame = GameFrame.getInstance();
        setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        frame.add(this, JLayeredPane.MODAL_LAYER);
        setVisible(true);
        
        new Thread(() -> {
            captureAndBlurBackground();
            SwingUtilities.invokeLater(this::repaint);
        }).start();
    }

    private void updateCardContent() {
        titleLabel.setText(isWin ? "YOU WON!" : "GAME OVER");
        messageLabel.setText(isWin ? "Congratulations! You guessed the word!" : "Better luck next time!");
    }

    public void hideOverlay() {
        setVisible(false);
        GameFrame.getInstance().remove(this);
        blurredBackground = null;
        instance = null;
    }

    private void captureAndBlurBackground() {
        try {
            Robot robot = new Robot();
            Container parent = GameFrame.getInstance();
            Point p = parent.getLocationOnScreen();
            Dimension d = parent.getSize();
            BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(p, d));
            blurredBackground = fastBlur(screenCapture, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage fastBlur(BufferedImage image, int radius) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);
        int[] blurred = new int[pixels.length];
        
        int boxSize = radius * 2 + 1;
        int div = boxSize * boxSize;
        
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (blurredBackground != null) {
            g.drawImage(blurredBackground, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(20, 20, 20));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    private void restartGame() {
        hideOverlay();
        Word.restart();
        Game.restart();
        ImagePanel.getInstance().resetToDefault();
        GameFrame frame = GameFrame.getInstance();
        frame.update();
        frame.getButtonPanel().enableAllButtons();
    }
}
