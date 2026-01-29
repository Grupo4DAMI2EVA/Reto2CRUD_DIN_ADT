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
    @FXML
    private Button buttonStore;  // Añadido para el botón Store

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
            
            FileHandler fileHandler = new FileHandler("logs/menu_window.log", true);
            
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
            logger.info(String.format("User profile set: %s, Type: %s", 
                       profile != null ? profile.getUsername() : "null",
                       profile != null ? profile.getClass().getSimpleName() : "null"));
        } catch (Exception e) {
            logger.severe(String.format("Error setting user profile: %s", e.getMessage()));
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for MenuWindowController");
        } catch (Exception e) {
            logger.severe(String.format("Error setting main controller: %s", e.getMessage()));
        }
    }

    public Controller getController() {
        return cont;
    }

    @FXML
    private void modifyWindow(ActionEvent event) {
        try {
            logger.info(String.format("Opening Modify window for user: %s", 
                       profile != null ? profile.getUsername() : "null"));
            
            handleModifyWindow();
            
        } catch (Exception e) {
            logger.severe(String.format("Error opening Modify window: %s", e.getMessage()));
            showAlert("Error", "Could not open the Modify Account window.");
        }
    }

    private void handleModifyWindow() throws Exception {
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
    }

    @FXML
    private void delete() {
        try {
            logger.info("Opening Delete Account window");
            
            if (profile instanceof User) {
                handleUserDeleteWindow();
            } else if (profile instanceof Admin) {
                handleAdminDeleteWindow();
            } else {
                handleUnknownProfileType();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error opening Delete Account window: %s", e.getMessage()));
            showAlert("Error", "Could not open the Delete Account window.");
        }
    }

    private void handleUserDeleteWindow() throws Exception {
        logger.info(String.format("Opening User Delete Account window for: %s", profile.getUsername()));
        
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
    }

    private void handleAdminDeleteWindow() throws Exception {
        logger.info(String.format("Opening Admin Delete Account window for admin: %s", profile.getUsername()));
        
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
    }

    private void handleUnknownProfileType() {
        logger.severe("Unknown profile type for delete operation");
        showAlert("Error", "Unknown user type. Cannot open delete window.");
    }

    @FXML
    private void logOut(ActionEvent event) {
        try {
            String username = profile != null ? profile.getUsername() : "unknown";
            logger.info(String.format("User logging out: %s", username));
            
            handleLogout(event);
            
        } catch (Exception e) {
            logger.severe(String.format("Error during logout: %s", e.getMessage()));
            showAlert("Error", "Could not complete logout.");
        }
    }

    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        
        String username = profile != null ? profile.getUsername() : "unknown";
        logger.info(String.format("Logout completed for user: %s", username));
    }

    @FXML
    private void openHelp(ActionEvent event) {
        try {
            logger.info(String.format("Opening Help window for user: %s", 
                       profile != null ? profile.getUsername() : "null"));
            
            handleHelpWindow();
            
        } catch (Exception e) {
            logger.severe(String.format("Error opening Help window: %s", e.getMessage()));
            showAlert("Error", "Could not open the Help window.");
        }
    }

    private void handleHelpWindow() throws Exception {
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
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing MenuWindowController");
            
            initializeUI();
            
            logger.info("MenuWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing MenuWindowController: %s", e.getMessage()));
        }
    }

    private void initializeUI() {
        if (labelUsername != null && profile != null) {
            labelUsername.setText(profile.getUsername());
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

    public void refreshUserInfo() {
        try {
            logger.info("Refreshing user information display");
            
            if (profile != null && labelUsername != null) {
                labelUsername.setText(profile.getUsername());
                logger.info(String.format("Username display refreshed: %s", profile.getUsername()));
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error refreshing user information: %s", e.getMessage()));
        }
    }

    public void updateProfile(Profile updatedProfile) {
        try {
            logger.info("Updating user profile in MenuWindowController");
            
            this.profile = updatedProfile;
            refreshUserInfo();
            
            logger.info("User profile updated successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error updating user profile: %s", e.getMessage()));
        }
    }

    public void setUsuario(Profile profile) {
        try {
            this.profile = profile;
            logger.info(String.format("Profile set via setUsuario: %s", 
                       profile != null ? profile.getUsername() : "null"));
        } catch (Exception e) {
            logger.severe(String.format("Error in setUsuario: %s", e.getMessage()));
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

    /**
     * Opens the Shop/Store window. Users go to ShopWindow, Admins go to AdminWindow
     */
    @FXML
    private void storeWindow(ActionEvent event) {
        try {
            logger.info(String.format("Opening Store window for user: %s", 
                       profile != null ? profile.getUsername() : "null"));
            
            if (profile instanceof Admin) {
                // ADMIN: Va a la ventana de administrador de tienda
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/StoreAdminWindow.fxml"));
                Parent root = fxmlLoader.load();

                AdminShopController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                // Crear y mostrar la nueva ventana
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Admin Game Store");
                stage.show();
            } else {
                // USER: Va a la ventana normal de tienda
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/StoreWindow.fxml"));
                Parent root = fxmlLoader.load();

                ShopWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                // Crear y mostrar la nueva ventana
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Game Store");
                stage.show();
            }

            // Cerrar la ventana actual del menú
            Stage currentStage = (Stage) buttonStore.getScene().getWindow();
            currentStage.close();

            logger.info("Store window opened successfully");

        } catch (Exception ex) {
            logger.severe(String.format("Error opening Store window: %s", ex.getMessage()));
            showAlert("Error", "Could not open the Store window.");
        }
    }
}