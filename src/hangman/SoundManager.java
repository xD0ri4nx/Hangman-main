package hangman;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayerFactory factory;
    private MediaPlayer loopingPlayer;
    private static final String AUDIO_PATH = "./src/hangman/audio/";

    private SoundManager() {
        factory = new MediaPlayerFactory();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void playOnce(String filename) {
        new Thread(() -> {
            try {
                MediaPlayer player = factory.newHeadlessMediaPlayer();
                player.playMedia(AUDIO_PATH + filename);
                
                while (player.isPlaying()) {
                    Thread.sleep(50);
                }
                player.release();
            } catch (Exception e) {
                System.err.println("Error playing sound: " + filename);
                e.printStackTrace();
            }
        }).start();
    }

    private void playLoop(String filename) {
        if (loopingPlayer != null) {
            loopingPlayer.stop();
            loopingPlayer.release();
        }
        loopingPlayer = factory.newHeadlessMediaPlayer();
        loopingPlayer.playMedia(AUDIO_PATH + filename, ":file-caching=300", "--loop");
    }

    public void playClick() {
        playOnce("click.wav");
    }

    public void playCorrect() {
        playOnce("correct.wav");
    }

    public void playWrong() {
        playOnce("wrong.wav");
    }

    public void playWin() {
        playLoop("win.wav");
    }

    public void playLose() {
        playLoop("lose.wav");
    }

    public void stopLooping() {
        if (loopingPlayer != null) {
            loopingPlayer.stop();
            loopingPlayer.release();
            loopingPlayer = null;
        }
    }
}
