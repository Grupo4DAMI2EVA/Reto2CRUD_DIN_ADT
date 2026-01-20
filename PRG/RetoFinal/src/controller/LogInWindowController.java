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
    
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private Button buttonLogIn;
    @FXML
    private Button buttonSignUp;
    @FXML
    private Label labelError;

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
            
            Handler fileHandler = new FileHandler("logs/login.log", true);
            
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
            logger.info("LogInWindowController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    @FXML
    private void signUp() {
        try {
            logger.info("Opening Sign Up window");
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(root));
            stage.show();

            SignUpWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setController(cont);

            Stage currentStage = (Stage) buttonSignUp.getScene().getWindow();
            currentStage.close();
            
            logger.info("Sign Up window opened successfully");
            
        } catch (Exception e) {
            logger.severe("Error opening Sign Up window: " + e.getMessage());
            showAlert("Error", "Could not open the Sign Up window.");
        }
    }

    @FXML
    private void logIn() {
        try {
            String username = textFieldUsername.getText();
            String password = passwordFieldPassword.getText();
            
            logger.info("Login attempt - Username: " + username);
            
            if (username.isEmpty() || password.isEmpty()) {
                logger.severe("Login attempt with empty fields");
                labelError.setText("Please fill in both username and password fields.");
                return;
            }

            Profile profile = cont.logIn(username, password);
            if (profile != null) {
                logger.info("Login successful for user: " + username + 
                           ", Type: " + profile.getClass().getSimpleName());
                
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                    Parent root = fxmlLoader.load();

                    MenuWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.setUser(profile);
                    controllerWindow.setController(cont);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    Stage currentStage = (Stage) buttonLogIn.getScene().getWindow();
                    currentStage.close();
                    
                    logger.info("Redirected to MenuWindow for user: " + username);
                    
                } catch (Exception e) {
                    logger.severe("Error opening MenuWindow after successful login: " + e.getMessage());
                    showAlert("Error", "Login successful but could not open the main menu.");
                }
            } else {
                logger.severe("Login failed for user: " + username + " - Invalid credentials");
                labelError.setText("The username and/or password are incorrect.");
                
                // Clear password field for security
                passwordFieldPassword.clear();
                passwordFieldPassword.requestFocus();
            }
            
        } catch (Exception e) {
            logger.severe("Error in login process: " + e.getMessage());
            labelError.setText("An error occurred during login. Please try again.");
            showAlert("Login Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing LogInWindowController");
            
            // Clear any existing data
            if (textFieldUsername != null) {
                textFieldUsername.clear();
            }
            if (passwordFieldPassword != null) {
                passwordFieldPassword.clear();
            }
            if (labelError != null) {
                labelError.setText("");
            }
            
            // Set focus to username field
            textFieldUsername.requestFocus();
            
            logger.info("LogInWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing LogInWindowController: " + e.getMessage());
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

    // Method to clear the login form
    public void clearForm() {
        try {
            logger.info("Clearing login form");
            
            textFieldUsername.clear();
            passwordFieldPassword.clear();
            labelError.setText("");
            textFieldUsername.requestFocus();
            
            logger.info("Login form cleared");
            
        } catch (Exception e) {
            logger.severe("Error clearing login form: " + e.getMessage());
        }
    }

    // Method to set a custom error message
    public void setErrorMessage(String message) {
        try {
            logger.info("Setting error message: " + message);
            
            if (labelError != null) {
                labelError.setText(message);
            }
            
        } catch (Exception e) {
            logger.severe("Error setting error message: " + e.getMessage());
        }
    }

    // Method to get the controller instance (for testing or external use)
    public Controller getController() {
        return cont;
    }

    // Method to simulate a login (for testing purposes)
    public void simulateLogin(String username, String password) {
        try {
            logger.info("Simulating login for testing - Username: " + username);
            
            textFieldUsername.setText(username);
            passwordFieldPassword.setText(password);
            logIn();
            
        } catch (Exception e) {
            logger.severe("Error in simulated login: " + e.getMessage());
        }
    }
}