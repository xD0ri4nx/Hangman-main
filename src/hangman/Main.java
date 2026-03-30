package hangman;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            ImageCompressor.compressImage(
                "./src/hangman/images/background.png",
                "./src/hangman/images/background.huff"
            );
        } catch (Exception e) {
            System.err.println("Compression failed: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();
        });
    }
}
