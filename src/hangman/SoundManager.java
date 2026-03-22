package hangman;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, String> soundPaths;
    private Clip loopingClip;

    private SoundManager() {
        soundPaths = new HashMap<>();
        soundPaths.put("click", "/audio/click.wav");
        soundPaths.put("correct", "/audio/correct.wav");
        soundPaths.put("wrong", "/audio/wrong.wav");
        soundPaths.put("win", "/audio/win.wav");
        soundPaths.put("lose", "/audio/lose.wav");
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void playSoundFile(String path) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            // Silently ignore sound errors - game should continue without audio
        }
    }

    public void playClick() {
        playSoundFile(soundPaths.get("click"));
    }

    public void playCorrect() {
        playSoundFile(soundPaths.get("correct"));
    }

    public void playWrong() {
        playSoundFile(soundPaths.get("wrong"));
    }

    public void playWin() {
        playSoundLoop(soundPaths.get("win"));
    }

    public void playLose() {
        playSoundLoop(soundPaths.get("lose"));
    }

    public void stopLooping() {
        if (loopingClip != null) {
            loopingClip.stop();
            loopingClip.close();
            loopingClip = null;
        }
    }

    private void playSoundLoop(String path) {
        try {
            if (loopingClip != null && loopingClip.isRunning()) {
                loopingClip.stop();
                loopingClip.close();
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(path));
            loopingClip = AudioSystem.getClip();
            loopingClip.open(audioIn);
            loopingClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            // Silently ignore sound errors - game should continue without audio
        }
    }
}
