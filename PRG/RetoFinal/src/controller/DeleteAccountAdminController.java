package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.stage.Stage;
import model.Profile;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class for deleting user accounts as an Admin.
 */
public class DeleteAccountAdminController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private ComboBox<String> ComboBoxUser; // ComboBox with all users

    @FXML
    private TextField TextFieldPassword; // Password field for confirmation

    private Controller cont; // Controller to handle business logic
    private Profile profile; // Currently logged-in admin

    @FXML
    private Button Button_Cancel; // Button to cancel the action
    @FXML
    private Button Button_Delete; // Button to delete selected user
    
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
            
            FileHandler fileHandler = new FileHandler("logs/DeleteAccountAdmin.log", true);
            
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

    // Set the controller instance
    public void setCont(Controller cont) {
        logger.info("Setting controller in DeleteAccountAdminController");
        this.cont = cont;
    }

    // Set the current admin profile
    public void setProfile(Profile profile) {
        logger.info("Setting admin profile: " + (profile != null ? profile.getUsername() : "null"));
        this.profile = profile;
    }

    // Populate the ComboBox with users from the controller
    public void setComboBoxUser() {
        logger.info("Populating ComboBox with users");
        
        try {
            List<String> users = cont.comboBoxInsert();
            int userCount = users != null ? users.size() : 0;
            
            ComboBoxUser.getItems().clear();
            ComboBoxUser.getItems().addAll(users);
            
            logger.info("ComboBox populated with " + userCount + " users");
            
        } catch (Exception e) {
            logger.severe("Error populating ComboBox with users: " + e.getMessage());
        }
    }

    // Cancel button action: returns to MenuWindow
    @FXML
    private void cancel() {
        logger.info("Cancel button clicked - Returning to MenuWindow");
        
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            controller.MenuWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUsuario(profile);
            controllerWindow.setCont(this.cont);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
            logger.info("MenuWindow opened successfully for admin: " + 
                       (profile != null ? profile.getUsername() : "unknown"));

            // Close current window
            Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
            currentStage.close();
            
            logger.info("DeleteAccountAdmin window closed");

        } catch (IOException ex) {
            logger.severe("Error navigating to MenuWindow from cancel: " + ex.getMessage());
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Delete button action: deletes the selected user
    @FXML
    private void delete() {
        logger.info("Delete button clicked - Starting account deletion process");
        
        // Validación de contraseña
        if (TextFieldPassword.getText().isEmpty()) {
            logger.warning("Delete attempted without password - Admin: " + 
                          (profile != null ? profile.getUsername() : "unknown"));
            
            javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Password required");
            error.setContentText("Please enter your password to delete the account.");
            error.showAndWait();
            return;
        }

        // Validación de usuario seleccionado
        if (ComboBoxUser.getValue() == null || ComboBoxUser.getValue().isEmpty()) {
            logger.warning("Delete attempted without selecting a user - Admin: " + 
                          (profile != null ? profile.getUsername() : "unknown"));
            
            javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("User not selected");
            error.setContentText("Please select a user to delete.");
            error.showAndWait();
            return;
        }

        // Registrar intento de eliminación (sin contraseña completa por seguridad)
        String userToDelete = ComboBoxUser.getValue();
        String adminUsername = profile != null ? profile.getUsername() : "unknown";
        
        logger.info("Admin " + adminUsername + " attempting to delete user: " + userToDelete);
        logger.info("Password provided: [PROTECTED], Length: " + TextFieldPassword.getText().length());

        // Confirmación de eliminación
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText("Are you sure you want to delete this account?");
        alert.setContentText("This action cannot be undone.");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            logger.info("Admin confirmed deletion of user: " + userToDelete);
            
            try {
                String adminPassword = TextFieldPassword.getText();

                logger.info("Calling dropOutAdmin - Admin: " + adminUsername + ", Target user: " + userToDelete);
                Boolean success = cont.dropOutAdmin(userToDelete, adminUsername, adminPassword);
                
                if (success) {
                    logger.info("Account deletion SUCCESSFUL - Admin: " + adminUsername + 
                               " deleted user: " + userToDelete);
                    
                    javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Deleted account");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("The account has been successfully deleted.");
                    successAlert.showAndWait();

                    try {
                        logger.info("Navigating back to MenuWindow after successful deletion");
                        
                        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                        javafx.scene.Parent root = fxmlLoader.load();

                        controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                        controllerWindow.setUsuario(profile);
                        controllerWindow.setCont(this.cont);
                        
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.show();
                        
                        Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
                        currentStage.close();
                        
                        logger.info("Successfully returned to MenuWindow after account deletion");

                    } catch (IOException ex) {
                        logger.severe("Error navigating to MenuWindow after deletion: " + ex.getMessage());
                        Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    logger.warning("Account deletion FAILED - Incorrect password for admin: " + adminUsername);
                    
                    javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Incorrect password");
                    error.setContentText("The password is incorrect. Please try again.");
                    error.showAndWait();
                }

            } catch (Exception ex) {
                logger.severe("Exception during account deletion - Admin: " + adminUsername + 
                            ", Target: " + userToDelete + ", Error: " + ex.getMessage());
                
                ex.printStackTrace();
                javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("The account could not be deleted.");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        } else {
            logger.info("Account deletion CANCELLED by admin: " + adminUsername);
            System.out.println("Deletion cancelled by the user.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing DeleteAccountAdminController");
        
        try {
            // Initialization logic can be added here if needed
            logger.info("DeleteAccountAdminController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing DeleteAccountAdminController: " + e.getMessage());
        }
    }
}