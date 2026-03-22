package hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonClickListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {

        JButton clickedButton = (JButton) e.getSource();

        String letter = clickedButton.getText();
        onButtonClick(letter);

        clickedButton.setBackground(Color.DARK_GRAY);
        clickedButton.setEnabled(false);
    }

    private void onButtonClick(String letter) {

        System.out.println("Button clicked: " + letter);
        SoundManager.getInstance().playClick();

        Game.getInstance().guess(letter.charAt(0));
        GameFrame.getInstance().update();
    }
}