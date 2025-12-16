package exception;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Custom exception thrown when a password validation fails.
 * Automatically shows a popup alert with the error message.
 * 
 * Author: acer
 */
public class passwordequalspassword extends Exception {

    public passwordequalspassword(String text) {
        super(text);
        showPopup(text);
    }

    /**
     * Shows a JavaFX error alert with the given message.
     *
     * @param message The message to display in the alert
     */
    private void showPopup(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Password error");
        alert.setHeaderText("Invalid password");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
