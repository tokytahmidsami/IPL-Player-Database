package Project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ServerClientApp extends Application {

    private int data = 0; // Simulated shared data between server and clients

    @Override
    public void start(Stage primaryStage) {
        // Server Stage - Displays a button and handles server logic
        Button serverButton = new Button("Server: Data = " + data);
        serverButton.setOnAction(e -> {
            // Simulate server logic - increment data
            data++;
            serverButton.setText("Server: Data = " + data);
            // Notify clients about the data change
            openClientStage();
        });

        StackPane root1 = new StackPane();
        root1.getChildren().add(serverButton);
        Scene scene1 = new Scene(root1, 300, 200);

        primaryStage.setTitle("Server Stage");
        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    private void openClientStage() {
        // Client Stage - Displays the data from the server
        Stage clientStage = new Stage();
        Button clientButton = new Button("Client: Waiting for data...");

        clientButton.setOnAction(e -> {
            clientButton.setText("Client: Data = " + data);
        });

        StackPane clientRoot = new StackPane();
        clientRoot.getChildren().add(clientButton);
        Scene clientScene = new Scene(clientRoot, 300, 200);

        clientStage.setTitle("Client Stage");
        clientStage.setScene(clientScene);
        clientStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}