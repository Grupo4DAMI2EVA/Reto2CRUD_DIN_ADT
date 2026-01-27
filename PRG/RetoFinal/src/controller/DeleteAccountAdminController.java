package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Profile;

public class DeleteAccountAdminController implements Initializable {

    private static final Logger logger = Logger.getLogger(DeleteAccountAdminController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private ComboBox<String> comboBoxUser;
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
            
            FileHandler fileHandler = new FileHandler("logs/admin_delete_account.log", true);
            
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
            logger.info("DeleteAccountAdminController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for DeleteAccountAdminController");
        } catch (Exception e) {
            logger.severe(String.format("Error setting main controller: %s", e.getMessage()));
        }
    }

    public void setProfile(Profile profile) {
        try {
            this.profile = profile;
            logger.info(String.format("Admin profile set: %s", 
                       profile != null ? profile.getUsername() : "null"));
        } catch (Exception e) {
            logger.severe(String.format("Error setting admin profile: %s", e.getMessage()));
        }
    }

    public void setComboBoxUser() {
        try {
            List<String> users = cont.comboBoxInsert();
            comboBoxUser.getItems().clear();
            comboBoxUser.getItems().addAll(users);
            
            logger.info(String.format("ComboBox populated with %d users", users.size()));
            
        } catch (Exception e) {
            logger.severe(String.format("Error populating ComboBox with users: %s", e.getMessage()));
            showAlert("Error", "Could not load user list.");
        }
    }

    @FXML
    private void cancel() {
        try {
            logger.info("Cancelling account deletion - returning to menu");
            
            navigateToMenuWindow();
            
        } catch (Exception e) {
            logger.severe(String.format("Error returning to menu: %s", e.getMessage()));
            showAlert("Error", "Could not return to the menu.");
        }
    }

    @FXML
    private void delete() {
        try {
            logger.info("Starting account deletion process");
            
            if (!validateInputs()) {
                return;
            }

            String userToDelete = comboBoxUser.getValue();
            logger.info(String.format("Attempting to delete user: %s", userToDelete));
            
            if (confirmDeletion()) {
                processAccountDeletion(userToDelete);
            } else {
                handleDeletionCancellation();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in delete account process: %s", e.getMessage()));
            showErrorAlert("Account Could Not Be Deleted", 
                String.format("An unexpected error occurred: %s", e.getMessage()));
        }
    }

    private boolean validateInputs() {
        if (textFieldPassword.getText().isEmpty()) {
            logger.severe("Delete attempt without entering password");
            showErrorAlert("Password Required", 
                "Please enter your password to delete the account.");
            return false;
        }

        if (comboBoxUser.getValue() == null || comboBoxUser.getValue().isEmpty()) {
            logger.severe("Delete attempt without selecting user");
            showErrorAlert("User Not Selected", 
                "Please select a user to delete.");
            return false;
        }

        return true;
    }

    private boolean confirmDeletion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete this account?");
        alert.setContentText("This action cannot be undone.");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }

    private void processAccountDeletion(String userToDelete) {
        String adminPassword = textFieldPassword.getText();
        String adminUsername = profile.getUsername();
        
        logger.info(String.format("Admin %s attempting to delete user: %s", adminUsername, userToDelete));

        Boolean success = cont.dropOutAdmin(userToDelete, adminUsername, adminPassword);
        if (success) {
            handleSuccessfulDeletion(userToDelete);
        } else {
            handleFailedDeletion();
        }
    }

    private void handleSuccessfulDeletion(String userToDelete) {
        logger.info(String.format("User account deleted successfully: %s", userToDelete));
        
        showSuccessAlert("Account Deleted", 
            "The account has been successfully deleted.");
        
        navigateToMenuWindowAfterDeletion();
    }

    private void handleFailedDeletion() {
        logger.severe("Failed to delete user: Incorrect admin password");
        showErrorAlert("Incorrect Password", 
            "The password is incorrect. Please try again.");
    }

    private void handleDeletionCancellation() {
        logger.info("User cancelled account deletion");
        System.out.println("Deletion cancelled by the user.");
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
        
        logger.info("Returned to menu successfully");
    }

    private void navigateToMenuWindowAfterDeletion() {
        try {
            logger.info("Returning to menu after successful deletion");
            
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
            logger.severe(String.format("Error returning to menu after deletion: %s", e.getMessage()));
            showAlert("Error", "Account deleted but could not return to menu.");
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
            logger.info("Initializing DeleteAccountAdminController");
            
            clearFormData();
            
            logger.info("DeleteAccountAdminController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing DeleteAccountAdminController: %s", e.getMessage()));
        }
    }

    private void clearFormData() {
        comboBoxUser.getItems().clear();
        textFieldPassword.clear();
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

    public void refreshUserList() {
        try {
            logger.info("Refreshing user list in ComboBox");
            
            List<String> users = cont.comboBoxInsert();
            String currentSelection = comboBoxUser.getValue();
            
            comboBoxUser.getItems().clear();
            comboBoxUser.getItems().addAll(users);
            
            if (currentSelection != null && users.contains(currentSelection)) {
                comboBoxUser.setValue(currentSelection);
            }
            
            logger.info(String.format("User list refreshed - %d users available", users.size()));
            
        } catch (Exception e) {
            logger.severe(String.format("Error refreshing user list: %s", e.getMessage()));
            showAlert("Error", "Could not refresh the user list.");
        }
    }

    public void clearForm() {
        try {
            logger.info("Clearing account deletion form");
            
            textFieldPassword.clear();
            comboBoxUser.setValue(null);
            
            logger.info("Form cleared successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error clearing form: %s", e.getMessage()));
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