package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import Project.auxiliary_classes.Manager;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.RequestID;
import Project.auxiliary_classes.ServerFeedback;
import Project.auxiliary_classes.ServerRequest;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenuController{
    //previous class UserLogInController
    /* next class SearchPlayerController, SearchClubController, 
     * AddPlayerController, TradePlayerController*/
    @FXML Button searchPlayer, searchClub, addPlayer, tradePlayer;
    @FXML Label loginStatusLabel, requiredText1, requiredText2;
    @FXML ImageView lock;
    @FXML Pane managerSection;
    Socket clientSocket;
    Stage stage;
    User user;

    public void goAhead(Socket c, Stage s, User u){
        clientSocket = c;
        stage = s;
        user = u;
        if(user instanceof Manager){
            loginStatusLabel.setText("Manager, ID : " + user.getUserID());
        }
        else{
            loginStatusLabel.setText("User, ID : " + user.getUserID());
            blurManagerSection();
        }
    }

    private void blurManagerSection(){
        GaussianBlur blur = new GaussianBlur(10);
        managerSection.setEffect(blur);
        managerSection.setDisable(true);
        requiredText1.setVisible(true);
        requiredText2.setVisible(true);
        lock.setVisible(true);
    }
    
    public void goToSearchPlayerWindow() throws Exception{
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/searchPlayerWindow.fxml"));
                StageManager.setAndShowStage(stage, loader.load());
                SearchPlayerController controller = loader.getController();
                controller.goAhead(clientSocket, stage, user);
            } 
            catch (Exception e1){
                e1.printStackTrace();
            }
        });
        pause.play();
    }
    
    public void goToSearchClubWindow(){
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/searchClubWindow.fxml"));
                StageManager.setAndShowStage(stage, loader.load());
                SearchClubController controller = loader.getController();
                controller.goAhead(clientSocket, stage, user);
            } 
            catch (Exception e1){
                e1.printStackTrace();
            }
        });
        pause.play();
    }

    public void goToAddPlayerWindow(){
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/addPlayerWindow.fxml"));
                StageManager.setAndShowStage(stage, loader.load());
                AddPlayerController controller = loader.getController();
                controller.goAhead(clientSocket, stage, user);
            }
            catch (Exception e1){
                e1.printStackTrace();
            }
        });
        pause.play();
    }

    public void goToTradePlayerWindow(){
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/tradePlayerWindow.fxml"));
                StageManager.setAndShowStage(stage, loader.load());
                TradePlayerController controller = loader.getController();
                controller.goAhead(clientSocket, stage, user);
            }
            catch (Exception e1){
                e1.printStackTrace();
            }
        });
        pause.play();
    }
    
    public void logout() throws Exception{
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            try{
                do{
                    ServerRequest request = new ServerRequest(user, RequestID.userlogoutRequest, null);
                    try {ServerRequest.sendRequest(clientSocket, request);}
                    catch(Exception e1){StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));}
                } 
                while(ServerFeedback.receiveFeedback(clientSocket).getSuccess() == false);
            } 
            catch (Exception e1){
                e1.printStackTrace();
            }
    
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/loginWindow.fxml"));
            try{
                StageManager.setAndShowStage(stage, loader.load());
            } catch (IOException e1){
                e1.printStackTrace();
            }
            LogInController controller = loader.getController();
            controller.goAhead(clientSocket, stage);
        });
        pause.play();
    }
}
