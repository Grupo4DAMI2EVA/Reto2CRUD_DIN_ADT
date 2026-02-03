package controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
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

public class AddGamesAdminController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
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
    private Button buttonAddGame;
    private Controller cont;
    private AdminShopController adminShopController;
    
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
            
            FileHandler fileHandler = new FileHandler("logs/AddGamesAdmin.log", true);
            
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
        logger.info("Setting controller in AddGamesAdminController");
        this.cont = cont;
    }

    public void setAdminShopController(AdminShopController adminShopController) {
        logger.info("Setting AdminShopController reference");
        this.adminShopController = adminShopController;
    }

    @FXML
    private void addGame(MouseEvent event) {
        logger.info("Add Game button clicked");
        
        try {
            // Forzar actualización de los valores editables de los spinners
            logger.info("Processing spinner values before adding game");
            
            if (spinnerPrice.getEditor().getText() != null && !spinnerPrice.getEditor().getText().isEmpty()) {
                try {
                    double priceValue = Double.valueOf(spinnerPrice.getEditor().getText());
                    spinnerPrice.getValueFactory().setValue(priceValue);
                    logger.info("Spinner price updated from editor: " + priceValue);
                } catch (NumberFormatException e) {
                    logger.warning("Invalid price format in spinner editor: " + spinnerPrice.getEditor().getText());
                }
            }
            
            if (spinnerStock.getEditor().getText() != null && !spinnerStock.getEditor().getText().isEmpty()) {
                try {
                    int stockValue = Integer.valueOf(spinnerStock.getEditor().getText());
                    spinnerStock.getValueFactory().setValue(stockValue);
                    logger.info("Spinner stock updated from editor: " + stockValue);
                } catch (NumberFormatException e) {
                    logger.warning("Invalid stock format in spinner editor: " + spinnerStock.getEditor().getText());
                }
            }
            
            // Obtener valores del formulario
            String gameName = textFieldName.getText();
            Platform platform = comboBoxPlatforms.getValue();
            String company = textFieldCompany.getText();
            GameGenre genre = comboBoxGenre.getValue();
            PEGI pegi = comboBoxPEGI.getValue();
            Double price = spinnerPrice.getValue();
            Integer stock = spinnerStock.getValue();
            LocalDate releaseDate = datePickerReleaseDate.getValue();
            
            // Registrar datos del juego a añadir
            logger.info("Attempting to add new game with data:");
            logger.info("  Name: " + gameName);
            logger.info("  Company: " + company);
            logger.info("  Genre: " + (genre != null ? genre.name() : "null"));
            logger.info("  Platform: " + (platform != null ? platform.name() : "null"));
            logger.info("  PEGI: " + (pegi != null ? pegi.name() : "null"));
            logger.info("  Price: " + price);
            logger.info("  Stock: " + stock);
            logger.info("  Release Date: " + releaseDate);
            
            // Validar campos requeridos
            if (gameName == null || gameName.trim().isEmpty() ||
                company == null || company.trim().isEmpty() ||
                genre == null || platform == null || pegi == null ||
                releaseDate == null) {
                
                logger.warning("Validation failed - Missing required fields for new game");
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Validation Error");
                error.setHeaderText("Missing Fields");
                error.setContentText("Please fill in all required fields.");
                error.showAndWait();
                return;
            }
            
            logger.info("Form validation passed - Attempting to add game to database");
            
            // Intentar añadir el juego
            boolean success = cont.addGame(company, genre, gameName, platform, pegi, price, stock, Date.valueOf(releaseDate));
            
            if (success) {
                logger.info("Game added SUCCESSFULLY to database: " + gameName);
                
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Game added successfully!");
                successAlert.setHeaderText(gameName + " was added successfully.");
                successAlert.setContentText("The game " + gameName + " was successfully added to the list of games in the store.");
                successAlert.showAndWait();
                
                logger.info("Showing success alert for game: " + gameName);

                // Preguntar si quiere añadir más juegos
                logger.info("Asking admin if they want to add more games");
                Alert choice = new Alert(Alert.AlertType.CONFIRMATION);
                choice.setTitle("Add more?");
                choice.setHeaderText("Do you want to add more games?");
                choice.showAndWait();
                
                // Recargar la tabla en AdminShopController (comentado en tu código)
                /*if (adminShopController != null) {
                    adminShopController.reloadGames();
                }*/
                
                if (choice.getResult().equals(ButtonType.OK)) {
                    logger.info("Admin chose to add another game - Clearing form");
                    
                    // Limpiar campos para añadir otro juego
                    textFieldName.clear();
                    comboBoxPlatforms.valueProperty().set(null);
                    textFieldCompany.clear();
                    spinnerStock.getValueFactory().setValue(0);
                    comboBoxGenre.valueProperty().set(null);
                    spinnerPrice.getValueFactory().setValue(0.0);
                    comboBoxPEGI.valueProperty().set(null);
                    datePickerReleaseDate.setValue(LocalDate.now());
                    
                    logger.info("Form cleared - Ready for new game entry");
                    
                } else {
                    logger.info("Admin chose NOT to add more games - Closing window");
                    
                    // Cerrar la ventana
                    Stage currentStage = (Stage) buttonAddGame.getScene().getWindow();
                    currentStage.close();
                    
                    logger.info("AddGamesAdmin window closed");
                }
                
            } else {
                logger.warning("Failed to add game to database: " + gameName);
                
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("ERROR");
                error.setContentText("There was an error while attempting to add the game. Check the fields to see if anything's wrong.");
                error.showAndWait();
            }
            
        } catch (Exception ex) {
            logger.severe("Exception while adding game: " + ex.getMessage());
            
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("ERROR");
            error.setContentText("There was an error while attempting to add the game. Check the fields to see if anything's wrong.");
            error.showAndWait();
        }
    }
    
    private void setupCBoxes() {
        logger.info("Setting up combo boxes and spinners");
        
        try {
            comboBoxGenre.getItems().addAll(GameGenre.values());
            comboBoxPlatforms.getItems().addAll(Platform.values());
            comboBoxPEGI.getItems().addAll(PEGI.values());
            
            logger.info("ComboBoxes populated - Genres: " + GameGenre.values().length + 
                       ", Platforms: " + Platform.values().length + 
                       ", PEGI: " + PEGI.values().length);
            
            SpinnerValueFactory<Integer> stockValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 1);
            spinnerStock.setValueFactory(stockValueFactory);
            spinnerStock.setEditable(true);
            
            SpinnerValueFactory<Double> priceValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, 0.0, 1.0);
            spinnerPrice.setValueFactory(priceValueFactory);
            spinnerPrice.setEditable(true);
            
            logger.info("Spinners configured - Stock range: 0-1000, Price range: 0.0-1000.0");
            
        } catch (Exception e) {
            logger.severe("Error setting up combo boxes: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing AddGamesAdminController");
        
        try {
            setupCBoxes();
            datePickerReleaseDate.setValue(LocalDate.now());
            
            logger.info("AddGamesAdminController initialized successfully");
            logger.info("Release date set to: " + LocalDate.now());
            
        } catch (Exception e) {
            logger.severe("Error initializing AddGamesAdminController: " + e.getMessage());
        }
    }
}