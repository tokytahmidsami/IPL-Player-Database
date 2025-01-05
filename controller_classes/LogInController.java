package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LogInController{
    @FXML Button asUserLogin;
    @FXML Button asClubManagerLogin;
    Socket clientSocket;
    Stage stage;
    User loggedInUser;

    public void goAhead(Socket c, Stage s){
        clientSocket = c;
        stage = s;
    }

    public void setLoggedInUser(User user){
        loggedInUser = user;
    }

    public User getLoggedInUser(){
        return loggedInUser;
    }

    public void goToUserLoginWindow() throws IOException{
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/userLoginWindow.fxml"));
            try{
                StageManager.setAndShowStage(stage, loader.load());
            } catch (IOException e1){
                e1.printStackTrace();
            }
            UserLogInController controller = loader.getController();
            controller.goAhead(clientSocket, stage);
        });
        pause.play();
    }

    public void goToClubManagerLoginWindow() throws IOException{
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/managerLoginWindow.fxml"));
            try{
                StageManager.setAndShowStage(stage, loader.load());
                ManagerLogInController controller = loader.getController();
                controller.goAhead(clientSocket, stage);
            } catch (Exception e1){
                e1.printStackTrace();
            }
        });
        pause.play();
    }

    //mandatory quit application
    public void quitApplication(){
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            Platform.exit();
        });
        pause.play();
    }
}
