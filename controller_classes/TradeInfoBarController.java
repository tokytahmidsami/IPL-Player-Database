package Project.controller_classes;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Project.auxiliary_classes.AlertBox;
import Project.auxiliary_classes.ConfirmationBox;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.Player;
import Project.auxiliary_classes.RequestID;
import Project.auxiliary_classes.ServerFeedback;
import Project.auxiliary_classes.ServerRequest;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TradeInfoBarController{
    // This class is the controller for the playerInfoBar.fxml file
    private Player player;
    @FXML HBox playerInfoBar;
    @FXML Label playerName;
    @FXML Button button;
    User manager;
    Socket clientSocket;
    Stage stage;
    boolean forSale;

    public void set(User u, Socket c, Stage s, Player p){
        manager = u;
        clientSocket = c;
        stage = s;
        player = p;
        playerName.setText(player.getName());
        if(manager.getUserID().equals(player.getClub())){
            button.setStyle("-fx-background-color: #ff4c4c; -fx-font-size: 16px;");
            button.setText("Cancel Sale");
        }
        else{
            button.setText("Buy");
        }
        forSale = false;
    }

    public void set(User u, Socket c, Stage s, Player p, boolean sale){
        manager = u;
        clientSocket = c;
        stage = s;
        player = p;
        forSale = sale;
        playerName.setText(player.getName());

        if(forSale){
            button.setText("Sell");
        }
        else if(manager.getUserID().equals(player.getClub())){
            button.setStyle("-fx-background-color: #ff4c4c; -fx-font-size: 16px;");
            button.setText("Cancel Sale");
        }
        else{
            button.setText("Buy");
        }
    }

    public void processTrade() throws Exception{
        PlaySound.playClick();
        if(forSale) processAddToMarket();
        else if(manager.getUserID().equals(player.getClub()))
            processCancelSale();
        else processBuy();
    }

    private void processAddToMarket() throws Exception{
        int price;
        String priceString = ((TextField)(playerInfoBar.getChildren().get(1))).getText();
        try{
            price = Integer.parseInt(priceString);
            if(price < 0) throw new Exception();
        }
        catch(Exception e){
            AlertBox.showError("Price must be a non negative integer.");
            return;
        }

        boolean sureToSell = ConfirmationBox.askConfirmation("Are you sure to add \"" + player.getName() + "\"\nto the market with price " + price + " Rs?");
        if(sureToSell){
            List<String> args = new ArrayList<>(Collections.nCopies(11, ""));
            args.set(0, player.getName());
            args.set(2, player.getClub());
            args.set(10, priceString);
            ServerRequest request = new ServerRequest(manager, RequestID.sellPlayerRequest, args);
            ServerFeedback feedback = new ServerFeedback(false, null, null);
            try{
                ServerRequest.sendRequest(clientSocket, request);
                feedback = ServerFeedback.receiveFeedback(clientSocket);
            }
            catch (Exception e){
                StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
                return;
            }
            if(feedback.getSuccess())
                AlertBox.showInfo("Player added to the market succesfully.\nRefresh before next transaction.");
            else
                AlertBox.showInfo("Couldn't add player to the market.\nPlayer might have been added earlier.\nPlease refresh and retry.");
        }
        else return;
    }

    private void processBuy() throws Exception{
        System.out.println("processing buying");
        boolean sureToBuy = ConfirmationBox.askConfirmation("Are you sure to buy \"" + player.getName() + "\"\nfor " + player.getPrice() + " Rs?");
        if(sureToBuy){
            List<String> args = new ArrayList<>(Collections.nCopies(12, ""));
            args.set(0, player.getName());
            args.set(2, player.getClub());
            args.set(11, manager.getUserID());
            ServerRequest request = new ServerRequest(manager, RequestID.buyPlayerRequest, args);
            ServerFeedback feedback = new ServerFeedback(false, null, null);
            try{
                ServerRequest.sendRequest(clientSocket, request);
                feedback = ServerFeedback.receiveFeedback(clientSocket);
            }
            catch (Exception e){
                StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
                return;
            }
            if(feedback.getSuccess())
                AlertBox.showInfo("Player bought succesfully.\nRefresh before next transaction.");
            else
                AlertBox.showInfo("Couldn't buy player.\nPlayer might have been bought earlier.\nPlease refresh and retry.");
        }
        else return;
    }

    private void processCancelSale() throws Exception{
        System.out.println("processing cancel sell");
        boolean sureToCancelSale = ConfirmationBox.askConfirmation("Are you sure to remove \"" + player.getName() + "\"\n worth " + player.getPrice() + " Rs from the market?");
        if(sureToCancelSale){
            List<String> args = new ArrayList<>(Collections.nCopies(11, ""));
            args.set(0, player.getName());
            args.set(2, player.getClub());
            ServerRequest request = new ServerRequest(manager, RequestID.cancelSalePlayerRequest, args);
            ServerFeedback feedback = new ServerFeedback(false, null, null);
            try{
                ServerRequest.sendRequest(clientSocket, request);
                feedback = ServerFeedback.receiveFeedback(clientSocket);    
            }
            catch (Exception e){
                StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
                return;
            }
            if(feedback.getSuccess())
                AlertBox.showInfo("Player removed from the market succesfully.\nRefresh before next transaction.");
            else
                AlertBox.showInfo("Couldn't remove player from the market.\nPlayer might have been bought earlier.\nPlease refresh and retry.");
        }
        else return;
    }

    public void active(){
        Background bg = new Background(new BackgroundFill(Color.rgb(50, 200, 100, 0.4), null, null));
        playerInfoBar.setBackground(bg);
    }

    public void inactive(){
        Background bg = new Background(new BackgroundFill(Color.rgb(50, 200, 200, 0.2), null, null));
        playerInfoBar.setBackground(bg);
    }
}
