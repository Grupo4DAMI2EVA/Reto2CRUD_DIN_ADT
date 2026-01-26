package controller;

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
    
    // NOMBRES ORIGINALES DE TU FXML
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

    private Controller cont = new Controller(new DBImplementation());

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
            
            // Verificar campos FXML
            logger.info("Field check:");
            logger.info("  Button_SignUp: " + (Button_SignUp == null ? "NULL" : "OK"));
            logger.info("  TextField_Username: " + (TextField_Username == null ? "NULL" : "OK"));
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
            Parent root = fxmlLoader.load();
            
            logger.info("FXML loaded successfully");
            
            SignUpWindowController controllerWindow = fxmlLoader.getController();
            logger.info("SignUpWindowController obtained: " + 
                       (controllerWindow == null ? "NULL" : controllerWindow.getClass().getName()));
            
            controllerWindow.setController(cont);
            
            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) Button_SignUp.getScene().getWindow();
            currentStage.close();
            
            logger.info("=== Sign Up window opened successfully ===");
            
        } catch (Exception e) {
            logger.severe("Error opening Sign Up window: " + e.toString());
            showAlert("Error", "Could not open the Sign Up window: " + e.getMessage());
        }
    }

    @FXML
    private void logIn() {
        try {
            logger.info("=== Login attempt ===");
            
            // Verificar que los campos existan
            if (TextField_Username == null || PasswordField_Password == null) {
                logger.severe("Login fields are null!");
                showAlert("System Error", "Application not properly initialized");
                return;
            }
            
            String username = TextField_Username.getText();
            String password = PasswordField_Password.getText();
            
            logger.info("Credentials - Username: '" + username + "', Password length: " + password.length());
            
            if (username.isEmpty() || password.isEmpty()) {
                logger.warning("Empty fields detected");
                if (labelIncorrecto != null) {
                    labelIncorrecto.setText("Please fill in both fields.");
                } else {
                    showAlert("Input Error", "Please fill in both username and password fields.");
                }
                return;
            }

            logger.info("Calling cont.logIn()...");
            Profile profile = cont.logIn(username, password);
            
            if (profile != null) {
                logger.info("=== LOGIN SUCCESSFUL ===");
                logger.info("User type: " + profile.getClass().getSimpleName());
                logger.info("Username: " + profile.getUsername());
                
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                    Parent root = fxmlLoader.load();

                    MenuWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.setUser(profile);
                    controllerWindow.setController(cont);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    Stage currentStage = (Stage) Button_LogIn.getScene().getWindow();
                    currentStage.close();
                    
                    logger.info("=== Redirected to MenuWindow successfully ===");
                    
                } catch (Exception e) {
                    logger.severe("Error opening MenuWindow: " + e.toString());
                    showAlert("Navigation Error", "Login successful but could not open main menu.");
                }
            } else {
                logger.warning("=== LOGIN FAILED - Invalid credentials ===");
                if (labelIncorrecto != null) {
                    labelIncorrecto.setText("The username and/or password are incorrect.");
                } else {
                    showAlert("Login Failed", "The username and/or password are incorrect.");
                }
                
                // Clear password for security
                PasswordField_Password.clear();
                PasswordField_Password.requestFocus();
            }
            
        } catch (Exception e) {
            logger.severe("Error in login process: " + e.toString());
            showAlert("Login Error", "An error occurred during login: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("=== Initializing LogInWindowController ===");
            
            // Verificar inyección de campos FXML
            logger.info("FXML Field Injection Check:");
            logger.info("  TextField_Username: " + (TextField_Username == null ? "NULL" : "OK"));
            logger.info("  PasswordField_Password: " + (PasswordField_Password == null ? "NULL" : "OK"));
            logger.info("  Button_LogIn: " + (Button_LogIn == null ? "NULL" : "OK"));
            logger.info("  Button_SignUp: " + (Button_SignUp == null ? "NULL" : "OK"));
            logger.info("  labelIncorrecto: " + (labelIncorrecto == null ? "NULL" : "OK"));
            
            // Limpiar campos
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
            
            logger.info("=== LogInWindowController initialized successfully ===");
            
        } catch (Exception e) {
            logger.severe("Error in initialize: " + e.toString());
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
            logger.severe("Error showing alert: " + e.toString());
        }
    }

    // Método para limpiar el formulario
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
            logger.severe("Error clearing form: " + e.toString());
        }
    }

    // Método para probar login rápido (testing)
    public void testLogin(String username, String password) {
        try {
            logger.info("Test login - Username: " + username);
            
            if (TextField_Username != null) {
                TextField_Username.setText(username);
            }
            if (PasswordField_Password != null) {
                PasswordField_Password.setText(password);
            }
            
            logIn();
            
        } catch (Exception e) {
            logger.severe("Error in testLogin: " + e.toString());
        }
    }
}