package controller;

import javafx.scene.control.ToggleGroup;
import exception.passwordequalspassword;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
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
import javafx.stage.Stage;
import model.Profile;

/**
 * Controller for the SignUp window. Handles user registration and navigation to login or main menu.
 */
public class SignUpWindowController implements Initializable {

    // Logger para esta clase
    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;

    @FXML
    private TextField textFieldEmail, textFieldName, textFieldSurname, textFieldTelephone;
    @FXML
    private TextField textFieldCardN, textFieldPassword, textFieldCPassword, textFieldUsername;
    @FXML
    private RadioButton rButtonM, rButtonW, rButtonO;
    @FXML
    private Button buttonSignUp, buttonLogIn;

    private Controller cont;
    private ToggleGroup grupOp;

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

            FileHandler fileHandler = new FileHandler("logs/SignUpWindow.log", true);

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

    public void setCont(Controller cont) {
        logger.info("Setting controller in SignUpWindowController");
        this.cont = cont;
    }

    /**
     * Navigates back to login window.
     */
    @FXML
    private void login() {
        logger.info("Login button clicked - Navigating to login window");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();
            controller.LogInWindowController controllerWindow = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) buttonLogIn.getScene().getWindow();
            currentStage.close();

            logger.info("Successfully navigated to login window");

        } catch (IOException ex) {
            logger.severe("Error navigating to login window: " + ex.getMessage());
            Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Signs up a new user and navigates to MenuWindow if successful.
     */
    @FXML
    private void signup() throws passwordequalspassword {
        logger.info("SignUp button clicked - Starting user registration");

        // Obtener datos del formulario
        String email = textFieldEmail.getText();
        String name = textFieldName.getText();
        String surname = textFieldSurname.getText();
        String telephone = textFieldTelephone.getText();
        String cardN = textFieldCardN.getText();
        String pass = textFieldPassword.getText();
        String passC = textFieldCPassword.getText();
        String username = textFieldUsername.getText();
        String gender = null;

        // Registrar datos obtenidos (sin contraseñas por seguridad)
        logger.info("Registration attempt - Username: " + username
                + ", Email: " + email + ", Name: " + name + " " + surname);
        logger.info("Gender selection - M: " + rButtonM.isSelected()
                + ", W: " + rButtonW.isSelected() + ", O: " + rButtonO.isSelected());

        // Determinar género seleccionado
        if (rButtonM.isSelected()) {
            gender = "Man";
            logger.info("Gender selected: Man");
        } else if (rButtonW.isSelected()) {
            gender = "Woman";
            logger.info("Gender selected: Woman");
        } else if (rButtonO.isSelected()) {
            gender = "Other";
            logger.info("Gender selected: Other");
        } else {
            logger.warning("No gender selected for registration");
        }

        // Validar contraseñas
        if (!pass.equals(passC)) {
            logger.warning("Password validation failed - Passwords do not match");
            logger.warning("Password1: [PROTECTED], Password2: [PROTECTED]");
            throw new passwordequalspassword("No son iguales las contraseñas");
        }

        logger.info("Password validation successful");

        // Intentar registro
        logger.info("Attempting to sign up user: " + username);
        if (cont.signUp(gender, cardN, username, pass, email, name, telephone, surname)) {
            logger.info("SignUp successful for user: " + username);

            // Intentar login automático
            logger.info("Attempting auto-login for new user: " + username);
            Profile profile = cont.logIn(username, pass);

            if (profile != null) {
                logger.info("Auto-login successful for user: " + username + " (ID: " + profile.getUserCode() + ")");

                try {
                    // Navegar a la ventana principal
                    logger.info("Navigating to MenuWindow for new user: " + username);
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                    Parent root = fxmlLoader.load();
                    controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.setUsuario(profile);
                    controllerWindow.setCont(this.cont);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    Stage currentStage = (Stage) buttonSignUp.getScene().getWindow();
                    currentStage.close();

                    logger.info("Successfully navigated to MenuWindow for new user: " + username);

                } catch (IOException ex) {
                    logger.severe("Error navigating to MenuWindow after signup: " + ex.getMessage());
                    Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                logger.severe("Auto-login failed after successful signup for user: " + username);
            }
        } else {
            logger.warning("SignUp failed for user: " + username + " - User may already exist or invalid data");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing SignUpWindowController");

        try {
            grupOp = new ToggleGroup();
            rButtonM.setToggleGroup(grupOp);
            rButtonW.setToggleGroup(grupOp);
            rButtonO.setToggleGroup(grupOp);

            logger.info("SignUpWindowController initialized successfully - ToggleGroup configured");

        } catch (Exception e) {
            logger.severe("Error initializing SignUpWindowController: " + e.getMessage());
        }
    }
}
