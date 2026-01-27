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
            
            FileHandler fileHandler = new FileHandler("logs/admin_shop.log", true);
            
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
            logger.info(String.format("User profile set for AdminShopController: %s", profile.getUsername()));
        } catch (Exception e) {
            logger.severe(String.format("Error setting user profile: %s", e.getMessage()));
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for AdminShopController");
        } catch (Exception e) {
            logger.severe(String.format("Error setting main controller: %s", e.getMessage()));
        }
    }

    @FXML
    private void addGame(MouseEvent event) {
        try {
            logger.info("Opening Add Game window");
            
            openAddGameWindow(event);
            
        } catch (IOException e) {
            logger.severe(String.format("Error opening Add Game window: %s", e.getMessage()));
            showAlert("Error", "Could not open the Add Game window.");
        } catch (Exception e) {
            logger.severe(String.format("Unexpected error in addGame: %s", e.getMessage()));
        }
    }

    private void openAddGameWindow(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddGamesAdminController.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Add New Game");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.show();
        
        logger.info("Add Game window opened successfully");
    }

    @FXML
    private void modifyGame(ActionEvent event) {
        try {
            logger.info("Attempting to modify game");
            
            if (selected == null) {
                handleModifyWithoutSelection();
            } else {
                handleModifyWithSelection();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in modifyGame: %s", e.getMessage()));
            showAlert("Error", "An error occurred while trying to modify the game.");
        }
    }

    private void handleModifyWithoutSelection() {
        logger.severe("Modify attempt without selecting a game");
        
        showInformationAlert("Error!", "No Selection", 
            "Please select a game before attempting to modify it.");
        
        logger.info("Alert shown: No game selected for modification");
    }

    private void handleModifyWithSelection() {
        logger.info(String.format("Game selected for modification: %s", selected.getName()));
        
        logger.info(String.format("Modify functionality not yet implemented for: %s", selected.getName()));
    }

    @FXML
    private void deleteGame(ActionEvent event) {
        try {
            logger.info("Attempting to delete game");
            
            if (selected == null) {
                handleDeleteWithoutSelection();
            } else {
                handleDeleteWithSelection();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in deleteGame: %s", e.getMessage()));
            showAlert("Error", "An error occurred while trying to delete the game.");
        }
    }

    private void handleDeleteWithoutSelection() {
        logger.severe("Delete attempt without selecting a game");
        
        showInformationAlert("Error!", "No Selection", 
            "Please select a game before attempting to delete it.");
        
        logger.info("Alert shown: No game selected for deletion");
    }

    private void handleDeleteWithSelection() {
        logger.info(String.format("Game selected for deletion: %s", selected.getName()));
        
        Alert confirmation = createDeletionConfirmationAlert();
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                confirmAndDeleteGame();
            } else {
                handleDeletionCancellation();
            }
        });
    }

    private Alert createDeletionConfirmationAlert() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(String.format("Delete Game: %s", selected.getName()));
        confirmation.setContentText("Are you sure you want to delete this game? This action cannot be undone.");
        return confirmation;
    }

    private void confirmAndDeleteGame() {
        logger.info(String.format("User confirmed deletion of game: %s", selected.getName()));
        
        logger.info(String.format("Game marked for deletion: %s", selected.getName()));
        
        clearGameSelection();
        
        logger.info("Delete functionality not yet implemented");
    }

    private void handleDeletionCancellation() {
        logger.info(String.format("User canceled deletion of game: %s", selected.getName()));
    }

    private void clearGameSelection() {
        selected = null;
        labelGameInfo.setText("No game selected");
        tableViewGames.getSelectionModel().clearSelection();
    }

    @FXML
    private void getSelectedTableItem() {
        try {
            selected = tableViewGames.getSelectionModel().getSelectedItem();
            if (selected != null) {
                labelGameInfo.setText(selected.getName());
                logger.info(String.format("Game selected from table: %s", selected.getName()));
            } else {
                logger.info("No game selected from table");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error getting selected table item: %s", e.getMessage()));
        }
    }

    @FXML
    private void exit(MouseEvent event) {
        try {
            logger.info("Closing Admin Shop window");
            
            closeWindow(event);
            
        } catch (Exception e) {
            logger.severe(String.format("Error closing Admin Shop window: %s", e.getMessage()));
            showAlert("Error", "Could not close the window.");
        }
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        
        logger.info("Admin Shop window closed successfully");
    }

    @FXML
    private void searchGames(ActionEvent event) {
        try {
            String searchText = textFieldSearch.getText();
            String genreFilter = textFieldGenre.getText();
            String platformFilter = textFieldPlatform.getText();
            
            logger.info(String.format("Searching games - Search: '%s', Genre: '%s', Platform: '%s'", 
                       searchText, genreFilter, platformFilter));
            
            handleSearchFunctionality();
            
        } catch (Exception e) {
            logger.severe(String.format("Error in searchGames: %s", e.getMessage()));
            showAlert("Error", "An error occurred while searching for games.");
        }
    }

    private void handleSearchFunctionality() {
        logger.info("Search functionality not yet implemented");
        showAlert("Information", "Search functionality is not yet implemented.");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing AdminShopController");
            
            initializeControllerComponents();
            
            logger.info("AdminShopController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing AdminShopController: %s", e.getMessage()));
            showAlert("Initialization Error", 
                "Could not initialize the Admin Shop. Please restart the application.");
        }
    }

    private void initializeControllerComponents() {
        initializeTableColumns();
        setupSelectionListener();
    }

    private void initializeTableColumns() {
        try {
            logger.info("Table columns initialization placeholder");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing table columns: %s", e.getMessage()));
        }
    }

    private void setupSelectionListener() {
        tableViewGames.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> getSelectedTableItem());
    }

    private void showInformationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
            
            logger.info("Game loading functionality not yet implemented");
            
        } catch (Exception e) {
            logger.severe(String.format("Error loading games: %s", e.getMessage()));
            showAlert("Error", "Could not load games into the table.");
        }
    }

    public void refreshGameList() {
        try {
            logger.info("Refreshing game list");
            
            clearGameSelection();
            
            logger.info("Refresh functionality not yet implemented");
            
        } catch (Exception e) {
            logger.severe(String.format("Error refreshing game list: %s", e.getMessage()));
        }
    }
}