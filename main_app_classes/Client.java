package Project.main_app_classes;

import java.io.IOException;
import java.net.Socket;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.StageManager;
import Project.controller_classes.LogInController;
import Project.controller_classes.ServerConnectingController;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Client extends Application{

    @Override
    public void start(Stage primaryStage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("../fxmls/serverConnectingWindow.fxml"));
        StageManager.setAndShowStage(primaryStage, root);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event ->{
            PlaySound.preloadSounds();
            try{
                Socket clientSocket = ServerConnectingController.connectToServer("127.0.0.1", Server.port);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/loginWindow.fxml"));
                StageManager.setAndShowStage(primaryStage, loader.load());
                LogInController controller = loader.getController();
                controller.goAhead(clientSocket, primaryStage);
            }
            catch(Exception e){//if couldn't connect to server...
                Parent next = root;
                try{
                    next = FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml"));
                } catch (IOException e1){
                    e1.printStackTrace();
                }
                StageManager.setAndShowStage(primaryStage, next);
            } 
        });
        pause.play();
    }

    public static void main(String[] args){
        launch(args);
    }
}