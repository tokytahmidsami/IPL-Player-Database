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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


public class SearchPlayerController{
    // This class is the controller for the searchPlayer.fxml file
    // Previous window: mainMenu.fxml, class MainMenuController
    // Next window: countryWisePlayerCount.fxml, class CountryWisePlayerCountController
    @FXML Label loginStatusLabel, playerName, playerPosition, playerCountry, playerAge, 
                playerClub, playerHeight, playerWeeklySalary, playerJourseyNo;
    @FXML TextField playerNameInput, salaryLowerLimitInput, salaryUpperLimitInput;
    @FXML ComboBox<String> playerCountryInput, playerClubInput, playerPositionInput;
    @FXML ScrollPane playerListPane;
    @FXML VBox vbox;
    @FXML ImageView playerImage;
    Socket clientSocket;
    Stage stage;
    User user;
    List<Player> players;
    ServerRequest request;
    List<String> args = new ArrayList<>(Collections.nCopies(10, ""));

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
        if(feedback.getSuccess()) players = feedback.getPlayers();

        playerCountryInput.getItems().add("Any");
        playerClubInput.getItems().add("Any");
        playerPositionInput.getItems().add("Any");
        for(Player p: players){
            if(!playerCountryInput.getItems().contains(p.getCountry()))
                playerCountryInput.getItems().add(p.getCountry());
            if(!playerClubInput.getItems().contains(p.getClub()))
                playerClubInput.getItems().add(p.getClub());
            if(!playerPositionInput.getItems().contains(p.getPosition()))
                playerPositionInput.getItems().add(p.getPosition());
        }

        vbox = new VBox(5);
        playerListPane.setContent(vbox);
        try{
            showPlayers();
        } 
        catch (Exception e){
            e.printStackTrace();
        }
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
        pause.setOnFinished((e)->{
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
            * args[8] = lower limit of salary
            * args[9] = upper limit of salary
            */
            args.set(0, playerNameInput.getText());
            args.set(1, playerCountryInput.getValue() != null ? playerCountryInput.getValue() : "");
            args.set(2, playerClubInput.getValue() != null ? playerClubInput.getValue() : "");
            args.set(3, playerPositionInput.getValue() != null ? playerPositionInput.getValue() : "");
            String lowerLimit = salaryLowerLimitInput.getText();
            String upperLimit = salaryUpperLimitInput.getText();
            try{
                int lower, upper;
                if(!lowerLimit.isEmpty()) lower = Integer.parseInt(lowerLimit);
                else lower = 0;
                if(!upperLimit.isEmpty()) upper = Integer.parseInt(upperLimit);
                else upper = Integer.MAX_VALUE;
                if (lower > upper){
                    Platform.runLater(()->{
                        AlertBox.showError("Lower limit cannot be greater than upper limit.");
                    });
                    return;
                }
                args.set(8, Integer.toString(lower));
                args.set(9, Integer.toString(upper));
            } 
            catch(NumberFormatException ex){
                Platform.runLater(()->{
                    AlertBox.showError("Salary should be an Integer.");
                });
                return;
            }

            request = new ServerRequest(user, RequestID.searchPlayerRequest, args);
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

    public void processCountryWisePlayerCount() throws Exception{
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            ServerRequest request = new ServerRequest(user, RequestID.countryWisePlayerCountRequest, null);
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/countryWisePlayerCountWindow.fxml"));
                    StageManager.setAndShowStage(stage, loader.load());
                    CountryWiseCountController controller = loader.getController();
                    controller.goAhead(clientSocket, stage, user, feedback.getPlayersByCountry());
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
}