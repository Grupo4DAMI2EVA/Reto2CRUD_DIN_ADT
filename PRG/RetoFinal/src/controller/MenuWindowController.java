package controller;

import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Admin;
import model.Profile;
import model.User;

/**
 * Controller for the main Menu window. Handles navigation to modify, delete, and logout actions.
 */
public class MenuWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Button Button_Delete;

    @FXML
    private Button Button_Modify;

    @FXML
    private Button Button_LogOut;

    @FXML
    private Label label_Username;

    private Profile profile;
    private Controller cont;
    @FXML
    private Button Button_Store;
    
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
            
            FileHandler fileHandler = new FileHandler("logs/MenuWindow.log", true);
            
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

    public void setUsuario(Profile profile) {
        logger.info("Setting user profile in MenuWindowController: " + 
                   (profile != null ? profile.getUsername() + 
                    " (Type: " + (profile instanceof Admin ? "Admin" : "User") + ")" : "null"));
        this.profile = profile;
        label_Username.setText(profile.getUsername());
        logger.info("Username label updated: " + profile.getUsername());
    }

    public void setCont(Controller cont) {
        logger.info("Setting controller in MenuWindowController");
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    /**
     * Opens the Modify window.
     */
    @FXML
    private void modifyVentana(ActionEvent event) {
        logger.info("Modify button clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown"));
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            controller.ModifyWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setProfile(profile);
            controllerWindow.setCont(this.cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            
            logger.info("ModifyWindow opened successfully for user: " + profile.getUsername());

            Stage currentStage = (Stage) Button_Modify.getScene().getWindow();
            currentStage.close();
            
            logger.info("MenuWindow closed (navigating to ModifyWindow)");

        } catch (IOException ex) {
            logger.severe("Error opening ModifyWindow: " + ex.getMessage());
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens the Delete Account window depending on profile type. Users open DeleteAccount; Admins open DeleteAccountAdmin.
     */
    @FXML
    private void delete() {
        String userType = profile instanceof Admin ? "Admin" : "User";
        logger.info("Delete button clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown") + 
                   ", Type: " + userType);
        
        try {
            FXMLLoader fxmlLoader;
            if (profile instanceof User) {
                logger.info("Opening user self-delete window for: " + profile.getUsername());
                
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccount.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                controller.DeleteAccountController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                
                logger.info("DeleteAccount window opened successfully for user: " + profile.getUsername());

                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();
                
                logger.info("MenuWindow closed (navigating to user DeleteAccount)");

            } else if (profile instanceof Admin) {
                logger.info("Opening admin delete user window for admin: " + profile.getUsername());
                
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccountAdmin.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                controller.DeleteAccountAdminController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);
                controllerWindow.setComboBoxUser();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                
                logger.info("DeleteAccountAdmin window opened successfully for admin: " + profile.getUsername());

                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();
                
                logger.info("MenuWindow closed (navigating to admin DeleteAccountAdmin)");
            }
        } catch (IOException ex) {
            logger.severe("Error opening Delete window: " + ex.getMessage());
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Closes the current window (used for logout).
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        logger.info("LogOut button clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown"));
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        
        logger.info("MenuWindow closed (user logged out)");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing MenuWindowController");
        
        try {
            // Initialization logic if needed
            logger.info("MenuWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing MenuWindowController: " + e.getMessage());
        }
    }

    /**
     * Opens the Shop/Store window. Users go to shopWindow, Admins go to AdminWindow
     */
    @FXML
    private void StoreWindow(ActionEvent event) {
        String userType = profile instanceof Admin ? "Admin" : "User";
        logger.info("Store button clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown") + 
                   ", Type: " + userType + ", Opening " + (profile instanceof Admin ? "Admin" : "User") + " store");
        
        try {
            if (profile instanceof Admin) {
                // ADMIN: Va a la ventana de administrador de tienda
                logger.info("Opening AdminShopController for admin: " + profile.getUsername());
                
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
                
                logger.info("AdminShopController window opened successfully");

            } else {
                // USER: Va a la ventana normal de tienda
                logger.info("Opening ShopWindowController for user: " + profile.getUsername());
                
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
                
                logger.info("ShopWindowController window opened successfully");
            }

            // Cerrar la ventana actual del menú
            Stage currentStage = (Stage) Button_Store.getScene().getWindow();
            currentStage.close();
            
            logger.info("MenuWindow closed (navigating to store)");

        } catch (IOException ex) {
            logger.severe("Error opening store window: " + ex.getMessage());
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, "Error al abrir la ventana de tienda", ex);
        }
    }
}