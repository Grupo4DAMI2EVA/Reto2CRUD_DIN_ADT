package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Profile;
import model.Videogame;

public class AdminShopController implements Initializable {

    private static final Logger logger = Logger.getLogger(AdminShopController.class.getName());
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
    
    private static final Logger logger = Logger.getLogger(AdminShopController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private MenuItem menuHelp;
    private ObservableList<Videogame> gamesList;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing AdminShopController");
            
            // INICIALIZAR la lista de juegos
            gamesList = FXCollections.observableArrayList();
            tableViewGames.setItems(gamesList);
            
            // Configurar las columnas de la tabla
            configureTableColumns();
            
            // Configurar icono de ayuda
            if (menuHelp != null) {
                try {
                    ImageView helpIcon = new ImageView("../images/Help_icon.png");
                    menuHelp.setGraphic(helpIcon);
                } catch (Exception e) {
                    logger.info("Help icon not found, skipping");
                }
            }
            
            // Configurar listener para selección de fila
            tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> getSelectedTableItem(newValue)
            );
            
            logger.info("AdminShopController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing AdminShopController: %s", e.getMessage()));
        }
    }
    
    private void configureTableColumns() {
        try {
            colTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
            colGenre.setCellValueFactory(new PropertyValueFactory<>("gameGenre"));
            colPlatform.setCellValueFactory(new PropertyValueFactory<>("platforms"));
            colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            colPegi.setCellValueFactory(new PropertyValueFactory<>("pegi"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            colCompanyName.setCellValueFactory(new PropertyValueFactory<>("companyName"));
            colReleaseDate.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
            logger.info("Table columns configured successfully");
        } catch (Exception e) {
            logger.severe(String.format("Error configuring table columns: %s", e.getMessage()));
        }
    }
    
    private void getSelectedTableItem(Videogame newValue) {
        try {
            selected = newValue;
            if (selected != null) {
                labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€");
            } else {
                labelGameInfo.setText("");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error selecting table item: %s", e.getMessage()));
        }
    }

    public void setUsuario(Profile profile) {
        try {
            this.profile = profile;
            labelWelcome.setText("Welcome, " + profile.getUsername());
            logger.info(String.format("User set for AdminShopController: %s", profile.getUsername()));
        } catch (Exception e) {
            logger.severe(String.format("Error setting user: %s", e.getMessage()));
        }
    }
    
    public void reloadGames() {
        try {
            logger.info("Reloading games");
            loadAllGames();
        } catch (Exception e) {
            logger.severe(String.format("Error reloading games: %s", e.getMessage()));
        }
    }
    
    private void loadAllGames() {
        try {
            if (cont != null) {
                gamesList.clear();
                gamesList.addAll(cont.getAllGames());
                tableViewGames.refresh();
                logger.info(String.format("Games loaded: %d", gamesList.size()));
            }
        } catch (Exception e) {
            logger.severe(String.format("Error loading games: %s", e.getMessage()));
        }
    }

    public void setCont(Controller cont) {
        try {
            this.cont = cont;
            loadAllGames();
            logger.info("Main controller set for AdminShopController");
        } catch (Exception e) {
            logger.severe(String.format("Error setting controller: %s", e.getMessage()));
        }
    }

    @FXML
    private void addGame(MouseEvent event) {
        try {
            logger.info("Opening Add Game window");
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddGamesWindow.fxml"));
            Parent root = fxmlLoader.load();
            
            AddGamesAdminController controllerWindow = fxmlLoader.getController();
            controllerWindow.setCont(cont);
            controllerWindow.setAdminShopController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Add Game Window");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
            
            logger.info("Add Game window opened successfully");
            
        } catch (IOException ex) {
            logger.severe(String.format("Error opening Add Game window: %s", ex.getMessage()));
            showAlert("Error", "Could not open the Add Game window.");
        } catch (Exception e) {
            logger.severe(String.format("Unexpected error in addGame: %s", e.getMessage()));
            showAlert("Error", "An unexpected error occurred.");
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
        if (selected == null) {
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("ERROR!");
            error.setHeaderText("No selection!");
            error.setContentText("Please select a game before attempting a modification.");
            error.showAndWait();
        } else {
            try {
                logger.info("Attempting to modify game: " + selected.getName());
                
                // Abrir ventana de modificación
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyGameAdmin.fxml"));
                Parent root = fxmlLoader.load();
                
                ModifyGameAdminController controllerWindow = fxmlLoader.getController();
                controllerWindow.setCont(cont);
                controllerWindow.setAdminShopController(this);
                controllerWindow.setVideogame(selected);
                
                Stage stage = new Stage();
                stage.setTitle("Modify Game Window");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                stage.showAndWait();
                
                // Recargar juegos después de cerrar la ventana
                loadAllGames();
                
                logger.info("Game modified successfully: " + selected.getName());
            } catch (IOException ex) {
                logger.severe(String.format("Error opening Modify Game window: %s", ex.getMessage()));
                Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void deleteGame(ActionEvent event) {
        if (selected == null) {
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("ERROR!");
            error.setHeaderText("No selection!");
            error.setContentText("Please select a game before attempting deletion of one.");
            error.showAndWait();
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete " + selected.getName() + "?");
            confirm.setContentText("Are you sure you want to delete this game?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                try {
                    logger.info("Attempting to delete game: " + selected.getName());
                    
                    if (cont.deleteGame(selected)) {
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Delete successful!");
                        success.setContentText("The game " + selected.getName() + " was deleted.");
                        success.showAndWait();
                        
                        logger.info("Game deleted successfully: " + selected.getName());
                        
                        // Recargar la lista
                        loadAllGames();
                        selected = null;
                        labelGameInfo.setText("");
                    } else {
                        Alert error = new Alert(Alert.AlertType.INFORMATION);
                        error.setTitle("ERROR!");
                        error.setHeaderText("An issue occurred");
                        error.setContentText("The game could not be deleted.");
                        error.showAndWait();
                        
                        logger.severe("Failed to delete game: " + selected.getName());
                    }
                } catch (Exception e) {
                    logger.severe(String.format("Error deleting game: %s", e.getMessage()));
                }
            }
        }
    }

    @FXML
    private void exit(MouseEvent event) {
        try {
            logger.info("Closing Admin Shop window");
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            Parent root = fxmlLoader.load();
            MenuWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUsuario(profile);
            controllerWindow.setCont(cont);
            Stage stage = new Stage();
            stage.setTitle("Main Window");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
            
            // Cerrar la ventana actual
            Stage currentStage = (Stage) buttonExit.getScene().getWindow();
            currentStage.close();
            
            logger.info("Admin Shop window closed successfully");
        } catch (IOException ex) {
            logger.severe(String.format("Error closing Admin Shop window: %s", ex.getMessage()));
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
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
}