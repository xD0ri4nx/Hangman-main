package hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SplashScreen extends JWindow {
    private VideoPlayer videoPlayer;
    private static final String VIDEO_PATH = "./src/video/videoplayback.mp4";

    public SplashScreen() {
        setSize(1200, 800);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.BLACK);

        videoPlayer = new VideoPlayer();
        contentPane.add(videoPlayer, BorderLayout.CENTER);

        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 80));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JLabel titleLabel = new JLabel("HANGMAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Press any key or click to skip");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        overlayPanel.add(Box.createVerticalGlue());
        overlayPanel.add(titleLabel);
        overlayPanel.add(Box.createVerticalStrut(10));
        overlayPanel.add(subtitleLabel);
        overlayPanel.add(Box.createVerticalGlue());

        contentPane.add(overlayPanel, BorderLayout.SOUTH);

        KeyAdapter skipListener = new KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                proceedToGame();
            }
        };
        addKeyListener(skipListener);

        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                proceedToGame();
            }
        };
        addMouseListener(clickListener);
    }

    public void showSplash() {
        setVisible(true);
        videoPlayer.play(this::proceedToGame);
    }

    private void proceedToGame() {
        videoPlayer.stop();
        dispose();
        SwingUtilities.invokeLater(() -> {
            GameFrame.getInstance().setVisible(true);
        });
    }
}
