package Project.auxiliary_classes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageManager {
    public static void setAndShowStage(Stage primaryStage, Parent root){
        primaryStage.setTitle("IPL Player Database");
        primaryStage.setScene(new Scene(root)); 
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
