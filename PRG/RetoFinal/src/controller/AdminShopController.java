package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Profile;
import model.Videogame;

public class AdminShopController implements Initializable {

    private static final Logger logger = Logger.getLogger(AdminShopController.class.getName());
    private static boolean loggerInitialized = false;
    
    private Label label_Username;
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
    private Button buttonAdd;
    @FXML
    private Button buttonExit;
    @FXML
    private Button buttonModify;
    @FXML
    private Button buttonDelete;

    private Profile profile;
    private Controller cont;
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
            
            Handler fileHandler = new FileHandler("logs/admin_shop.log", true);
            
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
            logger.info("AdminShopController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setUser(Profile profile) {
        try {
            this.profile = profile;
            if (label_Username != null) {
                label_Username.setText(profile.getUsername());
            }
            logger.info("User profile set for AdminShopController: " + profile.getUsername());
        } catch (Exception e) {
            logger.severe("Error setting user profile: " + e.getMessage());
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for AdminShopController");
        } catch (Exception e) {
            logger.severe("Error setting main controller: " + e.getMessage());
        }
    }

    @FXML
    private void addGame(MouseEvent event) {
        try {
            logger.info("Opening Add Game window");
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddGamesAdminController.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Game");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
            
            logger.info("Add Game window opened successfully");
            
        } catch (IOException e) {
            logger.severe("Error opening Add Game window: " + e.getMessage());
            showAlert("Error", "Could not open the Add Game window.");
        } catch (Exception e) {
            logger.severe("Unexpected error in addGame: " + e.getMessage());
        }
    }

    @FXML
    private void modifyGame(ActionEvent event) {
        try {
            logger.info("Attempting to modify game");
            
            if (selected == null) {
                logger.severe("Modify attempt without selecting a game");
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error!");
                alert.setHeaderText("No Selection");
                alert.setContentText("Please select a game before attempting to modify it.");
                alert.showAndWait();
                
                logger.info("Alert shown: No game selected for modification");
            } else {
                logger.info("Game selected for modification: " + selected.getName());
                // Modify method here
                // TODO: Implement game modification logic
                
                logger.info("Modify functionality not yet implemented for: " + selected.getName());
            }
            
        } catch (Exception e) {
            logger.severe("Error in modifyGame: " + e.getMessage());
            showAlert("Error", "An error occurred while trying to modify the game.");
        }
    }

    @FXML
    private void deleteGame(ActionEvent event) {
        try {
            logger.info("Attempting to delete game");
            
            if (selected == null) {
                logger.severe("Delete attempt without selecting a game");
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error!");
                alert.setHeaderText("No Selection");
                alert.setContentText("Please select a game before attempting to delete it.");
                alert.showAndWait();
                
                logger.info("Alert shown: No game selected for deletion");
            } else {
                logger.info("Game selected for deletion: " + selected.getName());
                
                // Ask for confirmation before deleting
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Deletion");
                confirmation.setHeaderText("Delete Game: " + selected.getName());
                confirmation.setContentText("Are you sure you want to delete this game? This action cannot be undone.");
                
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        logger.info("User confirmed deletion of game: " + selected.getName());
                        // Delete method here
                        // TODO: Implement game deletion logic
                        
                        logger.info("Game marked for deletion: " + selected.getName());
                        // After deletion, clear selection
                        selected = null;
                        labelGameInfo.setText("No game selected");
                        tableViewGames.getSelectionModel().clearSelection();
                        
                        logger.info("Delete functionality not yet implemented");
                    } else {
                        logger.info("User canceled deletion of game: " + selected.getName());
                    }
                });
            }
            
        } catch (Exception e) {
            logger.severe("Error in deleteGame: " + e.getMessage());
            showAlert("Error", "An error occurred while trying to delete the game.");
        }
    }

    @FXML
    private void getSelectedTableItem() {
        try {
            selected = tableViewGames.getSelectionModel().getSelectedItem();
            if (selected != null) {
                labelGameInfo.setText(selected.getName());
                logger.info("Game selected from table: " + selected.getName());
            } else {
                logger.info("No game selected from table");
            }
        } catch (Exception e) {
            logger.severe("Error getting selected table item: " + e.getMessage());
        }
    }

    @FXML
    private void exit(MouseEvent event) {
        try {
            logger.info("Closing Admin Shop window");
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
            logger.info("Admin Shop window closed successfully");
            
        } catch (Exception e) {
            logger.severe("Error closing Admin Shop window: " + e.getMessage());
            showAlert("Error", "Could not close the window.");
        }
    }

    @FXML
    private void searchGames(ActionEvent event) {
        try {
            String searchText = textFieldSearch.getText();
            String genreFilter = textFieldGenre.getText();
            String platformFilter = textFieldPlatform.getText();
            
            logger.info("Searching games - Search: '" + searchText + 
                       "', Genre: '" + genreFilter + 
                       "', Platform: '" + platformFilter + "'");
            
            // TODO: Implement search logic
            logger.info("Search functionality not yet implemented");
            
            showAlert("Information", "Search functionality is not yet implemented.");
            
        } catch (Exception e) {
            logger.severe("Error in searchGames: " + e.getMessage());
            showAlert("Error", "An error occurred while searching for games.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing AdminShopController");
            
            // Initialize table columns if needed
            initializeTableColumns();
            
            // Set up selection listener
            tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> getSelectedTableItem());
            
            logger.info("AdminShopController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing AdminShopController: " + e.getMessage());
            showAlert("Initialization Error", 
                "Could not initialize the Admin Shop. Please restart the application.");
        }
    }

    private void initializeTableColumns() {
        try {
            // TODO: Initialize table column cell value factories
            // This would typically connect to Videogame properties
            logger.info("Table columns initialization placeholder");
            
        } catch (Exception e) {
            logger.severe("Error initializing table columns: " + e.getMessage());
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

    // Method to load games into the table (to be called from outside)
    public void loadGames() {
        try {
            logger.info("Loading games into table");
            
            // TODO: Implement game loading logic
            // tableViewGames.setItems(...);
            
            logger.info("Game loading functionality not yet implemented");
            
        } catch (Exception e) {
            logger.severe("Error loading games: " + e.getMessage());
            showAlert("Error", "Could not load games into the table.");
        }
    }

    // Method to refresh the game list
    public void refreshGameList() {
        try {
            logger.info("Refreshing game list");
            
            // Clear current selection
            selected = null;
            labelGameInfo.setText("No game selected");
            tableViewGames.getSelectionModel().clearSelection();
            
            // TODO: Refresh data from database
            logger.info("Refresh functionality not yet implemented");
            
        } catch (Exception e) {
            logger.severe("Error refreshing game list: " + e.getMessage());
        }
    }
}