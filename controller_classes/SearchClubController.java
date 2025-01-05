package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Project.auxiliary_classes.AlertBox;
import Project.auxiliary_classes.Manager;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.Player;
import Project.auxiliary_classes.RequestID;
import Project.auxiliary_classes.ServerFeedback;
import Project.auxiliary_classes.ServerRequest;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SearchClubController{
    @FXML Label loginStatusLabel, playerName, playerPosition, playerCountry, playerAge, 
                playerClub, playerHeight, playerWeeklySalary, playerJourseyNo;
    @FXML ComboBox<String> playerClubInput, searchCriteria;
    @FXML ScrollPane playerListPane;
    @FXML VBox vbox;
    @FXML ImageView playerImage;
    Socket clientSocket;
    Stage stage;
    User user;
    List<Player> players;
    ServerRequest request;
    List<String> args = new ArrayList<>(Collections.nCopies(11, ""));

    //non button methods.........................................................................

    public void goAhead(Socket c, Stage s, User u) throws Exception{
        clientSocket = c;
        stage = s;
        user = u;

        if(user instanceof Manager)
            loginStatusLabel.setText("Manager, ID : " + user.getUserID());
        else loginStatusLabel.setText("User, ID : " + user.getUserID());

        request = new ServerRequest(user, RequestID.searchPlayerRequest, args);
        ServerFeedback feedback = new ServerFeedback(false, null, null);
        try{
            ServerRequest.sendRequest(clientSocket, request);
            feedback = ServerFeedback.receiveFeedback(clientSocket);
        }
        catch (Exception e){
            StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
            return;
        }
        players = feedback.getPlayers();

        searchCriteria.getItems().add("Any");
        searchCriteria.getItems().add("Max Height");
        searchCriteria.getItems().add("Max Weekly Salary");
        searchCriteria.getItems().add("Max Age");
        
        for(Player p: players){
            if(!playerClubInput.getItems().contains(p.getClub()))
                playerClubInput.getItems().add(p.getClub());
        }

        vbox = new VBox(5);
        playerListPane.setContent(vbox);
    }

    private void showPlayers() throws Exception{
        vbox.getChildren().clear();

        for (Player player : players){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/playerInfoBar.fxml"));
            HBox playerCard = loader.load();
            playerCard.setOnMouseClicked(e ->{
                PlaySound.playClick();
                playerName.setText(player.getName());
                String pos = player.getPosition();
                playerPosition.setText(pos);
                if(pos.equalsIgnoreCase("batsman"))
                    playerImage.setImage(new Image("Project/images/players/batsman.png"));  
                else if(pos.equalsIgnoreCase("bowler"))
                    playerImage.setImage(new Image("Project/images/players/bowler.png"));
                else if(pos.equalsIgnoreCase("allrounder"))
                    playerImage.setImage(new Image("Project/images/players/allrounder.png"));
                else
                    playerImage.setImage(new Image("Project/images/players/wicketkeeper.png"));          
                playerCountry.setText(player.getCountry());
                playerAge.setText(player.getAge() + " years");
                playerClub.setText(player.getClub());
                playerHeight.setText(player.getHeight() + " m");
                playerWeeklySalary.setText(player.getWeeklySalary() + " Rupee");
                playerJourseyNo.setText((player.getNumber() == -1)? "N/A" : player.getNumber() + "");
            });
            PlayerInfoBarController controller = loader.getController();
            controller.setPlayer(player);
            vbox.getChildren().add(playerCard);
        }
    }

    //button methods.............................................................................

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

    public void processFilter() throws Exception{
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        String club;
        if(playerClubInput.getValue() != null){
            club = playerClubInput.getValue();
            pause.setOnFinished((e)->{
                /*
                * the args will follow this pattern:
                * args[2] = club
                * args[10] = searchCriteria
                */
                args.set(2, club);
                args.set(10, searchCriteria.getValue() == null? "Any" : searchCriteria.getValue());
                request = new ServerRequest(user, RequestID.searchClubRequest, args);
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
                    if(feedback.getSuccess()){
                        players = feedback.getPlayers();
                        showPlayers();
                    } 
                    else
                        Platform.runLater(() -> AlertBox.showError("Error in fetching data."));
                } 
                catch (Exception ex){
                    ex.printStackTrace();
                }
            });
            pause.play();
        }
        else
            Platform.runLater(() -> AlertBox.showError("Please select a club."));
    }

    public void showTotalYearlySalary() throws Exception{
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        String club;
        if(playerClubInput.getValue() != null){
            club = playerClubInput.getValue();
            pause.setOnFinished((e)->{
                /*
                * the args will follow this pattern:
                * args[2] = club
                */
                args.set(2, club);
                request = new ServerRequest(user, RequestID.clubYearlySalaryRequest, args);
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
                    if(feedback.getSuccess()){
                        long total = feedback.getTotalYearlySalary();
                        Platform.runLater(() -> AlertBox.showInfo("Total yearly salary of " + club + " is " + total + " Rupee."));
                    } 
                    else
                        Platform.runLater(() -> AlertBox.showError("Error in fetching data."));
                } 
                catch (Exception ex){
                    ex.printStackTrace();
                }
            });
            pause.play();
        }
        else
            Platform.runLater(() -> AlertBox.showError("Please select a club."));
    }
}
