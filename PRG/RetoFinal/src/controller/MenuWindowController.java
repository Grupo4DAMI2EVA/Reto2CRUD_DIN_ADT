package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Admin;
import model.Profile;
import model.User;

public class MenuWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(MenuWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonModify;
    @FXML
    private Button buttonLogOut;
    @FXML
    private Label labelUsername;

    private Profile profile;
    private Controller cont;

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
            
            Handler fileHandler = new FileHandler("logs/menu_window.log", true);
            
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
            logger.info("MenuWindowController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setUser(Profile profile) {
        try {
            this.profile = profile;
            if (labelUsername != null) {
                labelUsername.setText(profile.getUsername());
            }
            logger.info("User profile set: " + (profile != null ? profile.getUsername() : "null") + 
                       ", Type: " + (profile != null ? profile.getClass().getSimpleName() : "null"));
        } catch (Exception e) {
            logger.severe("Error setting user profile: " + e.getMessage());
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for MenuWindowController");
        } catch (Exception e) {
            logger.severe("Error setting main controller: " + e.getMessage());
        }
    }

    public Controller getController() {
        return cont;
    }

    @FXML
    private void modifyWindow(ActionEvent event) {
        try {
            logger.info("Opening Modify window for user: " + (profile != null ? profile.getUsername() : "null"));
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
            Parent root = fxmlLoader.load();

            ModifyWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setProfile(profile);
            controllerWindow.setController(this.cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) buttonModify.getScene().getWindow();
            currentStage.close();
            
            logger.info("Modify window opened successfully");
            
        } catch (Exception e) {
            logger.severe("Error opening Modify window: " + e.getMessage());
            showAlert("Error", "Could not open the Modify Account window.");
        }
    }

    @FXML
    private void delete() {
        try {
            logger.info("Opening Delete Account window");
            
            if (profile instanceof User) {
                logger.info("Opening User Delete Account window for: " + profile.getUsername());
                
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccount.fxml"));
                Parent root = fxmlLoader.load();
                DeleteAccountController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setController(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                
                Stage currentStage = (Stage) buttonDelete.getScene().getWindow();
                currentStage.close();
                
                logger.info("User Delete Account window opened successfully");
                
            } else if (profile instanceof Admin) {
                logger.info("Opening Admin Delete Account window for admin: " + profile.getUsername());
                
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccountAdmin.fxml"));
                Parent root = fxmlLoader.load();
                DeleteAccountAdminController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setController(cont);
                controllerWindow.setComboBoxUser();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                
                Stage currentStage = (Stage) buttonDelete.getScene().getWindow();
                currentStage.close();
                
                logger.info("Admin Delete Account window opened successfully");
            } else {
                logger.severe("Unknown profile type for delete operation");
                showAlert("Error", "Unknown user type. Cannot open delete window.");
            }
            
        } catch (Exception e) {
            logger.severe("Error opening Delete Account window: " + e.getMessage());
            showAlert("Error", "Could not open the Delete Account window.");
        }
    }

    @FXML
    private void logOut(ActionEvent event) {
        try {
            String username = profile != null ? profile.getUsername() : "unknown";
            logger.info("User logging out: " + username);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
            logger.info("Logout completed for user: " + username);
            
        } catch (Exception e) {
            logger.severe("Error during logout: " + e.getMessage());
            showAlert("Error", "Could not complete logout.");
        }
    }

    @FXML
    private void openHelp(ActionEvent event) {
        try {
            logger.info("Opening Help window for user: " + (profile != null ? profile.getUsername() : "null"));
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/HelpWindow.fxml"));
            Parent root = fxmlLoader.load();

            HelpWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUser(profile);
            controllerWindow.setController(cont);

            Stage stage = new Stage();
            stage.setTitle("Help");
            stage.setScene(new Scene(root));
            stage.show();
            
            logger.info("Help window opened successfully");
            
        } catch (Exception e) {
            logger.severe("Error opening Help window: " + e.getMessage());
            showAlert("Error", "Could not open the Help window.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing MenuWindowController");
            
            // Set up any initial UI state
            if (labelUsername != null && profile != null) {
                labelUsername.setText(profile.getUsername());
            }
            
            logger.info("MenuWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing MenuWindowController: " + e.getMessage());
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

    // Method to refresh user information display
    public void refreshUserInfo() {
        try {
            logger.info("Refreshing user information display");
            
            if (profile != null && labelUsername != null) {
                labelUsername.setText(profile.getUsername());
                logger.info("Username display refreshed: " + profile.getUsername());
            }
            
        } catch (Exception e) {
            logger.severe("Error refreshing user information: " + e.getMessage());
        }
    }

    // Method to update the profile (e.g., after modification)
    public void updateProfile(Profile updatedProfile) {
        try {
            logger.info("Updating user profile in MenuWindowController");
            
            this.profile = updatedProfile;
            refreshUserInfo();
            
            logger.info("User profile updated successfully");
            
        } catch (Exception e) {
            logger.severe("Error updating user profile: " + e.getMessage());
        }
    }

    void setUsuario(Profile profile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setCont(Controller cont) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}