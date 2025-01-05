package Project.controller_classes;

import java.net.Socket;
import Project.auxiliary_classes.PlaySound;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;

public class ServerConnectingController {

    public static Socket connectToServer(String serverIP, int port) throws Exception{
        Socket s = new Socket(serverIP, port);
        return s;
    }

    public void exitApplication(){
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            Platform.exit();
        });
        pause.play();
    }
}
