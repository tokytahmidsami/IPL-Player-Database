package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import Project.auxiliary_classes.AlertBox;
import Project.auxiliary_classes.Manager;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.RequestID;
import Project.auxiliary_classes.ServerFeedback;
import Project.auxiliary_classes.ServerRequest;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ManagerLogInController{
    @FXML Button backToLogin, login, signUp;
    @FXML ComboBox<String> managerID;
    @FXML TextField password, newManagerID, newPassword;
    Socket clientSocket;
    Stage stage;
    User manager;
    List<String> managerIDs;

    //function to come here from prev or next window
    public void goAhead(Socket c, Stage s) throws Exception{
        clientSocket = c;
        stage = s;
        ServerFeedback feedback = new ServerFeedback(false, null, null);
        try{
            ServerRequest request = new ServerRequest(null, RequestID.getManagerIDs, null);
            ServerRequest.sendRequest(clientSocket, request);
            feedback = ServerFeedback.receiveFeedback(clientSocket);
        } 
        catch (Exception e){
            StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
            return;
        }
        if(feedback.getSuccess()){
            managerIDs = feedback.getResults();
            System.out.println("managers: " + managerIDs);
            managerID.getItems().addAll(managerIDs);
        }
        else
            AlertBox.showError("Error fetching list. Go back.");
        
    }

    //function to process login
    public void processLogin() throws Exception{
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            if(managerID.getValue() == null || password.getText().isBlank()){
                Platform.runLater(() -> AlertBox.showError("ManagerID or Password cannot be empty."));
                return;
            }
            manager = new Manager(managerID.getValue(), password.getText());
            ServerRequest request = new ServerRequest(manager, RequestID.managerLoginRequest, null);
            ServerFeedback  feedback = new ServerFeedback(false, null, null);
            try{
                try{
                    ServerRequest.sendRequest(clientSocket, request);
                    feedback = ServerFeedback.receiveFeedback(clientSocket);    
                }
                catch (Exception ex){
                    StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
                    return;
                }
                if (feedback.getSuccess()){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/mainMenu.fxml"));
                    StageManager.setAndShowStage(stage, loader.load());
                    MainMenuController controller = loader.getController();
                    controller.goAhead(clientSocket, stage, manager);
                } 
                else
                    Platform.runLater(() -> AlertBox.showError("Wrong managerID or Password.\n" + //
                                                "Or manager already logged in another device."));
                
            } 
            catch (Exception ex){
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    //function to process signup
    private static String formatManagerID(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "";
        }
        String[] words = id.trim().toLowerCase().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
            }
        }
        return String.join(" ", words);
    }

    public void processSignUp() throws Exception{
        PlaySound.playClick();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e ->{
            String ID = formatManagerID(newManagerID.getText());
            manager = new Manager(ID, newPassword.getText());
            if(manager.getUserID().isBlank() || manager.getPassword().isBlank()){
                Platform.runLater(() -> AlertBox.showError("ManagerID or Password cannot be empty."));
                return;
            }
            ServerRequest request = new ServerRequest(manager, RequestID.managerRegisterRequest, null);
            ServerFeedback feedback = new ServerFeedback(false, null, null);
            try{
                try{
                    ServerRequest.sendRequest(clientSocket, request);
                    feedback = ServerFeedback.receiveFeedback(clientSocket);
                }
                catch (Exception ex){
                    StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
                    return;
                }
                System.out.println("Feedback received");
                if (feedback.getSuccess()){
                    Platform.runLater(()->{
                        AlertBox.showInfo("Manager register succesful.");
                    });
                    Stage primaryStage = (Stage) signUp.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/mainMenu.fxml"));
                    StageManager.setAndShowStage(primaryStage, loader.load());
                    MainMenuController controller = loader.getController();
                    controller.goAhead(clientSocket, stage, manager);
                } 
                else
                    Platform.runLater(() -> AlertBox.showInfo("Club Already Exists."));            
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
}
