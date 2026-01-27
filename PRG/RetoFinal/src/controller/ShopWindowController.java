package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Videogame;

public class ShopWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Label labelWelcome;
    @FXML
    private Label labelBalance;
    @FXML
    private TableView<Videogame> tableViewGames;
    @FXML
    private TableColumn<?, ?> colTitle;
    @FXML
    private TableColumn<?, ?> colGenre;
    @FXML
    private TableColumn<?, ?> colPlatform;
    @FXML
    private TableColumn<?, ?> colPrice;
    @FXML
    private TableColumn<?, ?> colPegi;
    @FXML
    private TableColumn<?, ?> colStock;
    @FXML
    private TableColumn<?, ?> colCompanyName;
    @FXML
    private TableColumn<?, ?> colReleaseDate;
    @FXML
    private TextField textFieldSearch;
    @FXML
    private TextField textFieldGenre;
    @FXML
    private TextField textFieldPlatform;
    @FXML
    private Button buttonSearch;
    @FXML
    private Label labelGameInfo;
    @FXML
    private Button buttonAddToCart;
    @FXML
    private Button buttonCart;
    @FXML
    private Button buttonExit;
    @FXML
    private Button buttonReview;

    private Videogame selected;

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
            
            Handler fileHandler = new FileHandler("logs/shop_window.log", true);
            
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
            logger.info("ShopWindowController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing ShopWindowController");
            
            configureTableColumns();
            setupSelectionListener();
            setupSearchButton();
            updateButtonStates();
            
            logger.info("ShopWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing ShopWindowController: %s", e.getMessage()));
            showAlert("Initialization Error", 
                "Could not initialize the shop window. Please restart the application.");
        }
    }

    private void configureTableColumns() {
        try {
            logger.info("Configuring table columns");
            
        } catch (Exception e) {
            logger.severe(String.format("Error configuring table columns: %s", e.getMessage()));
        }
    }

    private void setupSelectionListener() {
        try {
            logger.info("Setting up table selection listener");
            
            tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> getSelectedTableItem());
            
            logger.info("Selection listener configured");
            
        } catch (Exception e) {
            logger.severe(String.format("Error setting up selection listener: %s", e.getMessage()));
        }
    }

    private void setupSearchButton() {
        try {
            logger.info("Setting up search button");
            
            buttonSearch.setOnAction(this::searchGames);
            
            logger.info("Search button configured");
            
        } catch (Exception e) {
            logger.severe(String.format("Error setting up search button: %s", e.getMessage()));
        }
    }

    @FXML
    private void getSelectedTableItem() {
        try {
            selected = tableViewGames.getSelectionModel().getSelectedItem();
            if (selected != null) {
                labelGameInfo.setText(selected.getName());
                logger.info(String.format("Game selected: %s", selected.getName()));
            } else {
                labelGameInfo.setText("No game selected");
                logger.info("No game selected from table");
            }
            updateButtonStates();
            
        } catch (Exception e) {
            logger.severe(String.format("Error getting selected table item: %s", e.getMessage()));
        }
    }
    
    @FXML
    private void addToCart(ActionEvent event) {
        try {
            logger.info("Attempting to add game to cart");
            
            if (selected == null) {
                handleAddToCartWithoutSelection();
            } else {
                handleAddToCartWithSelection();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in addToCart: %s", e.getMessage()));
            showAlert("Error", "Could not add the game to the cart.");
        }
    }

    private void handleAddToCartWithoutSelection() {
        logger.warning("Add to cart attempt without selecting a game");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error!");
        alert.setHeaderText("No Selection");
        alert.setContentText("Please select a game before adding it to the cart.");
        alert.showAndWait();
        
        logger.info("Alert shown: No game selected for cart");
    }

    private void handleAddToCartWithSelection() {
        logger.info(String.format("Adding game to cart: %s", selected.getName()));
        
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Added to Cart");
        success.setHeaderText("Game Added");
        success.setContentText(String.format("%s has been added to your cart.", selected.getName()));
        success.showAndWait();
        
        logger.info(String.format("Game added to cart: %s", selected.getName()));
    }

    @FXML
    private void searchGames(ActionEvent event) {
        try {
            String searchText = textFieldSearch.getText();
            String genreFilter = textFieldGenre.getText();
            String platformFilter = textFieldPlatform.getText();
            
            logger.info(String.format("Searching games - Search: '%s', Genre: '%s', Platform: '%s'", 
                       searchText, genreFilter, platformFilter));
            
            showAlert("Information", "Search functionality is not yet implemented.");
            
        } catch (Exception e) {
            logger.severe(String.format("Error in searchGames: %s", e.getMessage()));
            showAlert("Error", "An error occurred while searching for games.");
        }
    }

    @FXML
    private void viewCart(ActionEvent event) {
        try {
            logger.info("Opening cart window");
            
            showAlert("Information", "Cart view functionality is not yet implemented.");
            
        } catch (Exception e) {
            logger.severe(String.format("Error in viewCart: %s", e.getMessage()));
            showAlert("Error", "Could not open the cart window.");
        }
    }

    @FXML
    private void writeReview(ActionEvent event) {
        try {
            logger.info("Opening review window");
            
            if (selected == null) {
                handleReviewWithoutSelection();
            } else {
                handleReviewWithSelection();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in writeReview: %s", e.getMessage()));
            showAlert("Error", "Could not open the review window.");
        }
    }

    private void handleReviewWithoutSelection() {
        logger.warning("Review attempt without selecting a game");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error!");
        alert.setHeaderText("No Selection");
        alert.setContentText("Please select a game before writing a review.");
        alert.showAndWait();
        
        logger.info("Alert shown: No game selected for review");
    }

    private void handleReviewWithSelection() {
        logger.info(String.format("Opening review window for game: %s", selected.getName()));
        
        showAlert("Information", "Review functionality is not yet implemented.");
    }

    @FXML
    private void exitShop(ActionEvent event) {
        try {
            logger.info("Closing shop window");
            
            showAlert("Information", "Exit functionality is not yet implemented.");
            
        } catch (Exception e) {
            logger.severe(String.format("Error in exitShop: %s", e.getMessage()));
            showAlert("Error", "Could not close the shop window.");
        }
    }

    private void updateButtonStates() {
        try {
            boolean hasGameSelected = (selected != null);
            
            buttonAddToCart.setDisable(!hasGameSelected);
            buttonReview.setDisable(!hasGameSelected);
            
            logger.info(String.format("Button states updated - Game selected: %s", hasGameSelected));
            
        } catch (Exception e) {
            logger.severe(String.format("Error updating button states: %s", e.getMessage()));
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

    public void loadGames() {
        try {
            logger.info("Loading games into table");
            
        } catch (Exception e) {
            logger.severe(String.format("Error loading games: %s", e.getMessage()));
            showAlert("Error", "Could not load games into the table.");
        }
    }

    public void setUserInfo(String username, double balance) {
        try {
            logger.info(String.format("Setting user info - Username: %s, Balance: %.2f", username, balance));
            
            if (labelWelcome != null) {
                labelWelcome.setText(String.format("Welcome, %s!", username));
            }
            
            if (labelBalance != null) {
                labelBalance.setText(String.format("Balance: $%.2f", balance));
            }
            
            logger.info("User info displayed successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error setting user info: %s", e.getMessage()));
        }
    }

    public void clearSelection() {
        try {
            logger.info("Clearing game selection");
            
            selected = null;
            tableViewGames.getSelectionModel().clearSelection();
            labelGameInfo.setText("No game selected");
            updateButtonStates();
            
            logger.info("Selection cleared");
            
        } catch (Exception e) {
            logger.severe(String.format("Error clearing selection: %s", e.getMessage()));
        }
    }
}