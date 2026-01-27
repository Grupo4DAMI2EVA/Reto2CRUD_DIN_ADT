package controller;

import exception.passwordequalspassword;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Profile;
import model.User;

/**
 * FXML Controller class for modifying a user's profile.
 */
public class ModifyWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ModifyWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Label LabelUsername;
    @FXML
    private Label LabelEmail;
    @FXML
    private TextField TextField_Name;
    @FXML
    private TextField TextField_Surname;
    @FXML
    private TextField TextField_Telephone;
    @FXML
    private TextField TextField_NewPass;
    @FXML
    private TextField TextField_CNewPass;
    @FXML
    private Button Button_Cancel;
    @FXML
    private Button Button_SaveChanges;

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
            
            FileHandler fileHandler = new FileHandler("logs/modify_window.log", true);
            
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
            logger.info("ModifyWindowController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setCont(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Controller set in ModifyWindowController");
        } catch (Exception e) {
            logger.severe(String.format("Error setting controller: %s", e.getMessage()));
        }
    }

    public void setProfile(Profile profile) {
        try {
            this.profile = profile;
            LabelUsername.setText(profile.getUsername());
            LabelEmail.setText(profile.getEmail());
            
            logger.info(String.format("Profile set - Username: %s, Email: %s", 
                       profile.getUsername(), profile.getEmail()));
            
        } catch (Exception e) {
            logger.severe(String.format("Error setting profile: %s", e.getMessage()));
        }
    }

    @FXML
    private void save(ActionEvent event) throws passwordequalspassword {
        try {
            logger.info("Starting user data update process");
            
            String name = TextField_Name.getText();
            String surname = TextField_Surname.getText();
            String telephone = TextField_Telephone.getText();
            String newPass = TextField_NewPass.getText();
            String cNewPass = TextField_CNewPass.getText();
            String gender = "";
            String username;
            String email;

            if (profile instanceof User) {
                gender = ((User) profile).getGender();
            }

            username = profile.getUsername();
            email = profile.getEmail();

            logger.info(String.format("Form data collected - Name: %s, Surname: %s, Telephone: %s", 
                       name, surname, telephone));

            if (name == null || name.isEmpty() || name.equals("Insert your new name")) {
                name = profile.getName();
                logger.info("Using existing name from profile");
            }
            if (surname == null || surname.isEmpty() || surname.equals("Insert your new surname")) {
                surname = profile.getSurname();
                logger.info("Using existing surname from profile");
            }
            if (telephone == null || telephone.isEmpty() || telephone.equals("Insert your new telephone")) {
                telephone = profile.getTelephone();
                logger.info("Using existing telephone from profile");
            }
            
            boolean isPasswordEmpty = newPass == null || newPass.isEmpty() || cNewPass == null || cNewPass.isEmpty()
                || newPass.equals("New Password") || cNewPass.equals("Confirm New Password");
                
            if (isPasswordEmpty) {
                handleUpdateWithoutPasswordChange(newPass, email, name, telephone, surname, username, gender);
            } else {
                handleUpdateWithPasswordChange(newPass, cNewPass, email, name, telephone, surname, username, gender);
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in save method: %s", e.getMessage()));
            showAlert("Error", "An unexpected error occurred while updating user data.");
        }
    }

    private void handleUpdateWithoutPasswordChange(String newPass, String email, String name, 
                                                  String telephone, String surname, 
                                                  String username, String gender) {
        try {
            logger.info("Attempting update without password change");
            
            newPass = profile.getPassword();
            Boolean success = cont.modificarUser(newPass, email, name, telephone, surname, username, gender);
            
            if (success) {
                handleSuccessfulUpdate(name, surname, telephone, newPass);
            } else {
                handleFailedUpdate();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in update without password change: %s", e.getMessage()));
            throw e;
        }
    }

    private void handleUpdateWithPasswordChange(String newPass, String cNewPass, String email, 
                                               String name, String telephone, String surname, 
                                               String username, String gender) throws passwordequalspassword {
        try {
            logger.info("Attempting update with password change");
            
            if (!newPass.equals(cNewPass)) {
                logger.warning("Password mismatch detected");
                throw new passwordequalspassword("Las contraseñas no coinciden");
            } else {
                Boolean success = cont.modificarUser(newPass, email, name, telephone, surname, username, gender);
                
                if (success) {
                    handleSuccessfulUpdate(name, surname, telephone, newPass);
                } else {
                    handleFailedUpdate();
                }
            }
            
        } catch (passwordequalspassword e) {
            logger.severe("Password validation failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe(String.format("Error in update with password change: %s", e.getMessage()));
            throw e;
        }
    }

    private void handleSuccessfulUpdate(String name, String surname, String telephone, String newPass) {
        try {
            profile.setName(name);
            profile.setSurname(surname);
            profile.setTelephone(telephone);
            profile.setPassword(newPass);

            logger.info(String.format("Profile updated successfully - Name: %s, Surname: %s, Telephone: %s", 
                       name, surname, telephone));
            
            showSuccessAlert("User data has been successfully updated.");
            navigateToMenuWindow();
            
        } catch (Exception e) {
            logger.severe(String.format("Error in successful update handler: %s", e.getMessage()));
            throw e;
        }
    }

    private void handleFailedUpdate() {
        logger.warning("Update failed in controller.modificarUser()");
        showErrorAlert("Update failed", "Could not update user data.");
    }

    private void showSuccessAlert(String message) {
        javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        successAlert.setContentText(message);
        successAlert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert error = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR);
        error.setTitle(title);
        error.setHeaderText(title);
        error.setContentText(message);
        error.showAndWait();
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToMenuWindow() {
        try {
            logger.info("Navigating to MenuWindow");
            
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/MenuWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            MenuWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUsuario(profile);
            controllerWindow.setCont(this.cont);
            
            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
            Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
            currentStage.close();
            
            logger.info("Navigation to MenuWindow completed");
            
        } catch (IOException ex) {
            logger.severe(String.format("Error loading MenuWindow: %s", ex.getMessage()));
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancel() {
        try {
            logger.info("Cancel button pressed, returning to MenuWindow");
            
            navigateToMenuWindow();
            
        } catch (Exception e) {
            logger.severe(String.format("Error in cancel method: %s", e.getMessage()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("ModifyWindowController initialized");
            
        } catch (Exception e) {
            logger.severe(String.format("Error in initialize: %s", e.getMessage()));
        }
    }

    void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Controller set via setController method");
            
        } catch (Exception e) {
            logger.severe(String.format("Error in setController: %s", e.getMessage()));
        }
    }
}