package Project.controller_classes;

import java.util.List;

import Project.auxiliary_classes.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CountryWiseCountBarController {
    @FXML Label countryName, playerCount;

    public void setCountryName(String name){
        countryName.setText(name);
    }

    public void setPlayerCount(List<Player> count){
        playerCount.setText(count.size() + "");
    }
}
