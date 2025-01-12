package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import Project.auxiliary_classes.*;

public class UserLogInController{
    @FXML Button backToLogin, login, signUp;
    @FXML TextField userID, textPassword, newUserID, textNewPassword;
    @FXML PasswordField password, newPassword;
    @FXML ImageView passView, newPassView;
    boolean showPass = false, showNewPass = false;
    Socket clientSocket;
    Stage stage;
    User user;

    //function to come here from prev or next window
    public void goAhead(Socket c, Stage s){
        clientSocket = c;
        stage = s;

        password.textProperty().bindBidirectional(textPassword.textProperty());
        passView.setImage(new Image("Project/images/showpass.png"));
        password.setVisible(true);
        password.setManaged(true);
        textPassword.setVisible(false);
        textPassword.setManaged(false);

        newPassword.textProperty().bindBidirectional(textNewPassword.textProperty());
        newPassView.setImage(new Image("Project/images/showpass.png"));
        newPassword.setVisible(true);
        newPassword.setManaged(true);
        textNewPassword.setVisible(false);
        textNewPassword.setManaged(false);
    }

    //function to process login
    public void processLogin() throws Exception{
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            user = new User(userID.getText(), password.getText());
            if(user.getUserID().isBlank() || user.getPassword().isBlank()){
                Platform.runLater(() -> AlertBox.showError("UserID or Password cannot be empty."));
                return;
            }
            ServerRequest request = new ServerRequest(user, RequestID.userloginRequest, null);
            ServerFeedback feedback = new ServerFeedback(false, null, null);
            try{
                try{
                    ServerRequest.sendRequest(clientSocket, request);
                    feedback = ServerFeedback.receiveFeedback(clientSocket);
                }
                catch (Exception exp){
                    StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
                    return;
                }
                System.out.println("Feedback received");
                if (feedback.getSuccess()){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/mainMenu.fxml"));
                    StageManager.setAndShowStage(stage, loader.load());
                    MainMenuController controller = loader.getController();
                    controller.goAhead(clientSocket, stage, user);
                } 
                else
                    Platform.runLater(() -> AlertBox.showError("Wrong UserID or Password.\n" + //
                                                "Or user already logged in another device."));
                
            } 
            catch (Exception ex){
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    //function to process signup
    public void processSignUp() throws Exception{
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            User user = new User(newUserID.getText(), newPassword.getText());
            if(user.getUserID().isBlank() || user.getPassword().isBlank()){
                Platform.runLater(() -> AlertBox.showError("UserID or Password cannot be empty."));
                return;
            }
            ServerRequest request = new ServerRequest(user, RequestID.usersignUpRequest, null);
            ServerFeedback feedback = new ServerFeedback(false, null, null);
            try{
                try{
                    ServerRequest.sendRequest(clientSocket, request);
                    feedback = ServerFeedback.receiveFeedback(clientSocket);
                }
                catch (Exception exp){
                    StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
                    return;
                }
                System.out.println("Feedback received");
                if (feedback.getSuccess()){
                    Platform.runLater(()->{
                        AlertBox.showInfo("User sign up succesful.");
                    });
                    Stage primaryStage = (Stage) signUp.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/mainMenu.fxml"));
                    StageManager.setAndShowStage(primaryStage, loader.load());
                    MainMenuController controller = loader.getController();
                    controller.goAhead(clientSocket, stage, user);
                } 
                else
                    Platform.runLater(() -> AlertBox.showInfo("User Already Exists."));
                
            } 
            catch (Exception ex){
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    //function to go back to login window
    public void goBack() throws IOException{
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
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

    public void toggleShow(){
        if(!showPass){
            showPass = true;
            passView.setImage(new Image("Project/images/hidepass.png"));
            password.setVisible(false);
            password.setManaged(false);
            textPassword.setVisible(true);
            textPassword.setManaged(true);
        }
        else{
            showPass = false;
            passView.setImage(new Image("Project/images/showpass.png"));
            password.setVisible(true);
            password.setManaged(true);
            textPassword.setVisible(false);
            textPassword.setManaged(false);
        }
    }

    public void toggleNewShow(){
        if(!showNewPass){
            showNewPass = true;
            newPassView.setImage(new Image("Project/images/hidepass.png"));
            newPassword.setVisible(false);
            newPassword.setManaged(false);
            textNewPassword.setVisible(true);
            textNewPassword.setManaged(true);
        }
        else{
            showNewPass = false;
            newPassView.setImage(new Image("Project/images/showpass.png"));
            newPassword.setVisible(true);
            newPassword.setManaged(true);
            textNewPassword.setVisible(false);
            textNewPassword.setManaged(false);
        }
    }
}
