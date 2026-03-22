package hangman;

import static java.lang.Character.toLowerCase;

public class Game implements Guessable, Playable {
    private static Game instance;
    private final String secretWord;
    private String displayedWord;
    private int guesses;
    private final int maxGuesses = 9;

    private Game() {
        Word.getInstance();
        secretWord = Word.getSecretWord();
        displayedWord = Word.getDisplayedWord();
        guesses = 0;
    }

    public static Game getInstance() {
        if(instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public String getDisplayedWord() {
        return displayedWord;
    }

    public int getGuesses() {
        return guesses;
    }

    @Override
    public boolean isLost() {
        return guesses >= maxGuesses;
    }

    @Override
    public boolean isWon() {
        return guesses < maxGuesses && displayedWord.equals(secretWord);
    }

    @Override
    public void guess(char letter) {

        boolean guessed = false;

        StringBuilder newWord = new StringBuilder(displayedWord.length());
        for (int i = 0; i < secretWord.length(); i++) {
            if (toLowerCase(secretWord.charAt(i)) == toLowerCase(letter)) {
                newWord.append(secretWord.charAt(i));
                guessed = true;
            }
            else {
                newWord.append(displayedWord.charAt(i));
            }
        }

        if (guessed) {
            displayedWord = newWord.toString();
            if(isWon()) {
                gameWon();
            }
        } else {
            guesses++;
            if (isLost()) {
                displayedWord = secretWord;
                gameOver();
            }
        }
    }

    @Override
    public void gameOver() {
        GameOverOverlay.getInstance().showOverlay(false);
    }

    @Override
    public void gameWon() {
        GameOverOverlay.getInstance().showOverlay(true);
    }

    public static void restart() {
        instance = null;
        instance = new Game();
    }
}