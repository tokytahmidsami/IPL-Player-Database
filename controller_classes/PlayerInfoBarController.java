package Project.controller_classes;

import Project.auxiliary_classes.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class PlayerInfoBarController {
    // This class is the controller for the playerInfoBar.fxml file
    private Player player;
    @FXML HBox playerInfoBar;
    @FXML Label playerName, playerClub;

    public void setPlayer(Player p){
        player = p;
        playerName.setText(player.getName());
        playerClub.setText(player.getClub());
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
