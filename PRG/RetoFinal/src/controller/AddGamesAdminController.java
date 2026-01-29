package controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.logging.*;
import model.*;

public class AddGamesAdminController implements Initializable {

    private static final Logger logger = Logger.getLogger(AddGamesAdminController.class.getName());
    private static boolean loggerInicializado = false;
    
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
        inicializarLogger();
    }
    
    private static synchronized void inicializarLogger() {
        if (loggerInicializado) {
            return;
        }
        
        try {
            java.io.File logsFolder = new java.io.File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }
            
            Handler fileHandler = new FileHandler("logs/admin_games.log", true);
            
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
            
            loggerInicializado = true;
            logger.info("AddGamesAdminController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInicializado = true;
        }
    }

    public void setCont(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set in AddGamesAdminController");
        } catch (Exception e) {
            logger.severe("Error setting main controller: " + e.getMessage());
        }
    }

    public void setAdminShopController(AdminShopController adminShopController) {
        this.adminShopController = adminShopController;
    }

    @FXML
    private void addGame(MouseEvent event) {
        try {
            logger.info("Starting game addition process");
            
            // Forzar actualización de los valores editables de los spinners
            if (spinnerPrice.getEditor().getText() != null && !spinnerPrice.getEditor().getText().isEmpty()) {
                try {
                    spinnerPrice.getValueFactory().setValue(Double.valueOf(spinnerPrice.getEditor().getText()));
                } catch (NumberFormatException e) {
                    // Mantener valor actual si hay error
                }
            }
            if (spinnerStock.getEditor().getText() != null && !spinnerStock.getEditor().getText().isEmpty()) {
                try {
                    spinnerStock.getValueFactory().setValue(Integer.valueOf(spinnerStock.getEditor().getText()));
                } catch (NumberFormatException e) {
                    // Mantener valor actual si hay error
                }
            }
            
            String gameName = textFieldName.getText();
            Platform platform = comboBoxPlatforms.getValue();
            String company = textFieldCompany.getText();
            GameGenre genre = comboBoxGenre.getValue();
            Integer stock = spinnerStock.getValue();
            Double price = spinnerPrice.getValue();
            PEGI pegi = comboBoxPEGI.getValue();
            LocalDate releaseDate = datePickerReleaseDate.getValue();
            
            if (!validateGameData(gameName, platform, company, genre, stock, price, pegi, releaseDate)) {
                return;
            }
            
            logger.info("Game data validated successfully - " + gameName);
            
            if (cont.addGame(company, genre, gameName, platform, pegi, price, stock, Date.valueOf(releaseDate))) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Game added successfully!");
                success.setHeaderText(gameName + " was added successfully.");
                success.setContentText("The game " + gameName + " was successfully added to the list of games in the store.");
                success.showAndWait();
                
                logger.info(String.format("Game added successfully: %s | Platform: %s | Company: %s | Genre: %s | Stock: %d | Price: $%.2f | PEGI: %s | Date: %s",
                    gameName, platform, company, genre, stock, price, pegi, releaseDate));

                Alert choice = new Alert(Alert.AlertType.CONFIRMATION);
                choice.setTitle("Add more?");
                choice.setHeaderText("Do you want to add more games?");
                choice.showAndWait();
                
                // Recargar la tabla en AdminShopController
                if (adminShopController != null) {
                    adminShopController.reloadGames();
                }
                
                if (choice.getResult().equals(ButtonType.OK)) {
                    logger.info("User chose to add more games");
                    clearForm();
                } else {
                    logger.info("User closed window after adding game");
                    Stage currentStage = (Stage) buttonAddGame.getScene().getWindow();
                    currentStage.close();
                    logger.info("Add games window closed");
                }
            } else {
                logger.severe(String.format("Database error while adding game: %s", gameName));
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("ERROR");
                error.setContentText("There was an error while attempting to add the game. Check the fields to see if anything's wrong.");
                error.showAndWait();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Unexpected error adding game: %s", e.getMessage()));
            showAlert("Error", "An unexpected error occurred while adding the game.");
        }
    }

    private boolean validateGameData(String gameName, Platform platform, String company, 
                                   GameGenre genre, Integer stock, Double price, 
                                   PEGI pegi, LocalDate releaseDate) {
        
        if (gameName == null || gameName.trim().isEmpty()) {
            logger.severe("Attempt to add game without name");
            showAlert("Error", "Game name cannot be empty.");
            return false;
        }
        
        if (platform == null) {
            logger.severe(String.format("Platform not selected for game: %s", gameName));
            showAlert("Error", "You must select a platform.");
            return false;
        }
        
        if (company == null || company.trim().isEmpty()) {
            logger.severe(String.format("Company not specified for game: %s", gameName));
            showAlert("Error", "Developer company cannot be empty.");
            return false;
        }
        
        if (genre == null) {
            logger.severe(String.format("Genre not selected for game: %s", gameName));
            showAlert("Error", "You must select a genre.");
            return false;
        }
        
        if (stock == null || stock <= 0) {
            logger.severe(String.format("Invalid stock for game: %s - Stock: %d", gameName, stock));
            showAlert("Error", "Stock must be greater than 0.");
            return false;
        }
        
        if (price == null || price <= 0) {
            logger.severe(String.format("Invalid price for game: %s - Price: %.2f", gameName, price));
            showAlert("Error", "Price must be greater than 0.");
            return false;
        }
        
        if (pegi == null) {
            logger.severe(String.format("PEGI not specified for game: %s", gameName));
            showAlert("Error", "PEGI rating cannot be empty.");
            return false;
        }
        
        if (releaseDate == null) {
            logger.severe(String.format("Release date not specified for game: %s", gameName));
            showAlert("Error", "You must select a release date.");
            return false;
        }
        
        return true;
    }

    private void clearForm() {
        try {
            logger.info("Clearing form for new game");
            
            textFieldName.clear();
            comboBoxPlatforms.setValue(null);
            textFieldCompany.clear();
            spinnerStock.getValueFactory().setValue(1);
            comboBoxGenre.setValue(null);
            spinnerPrice.getValueFactory().setValue(59.99);
            comboBoxPEGI.setValue(null);
            datePickerReleaseDate.setValue(null);
            
            logger.info("Form cleared successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error clearing form: %s", e.getMessage()));
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
            logger.severe(String.format("Error showing alert: %s", e.getMessage()));
        }
    }
    
    private void setupCBoxes() {
        comboBoxGenre.getItems().addAll(GameGenre.values());
        comboBoxPlatforms.getItems().addAll(Platform.values());
        comboBoxPEGI.getItems().addAll(PEGI.values());
        
        // Initialize Spinner for Stock (0 to 1000, step 1, initial value 0)
        SpinnerValueFactory<Integer> stockValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 1);
        spinnerStock.setValueFactory(stockValueFactory);
        spinnerStock.setEditable(true);
        
        // Initialize Spinner for Price (0.0 to 1000.0, step 0.01, initial value 0.0)
        SpinnerValueFactory<Double> priceValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, 0.0, 0.01);
        spinnerPrice.setValueFactory(priceValueFactory);
        spinnerPrice.setEditable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing AddGamesAdminController");
            
            setupCBoxes();
            
            logger.info("AddGamesAdminController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing AddGamesAdminController: %s", e.getMessage()));
            showAlert("Initialization Error", 
                "Could not initialize the add games form.");
        }
    }
}