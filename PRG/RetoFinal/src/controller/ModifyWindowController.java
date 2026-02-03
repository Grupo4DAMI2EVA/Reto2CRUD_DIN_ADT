package controller;

import exception.passwordequalspassword;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Profile;
import model.User;

/**
 * FXML Controller class for modifying a user's profile.
 */
public class ModifyWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Label LabelUsername; // Label showing current username
    @FXML
    private Label LabelEmail; // Label showing current email
    @FXML
    private TextField TextField_Name; // Field to modify name
    @FXML
    private TextField TextField_Surname; // Field to modify surname
    @FXML
    private TextField TextField_Telephone; // Field to modify telephone
    @FXML
    private TextField TextField_NewPass; // Field to enter new password
    @FXML
    private TextField TextField_CNewPass; // Field to confirm new password
    @FXML
    private Button Button_Cancel;

    private Controller cont; // Controller instance for business logic
    private Profile profile; // Currently logged-in user
    @FXML
    private Button Button_SaveChanges;
    
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
            
            FileHandler fileHandler = new FileHandler("logs/ModifyWindow.log", true);
            
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

    // Set controller instance
    public void setCont(Controller cont) {
        logger.info("Setting controller in ModifyWindowController");
        this.cont = cont;
    }

    // Set current profile and populate labels
    public void setProfile(Profile profile) {
        logger.info("Setting profile for modification: " + 
                   (profile != null ? profile.getUsername() + 
                    " (Type: " + (profile instanceof User ? "User" : "Admin") + ")" : "null"));
        this.profile = profile;
        LabelUsername.setText(profile.getUsername());
        LabelEmail.setText(profile.getEmail());
        logger.info("Labels updated - Username: " + profile.getUsername() + ", Email: " + profile.getEmail());
    }

    // Save changes button action
    @FXML
    private void save(ActionEvent event) throws passwordequalspassword {
        logger.info("Save changes button clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown"));
        
        try {
            // Read all input values
            String name = TextField_Name.getText();
            String surname = TextField_Surname.getText();
            String telephone = TextField_Telephone.getText();
            String newPass = TextField_NewPass.getText();
            String cNewPass = TextField_CNewPass.getText();
            String gender = "";
            String username;
            String email;

            // Registrar datos ingresados (sin contraseñas completas por seguridad)
            logger.info("Modification form data - Name: '" + name + 
                       "', Surname: '" + surname + 
                       "', Telephone: '" + telephone + 
                       "', NewPass length: " + (newPass != null ? newPass.length() : 0) + 
                       ", CNewPass length: " + (cNewPass != null ? cNewPass.length() : 0));

            // obtener el genero actual si es un User
            if (profile instanceof User) {
                gender = ((User) profile).getGender();
                logger.info("User gender retrieved: " + gender);
            } else {
                logger.info("Profile is Admin - no gender field");
            }

            username = profile.getUsername();
            email = profile.getEmail();
            
            logger.info("Using existing - Username: " + username + ", Email: " + email);

            // Validar y usar valores por defecto si es necesario
            if (name == null || name.isEmpty() || name.equals("Insert your new name")) {
                name = profile.getName();
                logger.info("Using default name: " + name);
            }
            if (surname == null || surname.isEmpty() || surname.equals("Insert your new surname")) {
                surname = profile.getSurname();
                logger.info("Using default surname: " + surname);
            }
            if (telephone == null || telephone.isEmpty() || telephone.equals("Insert your new telephone")) {
                telephone = profile.getTelephone();
                logger.info("Using default telephone: " + telephone);
            }
            if (newPass == null || newPass.isEmpty() || cNewPass == null || cNewPass.isEmpty()
                    || newPass.equals("New Password") || cNewPass.equals("Confirm New Password")) {
                newPass = profile.getPassword();
                logger.info("Using existing password (no new password provided)");
                
                logger.info("Attempting to modify user without password change");
                Boolean success = cont.modificarUser(newPass, email, name, telephone, surname, username, gender);
                
                if (success) {
                    logger.info("User modification SUCCESSFUL without password change - User: " + username);
                    
                    // actualizar el objeto profile con los nuevos valores
                    profile.setName(name);
                    profile.setSurname(surname);
                    profile.setTelephone(telephone);
                    profile.setPassword(newPass);

                    javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("User data has been successfully updated.");
                    successAlert.showAndWait();

                    try {
                        logger.info("Navigating back to MenuWindow after successful modification");
                        
                        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                        javafx.scene.Parent root = fxmlLoader.load();

                        controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                        controllerWindow.setUsuario(profile);
                        controllerWindow.setCont(this.cont);
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.show();
                        
                        Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
                        currentStage.close();
                        
                        logger.info("ModifyWindow closed - Successfully returned to MenuWindow");

                    } catch (IOException ex) {
                        logger.severe("Error navigating to MenuWindow after modification: " + ex.getMessage());
                        Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    logger.warning("User modification FAILED without password change - User: " + username);
                    
                    javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Update failed");
                    error.setContentText("Could not update user data.");
                    error.showAndWait();
                }
            } else {
                // Validar contraseñas si se proporcionaron nuevas
                logger.info("New passwords provided - Validating password match");
                
                if (!newPass.equals(cNewPass)) {
                    logger.warning("Password validation failed - Passwords do not match for user: " + username);
                    throw new passwordequalspassword("Las contraseñas no coinciden");
                } else {
                    logger.info("Passwords match - Proceeding with password change");
                    
                    Boolean success = cont.modificarUser(newPass, email, name, telephone, surname, username, gender);
                    
                    if (success) {
                        logger.info("User modification SUCCESSFUL with password change - User: " + username);
                        
                        // actualizar el objeto profile con los nuevos valores
                        profile.setName(name);
                        profile.setSurname(surname);
                        profile.setTelephone(telephone);
                        profile.setPassword(newPass);

                        javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("User data has been successfully updated.");
                        successAlert.showAndWait();

                        try {
                            logger.info("Navigating back to MenuWindow after successful modification with password change");
                            
                            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                            javafx.scene.Parent root = fxmlLoader.load();

                            controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                            controllerWindow.setUsuario(profile);
                            controllerWindow.setCont(this.cont);

                            Stage stage = new Stage();
                            stage.setScene(new javafx.scene.Scene(root));
                            stage.show();

                            Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
                            currentStage.close();
                            
                            logger.info("ModifyWindow closed - Successfully returned to MenuWindow with password change");

                        } catch (IOException ex) {
                            logger.severe("Error navigating to MenuWindow after modification: " + ex.getMessage());
                            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        logger.warning("User modification FAILED with password change - User: " + username);
                        
                        javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("Update failed");
                        error.setContentText("Could not update user data.");
                        error.showAndWait();
                    }
                }
            }
            
        } catch (passwordequalspassword ex) {
            logger.warning("PasswordEqualsPassword exception: " + ex.getMessage());
            throw ex; // Re-lanzar para manejo superior
            
        } catch (Exception ex) {
            logger.severe("Unexpected error during user modification: " + ex.getMessage());
            throw ex; // Re-lanzar para manejo superior
        }
    }

    // Cancel button action: returns to MenuWindow without saving
    @FXML
    private void cancel() {
        logger.info("Cancel button clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown"));
        
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            controller.MenuWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUsuario(profile);
            controllerWindow.setCont(this.cont);

            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
            logger.info("MenuWindow opened successfully");

            Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
            currentStage.close();
            
            logger.info("ModifyWindow closed (cancelled)");

        } catch (IOException ex) {
            logger.severe("Error navigating to MenuWindow from cancel: " + ex.getMessage());
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing ModifyWindowController");
        
        try {
            // Initialization logic (if needed) can be added here
            logger.info("ModifyWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing ModifyWindowController: " + e.getMessage());
        }
    }
}