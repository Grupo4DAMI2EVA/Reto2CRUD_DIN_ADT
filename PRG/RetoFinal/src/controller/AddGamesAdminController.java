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

    @FXML
    private void addGame(MouseEvent event) {
        try {
            logger.info("Starting game addition process");
            
            String gameName = textFieldName.getText();
            
            if (gameName == null || gameName.trim().isEmpty()) {
                logger.severe("Attempt to add game without name");
                showAlert("Error", "Game name cannot be empty.");
                return;
            }
            
            logger.info("Validating game data: " + gameName);
            
            Platform platform = comboBoxPlatforms.getValue();
            if (platform == null) {
                logger.severe("Platform not selected for game: " + gameName);
                showAlert("Error", "You must select a platform.");
                return;
            }
            
            String company = textFieldCompany.getText();
            if (company == null || company.trim().isEmpty()) {
                logger.severe("Company not specified for game: " + gameName);
                showAlert("Error", "Developer company cannot be empty.");
                return;
            }
            
            GameGenre genre = comboBoxGenre.getValue();
            if (genre == null) {
                logger.severe("Genre not selected for game: " + gameName);
                showAlert("Error", "You must select a genre.");
                return;
            }
            
            Integer stock = spinnerStock.getValue();
            if (stock == null || stock <= 0) {
                logger.severe("Invalid stock for game: " + gameName + " - Stock: " + stock);
                showAlert("Error", "Stock must be greater than 0.");
                return;
            }
            
            Double price = spinnerPrice.getValue();
            if (price == null || price <= 0) {
                logger.severe("Invalid price for game: " + gameName + " - Price: " + price);
                showAlert("Error", "Price must be greater than 0.");
                return;
            }
            
            PEGI pegi = comboBoxPEGI.getValue();
            if (pegi == null) {
                logger.severe("PEGI not specified for game: " + gameName);
                showAlert("Error", "PEGI rating cannot be empty.");
                return;
            }
            
            java.time.LocalDate releaseDate = datePickerReleaseDate.getValue();
            if (releaseDate == null) {
                logger.severe("Release date not specified for game: " + gameName);
                showAlert("Error", "You must select a release date.");
                return;
            }
            
            logger.info("Game data validated successfully - " + gameName);
            
            if (cont.addGame(company, genre, gameName, platform, pegi, price, stock, Date.valueOf(releaseDate))) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Game added successfully!");
                success.setHeaderText(gameName + " was added successfully.");
                success.setContentText("The game " + gameName + " was successfully added to the list of games in the store.");
                success.showAndWait();
                
                logger.info("Game added successfully: " + gameName + " | Platform: " + platform + " | Company: " + company + " | Genre: " + genre + " | Stock: " + stock + " | Price: $" + price + " | PEGI: " + pegi + " | Date: " + releaseDate);

                Alert choice = new Alert(Alert.AlertType.CONFIRMATION);
                choice.setTitle("Add more?");
                choice.setHeaderText("Do you want to add more games?");
                choice.showAndWait();
                
                if (choice.getResult().equals(ButtonType.CLOSE)) {
                    logger.info("User closed window after adding game");
                    Stage currentStage = (Stage) buttonAddGame.getScene().getWindow();
                    currentStage.close();
                    logger.info("Add games window closed");
                } else {
                    logger.info("User chose to add more games");
                    clearForm();
                }
            } else {
                logger.severe("Database error while adding game: " + gameName);
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("ERROR");
                error.setContentText("There was an error while attempting to add the game. Check the fields to see if anything's wrong.");
                error.showAndWait();
            }
            
        } catch (Exception e) {
            logger.severe("Unexpected error adding game: " + e.getMessage());
            showAlert("Error", "An unexpected error occurred while adding the game.");
        }
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
            logger.severe("Error clearing form: " + e.getMessage());
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing AddGamesAdminController");
            
            setupSpinners();
            loadComboBoxData();
            
            logger.info("AddGamesAdminController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing AddGamesAdminController: " + e.getMessage());
            showAlert("Initialization Error", 
                "Could not initialize the add games form.");
        }
    }

    private void setupSpinners() {
        try {
            SpinnerValueFactory<Integer> stockFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1);
            spinnerStock.setValueFactory(stockFactory);
            
            SpinnerValueFactory<Double> priceFactory = 
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.99, 299.99, 59.99, 0.01);
            spinnerPrice.setValueFactory(priceFactory);
            
            logger.info("Spinners configured successfully");
            
        } catch (Exception e) {
            logger.severe("Error configuring spinners: " + e.getMessage());
        }
    }

    private void loadComboBoxData() {
        try {
            comboBoxPlatforms.getItems().setAll(Platform.values());
            comboBoxGenre.getItems().setAll(GameGenre.values());
            comboBoxPEGI.getItems().setAll(PEGI.values());
            
            logger.info("ComboBoxes loaded - Platforms: " + Platform.values().length + 
                       ", Genres: " + GameGenre.values().length + 
                       ", PEGI: " + PEGI.values().length);
            
        } catch (Exception e) {
            logger.severe("Error loading ComboBox data: " + e.getMessage());
        }
    }
}