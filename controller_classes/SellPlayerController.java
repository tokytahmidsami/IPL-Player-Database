package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Project.auxiliary_classes.AlertBox;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.Player;
import Project.auxiliary_classes.RequestID;
import Project.auxiliary_classes.ServerFeedback;
import Project.auxiliary_classes.ServerRequest;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SellPlayerController {
    Socket clientSocket;
    Stage stage;
    User user;
    List<Player> playersAvailableForSale;
    @FXML ScrollPane playerListPane;
    @FXML VBox playersForSale;
    @FXML Label loginStatusLabel, playerName, playerPosition, playerCountry, playerAge, 
                playerClub, playerHeight, playerWeeklySalary, playerJourseyNo;
    @FXML ImageView playerImage;

    public void goAhead(Socket c, Stage s, User u) throws Exception{
        clientSocket = c;
        stage = s;
        user = u;

        loginStatusLabel.setText("Manager, ID : " + user.getUserID());

        setAndShowPlayers();
    }

    private void showPlayers() throws Exception{
        playersForSale.getChildren().clear();
        System.out.println(playersAvailableForSale.size());

        for (Player player : playersAvailableForSale){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/sellInfoBar.fxml"));
            HBox tradeCard = loader.load();
            tradeCard.setOnMouseClicked(e ->{
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
            TradeInfoBarController controller = loader.getController();
            controller.set(user, clientSocket, stage, player, true);
            playersForSale.getChildren().add(tradeCard);
        }
    }

    private void setAndShowPlayers() throws Exception{
        List<String> args = new ArrayList<>(Collections.nCopies(11, ""));
        args.set(2, user.getUserID());
        args.set(10, "Any");
        ServerRequest request = new ServerRequest(user, RequestID.searchClubRequest, args);
        ServerFeedback feedback = new ServerFeedback(false, null, null);
        try{
            ServerRequest.sendRequest(clientSocket, request);
            feedback = ServerFeedback.receiveFeedback(clientSocket);
        }
        catch (Exception e){
            StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
            return;
        }
        playersAvailableForSale = new ArrayList<>();

        if(feedback.getSuccess()){
            for(Player p: feedback.getPlayers())
                if(p.getOnSaleStatus() == false)
                    playersAvailableForSale.add(p);
        }
        else AlertBox.showError("Couldn't load players. Try again.");

        playersForSale = new VBox(5);
        playerListPane.setContent(playersForSale);
        try{
            showPlayers();
        } 
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void processRefresh() throws Exception{
        setAndShowPlayers();
    }

    //button methods...........................................................
    public void goToPlayerMarket(){
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/tradePlayerWindow.fxml"));
            try{
                StageManager.setAndShowStage(stage, loader.load());
            } catch (IOException e1){
                e1.printStackTrace();
            }
            TradePlayerController controller = loader.getController();
            try {
                controller.goAhead(clientSocket, stage, user);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        pause.play();
    }
}
