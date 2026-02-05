package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;
import model.*;

/**
 * Controller class for the Admin window.
 *
 * @author Jagoba
 */
public class AdminShopController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;

    @FXML
    private Label labelWelcome;
    /**
     * Label to display the balance of the admin.
     */
    @FXML
    private Label labelBalance;
    /**
     * Table containing all games (and displaying them).
     */
    @FXML
    private TableView<Videogame> tableViewGames;
    @FXML
    private TableColumn<Videogame, String> colTitle;
    @FXML
    private TableColumn<Videogame, GameGenre> colGenre;
    @FXML
    private TableColumn<Videogame, Platform> colPlatform;
    @FXML
    private TableColumn<Videogame, Integer> colPrice;
    @FXML
    private TableColumn<Videogame, String> colPegi;
    @FXML
    private TableColumn<Videogame, PEGI> colStock;
    @FXML
    private TableColumn<Videogame, String> colCompanyName;
    @FXML
    private TableColumn<Videogame, LocalDate> colReleaseDate;
    /**
     * Text field used to for filtering games by name.
     */
    @FXML
    private TextField textFieldSearch;
    /**
     * Text field used for filtering games by genre.
     */
    @FXML
    private ComboBox<GameGenre> comboBoxGenre;

    /**
     * Text field used for filtering games by platform.
     */
    @FXML
    private ComboBox<Platform> comboBoxPlatform;
    @FXML
    private Button buttonSearch;
    /**
     * Label displaying some information about a videogame selected by the admin.
     */
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
    @FXML
    private MenuItem menuHelp;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menu;
    private ObservableList<Videogame> gamesList;

    /**
     * The admin profile.
     */
    private Profile profile;
    /**
     * The controller used for calling database methods.
     */
    private Controller cont;
    /**
     * The currently selected videogame from the table.
     */
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
            
            FileHandler fileHandler = new FileHandler("logs/AdminShopWindow.log", true);
            
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing AdminShopController");

        try {
            // INICIALIZAR la lista de juegos
            gamesList = FXCollections.observableArrayList();
            tableViewGames.setItems(gamesList);

            // Configurar las columnas de la tabla
            configureTableColumns();

            // Configurar los ComboBoxes
            configureComboBoxes();

            // Configurar listener para selección de fila
            tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> getSelectedTableItem(newValue)
            );

            logger.info("AdminShopController initialized successfully");

        } catch (Exception e) {
            logger.severe("Error initializing AdminShopController: " + e.getMessage());
        }
    }

    // AÑADIR: Método para configurar columnas
    private void configureTableColumns() {
        logger.info("Configuring table columns");

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
            logger.severe("Error configuring table columns: " + e.getMessage());
        }
    }

    // AÑADIR: Método para configurar ComboBoxes
    private void configureComboBoxes() {
        logger.info("Configuring ComboBoxes");

        try {
            // Configurar ComboBox de géneros
            comboBoxGenre.getItems().setAll(GameGenre.values());
            comboBoxGenre.setValue(GameGenre.ALL);

            // Configurar ComboBox de plataformas
            comboBoxPlatform.getItems().setAll(Platform.values());
            comboBoxPlatform.setValue(Platform.ALL);

            logger.info("ComboBoxes configured successfully");

        } catch (Exception e) {
            logger.severe("Error configuring ComboBoxes: " + e.getMessage());
        }
    }

    // AÑADIR: Método para manejar selección de items
    private void getSelectedTableItem(Videogame newValue) {
        selected = newValue;
        if (selected != null) {
            logger.info("Game selected in table: " + selected.getName()
                    + " (ID: " + selected.getIdVideogame() + ", Price: " + selected.getPrice() + "€)");
            labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€");
        } else {
            logger.info("Game selection cleared in table");
            labelGameInfo.setText("");
        }
    }

    /**
     * Method called from a different controller to set up the Admin profile ahead of time.
     *
     * @param profile The admin profile
     */
    public void setUsuario(Profile profile) {
        logger.info("Setting admin profile: " + (profile != null ? profile.getUsername() : "null"));
        this.profile = profile;
        labelWelcome.setText("Welcome, " + profile.getUsername());
        logger.info("Welcome message set for admin: " + profile.getUsername());
    }

    // AÑADIR: Este método falta y es llamado desde ModifyGameAdminController
    public void reloadGames() {
        logger.info("Reloading games list (called from external controller)");
        loadAllGames();
    }

    private void loadAllGames() {
        gamesList.setAll(cont.getAllGames());
    }

    /**
     * Method called from a different controller to set up the controller ahead of time.
     *
     * @param cont The controller instance
     */
    public void setCont(Controller cont) {
        logger.info("Setting controller in AdminShopController");
        this.cont = cont;
        loadAllGames();
    }

    /**
     * Used for two things: If any filters are applied, it calls the Search method, otherwise it calls the method to reload the game list.
     */
    private void refreshGamesList() {
        logger.info("Refreshing games list");

        String name = textFieldSearch.getText();
        GameGenre genre = comboBoxGenre.getValue();
        Platform platform = comboBoxPlatform.getValue();

        boolean hasFilters = (name != null && !name.isEmpty())
                || genre != GameGenre.ALL
                || platform != Platform.ALL;

        if (hasFilters) {
            logger.info("Applying filters - Name: '" + name + "', Genre: " + genre + ", Platform: " + platform);
            gamesList.setAll(
                    cont.getGamesFiltered(
                            name,
                            genre == GameGenre.ALL ? "" : genre.name(),
                            platform == Platform.ALL ? "" : platform.name()
                    )
            );
        } else {
            logger.info("Loading all games (no filters)");
            gamesList.setAll(cont.getAllGames());
        }

        logger.info("Games list refreshed. Total games: " + gamesList.size());
    }

    /**
     * Opens the Add Game window, setting up the controller ahead of time.
     *
     * @param event
     */
    @FXML
    private void addGame(ActionEvent event) {
        logger.info("Add Game button clicked");

        try {
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
            logger.severe("Error opening Add Game window: " + ex.getMessage());
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calls for the method to modify the selected game.
     *
     * @param event
     */
    @FXML
    private void modifyGame(ActionEvent event) {
        logger.info("Modify Game button clicked");

        if (selected == null) {
            logger.warning("Modify attempted without game selection");

            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("ERROR!");
            error.setHeaderText("No selection!");
            error.setContentText("Please select a game before attempting a modification.");
            error.showAndWait();
        } else {
            logger.info("Opening Modify Game window for: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");

            try {
                // Abrir ventana de modificación
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyGameWindow.fxml"));
                Parent root = fxmlLoader.load();

                ModifyGameAdminController controllerWindow = fxmlLoader.getController();
                controllerWindow.setCont(cont);
                controllerWindow.setAdminShopController(this);
                controllerWindow.setVideogame(selected);

                Stage stage = new Stage();
                stage.setTitle("Modify Game Window");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);

                if (event != null && event.getSource() instanceof Node) {
                    Window ownerWindow = ((Node) event.getSource()).getScene().getWindow();
                    stage.initOwner(ownerWindow);
                }

                stage.showAndWait();

                // Recargar juegos después de cerrar la ventana
                logger.info("Modify Game window closed - Reloading games list");
                loadAllGames();

            } catch (IOException ex) {
                logger.severe("Error opening Modify Game window: " + ex.getMessage());
                Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Calls for the method to delete the selected game.
     *
     * @param event
     */
    @FXML
    private void deleteGame(ActionEvent event) {
        logger.info("Delete Game button clicked");

        if (selected == null) {
            logger.warning("Delete attempted without game selection");

            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("ERROR!");
            error.setHeaderText("No selection!");
            error.setContentText("Please select a game before attempting deletion of one.");
            error.showAndWait();
        } else {
            logger.info("Confirming deletion of game: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete " + selected.getName() + "?");
            confirm.setContentText("Are you sure you want to delete this game?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                logger.info("Admin confirmed deletion of game: " + selected.getName());

                try {
                    logger.info("Attempting to delete game from database: " + selected.getName());

                    if (cont.deleteGame(selected)) {
                        logger.info("Game deleted SUCCESSFULLY: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");

                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Delete successful!");
                        success.setContentText("The game " + selected.getName() + " was deleted.");
                        success.showAndWait();

                        // Recargar la lista
                        loadAllGames();
                        selected = null;
                        labelGameInfo.setText("");

                        logger.info("Games list reloaded after deletion");

                    } else {
                        logger.warning("Failed to delete game from database: " + selected.getName());

                        Alert error = new Alert(Alert.AlertType.INFORMATION);
                        error.setTitle("ERROR!");
                        error.setHeaderText("An issue occurred");
                        error.setContentText("The game could not be deleted.");
                        error.showAndWait();
                    }

                } catch (Exception e) {
                    logger.severe("Exception while deleting game: " + e.getMessage());
                }
            } else {
                logger.info("Game deletion CANCELLED by admin for: " + selected.getName());
            }
        }
    }

    /**
     * Searches for videogames by calling a method, which uses the provided filters.
     *
     * @param event the action event triggered by the search button
     */
    @FXML
    private void search(ActionEvent event) {
        logger.info("Search button clicked");
        logger.info("Search filters - Text: '" + textFieldSearch.getText() + "', Genre: " + comboBoxGenre.getValue() + ", Platform: " + comboBoxPlatform.getValue());

        try {
            GameGenre genre = comboBoxGenre.getValue();
            Platform platform = comboBoxPlatform.getValue();

            String genreFilter = (genre == GameGenre.ALL) ? "" : genre.name();
            String platformFilter = (platform == Platform.ALL) ? "" : platform.name();

            gamesList.setAll(
                    cont.getGamesFiltered(
                            textFieldSearch.getText(),
                            genreFilter,
                            platformFilter
                    )
            );

            logger.info("Search completed. Found " + gamesList.size() + " games matching criteria");

        } catch (Exception e) {
            logger.severe("Error in search operation: " + e.getMessage());
            showAlert("Search Error", "An error occurred while searching. Please try again.");
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

    /**
     * Opens the Help window, setting up the admin profile ahead of time.
     *
     * @param event
     */
    @FXML
    private void helpWindow(ActionEvent event) {
        logger.info("Help menu item clicked");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/HelpWindow.fxml"));
            Parent root = fxmlLoader.load();
            HelpWindowController hCont = fxmlLoader.getController();
            hCont.setUsuario(profile);
            Stage stage = new Stage();
            stage.setTitle("Help Window");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();

            logger.info("Help window opened successfully");

        } catch (IOException ex) {
            logger.severe("Error opening Help window: " + ex.getMessage());
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Closes the curret window and opens the menu window.
     *
     * @param event
     */
    @FXML
    private void exit(ActionEvent event) {
        logger.info("Exit button clicked - Returning to MenuWindow");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            Parent root = loader.load();

            MenuWindowController controller = loader.getController();
            controller.setUsuario(profile);
            controller.setCont(cont);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Main Window");
            stage.setScene(new Scene(root));
            stage.show();

            logger.info("MenuWindow opened successfully for admin: " + profile.getUsername());

            // Cerrar la ventana actual
            Stage currentStage = (Stage) buttonExit.getScene().getWindow();
            currentStage.close();

            logger.info("AdminShop window closed");

        } catch (IOException ex) {
            logger.severe("Error exiting to MenuWindow: " + ex.getMessage());
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
