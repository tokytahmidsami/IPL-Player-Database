package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Project.auxiliary_classes.AlertBox;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.RequestID;
import Project.auxiliary_classes.ServerFeedback;
import Project.auxiliary_classes.ServerRequest;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AddPlayerController{
    Socket clientSocket;
    Stage stage;
    User user;
    @FXML Label loginStatusLabel;
    @FXML TextField playerNameInput, height, age, salary, jourseyNo;
    @FXML ComboBox<String> playerCountryInput, playerClubInput, playerPositionInput;
    List<String> countries;
    List<String> args = new ArrayList<>(Collections.nCopies(8, ""));

    public void goAhead(Socket c, Stage s, User u) throws Exception{
        clientSocket = c;
        stage = s;
        user = u;

        loginStatusLabel.setText("Manager, ID : " + user.getUserID());
        ServerFeedback feedback = new ServerFeedback(false, null, null);
        try{
            ServerRequest request = new ServerRequest(user, RequestID.getAllCountriesRequest, null);
            ServerRequest.sendRequest(clientSocket, request);
            feedback = ServerFeedback.receiveFeedback(clientSocket);
        }
        catch(Exception e){
            StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
            return;
        }
        if(feedback.getSuccess())
            countries = feedback.getResults();
        else
            AlertBox.showError("Error fecthing country list.");
        
        playerCountryInput.getItems().addAll(countries);
        playerClubInput.getItems().add(user.getUserID());
        playerPositionInput.getItems().addAll("Batsman", "Bowler", "Allrounder", "Weecketkeeper");
    }

    public void goToMainMenu() throws Exception{
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/mainMenu.fxml"));
            try{
                StageManager.setAndShowStage(stage, loader.load());
            } catch (IOException e1){
                e1.printStackTrace();
            }
            MainMenuController controller = loader.getController();
            controller.goAhead(clientSocket, stage, user);
        });
        pause.play();
    }

    public void addPlayer() throws Exception{
        PlaySound.playClick();
        /*
        * the args will follow this pattern:
        * args[0] = playername
        * args[1] = country
        * args[2] = club
        * args[3] = position
        * args[4] = salary
        * args[5] = player height
        * args[6] = player age
        * args[7] = player jersey number
        */
        String name = playerNameInput.getText();
        if(name.isBlank()){
            AlertBox.showError("You must mention the name.");
            return;
        }
        args.set(0, name);

        String country = playerCountryInput.getValue();
        if(country == null){
            AlertBox.showError("You must choose a country.");
            return;
        }
        args.set(1, country);

        String club = playerClubInput.getValue();
        if(club == null){
            AlertBox.showError("You must select the club.");
            return;
        }
        args.set(2, club);

        String playrHeight = height.getText();
        try{
            double d = Double.parseDouble(playrHeight);
            if(d < 0) throw new Exception(); 
        }
        catch(Exception e){
            AlertBox.showError("The height must be specified and must be non-negative.");
            return;
        }
        args.set(5, playrHeight);

        String playerAge = age.getText();
        try{
            int i = Integer.parseInt(playerAge);
            if(i < 0) throw new Exception();
        }
        catch(Exception e){
            AlertBox.showError("The age must be specified and must be a non-negative integer.");
            return;
        }
        args.add(6, playerAge);

        String position = playerPositionInput.getValue();
        if(position == null){
            AlertBox.showError("You must choose a postition");
            return;
        }
        args.set(3, position);
        
        String sal = salary.getText();
        try{
            int i = Integer.parseInt(sal);
            if(i < 0) throw new Exception();
        }
        catch(Exception e){
            AlertBox.showError("The salary must be specified and must be a non-negative integer.");
            return;
        }
        args.set(4, sal);

        String joursey = jourseyNo.getText();
        if(!joursey.isBlank()){
            try{
                int i = Integer.parseInt(joursey);
                if(i < 0) throw new Exception();
            }
            catch(Exception e){
                AlertBox.showError("The joursey no. must be a non-negative integer.");
                return;
            }
        }
        else joursey = "-1";
        args.set(7, joursey);

        ServerRequest request = new ServerRequest(user, RequestID.addPlayerRequest, args);
        ServerFeedback feedback = new ServerFeedback(false, null, null);
        try{
            ServerRequest.sendRequest(clientSocket, request);
            feedback = ServerFeedback.receiveFeedback(clientSocket);
        }
        catch(Exception e){
            StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
            return;
        }
        if(feedback.getSuccess())
            AlertBox.showInfo("Player added succesfully.");
        else 
            AlertBox.showError("Couldn't add player.");
        return;
    }

}
