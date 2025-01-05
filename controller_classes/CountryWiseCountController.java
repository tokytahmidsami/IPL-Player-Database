package Project.controller_classes;

import java.net.Socket;
import java.util.List;
import java.util.Map;

import Project.auxiliary_classes.Manager;
import Project.auxiliary_classes.PlaySound;
import Project.auxiliary_classes.Player;
import Project.auxiliary_classes.StageManager;
import Project.auxiliary_classes.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CountryWiseCountController{
    Map<String, List<Player>> countryWisePlayers;
    Stage stage;
    Socket clientSocket;
    User user;
    @FXML ImageView back;
    @FXML Label loginStatusLabel;
    @FXML ScrollPane countryWisePane;
    @FXML VBox vboxCountryWise;

    public void goAhead(Socket so, Stage s, User u, Map<String, List<Player>> list){
        stage = s;
        clientSocket = so;
        user = u;
        countryWisePlayers = list;

        if(user instanceof Manager)
            loginStatusLabel.setText("Manager, ID : " + user.getUserID());
        else loginStatusLabel.setText("User, ID : " + user.getUserID());

        vboxCountryWise.getChildren().clear();
        vboxCountryWise.setSpacing(10);
        for(var entry : countryWisePlayers.entrySet()){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/countryWiseCountBar.fxml"));
                HBox hb = loader.load();
                CountryWiseCountBarController controller = loader.getController();
                controller.setCountryName(entry.getKey());
                controller.setPlayerCount(entry.getValue());
                vboxCountryWise.getChildren().add(hb);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        countryWisePane.setContent(vboxCountryWise);
    }

    public void goToPlayerSearchingMenu() throws Exception{
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        PlaySound.playClick();
        pause.setOnFinished((e)->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmls/searchPlayerWindow.fxml"));
            try{
                StageManager.setAndShowStage(stage, loader.load());
                SearchPlayerController controller = loader.getController();
                controller.goAhead(clientSocket, stage, user);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        pause.play();
    }
}
