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
            
            Handler fileHandler = new FileHandler("logs/admin_delete_account.log", true);
            
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
            logger.severe("Error setting main controller: " + e.getMessage());
        }
    }

    public void setProfile(Profile profile) {
        try {
            this.profile = profile;
            logger.info("Admin profile set: " + (profile != null ? profile.getUsername() : "null"));
        } catch (Exception e) {
            logger.severe("Error setting admin profile: " + e.getMessage());
        }
    }

    public void setComboBoxUser() {
        try {
            List<String> users = cont.comboBoxInsert();
            comboBoxUser.getItems().clear();
            comboBoxUser.getItems().addAll(users);
            
            logger.info("ComboBox populated with " + users.size() + " users");
            
        } catch (Exception e) {
            logger.severe("Error populating ComboBox with users: " + e.getMessage());
            showAlert("Error", "Could not load user list.");
        }
    }

    @FXML
    private void cancel() {
        try {
            logger.info("Cancelling account deletion - returning to menu");
            
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
            
        } catch (Exception e) {
            logger.severe("Error returning to menu: " + e.getMessage());
            showAlert("Error", "Could not return to the menu.");
        }
    }

    @FXML
    private void delete() {
        try {
            logger.info("Starting account deletion process");
            
            if (textFieldPassword.getText().isEmpty()) {
                logger.severe("Delete attempt without entering password");
                
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Password Required");
                error.setContentText("Please enter your password to delete the account.");
                error.showAndWait();
                return;
            }

            if (comboBoxUser.getValue() == null || comboBoxUser.getValue().isEmpty()) {
                logger.severe("Delete attempt without selecting user");
                
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("User Not Selected");
                error.setContentText("Please select a user to delete.");
                error.showAndWait();
                return;
            }

            String userToDelete = comboBoxUser.getValue();
            logger.info("Attempting to delete user: " + userToDelete);
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Account");
            alert.setHeaderText("Are you sure you want to delete this account?");
            alert.setContentText("This action cannot be undone.");

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                
                String adminPassword = textFieldPassword.getText();
                String adminUsername = profile.getUsername();
                
                logger.info("Admin " + adminUsername + " attempting to delete user: " + userToDelete);

                Boolean success = cont.dropOutAdmin(userToDelete, adminUsername, adminPassword);
                if (success) {
                    logger.info("User account deleted successfully: " + userToDelete);
                    
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Account Deleted");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("The account has been successfully deleted.");
                    successAlert.showAndWait();

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
                        logger.severe("Error returning to menu after deletion: " + e.getMessage());
                        showAlert("Error", "Account deleted but could not return to menu.");
                    }
                    
                } else {
                    logger.severe("Failed to delete user: Incorrect admin password");
                    
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Incorrect Password");
                    error.setContentText("The password is incorrect. Please try again.");
                    error.showAndWait();
                }

            } else {
                logger.info("User cancelled account deletion");
                System.out.println("Deletion cancelled by the user.");
            }
            
        } catch (Exception e) {
            logger.severe("Error in delete account process: " + e.getMessage());
            
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
            logger.info("Initializing DeleteAccountAdminController");
            
            // Clear any existing data
            comboBoxUser.getItems().clear();
            textFieldPassword.clear();
            
            logger.info("DeleteAccountAdminController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing DeleteAccountAdminController: " + e.getMessage());
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

    // Method to refresh the user list
    public void refreshUserList() {
        try {
            logger.info("Refreshing user list in ComboBox");
            
            List<String> users = cont.comboBoxInsert();
            String currentSelection = comboBoxUser.getValue();
            
            comboBoxUser.getItems().clear();
            comboBoxUser.getItems().addAll(users);
            
            // Try to restore previous selection if it still exists
            if (currentSelection != null && users.contains(currentSelection)) {
                comboBoxUser.setValue(currentSelection);
            }
            
            logger.info("User list refreshed - " + users.size() + " users available");
            
        } catch (Exception e) {
            logger.severe("Error refreshing user list: " + e.getMessage());
            showAlert("Error", "Could not refresh the user list.");
        }
    }

    // Method to clear form
    public void clearForm() {
        try {
            logger.info("Clearing account deletion form");
            
            textFieldPassword.clear();
            comboBoxUser.setValue(null);
            
            logger.info("Form cleared successfully");
            
        } catch (Exception e) {
            logger.severe("Error clearing form: " + e.getMessage());
        }
    }

    void setCont(Controller cont) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}