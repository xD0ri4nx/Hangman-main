package hangman;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    ButtonPanel() {

        setPreferredSize(new Dimension(1200, 100));
        setOpaque(false);
        setLayout(new GridLayout(3, 9));

        for (char letter : ALPHABET.toCharArray()) {

            JButton button = new JButton(String.valueOf(letter));
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            button.setForeground(Color.WHITE);

            button.addActionListener(new ButtonClickListener());

            add(button);
        }
    }

    public void disableAllButtons() {
        for (Component component : getComponents()) {
            if (component instanceof JButton) {
                component.setEnabled(false);
            }
        }
    }

    public void enableAllButtons() {
        for (Component component : getComponents()) {
            if (component instanceof JButton) {
                component.setEnabled(true);
                component.setBackground(Color.BLACK);
            }
        }
    }

    public void updateButtonPanel() {
        if(Game.getInstance().isLost() || Game.getInstance().isWon()){
            disableAllButtons();
        }
    }
}