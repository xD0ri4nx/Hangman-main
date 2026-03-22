package hangman;

public interface Playable {
    boolean isWon();
    boolean isLost();
    void gameWon();
    void gameOver();
}