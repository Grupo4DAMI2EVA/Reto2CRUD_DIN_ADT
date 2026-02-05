package controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.*;

/**
 * Controller class for the Modify Game Admin window. Manages the administrative interface
 * that allows administrators to edit existing videogames in the store catalog.
 * Handles form pre-population, validation, data collection, and database update operations.
 *
 * @author deorbe
 * @version 1.0
 */
public class ModifyGameAdminController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    // FXML UI Components
    @FXML
    private TextField textFieldName;
    @FXML
    private ComboBox<Platform> comboBoxPlatforms;
    @FXML
    private TextField textFieldCompany;
    @FXML
    private Spinner<Integer> spinnerStock;
    @FXML
    private ComboBox<GameGenre> comboBoxGenre;
    @FXML
    private Spinner<Double> spinnerPrice;
    @FXML
    private ComboBox<PEGI> comboBoxPEGI;
    @FXML
    private DatePicker datePickerReleaseDate;
    @FXML
    private Button buttonModifyGame;
    
    private Controller cont;
    private AdminShopController adminShopController;
    private Videogame videogameToModify;
    
    static {
        initializeLogger();
    }
    
    /**
     * Initializes the logging system in a synchronized manner to prevent
     * multiple initializations in multi-threaded environments.
     */
    private static synchronized void initializeLogger() {
        if (loggerInitialized) {
            return;
        }
        
        try {
            java.io.File logsFolder = new java.io.File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }
            
            FileHandler fileHandler = new FileHandler("logs/ModifyGameAdmin.log", true);
            
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
     * Sets the Controller instance for database operations.
     *
     * @param cont The Controller instance for database access
     */
    public void setCont(Controller cont) {
        logger.info("Setting controller in ModifyGameAdminController");
        this.cont = cont;
    }

    /**
     * Sets the reference to AdminShopController for table refresh operations.
     *
     * @param adminShopController The AdminShopController instance
     */
    public void setAdminShopController(AdminShopController adminShopController) {
        logger.info("Setting AdminShopController reference");
        this.adminShopController = adminShopController;
    }

    /**
     * Sets the videogame to be modified and loads its data into the form.
     *
     * @param videogame The Videogame object to be modified
     */
    public void setVideogame(Videogame videogame) {
        logger.info("Setting videogame to modify: " + 
                   (videogame != null ? videogame.getName() + " (ID: " + videogame.getIdVideogame() + ")" : "null"));
        this.videogameToModify = videogame;
        loadGameData();
    }

    /**
     * Loads the current videogame data into all form fields.
     * Pre-populates text fields, ComboBoxes, Spinners, and DatePicker
     * with the existing game information for editing.
     */
    private void loadGameData() {
        logger.info("Loading game data into form");
        
        if (videogameToModify != null) {
            try {
                textFieldName.setText(videogameToModify.getName());
                textFieldCompany.setText(videogameToModify.getCompanyName());
                comboBoxGenre.setValue(videogameToModify.getGameGenre());
                comboBoxPlatforms.setValue(videogameToModify.getPlatforms());
                comboBoxPEGI.setValue(videogameToModify.getPegi());
                spinnerPrice.getValueFactory().setValue(videogameToModify.getPrice());
                spinnerStock.getValueFactory().setValue(videogameToModify.getStock());
                
                logger.info("Loaded game data - Name: " + videogameToModify.getName() + 
                           ", Company: " + videogameToModify.getCompanyName() + 
                           ", Genre: " + videogameToModify.getGameGenre() + 
                           ", Price: " + videogameToModify.getPrice() + 
                           ", Stock: " + videogameToModify.getStock());
                
                // Convertir Date a LocalDate
                if (videogameToModify.getReleaseDate() != null) {
                    // Para java.sql.Date, usar toLocalDate() directamente
                    if (videogameToModify.getReleaseDate() instanceof java.sql.Date) {
                        datePickerReleaseDate.setValue(((java.sql.Date) videogameToModify.getReleaseDate()).toLocalDate());
                        logger.info("Release date loaded: " + datePickerReleaseDate.getValue());
                    } else {
                        // Para java.util.Date
                        LocalDate localDate = videogameToModify.getReleaseDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                        datePickerReleaseDate.setValue(localDate);
                        logger.info("Release date loaded: " + localDate);
                    }
                } else {
                    logger.info("No release date available for this game");
                }
                
            } catch (Exception e) {
                logger.severe("Error loading game data: " + e.getMessage());
            }
        } else {
            logger.warning("Attempted to load data but videogameToModify is null");
        }
    }

    /**
     * Handles the modify game button click event. Validates form data,
     * updates the videogame object with new values, and saves changes to the database.
     * Shows success/error messages and refreshes the AdminShopController table.
     *
     * @param event The MouseEvent triggered by clicking the modify game button
     */
    @FXML
    private void modifyGame(MouseEvent event) {
        logger.info("Modify Game button clicked");
        
        try {
            // Validar que no hay campos vacíos
            if (textFieldName.getText() == null || textFieldName.getText().trim().isEmpty() ||
                textFieldCompany.getText() == null || textFieldCompany.getText().trim().isEmpty() ||
                comboBoxGenre.getValue() == null ||
                comboBoxPlatforms.getValue() == null ||
                comboBoxPEGI.getValue() == null ||
                datePickerReleaseDate.getValue() == null) {
                
                logger.warning("Validation failed - Missing required fields");
                logger.warning("Fields status - Name: " + textFieldName.getText() + 
                             ", Company: " + textFieldCompany.getText() + 
                             ", Genre: " + comboBoxGenre.getValue() + 
                             ", Platform: " + comboBoxPlatforms.getValue() + 
                             ", PEGI: " + comboBoxPEGI.getValue() + 
                             ", Date: " + datePickerReleaseDate.getValue());
                
                Alert error = new Alert(Alert.AlertType.WARNING);
                error.setTitle("Validation Error");
                error.setHeaderText("Missing Fields");
                error.setContentText("Please fill in all required fields.");
                error.showAndWait();
                return;
            }
            
            logger.info("Form validation passed - Proceeding with game modification");
            
            // Registrar datos antes de modificar
            logger.info("Game modification data - " +
                       "Name: " + textFieldName.getText() + 
                       ", Company: " + textFieldCompany.getText() + 
                       ", Genre: " + comboBoxGenre.getValue() + 
                       ", Platform: " + comboBoxPlatforms.getValue() + 
                       ", PEGI: " + comboBoxPEGI.getValue() + 
                       ", Price: " + spinnerPrice.getValue() + 
                       ", Stock: " + spinnerStock.getValue() + 
                       ", Release Date: " + datePickerReleaseDate.getValue());
            
            // Actualizar el objeto videogame con los nuevos valores
            videogameToModify.setName(textFieldName.getText());
            videogameToModify.setCompanyName(textFieldCompany.getText());
            videogameToModify.setGameGenre(comboBoxGenre.getValue());
            videogameToModify.setPlatforms(comboBoxPlatforms.getValue());
            videogameToModify.setPegi(comboBoxPEGI.getValue());
            videogameToModify.setPrice(spinnerPrice.getValue());
            videogameToModify.setStock(spinnerStock.getValue());
            videogameToModify.setReleaseDate(Date.valueOf(datePickerReleaseDate.getValue()));

            logger.info("Attempting to modify game in database - Game ID: " + videogameToModify.getIdVideogame());
            
            if (cont.modifyGame(videogameToModify)) {
                logger.info("Game modified successfully - Game: " + videogameToModify.getName() + 
                           " (ID: " + videogameToModify.getIdVideogame() + ")");
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Game modified successfully!");
                success.setHeaderText(videogameToModify.getName() + " was modified successfully.");
                success.setContentText("The game " + videogameToModify.getName() + " was successfully modified in the store.");
                success.showAndWait();

                // Recargar la tabla en AdminShopController
                if (adminShopController != null) {
                    logger.info("Notifying AdminShopController to reload games");
                    adminShopController.reloadGames();
                } else {
                    logger.warning("AdminShopController reference is null - cannot reload games table");
                }

                // Cerrar la ventana
                Stage currentStage = (Stage) buttonModifyGame.getScene().getWindow();
                currentStage.close();
                
                logger.info("ModifyGame window closed after successful modification");
                
            } else {
                logger.warning("Game modification failed in database - Game: " + videogameToModify.getName());
                
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("ERROR");
                error.setContentText("There was an error while attempting to modify the game. Check the fields to see if anything's wrong.");
                error.showAndWait();
            }
            
        } catch (Exception ex) {
            logger.severe("Unexpected error modifying game: " + ex.getMessage());
            
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("ERROR");
            error.setHeaderText("Unexpected Error");
            error.setContentText("An unexpected error occurred: " + ex.getMessage());
            error.showAndWait();
            
            // También registrar el stack trace completo
            Logger.getLogger(ModifyGameAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialization method called automatically by JavaFX after loading the FXML file.
     * Sets up ComboBoxes with enum values and configures Spinners with appropriate ranges.
     *
     * @param url Location used to resolve relative paths for the root object
     * @param rb Resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing ModifyGameAdminController");
        
        try {
            comboBoxGenre.getItems().addAll(GameGenre.values());
            comboBoxPlatforms.getItems().addAll(Platform.values());
            comboBoxPEGI.getItems().addAll(PEGI.values());
            
            SpinnerValueFactory<Integer> stockValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 1);
            spinnerStock.setValueFactory(stockValueFactory);
            spinnerStock.setEditable(true);
            
            SpinnerValueFactory<Double> priceValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, 0.0, 1.0);
            spinnerPrice.setValueFactory(priceValueFactory);
            spinnerPrice.setEditable(true);
            
            logger.info("ModifyGameAdminController initialized successfully");
            logger.info("Loaded enums - Genres: " + GameGenre.values().length + 
                       ", Platforms: " + Platform.values().length + 
                       ", PEGI ratings: " + PEGI.values().length);
            
        } catch (Exception e) {
            logger.severe("Error initializing ModifyGameAdminController: " + e.getMessage());
        }
    }
}