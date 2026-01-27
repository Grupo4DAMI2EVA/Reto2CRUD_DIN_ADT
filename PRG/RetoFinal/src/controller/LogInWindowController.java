package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DBImplementation;
import model.Profile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;

public class LogInWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(LogInWindowController.class.getName());
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
    private Label labelIncorrecto;

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
            
            Handler fileHandler = new FileHandler("logs/login_fixed.log", true);
            
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
            logger.info("LogInWindowController logger initialized - USING ORIGINAL NAMES");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.toString());
            loggerInitialized = true;
        }
    }

    @FXML
    private void signUp() {
        try {
            logger.info("=== Opening Sign Up window ===");
            
            if (!validateSignUpFields()) {
                return;
            }
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
            Parent root = fxmlLoader.load();
            
            logger.info("FXML loaded successfully");
            
            SignUpWindowController controllerWindow = fxmlLoader.getController();
            logger.info(String.format("SignUpWindowController obtained: %s", 
                       controllerWindow == null ? "NULL" : controllerWindow.getClass().getName()));
            
            controllerWindow.setController(cont);
            
            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) Button_SignUp.getScene().getWindow();
            currentStage.close();
            
            logger.info("=== Sign Up window opened successfully ===");
            
        } catch (Exception e) {
            logger.severe(String.format("Error opening Sign Up window: %s", e.toString()));
            showAlert("Error", "Could not open the Sign Up window: " + e.getMessage());
        }
    }

    @FXML
private void logIn() {
    try {
        logger.info("=== Login attempt ===");
        
        String username = TextField_Username.getText();
        String password = PasswordField_Password.getText();
        
        logger.info(String.format("Credentials - Username: '%s', Password length: %d", username, password.length()));
        
        if (!validateLoginFields(username, password)) {
            return;
        }

        logger.info("Calling cont.logIn()...");
        Profile profile = cont.logIn(username, password);
        
        if (profile != null) {
            logger.info("=== LOGIN SUCCESSFUL ===");
            logger.info(String.format("User type: %s, Username: %s", 
                       profile.getClass().getSimpleName(), profile.getUsername()));
            
            // AÑADE ESTO PARA DEBUG
            logger.info("Attempting to open MenuWindow...");
            logger.info("FXML Path: /view/MenuWindow.fxml");
            
            handleSuccessfulLogin(profile, username);
        } else {
            handleFailedLogin(username);
        }
        
    } catch (Exception e) {
        logger.severe(String.format("Error in login process: %s", e.toString()));
        // AÑADE ESTO PARA VER EL STACK TRACE COMPLETO
        e.printStackTrace();
        showAlert("Login Error", "An error occurred during login: " + e.getMessage());
    }
}

    private boolean validateSignUpFields() {
        logger.info("Validating Sign Up fields");
        logger.info(String.format("Button_SignUp: %s", Button_SignUp == null ? "NULL" : "OK"));
        logger.info(String.format("TextField_Username: %s", TextField_Username == null ? "NULL" : "OK"));
        
        if (Button_SignUp == null) {
            logger.severe("Button_SignUp is null");
            showAlert("System Error", "Application not properly initialized");
            return false;
        }
        
        return true;
    }

    private boolean validateLoginFields(String username, String password) {
        if (TextField_Username == null || PasswordField_Password == null) {
            logger.severe("Login fields are null!");
            showAlert("System Error", "Application not properly initialized");
            return false;
        }
        
        if (username.isEmpty() || password.isEmpty()) {
            logger.warning("Empty fields detected");
            if (labelIncorrecto != null) {
                labelIncorrecto.setText("Please fill in both fields.");
            } else {
                showAlert("Input Error", "Please fill in both username and password fields.");
            }
            return false;
        }
        
        return true;
    }

    private void handleSuccessfulLogin(Profile profile, String username) {
    try {
        logger.info("Trying SIMPLIFIED version...");
        
        // Cargar FXML sin controlador primero
        Parent root = FXMLLoader.load(getClass().getResource("/view/MenuWindow.fxml"));
        
        // Crear ventana simple
        Stage stage = new Stage();
        stage.setTitle("Main Menu - " + username);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        
        // Cerrar login
        Stage currentStage = (Stage) Button_LogIn.getScene().getWindow();
        currentStage.close();
        
        logger.info("Simplified version worked!");
        
    } catch (Exception e) {
        logger.severe("Simplified version also failed: " + e.getMessage());
        e.printStackTrace();
        
        // Muestra una ventana de emergencia
        Label label = new Label("Login successful!\nUsername: " + username + "\n\nMenuWindow.fxml not loading properly.");
        Scene scene = new Scene(label, 300, 200);
        Stage emergencyStage = new Stage();
        emergencyStage.setScene(scene);
        emergencyStage.show();
    }
}

    private void handleFailedLogin(String username) {
        logger.warning("=== LOGIN FAILED - Invalid credentials ===");
        if (labelIncorrecto != null) {
            labelIncorrecto.setText("The username and/or password are incorrect.");
        } else {
            showAlert("Login Failed", "The username and/or password are incorrect.");
        }
        
        PasswordField_Password.clear();
        PasswordField_Password.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("=== Initializing LogInWindowController ===");
            
            logFieldInjectionStatus();
            clearFormOnInitialize();
            
            logger.info("=== LogInWindowController initialized successfully ===");
            
        } catch (Exception e) {
            logger.severe(String.format("Error in initialize: %s", e.toString()));
        }
    }

    private void logFieldInjectionStatus() {
        logger.info("FXML Field Injection Check:");
        logger.info(String.format("  TextField_Username: %s", TextField_Username == null ? "NULL" : "OK"));
        logger.info(String.format("  PasswordField_Password: %s", PasswordField_Password == null ? "NULL" : "OK"));
        logger.info(String.format("  Button_LogIn: %s", Button_LogIn == null ? "NULL" : "OK"));
        logger.info(String.format("  Button_SignUp: %s", Button_SignUp == null ? "NULL" : "OK"));
        logger.info(String.format("  labelIncorrecto: %s", labelIncorrecto == null ? "NULL" : "OK"));
    }

    private void clearFormOnInitialize() {
        if (TextField_Username != null) {
            TextField_Username.clear();
            TextField_Username.requestFocus();
        }
        if (PasswordField_Password != null) {
            PasswordField_Password.clear();
        }
        if (labelIncorrecto != null) {
            labelIncorrecto.setText("");
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
            logger.severe(String.format("Error showing alert: %s", e.toString()));
        }
    }

    public void clearForm() {
        try {
            logger.info("Clearing login form");
            
            if (TextField_Username != null) {
                TextField_Username.clear();
            }
            if (PasswordField_Password != null) {
                PasswordField_Password.clear();
            }
            if (labelIncorrecto != null) {
                labelIncorrecto.setText("");
            }
            if (TextField_Username != null) {
                TextField_Username.requestFocus();
            }
            
            logger.info("Login form cleared");
            
        } catch (Exception e) {
            logger.severe(String.format("Error clearing form: %s", e.toString()));
        }
    }

    public void testLogin(String username, String password) {
        try {
            logger.info(String.format("Test login - Username: %s", username));
            
            if (TextField_Username != null) {
                TextField_Username.setText(username);
            }
            if (PasswordField_Password != null) {
                PasswordField_Password.setText(password);
            }
            
            logIn();
            
        } catch (Exception e) {
            logger.severe(String.format("Error in testLogin: %s", e.toString()));
        }
    }
}