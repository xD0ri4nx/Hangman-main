package hangman;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private static GameFrame instance;
    ImagePanel imagePanel = ImagePanel.getInstance();
    ButtonPanel buttonPanel = new ButtonPanel();
    JLabel displayedWord = new JLabel(Game.getInstance().getDisplayedWord());

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }

    private JLayeredPane layeredPane;

    GameFrame() {
        setTitle("Hangman");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        setContentPane(layeredPane);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBounds(0, 0, 1200, 800);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.BLACK);

        String iconImagePath = "./src/hangman/images/iconImage.png";
        Image icon = new ImageIcon(iconImagePath).getImage();
        setIconImage(icon);

        imagePanel.setPreferredSize(new Dimension(1200, 550));
        contentPanel.add(imagePanel, BorderLayout.NORTH);

        displayedWord.setHorizontalAlignment(JLabel.CENTER);
        displayedWord.setFont(new java.awt.Font("Arial", Font.PLAIN, 40));
        displayedWord.setForeground(Color.WHITE);
        contentPanel.add(displayedWord, BorderLayout.CENTER);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);

        setVisible(true);
    }

    public static GameFrame getInstance() {
        if(instance == null) {
            instance = new GameFrame();
        }
        return instance;
    }

    public void update() {
        imagePanel.updateImagePanel();
        displayedWord.setText(Game.getInstance().getDisplayedWord());
        buttonPanel.updateButtonPanel();
    }
}