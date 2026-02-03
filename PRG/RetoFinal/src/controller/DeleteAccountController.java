package controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import model.Profile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Controller for the Delete Account window for regular Users. This controller allows a user to delete their own account.
 */
public class DeleteAccountController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    // Label displaying the username of the logged-in user
    @FXML
    private Label LabelUsername;

    // TextField to enter the user's password for confirmation
    @FXML
    private TextField TextFieldPassword;

    // Buttons to cancel or execute deletion
    @FXML
    private Button Button_Cancel;
    @FXML
    private Button Button_Delete;

    // Reference to the main Controller handling business logic
    private Controller cont;

    // Current logged-in profile
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
            
            FileHandler fileHandler = new FileHandler("logs/LogInWindow.log", true);
            
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    if (record.getLevel() == Level.INFO || record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
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
            logger.info("AdminShopController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    /**
     * Sets the Controller instance.
     *
     * @param cont Controller object
     */
    public void setCont(Controller cont) {
        logger.info("Setting controller in DeleteAccountController");
        this.cont = cont;
    }

    /**
     * Sets the current logged-in profile and updates the username label.
     *
     * @param profile Profile object
     */
    public void setProfile(Profile profile) {
        logger.info("Setting user profile for deletion: " + (profile != null ? profile.getUsername() : "null"));
        this.profile = profile;
        LabelUsername.setText(profile.getUsername());
        logger.info("Username label updated: " + profile.getUsername());
    }

    /**
     * Handles cancel button action. Closes the current window and returns to MenuWindow.
     */
    @FXML
    private void cancel() {
        logger.info("Cancel button clicked - User: " + (profile != null ? profile.getUsername() : "unknown"));
        
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            controller.MenuWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUsuario(profile);
            controllerWindow.setCont(this.cont);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
            logger.info("MenuWindow opened successfully for user: " + profile.getUsername());

            Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
            currentStage.close();
            
            logger.info("DeleteAccount window closed (cancelled)");

        } catch (IOException ex) {
            logger.severe("Error navigating to MenuWindow from cancel: " + ex.getMessage());
            java.util.logging.Logger.getLogger(MenuWindowController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles delete button action. Confirms deletion and calls the Controller to remove the user account.
     */
    @FXML
    private void delete() {
        logger.info("Delete button clicked - Starting self-account deletion process");
        
        // Validación de contraseña
        if (TextFieldPassword.getText().isEmpty()) {
            logger.warning("Self-delete attempted without password - User: " + 
                          (profile != null ? profile.getUsername() : "unknown"));
            
            javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Password required");
            error.setContentText("Please enter your password to delete the account.");
            error.showAndWait();
            return;
        }

        String username = LabelUsername.getText();
        String password = TextFieldPassword.getText();
        
        logger.info("User " + username + " attempting self-account deletion");
        logger.info("Password provided: [PROTECTED], Length: " + password.length());

        // Confirmación de eliminación
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            logger.info("User confirmed self-account deletion: " + username);
            
            try {
                logger.info("Calling dropOutUser - User: " + username + " (self-deletion)");
                Boolean success = cont.dropOutUser(username, password);
                
                if (success) {
                    logger.info("Self-account deletion SUCCESSFUL - User: " + username + 
                               " (ID: " + (profile != null ? profile.getUserCode() : "unknown") + ")");
                    
                    javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Deleted account");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Your account has been successfully deleted.");
                    successAlert.showAndWait();

                    try {
                        logger.info("Navigating to LoginWindow after successful self-deletion");
                        
                        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
                        javafx.scene.Parent root = fxmlLoader.load();

                        controller.LogInWindowController controllerWindow = fxmlLoader.getController();
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.show();
                        
                        Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                        currentStage.close();
                        
                        logger.info("Successfully navigated to LoginWindow after account deletion");

                    } catch (IOException ex) {
                        logger.severe("Error navigating to LoginWindow after self-deletion: " + ex.getMessage());
                        Logger.getLogger(DeleteAccountController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    logger.warning("Self-account deletion FAILED - Incorrect password for user: " + username);
                    
                    javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Incorrect password");
                    error.setContentText("The password is incorrect. Please try again.");
                    error.showAndWait();
                }

            } catch (Exception ex) {
                logger.severe("Exception during self-account deletion - User: " + username + 
                            ", Error: " + ex.getMessage());
                
                ex.printStackTrace();
                javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("The account could not be deleted.");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        } else {
            logger.info("Self-account deletion CANCELLED by user: " + username);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing DeleteAccountController (User self-deletion)");
        
        try {
            // Initialization logic if needed
            logger.info("DeleteAccountController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing DeleteAccountController: " + e.getMessage());
        }
    }
}