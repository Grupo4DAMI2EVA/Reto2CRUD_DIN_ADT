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
            
            Handler fileHandler = new FileHandler("logs/delete_account.log", true);
            
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
            logger.severe("Error setting main controller: " + e.getMessage());
        }
    }

    public void setProfile(Profile profile) {
        try {
            this.profile = profile;
            if (labelUsername != null) {
                labelUsername.setText(profile.getUsername());
            }
            logger.info("User profile set: " + (profile != null ? profile.getUsername() : "null"));
        } catch (Exception e) {
            logger.severe("Error setting user profile: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        try {
            logger.info("User cancelled account deletion - returning to menu");
            
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
            
        } catch (Exception e) {
            logger.severe("Error returning to menu: " + e.getMessage());
            showAlert("Error", "Could not return to the menu.");
        }
    }

    @FXML
    private void delete() {
        try {
            logger.info("Starting user account deletion process");
            
            if (textFieldPassword.getText().isEmpty()) {
                logger.severe("Delete attempt without entering password");
                
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Password Required");
                error.setContentText("Please enter your password to delete your account.");
                error.showAndWait();
                return;
            }

            String username = labelUsername.getText();
            logger.info("User " + username + " attempting to delete their own account");
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Account");
            alert.setHeaderText("Are you sure you want to delete your account?");
            alert.setContentText("This action cannot be undone. All your data will be permanently lost.");

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                
                String password = textFieldPassword.getText();
                logger.info("Confirming deletion for user: " + username);

                Boolean success = cont.dropOutUser(username, password);
                if (success) {
                    logger.info("User account deleted successfully: " + username);
                    
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Account Deleted");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Your account has been successfully deleted.");
                    successAlert.showAndWait();

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
                        logger.severe("Error redirecting to login screen after deletion: " + e.getMessage());
                        showAlert("Error", "Account deleted but could not redirect to login screen.");
                    }
                    
                } else {
                    logger.severe("Failed to delete account: Incorrect password for user: " + username);
                    
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Incorrect Password");
                    error.setContentText("The password is incorrect. Please try again.");
                    error.showAndWait();
                }

            } else {
                logger.info("User cancelled account deletion: " + (profile != null ? profile.getUsername() : "unknown"));
                System.out.println("Account deletion cancelled by the user.");
            }
            
        } catch (Exception e) {
            logger.severe("Error in account deletion process: " + e.getMessage());
            
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Account Could Not Be Deleted");
            error.setContentText("An unexpected error occurred: " + e.getMessage());
            error.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing DeleteAccountController");
            
            // Clear any existing data
            if (textFieldPassword != null) {
                textFieldPassword.clear();
            }
            
            logger.info("DeleteAccountController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing DeleteAccountController: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        try {
            logger.info("Showing alert: " + title + " - " + message);
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
        } catch (Exception e) {
            logger.severe("Error showing alert: " + e.getMessage());
        }
    }

    // Method to clear the form
    public void clearForm() {
        try {
            logger.info("Clearing account deletion form");
            
            textFieldPassword.clear();
            
            logger.info("Form cleared successfully");
            
        } catch (Exception e) {
            logger.severe("Error clearing form: " + e.getMessage());
        }
    }

    // Method to refresh user information
    public void refreshUserInfo() {
        try {
            logger.info("Refreshing user information");
            
            if (profile != null && labelUsername != null) {
                labelUsername.setText(profile.getUsername());
                logger.info("Username refreshed: " + profile.getUsername());
            }
            
        } catch (Exception e) {
            logger.severe("Error refreshing user information: " + e.getMessage());
        }
    }

    void setCont(Controller cont) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}