package Project.auxiliary_classes;

import java.nio.file.Paths;

import javafx.scene.media.AudioClip;

public class PlaySound {
    private final static String click = "Project/sounds/mouseClick.mp3";
    private static AudioClip clickSound;

    public static void preloadSounds() {
        clickSound = new AudioClip(Paths.get(click).toUri().toString());
    }
    
    public static void playClick() {
        clickSound.play();
    }

}
