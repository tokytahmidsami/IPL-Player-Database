package Project.controller_classes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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

public class TradePlayerController{
    Socket clientSocket;
    Stage stage;
    User user;
    String name = "", country = "", club = "", position = "",
           priceLowerString = "", priceUpperString = "", ageLowerString = "", ageUpperString = "", 
           heightLowerString = "", heightUpperString = "", salaryLowerString = "", salaryUpperString = "";
    List<Player> playersOnSale, playersOnSaleFiltered;
    List<String> countries, clubs, positions = Arrays.asList("Any", "Batsman", "Bowler", "Allrounder", "Weecketkeeper");
    @FXML TextField playerNameInput, ageLowerLimitInput, ageUpperLimitInput, heightLowerLimitInput, heightUpperLimitInput,
                    salaryLowerLimitInput, salaryUpperLimitInput, priceLowerLimitInput, priceUpperLimitInput;
    @FXML ComboBox<String> playerCountryInput, playerClubInput, playerPositionInput;
    @FXML Label loginStatusLabel, playerName, playerPosition, playerCountry, playerAge, playerPrice,
                playerClub, playerHeight, playerWeeklySalary, playerJourseyNo, playerOnMarketCount;
    @FXML VBox vbox;
    @FXML ScrollPane playerListPane;
    @FXML ImageView playerImage;
    
