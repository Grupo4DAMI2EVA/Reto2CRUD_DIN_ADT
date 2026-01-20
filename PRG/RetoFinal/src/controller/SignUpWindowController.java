package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Profile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;

public class SignUpWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(SignUpWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private TextField textFieldEmail, textFieldName, textFieldSurname, textFieldTelephone;
    @FXML
    private TextField textFieldCardNumber, textFieldPassword, textFieldConfirmPassword, textFieldUsername;
    @FXML
    private RadioButton radioButtonMale, radioButtonFemale, radioButtonOther;
    @FXML
    private Button buttonSignUp, buttonLogIn;

    private Controller cont;
    private ToggleGroup genderGroup;

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
            
            Handler fileHandler = new FileHandler("logs/signup.log", true);
            
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
            logger.info("SignUpWindowController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for SignUpWindowController");
        } catch (Exception e) {
            logger.severe("Error setting main controller: " + e.getMessage());
        }
    }

    @FXML
    private void login() {
        try {
            logger.info("Navigating to login window");
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();
            LogInWindowController controllerWindow = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
            
            Stage currentStage = (Stage) buttonLogIn.getScene().getWindow();
            currentStage.close();
            
            logger.info("Successfully navigated to login window");
            
        } catch (Exception e) {
            logger.severe("Error navigating to login window: " + e.getMessage());
            showAlert("Error", "Could not open the login window.");
        }
    }

    @FXML
    private void signup() {
        try {
            logger.info("Starting signup process");
            
            String email = textFieldEmail.getText();
            String name = textFieldName.getText();
            String surname = textFieldSurname.getText();
            String telephone = textFieldTelephone.getText();
            String cardNumber = textFieldCardNumber.getText();
            String password = textFieldPassword.getText();
            String confirmPassword = textFieldConfirmPassword.getText();
            String username = textFieldUsername.getText();
            
            logger.info("Signup attempt - Username: " + username + ", Email: " + email);
            
            if (!validateForm(email, name, surname, telephone, cardNumber, password, confirmPassword, username)) {
                logger.warning("Form validation failed");
                return;
            }

            String gender = null;
            if (radioButtonMale.isSelected()) {
                gender = "Male";
            } else if (radioButtonFemale.isSelected()) {
                gender = "Female";
            } else if (radioButtonOther.isSelected()) {
                gender = "Other";
            }

            if (!password.equals(confirmPassword)) {
                logger.severe("Password mismatch during signup");
                showAlert("Password Error", "Passwords do not match. Please enter the same password in both fields.");
                return;
            }

            logger.info("Creating user account with gender: " + gender);
            
            if (cont.signUp(gender, cardNumber, username, password, email, name, telephone, surname)) {
                logger.info("Signup successful for username: " + username);
                
                Profile profile = cont.logIn(username, password);
                if (profile != null) {
                    logger.info("Auto-login successful after signup");
                    
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                        Parent root = fxmlLoader.load();
                        MenuWindowController controllerWindow = fxmlLoader.getController();
                        controllerWindow.setUser(profile);
                        controllerWindow.setController(this.cont);
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.show();
                        
                        Stage currentStage = (Stage) buttonSignUp.getScene().getWindow();
                        currentStage.close();
                        
                        logger.info("Successfully navigated to menu window after signup");
                        
                    } catch (Exception e) {
                        logger.severe("Error navigating to menu after signup: " + e.getMessage());
                        showAlert("Error", "Account created but could not open the menu.");
                    }
                } else {
                    logger.severe("Auto-login failed after successful signup for: " + username);
                    showAlert("Error", "Account created but automatic login failed. Please login manually.");
                }
            } else {
                logger.warning("Signup failed for username: " + username);
                showAlert("Signup Failed", "Could not create account. The username may already be taken.");
            }
            
        } catch (Exception e) {
            logger.severe("Error in signup process: " + e.getMessage());
            showAlert("Signup Error", "An error occurred during signup. Please try again.");
        }
    }

    private boolean validateForm(String email, String name, String surname, String telephone, 
                                String cardNumber, String password, String confirmPassword, String username) {
        try {
            logger.info("Validating signup form data");
            
            if (username == null || username.trim().isEmpty()) {
                logger.warning("Validation failed: username empty");
                showAlert("Validation Error", "Username cannot be empty.");
                return false;
            }
            
            if (email == null || email.trim().isEmpty()) {
                logger.warning("Validation failed: email empty");
                showAlert("Validation Error", "Email cannot be empty.");
                return false;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                logger.warning("Validation failed: invalid email format");
                showAlert("Validation Error", "Please enter a valid email address.");
                return false;
            }
            
            if (name == null || name.trim().isEmpty()) {
                logger.warning("Validation failed: name empty");
                showAlert("Validation Error", "Name cannot be empty.");
                return false;
            }
            
            if (surname == null || surname.trim().isEmpty()) {
                logger.warning("Validation failed: surname empty");
                showAlert("Validation Error", "Surname cannot be empty.");
                return false;
            }
            
            if (password == null || password.trim().isEmpty()) {
                logger.warning("Validation failed: password empty");
                showAlert("Validation Error", "Password cannot be empty.");
                return false;
            }
            
            if (password.length() < 6) {
                logger.warning("Validation failed: password too short");
                showAlert("Validation Error", "Password must be at least 6 characters long.");
                return false;
            }
            
            if (cardNumber == null || cardNumber.trim().isEmpty()) {
                logger.warning("Validation failed: card number empty");
                showAlert("Validation Error", "Card number cannot be empty.");
                return false;
            }
            
            if (!cardNumber.matches("\\d+")) {
                logger.warning("Validation failed: invalid card number format");
                showAlert("Validation Error", "Card number must contain only numbers.");
                return false;
            }
            
            if (!radioButtonMale.isSelected() && !radioButtonFemale.isSelected() && !radioButtonOther.isSelected()) {
                logger.warning("Validation failed: no gender selected");
                showAlert("Validation Error", "Please select a gender.");
                return false;
            }
            
            logger.info("Form validation successful");
            return true;
            
        } catch (Exception e) {
            logger.severe("Error during form validation: " + e.getMessage());
            showAlert("Validation Error", "An error occurred while validating the form.");
            return false;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing SignUpWindowController");
            
            genderGroup = new ToggleGroup();
            radioButtonMale.setToggleGroup(genderGroup);
            radioButtonFemale.setToggleGroup(genderGroup);
            radioButtonOther.setToggleGroup(genderGroup);
            
            clearForm();
            
            logger.info("SignUpWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing SignUpWindowController: " + e.getMessage());
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

    public void clearForm() {
        try {
            logger.info("Clearing signup form");
            
            textFieldEmail.clear();
            textFieldName.clear();
            textFieldSurname.clear();
            textFieldTelephone.clear();
            textFieldCardNumber.clear();
            textFieldPassword.clear();
            textFieldConfirmPassword.clear();
            textFieldUsername.clear();
            genderGroup.selectToggle(null);
            textFieldUsername.requestFocus();
            
            logger.info("Signup form cleared");
            
        } catch (Exception e) {
            logger.severe("Error clearing form: " + e.getMessage());
        }
    }

    public void setDefaultValues() {
        try {
            logger.info("Setting default values for testing");
            
            textFieldUsername.setText("testuser");
            textFieldEmail.setText("test@example.com");
            textFieldName.setText("Test");
            textFieldSurname.setText("User");
            textFieldTelephone.setText("123456789");
            textFieldCardNumber.setText("1234567890123456");
            textFieldPassword.setText("password123");
            textFieldConfirmPassword.setText("password123");
            radioButtonMale.setSelected(true);
            
            logger.info("Default values set");
            
        } catch (Exception e) {
            logger.severe("Error setting default values: " + e.getMessage());
        }
    }
}