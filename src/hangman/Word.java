package hangman;

import org.json.simple.JSONArray;
import java.util.Random;

public class Word {
    private static Word instance;
    private static String secretWord;
    private static String displayedWord;

    private Word() {
        secretWord = generateRandomWord();
        displayedWord = generateDisplayedWord();
    }

    public static Word getInstance() {
        if(instance == null) {
            instance = new Word();
        }
        return instance;
    }

    public static String getSecretWord() {
        return secretWord;
    }
    public static String getDisplayedWord() { return displayedWord; }

    private static String generateRandomWord() {

        Random random = new Random();

        JSONArray words = JSONFileReader.readJSONFile();

        if (words.size() > 0) {
            int randomIndex = random.nextInt(words.size());
            return words.get(randomIndex).toString();
        }

        return null;
    }

    private static String generateDisplayedWord() {

        return "_".repeat(secretWord.length());
    }

    public static void restart() {
        instance = null;
        instance = new Word();
    }
}