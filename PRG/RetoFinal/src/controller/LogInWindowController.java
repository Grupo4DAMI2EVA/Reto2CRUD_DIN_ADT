package controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Profile;

/**
 * Controller for the Login window. Handles user login and navigation to the main menu or signup window.
 */
public class LogInWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private TextField TextField_Username;

    @FXML
    private PasswordField PasswordField_Password;

    @FXML
    private Button Button_LogIn;

    @FXML
    private Button Button_SignUp;

    @FXML
    private Label labelIncorrecto; // Label to show error messages

    // Controller handling business logic
    private Controller cont = new Controller();
    
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
     * Opens the SignUp window.
     */
    @FXML
    private void signUp() {
        logger.info("SignUp button clicked - Navigating to SignUp window");
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("SignUp");
            stage.setScene(new Scene(root));
            stage.show();

            controller.SignUpWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setCont(cont);
            
            logger.info("SignUp window opened successfully");

            // Close current window
            Stage currentStage = (Stage) Button_SignUp.getScene().getWindow();
            currentStage.close();
            
            logger.info("Login window closed");
            
        } catch (IOException ex) {
            logger.severe("Error opening SignUp window: " + ex.getMessage());
            Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Attempts to log in the user. If successful, opens MenuWindow; otherwise, shows an error.
     */
    @FXML
    private void logIn() {
        logger.info("LogIn button clicked - Attempting user authentication");
        
        String username = TextField_Username.getText();
        String password = PasswordField_Password.getText();
        
        // Log seguro (no mostrar contraseña completa)
        logger.info("Login attempt - Username: " + username + 
                   ", Password length: " + (password != null ? password.length() : 0) + " characters");
        
        if (username.equals("") || password.equals("")) {
            logger.warning("Login failed - Empty username or password field");
            labelIncorrecto.setText("Please fill in both fields.");
        } else {
            logger.info("Attempting authentication for user: " + username);
            Profile profile = cont.logIn(username, password);
            
            if (profile != null) {
                logger.info("Login successful for user: " + username + " (ID: " + profile.getUserCode() + ")");
                
                try {
                    logger.info("Navigating to MenuWindow for user: " + username);
                    
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                    Parent root = fxmlLoader.load();

                    controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.setUsuario(profile);
                    controllerWindow.setCont(cont);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                    
                    logger.info("MenuWindow opened successfully for user: " + username);

                    Stage currentStage = (Stage) Button_LogIn.getScene().getWindow();
                    currentStage.close();
                    
                    logger.info("Login window closed after successful login");

                } catch (IOException ex) {
                    logger.severe("Error opening MenuWindow after successful login: " + ex.getMessage());
                    Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                logger.warning("Login failed - Invalid credentials for username: " + username);
                labelIncorrecto.setText("The username and/or password are incorrect.");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing LogInWindowController");
        
        try {
            // Initialization logic if needed
            logger.info("LogInWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing LogInWindowController: " + e.getMessage());
        }
    }
}