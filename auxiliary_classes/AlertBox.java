package Project.auxiliary_classes;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertBox {
    public static void displayAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.getDialogPane().getStylesheets().add(AlertBox.class.getResource("/Project/css_files/alertBox.css").toExternalForm());

        alert.getDialogPane().setStyle("-fx-padding: 15px;");
        
        alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0)).setStyle("-fx-cursor: hand;");

        alert.showAndWait();
    }

    public static void showError(String message) {
        displayAlert(AlertType.ERROR, "Error", "An Error Occurred", message);
    }

    public static void showInfo(String message) {
        displayAlert(AlertType.INFORMATION, "Information", null, message);
    }

    public static void showWarning(String message) {
        displayAlert(AlertType.WARNING, "Warning", "Be Cautious", message);
    }
}