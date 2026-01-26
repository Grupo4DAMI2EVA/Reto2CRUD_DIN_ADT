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
    
    // USANDO LOS NOMBRES ORIGINALES DE TU FXML
    @FXML
    private TextField textFieldEmail, textFieldName, textFieldSurname, textFieldTelephone;
    @FXML
    private TextField textFieldCardN, textFieldPassword, textFieldCPassword, textFieldUsername; // NOMBRES ORIGINALES
    @FXML
    private RadioButton rButtonM, rButtonW, rButtonO; // NOMBRES ORIGINALES
    @FXML
    private Button buttonSignUp, buttonLogIn;

    private Controller cont;
    private ToggleGroup grupOp; // Nombre original

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
            
            Handler fileHandler = new FileHandler("logs/signup_working.log", true);
            
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
            logger.info("SignUpWindowController logger initialized - USING ORIGINAL NAMES");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.toString());
            loggerInitialized = true;
        }
    }

    public void setController(Controller cont) {
        try {
            logger.info("setController called");
            this.cont = cont;
        } catch (Exception e) {
            logger.severe("Error in setController: " + e.toString());
        }
    }

    @FXML
    private void login() {
        try {
            logger.info("Opening login window");
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
            
            Stage currentStage = (Stage) buttonLogIn.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            logger.severe("Error opening login window: " + e.toString());
            showAlert("Error", "Could not open login window");
        }
    }

    @FXML
    private void signup() {
        try {
            logger.info("=== SIGNUP STARTED ===");
            
            // Verificar campos
            logger.info("Field check:");
            logger.info("  textFieldCardN: " + (textFieldCardN == null ? "NULL" : "OK"));
            logger.info("  textFieldCPassword: " + (textFieldCPassword == null ? "NULL" : "OK"));
            logger.info("  rButtonM: " + (rButtonM == null ? "NULL" : "OK"));
            logger.info("  rButtonW: " + (rButtonW == null ? "NULL" : "OK"));
            logger.info("  rButtonO: " + (rButtonO == null ? "NULL" : "OK"));
            
            // Obtener datos
            String email = textFieldEmail.getText();
            String name = textFieldName.getText();
            String surname = textFieldSurname.getText();
            String telephone = textFieldTelephone.getText();
            String cardNumber = textFieldCardN != null ? textFieldCardN.getText() : "";
            String password = textFieldPassword.getText();
            String confirmPassword = textFieldCPassword != null ? textFieldCPassword.getText() : "";
            String username = textFieldUsername.getText();
            
            logger.info("Data collected:");
            logger.info("  Username: " + username);
            logger.info("  Email: " + email);
            logger.info("  Card: " + cardNumber);
            logger.info("  Password: " + (password.isEmpty() ? "EMPTY" : "SET"));
            logger.info("  Confirm: " + (confirmPassword.isEmpty() ? "EMPTY" : "SET"));
            
            // Validar datos requeridos
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all required fields");
                return;
            }
            
            // Obtener género
            String gender = getGender();
            logger.info("Gender: " + gender);
            
            // Validar contraseñas
            if (!password.equals(confirmPassword)) {
                showAlert("Password Error", "Passwords do not match");
                return;
            }
            
            // Validar controlador
            if (cont == null) {
                logger.severe("Controller is null");
                showAlert("System Error", "Application not properly initialized");
                return;
            }
            
            // Intentar crear cuenta
            logger.info("Calling cont.signUp()...");
            boolean success = cont.signUp(gender, cardNumber, username, password, email, name, telephone, surname);
            
            if (success) {
                logger.info("=== SIGNUP SUCCESSFUL ===");
                showAlert("Success", "Account created successfully!");
                login(); // Volver al login
            } else {
                logger.warning("=== SIGNUP FAILED ===");
                showAlert("Error", "Could not create account. Username may already exist.");
            }
            
        } catch (Exception e) {
            logger.severe("Error in signup: " + e.toString());
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }
    
    private String getGender() {
        if (rButtonM == null || rButtonW == null || rButtonO == null) {
            return "Other";
        }
        
        if (rButtonM.isSelected()) {
            return "Male";
        } else if (rButtonW.isSelected()) {
            return "Female";
        } else if (rButtonO.isSelected()) {
            return "Other";
        } else {
            return "Other"; // Default
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing with original names");
            
            // Configurar ToggleGroup con nombres originales
            grupOp = new ToggleGroup();
            
            if (rButtonM != null) rButtonM.setToggleGroup(grupOp);
            if (rButtonW != null) rButtonW.setToggleGroup(grupOp);
            if (rButtonO != null) rButtonO.setToggleGroup(grupOp);
            
            logger.info("ToggleGroup configured");
            logger.info("Initialize completed successfully");
            
        } catch (Exception e) {
            logger.severe("Error in initialize: " + e.toString());
        }
    }

    private void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
            logger.info("Alert: " + title + " - " + message);
            
        } catch (Exception e) {
            logger.severe("Error showing alert: " + e.toString());
        }
    }
}