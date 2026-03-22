package hangman;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VideoPlayer extends JPanel {
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private Timer checkTimer;
    private static final String VIDEO_PATH = "./src/video/videoplayback.mp4";
    private Runnable onFinished;
    private Canvas canvas;

    static {
        String[] possiblePaths = {
            "D:\\VLC",
            "C:\\Program Files\\VideoLAN\\VLC",
            "C:\\Program Files (x86)\\VideoLAN\\VLC",
            System.getProperty("user.home") + "\\AppData\\Local\\Programs\\VLC"
        };
        
        for (String path : possiblePaths) {
            File vlcDir = new File(path);
            if (vlcDir.exists() && new File(path, "libvlc.dll").exists()) {
                System.setProperty("jna.library.path", path);
                System.out.println("Found VLC at: " + path);
                break;
            }
        }
    }

    public VideoPlayer() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        add(canvas, BorderLayout.CENTER);

        factory = new MediaPlayerFactory();
        mediaPlayer = factory.newEmbeddedMediaPlayer();
        mediaPlayer.setVideoSurface(factory.newVideoSurface(canvas));
    }

    public void play(Runnable onFinished) {
        this.onFinished = onFinished;
        File videoFile = new File(VIDEO_PATH);
        mediaPlayer.playMedia(videoFile.getAbsolutePath());
        
        checkTimer = new Timer();
        checkTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    SwingUtilities.invokeLater(() -> {
                        if (onFinished != null) {
                            onFinished.run();
                        }
                    });
                    checkTimer.cancel();
                }
            }
        }, 500, 500);
    }

    public void stop() {
        if (checkTimer != null) {
            checkTimer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (factory != null) {
            factory.release();
        }
    }
}
