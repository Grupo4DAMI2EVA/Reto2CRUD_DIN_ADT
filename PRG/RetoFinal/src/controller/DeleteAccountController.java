package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Profile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;

public class DeleteAccountController implements Initializable {

    private static final Logger logger = Logger.getLogger(DeleteAccountController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Label labelUsername;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonDelete;

    private Controller cont;
    private Profile profile;

    static {
        initializeLogger();
    }
    
    private static synchronized void initializeLogger() {
        if (loggerInitialized) {
            return;
        }
        
        try {
            java.io.File logsFolder = new java.io.File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }
            
            FileHandler fileHandler = new FileHandler("logs/delete_account.log", true);
            
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    if (record.getLevel() == Level.INFO || record.getLevel() == Level.SEVERE) {
                        return String.format("[%1$tF %1$tT] [%2$s] %3$s %n",
                                new java.util.Date(record.getMillis()),
                                record.getLevel(),
                                record.getMessage());
                    }
                    return "";
                }
            });
            
            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false);
            
            loggerInitialized = true;
            logger.info("DeleteAccountController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for DeleteAccountController");
        } catch (Exception e) {
            logger.severe(String.format("Error setting main controller: %s", e.getMessage()));
        }
    }

    public void setProfile(Profile profile) {
        try {
            this.profile = profile;
            if (labelUsername != null) {
                labelUsername.setText(profile.getUsername());
            }
            logger.info(String.format("User profile set: %s", 
                       profile != null ? profile.getUsername() : "null"));
        } catch (Exception e) {
            logger.severe(String.format("Error setting user profile: %s", e.getMessage()));
        }
    }

    @FXML
    private void cancel() {
        try {
            logger.info("User cancelled account deletion - returning to menu");
            
            navigateToMenuWindow();
            
        } catch (Exception e) {
            logger.severe(String.format("Error returning to menu: %s", e.getMessage()));
            showAlert("Error", "Could not return to the menu.");
        }
    }

    @FXML
    private void delete() {
        try {
            logger.info("Starting user account deletion process");
            
            if (!validatePasswordField()) {
                return;
            }

            logger.info(String.format("User %s attempting to delete their own account", 
                       labelUsername.getText()));
            
            if (confirmDeletion()) {
                processAccountDeletion();
            } else {
                handleDeletionCancellation();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in account deletion process: %s", e.getMessage()));
            showErrorAlert("Account Could Not Be Deleted", 
                String.format("An unexpected error occurred: %s", e.getMessage()));
        }
    }

    private boolean validatePasswordField() {
        if (textFieldPassword.getText().isEmpty()) {
            logger.severe("Delete attempt without entering password");
            
            showErrorAlert("Password Required", 
                "Please enter your password to delete your account.");
            return false;
        }
        return true;
    }

    private boolean confirmDeletion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone. All your data will be permanently lost.");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }

    private void processAccountDeletion() {
        String username = labelUsername.getText();
        String password = textFieldPassword.getText();
        
        logger.info(String.format("Confirming deletion for user: %s", username));

        Boolean success = cont.dropOutUser(username, password);
        if (success) {
            handleSuccessfulDeletion(username);
        } else {
            handleFailedDeletion(username);
        }
    }

    private void handleSuccessfulDeletion(String username) {
        logger.info(String.format("User account deleted successfully: %s", username));
        
        showSuccessAlert("Account Deleted", 
            "Your account has been successfully deleted.");
        
        navigateToLoginScreen();
    }

    private void handleFailedDeletion(String username) {
        logger.severe(String.format("Failed to delete account: Incorrect password for user: %s", username));
        
        showErrorAlert("Incorrect Password", 
            "The password is incorrect. Please try again.");
    }

    private void handleDeletionCancellation() {
        logger.info(String.format("User cancelled account deletion: %s", 
                   profile != null ? profile.getUsername() : "unknown"));
        System.out.println("Account deletion cancelled by the user.");
    }

    private void navigateToMenuWindow() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
        Parent root = fxmlLoader.load();

        MenuWindowController controllerWindow = fxmlLoader.getController();
        controllerWindow.setUser(profile);
        controllerWindow.setController(this.cont);
        
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        
        Stage currentStage = (Stage) buttonCancel.getScene().getWindow();
        currentStage.close();
        
        logger.info("Successfully returned to menu");
    }

    private void navigateToLoginScreen() {
        try {
            logger.info("Redirecting to login screen after successful deletion");
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();

            LogInWindowController controllerWindow = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            
            Stage currentStage = (Stage) buttonDelete.getScene().getWindow();
            currentStage.close();
            
            logger.info("Successfully redirected to login screen");
            
        } catch (Exception e) {
            logger.severe(String.format("Error redirecting to login screen after deletion: %s", e.getMessage()));
            showAlert("Error", "Account deleted but could not redirect to login screen.");
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(title);
        successAlert.setHeaderText(null);
        successAlert.setContentText(message);
        successAlert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(title);
        error.setHeaderText(title);
        error.setContentText(message);
        error.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing DeleteAccountController");
            
            clearFormData();
            
            logger.info("DeleteAccountController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing DeleteAccountController: %s", e.getMessage()));
        }
    }

    private void clearFormData() {
        if (textFieldPassword != null) {
            textFieldPassword.clear();
        }
    }

    private void showAlert(String title, String message) {
        try {
            logger.info(String.format("Showing alert: %s - %s", title, message));
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
        } catch (Exception e) {
            logger.severe(String.format("Error showing alert: %s", e.getMessage()));
        }
    }

    public void clearForm() {
        try {
            logger.info("Clearing account deletion form");
            
            textFieldPassword.clear();
            
            logger.info("Form cleared successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error clearing form: %s", e.getMessage()));
        }
    }

    public void refreshUserInfo() {
        try {
            logger.info("Refreshing user information");
            
            if (profile != null && labelUsername != null) {
                labelUsername.setText(profile.getUsername());
                logger.info(String.format("Username refreshed: %s", profile.getUsername()));
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error refreshing user information: %s", e.getMessage()));
        }
    }

    public void setCont(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Controller set via setCont method");
        } catch (Exception e) {
            logger.severe(String.format("Error in setCont: %s", e.getMessage()));
        }
    }
}