    //non button methods.............................................................
    public void goAhead(Socket c, Stage s, User u) throws Exception{
        clientSocket = c;
        stage = s;
        user = u;
    
        loginStatusLabel.setText("Manager, ID : " + user.getUserID());

        ServerRequest request = new ServerRequest(user, RequestID.getPlayersOnSaleRequest, null);
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
            playersOnSale = playersOnSaleFiltered = feedback.getPlayers();
        else AlertBox.showError("Couldn't load players.");

        playerPositionInput.getItems().addAll(positions);

        request = new ServerRequest(user, RequestID.getManagerIDs, null);
        try{
            ServerRequest.sendRequest(clientSocket, request);
            feedback = ServerFeedback.receiveFeedback(clientSocket);
        }
        catch (Exception e){
            StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
            return;
        }
        if(feedback.getSuccess()){
            clubs = feedback.getResults();
            playerClubInput.getItems().add("Any");
            playerClubInput.getItems().addAll(clubs);
        }
        else AlertBox.showError("Couldn't load Clubs.");

        request = new ServerRequest(user, RequestID.getAllCountriesRequest, null);
        try{
            ServerRequest.sendRequest(clientSocket, request);
            feedback = ServerFeedback.receiveFeedback(clientSocket);
        }
        catch (Exception e){
            StageManager.setAndShowStage(stage, FXMLLoader.load(getClass().getResource("../fxmls/serverNotFoundErrorWindow.fxml")));
            return;
        }
        if(feedback.getSuccess()){
            countries = feedback.getResults();
            playerCountryInput.getItems().add("Any");
            playerCountryInput.getItems().addAll(countries);
        }
        else AlertBox.showError("Couldn't load Countries.");

        setFilters();

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
        playerOnMarketCount.setText("(" + playersOnSaleFiltered.size() + "/" + playersOnSale.size() + ")");

        for (Player player : playersOnSaleFiltered){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/tradeInfoBar.fxml"));
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
                playerPrice.setText(player.getPrice() + " Rs");
            });
            TradeInfoBarController controller = loader.getController();
            controller.set(user, clientSocket, stage, player);
            vbox.getChildren().add(tradeCard);
        }
    }

    private void setFilters(){
        name = playerNameInput.getText();
        country = playerCountryInput.getValue();
        club = playerClubInput.getValue();
        position = playerPositionInput.getValue();

        priceLowerString = priceLowerLimitInput.getText();
        priceUpperString = priceUpperLimitInput.getText();
        int priceUpper = Integer.MAX_VALUE, priceLower = 0;
        try{
            if(!priceLowerString.isBlank())
                priceLower = Integer.parseInt(priceLowerString);
            else priceLowerString = "0";
            if(!priceUpperString.isBlank())
                priceUpper = Integer.parseInt(priceUpperString);
            else priceUpperString = Integer.MAX_VALUE + "";
            if(priceLower < 0 || priceUpper < 0) throw new Exception();
            if(priceLower > priceUpper){
                AlertBox.showError("Price lower limit can't be greater than\nupper limit.");
                priceLowerString = "0"; 
                priceUpperString = Integer.MAX_VALUE + "";
                return;
            }
        }
        catch(Exception e){
            AlertBox.showError("Price must be a non-negative integer.");
            return;
        }

        ageLowerString = ageLowerLimitInput.getText();
        ageUpperString = ageUpperLimitInput.getText();
        int ageUpper = Integer.MAX_VALUE, ageLower = 0;
        try{
            if(!ageLowerString.isBlank())
                ageLower = Integer.parseInt(ageLowerString);
            else ageLowerString = "0";
            if(!ageUpperString.isBlank())
                ageUpper = Integer.parseInt(ageUpperString);
            else ageUpperString = Integer.MAX_VALUE + "";
            if(ageLower < 0 || ageUpper < 0) throw new Exception();
            if(ageLower > ageUpper){
                AlertBox.showError("Age lower limit can't be greater than\nupper limit.");
                ageLowerString = "0";
                ageUpperString = Integer.MAX_VALUE + "";
                return;
            }
        }
        catch(Exception e){
            AlertBox.showError("Age must be a non-negative integer.");
            return;
        }

        heightLowerString = heightLowerLimitInput.getText();
        heightUpperString = heightUpperLimitInput.getText();
        double heightUpper = Double.MAX_VALUE, heightLower = 0;
        try{
            if(!heightLowerString.isBlank())
                heightLower = Double.parseDouble(heightLowerString);
            else heightLowerString = "0";
            if(!heightUpperString.isBlank())
                heightUpper = Integer.parseInt(heightUpperString);
            else heightUpperString = Double.MAX_VALUE + "";
            if(heightLower < 0 || heightUpper < 0) throw new Exception();
            if(heightLower > heightUpper){
                AlertBox.showError("Height lower limit can't be greater than\nupper limit.");
                heightLowerString = "0";
                heightUpperString = Double.MAX_VALUE + "";
                return;
            }
        }
        catch(Exception e){
            AlertBox.showError("Height must be a non-negative floationg point number.");
            return;
        }

        salaryLowerString = salaryLowerLimitInput.getText();
        salaryUpperString = salaryUpperLimitInput.getText();
        int salaryUpper = Integer.MAX_VALUE, salaryLower = 0;
        try{
            if(!salaryLowerString.isBlank())
                salaryLower = Integer.parseInt(salaryLowerString);
            else salaryLowerString = "0";
            if(!salaryUpperString.isBlank())
                salaryUpper = Integer.parseInt(salaryUpperString);
            else salaryUpperString = Integer.MAX_VALUE + "";
            if(salaryLower < 0 || salaryUpper < 0) throw new Exception();
            if(salaryLower > salaryUpper){
                AlertBox.showError("Salary lower limit can't be greater than\nupper limit.");
                salaryLowerString = "0";
                salaryUpperString = Integer.MAX_VALUE + "";
                return;
            }
        }
        catch(Exception e){
            AlertBox.showError("Salary must be a non-negative integer.");
            return;
        }
    }
    
    private void filterMain() throws Exception{
        playersOnSaleFiltered = playersOnSale;

        if(!name.isBlank()){
            playersOnSaleFiltered = new ArrayList<>();
            for(Player p: playersOnSale){
                if(p.getName().equalsIgnoreCase(name)){
                    playersOnSaleFiltered.add(p);
                    break;
                }
            }
        }

        if(country != null && !country.equals("Any")){
            List<Player> temp = playersOnSaleFiltered;
            playersOnSaleFiltered = new ArrayList<>();
            for(Player p: temp)
                if(p.getCountry().equals(country))
                    playersOnSaleFiltered.add(p);
        }

        if(club != null && !club.equals("Any")){
            List<Player> temp = playersOnSaleFiltered;
            playersOnSaleFiltered = new ArrayList<>();
            for(Player p: temp)
                if(p.getClub().equals(club))
                    playersOnSaleFiltered.add(p);
        }

        if(position != null && !position.equals("Any")){
            List<Player> temp = playersOnSaleFiltered;
            playersOnSaleFiltered = new ArrayList<>();
            for(Player p: temp)
                if(p.getPosition().equals(position))
                    playersOnSaleFiltered.add(p);
        }

        int priceUpper = Integer.parseInt(priceUpperString), 
            priceLower = Integer.parseInt(priceLowerString);    
        List<Player> temp = playersOnSaleFiltered;
        playersOnSaleFiltered = new ArrayList<>();
        for(Player p: temp){
            int price = p.getPrice();
            if(priceLower <= price && price <= priceUpper)
                playersOnSaleFiltered.add(p);
        }

        int ageUpper = Integer.parseInt(ageUpperString), 
            ageLower = Integer.parseInt(ageLowerString);
        temp = playersOnSaleFiltered;
        playersOnSaleFiltered = new ArrayList<>();
        for(Player p: temp){
            int age = p.getAge();
            if(ageLower <= age && age <= ageUpper)
                playersOnSaleFiltered.add(p);
        }
            

        double heightUpper = Double.parseDouble(heightUpperString), 
               heightLower = Double.parseDouble(heightLowerString);
        temp = playersOnSaleFiltered;
        playersOnSaleFiltered = new ArrayList<>();
        for(Player p: temp){
            double height = p.getHeight();
            if(heightLower <= height && height <= heightUpper)
                playersOnSaleFiltered.add(p);
        }

        int salaryUpper = Integer.parseInt(salaryUpperString), 
            salaryLower = Integer.parseInt(salaryLowerString);
        temp = playersOnSaleFiltered;
        playersOnSaleFiltered = new ArrayList<>();
        for(Player p: temp){
            int salary = p.getWeeklySalary();
            if(salaryLower <= salary && salary <= salaryUpper)
                playersOnSaleFiltered.add(p);
        }

        vbox = new VBox(5);
        playerListPane.setContent(vbox);
        showPlayers();
    }

    //button methods.................................................................

    public void goToSellToMarket(){
        System.out.println("Sell too market...");
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/sellPlayerWindow.fxml"));
            try{
                StageManager.setAndShowStage(stage, loader.load());
            } catch (IOException e1){
                e1.printStackTrace();
            }
            SellPlayerController controller = loader.getController();
            try {
                controller.goAhead(clientSocket, stage, user);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        pause.play();
    }

    public void updatePlayerList() throws Exception{
        //The diff between this and processRefresh is that this method doesn't 
        //clear the filters. processRefresh clears the filters.
        ServerRequest request = new ServerRequest(user, RequestID.getPlayersOnSaleRequest, null);
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
            playersOnSale = feedback.getPlayers();
        else AlertBox.showError("Couldn't load players.");

        filterMain();
    }

    public void processRefresh() throws Exception{
        PlaySound.playClick();
        System.out.println("Refreshing...");
        ServerRequest request = new ServerRequest(user, RequestID.getPlayersOnSaleRequest, null);
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
            playersOnSale = playersOnSaleFiltered = feedback.getPlayers();
        else AlertBox.showError("Couldn't load players.");

        playerNameInput.clear();
        ageLowerLimitInput.clear();
        ageUpperLimitInput.clear();
        heightLowerLimitInput.clear();
        heightUpperLimitInput.clear();
        salaryLowerLimitInput.clear();
        salaryUpperLimitInput.clear();
        priceLowerLimitInput.clear();
        priceUpperLimitInput.clear();
        
        playerPositionInput.getSelectionModel().clearSelection();
        playerPositionInput.getItems().clear();
        playerPositionInput.getItems().addAll(positions);

        playerClubInput.getSelectionModel().clearSelection();
        playerClubInput.getItems().clear();
        playerClubInput.getItems().addAll(clubs);

        playerCountryInput.getSelectionModel().clearSelection();
        playerCountryInput.getItems().clear();
        playerCountryInput.getItems().addAll(countries);

        setFilters();//filters set to initial values
        showPlayers();
    }

    public void setAndApplyFilters(){
        PlaySound.playClick();
        setFilters();
        try{
            filterMain();
        } 
        catch (Exception e){
            e.printStackTrace();
        }
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
}
