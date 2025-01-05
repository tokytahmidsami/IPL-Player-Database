package Project.auxiliary_classes;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class ConfirmationBox {
    public static boolean askConfirmation(String query) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(query);

        alert.getDialogPane().getStylesheets().add(ConfirmationBox.class.getResource("/Project/css_files/alertBox.css").toExternalForm());
        alert.getDialogPane().setStyle("-fx-padding: 15px;");

        ButtonType proceedButton = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(proceedButton, cancelButton);

        alert.getDialogPane().lookupButton(proceedButton).setStyle("-fx-cursor: hand;");
        alert.getDialogPane().lookupButton(cancelButton).setStyle("-fx-cursor: hand;");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == proceedButton;
    }
}
