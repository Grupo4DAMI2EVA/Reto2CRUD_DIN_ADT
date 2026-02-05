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
 * Controller class for the Admin Shop window. Manages the administrative interface
 * where administrators can view, search, add, modify and delete video games from the store.
 * Provides comprehensive game management functionality with filtering capabilities.
 *
 * @author deorbe
 * @version 1.0
 */
public class AdminShopController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;

    // FXML UI Components
    @FXML
    private Label labelWelcome;
    @FXML
    private Label labelBalance;
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
    @FXML
    private TextField textFieldSearch;
    @FXML
    private ComboBox<GameGenre> comboBoxGenre;

    @FXML
    private ComboBox<Platform> comboBoxPlatform;
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
    @FXML
    private MenuItem menuHelp;
    @FXML
    private MenuItem menuHelpManual;
    @FXML
    private MenuItem menuHelpReport;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menu;
    private ObservableList<Videogame> gamesList;

    /**
     * State variables.
     */
    private Profile profile;
    private Controller cont;
    private Videogame selected;

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

    /**
     * Initialization method called automatically by JavaFX after loading the FXML file.
     * Configures the table columns, sets up ComboBoxes with enum values, initializes
     * the games list, and sets up selection listeners for the table.
     *
     * @param url Location used to resolve relative paths for the root object
     * @param rb Resources used to localize the root object
     */
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

    /**
     * Configures all table columns with appropriate cell value factories.
     * Sets up PropertyValueFactory for each column to display videogame properties.
     */
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

    /**
     * Configures the ComboBox components for genre and platform filtering.
     * Populates them with all available enum values and sets default values to ALL.
     */
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

    /**
     * Handles table row selection events. Updates the selected game reference
     * and displays game information in the info label.
     *
     * @param newValue The newly selected videogame from the table, or null if selection is cleared
     */
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

    /**
     * Reloads the games list from the database. Called externally by other controllers
     * (like ModifyGameAdminController) to refresh the table after game modifications.
     */
    public void reloadGames() {
        logger.info("Reloading games list (called from external controller)");
        loadAllGames();
    }

    /**
     * Loads all games from the database and updates the observable list.
     * This method refreshes the table with the complete catalog of games.
     */
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
     * Refreshes the games list based on current filter settings.
     * If filters are applied (name, genre, or platform), performs a filtered search.
     * Otherwise, loads all games. This method intelligently determines whether
     * to apply filters or show all games based on current UI state.
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
     * Opens the Add Game window for administrators to add new videogames to the store.
     * Sets up the AddGamesAdminController with necessary dependencies before showing the window.
     *
     * @param event The ActionEvent triggered by the add game button
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
     * Opens the Modify Game window for editing the currently selected game.
     * Validates that a game is selected before opening the modification window.
     * Sets up the ModifyGameAdminController with the selected game data.
     *
     * @param event The ActionEvent triggered by the modify game button
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
     * Deletes the currently selected game from the store after confirmation.
     * Validates selection, shows confirmation dialog, and removes the game from
     * the database if the administrator confirms the action.
     *
     * @param event The ActionEvent triggered by the delete game button
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
     * Performs a filtered search for videogames based on current filter criteria.
     * Uses text search, genre filter, and platform filter to find matching games.
     * Updates the games list with search results.
     *
     * @param event The ActionEvent triggered by the search button
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

    /**
     * Displays a warning alert dialog with the specified title and message.
     *
     * @param title The title of the alert dialog
     * @param message The message content to display
     */
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
     * Abre el manual de usuario en formato PDF.
     * Busca el archivo PDF en varias ubicaciones posibles y lo abre
     * con el visor de PDF predeterminado del sistema.
     *
     * @param event Evento de acción del menú "Help"
     */
    @FXML
    private void helpWindow(ActionEvent event) {
        logger.info("Opening user manual PDF");
        
        try {
            // Ruta relativa al PDF del manual
            String pdfFileName = "Manual de Administrador - Tienda de Videojuegos.pdf";
            String pdfPath = "pdf/" + pdfFileName;
            
            // Obtener la ruta absoluta del archivo
            java.io.File pdfFile = new java.io.File(pdfPath);
            
            if (!pdfFile.exists()) {
                logger.warning("User manual PDF not found at: " + pdfFile.getAbsolutePath());
                
                // Intentar buscar en diferentes ubicaciones comunes
                String[] possiblePaths = {
                    pdfPath,
                    "src/pdf/" + pdfFileName,
                    "resources/pdf/" + pdfFileName,
                    "../pdf/" + pdfFileName,
                    "./pdf/" + pdfFileName
                };
                
                boolean found = false;
                for (String path : possiblePaths) {
                    pdfFile = new java.io.File(path);
                    if (pdfFile.exists()) {
                        found = true;
                        logger.info("Found manual PDF at: " + pdfFile.getAbsolutePath());
                        break;
                    }
                }
                
                if (!found) {
                    showAlert("File Not Found", 
                        "User manual PDF not found. Please ensure 'Manual de Usuario - Tienda de Videojuegos.pdf' exists in the 'pdf' folder.");
                    return;
                }
            }
            
            // Abrir el PDF con el programa predeterminado del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(pdfFile);
                    logger.info("Successfully opened user manual PDF: " + pdfFile.getName());
                } else {
                    throw new IOException("OPEN action not supported on this platform");
                }
            } else {
                throw new IOException("Desktop not supported on this platform");
            }
            
        } catch (IOException ex) {
            logger.severe("Error opening user manual PDF: " + ex.getMessage());
            
            // Mostrar instrucciones alternativas
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Opening PDF");
            alert.setHeaderText("Could not open user manual automatically");
            alert.setContentText("Error: " + ex.getMessage() + 
                               "\n\nPlease open the PDF manually from the 'pdf' folder:\n" +
                               "1. Navigate to the 'pdf' folder in the application directory\n" +
                               "2. Open 'Manual de Usuario - Tienda de Videojuegos.pdf'");
            alert.showAndWait();
        }
    }

    /**
     * Abre el informe del proyecto en formato PDF.
     * Busca el archivo PDF en varias ubicaciones posibles y lo abre
     * con el visor de PDF predeterminado del sistema.
     *
     * @param event Evento de acción del menú "Help Report"
     */
    @FXML
    private void reportPdf(ActionEvent event) {
        logger.info("Opening project report PDF");
        
        try {
            // Ruta relativa al PDF del informe
            String pdfFileName = "Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf";
            String pdfPath = "pdf/" + pdfFileName;
            
            // Obtener la ruta absoluta del archivo
            java.io.File pdfFile = new java.io.File(pdfPath);
            
            if (!pdfFile.exists()) {
                logger.warning("Project report PDF not found at: " + pdfFile.getAbsolutePath());
                
                // Intentar buscar en diferentes ubicaciones comunes
                String[] possiblePaths = {
                    pdfPath,
                    "src/pdf/" + pdfFileName,
                    "resources/pdf/" + pdfFileName,
                    "../pdf/" + pdfFileName,
                    "./pdf/" + pdfFileName
                };
                
                boolean found = false;
                for (String path : possiblePaths) {
                    pdfFile = new java.io.File(path);
                    if (pdfFile.exists()) {
                        found = true;
                        logger.info("Found report PDF at: " + pdfFile.getAbsolutePath());
                        break;
                    }
                }
                
                if (!found) {
                    showAlert("File Not Found", 
                        "Project report PDF not found. Please ensure 'Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf' exists in the 'pdf' folder.");
                    return;
                }
            }
            
            // Abrir el PDF con el programa predeterminado del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(pdfFile);
                    logger.info("Successfully opened project report PDF: " + pdfFile.getName());
                    
                    // Mostrar confirmación
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("PDF Opened");
                    info.setHeaderText("Project report opened successfully");
                    info.setContentText("The project report PDF has been opened in your default PDF viewer.");
                    info.showAndWait();
                    
                } else {
                    throw new IOException("OPEN action not supported on this platform");
                }
            } else {
                throw new IOException("Desktop not supported on this platform");
            }
            
        } catch (IOException ex) {
            logger.severe("Error opening project report PDF: " + ex.getMessage());
            
            // Mostrar instrucciones alternativas
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Opening PDF");
            alert.setHeaderText("Could not open project report automatically");
            alert.setContentText("Error: " + ex.getMessage() + 
                               "\n\nPlease open the PDF manually from the 'pdf' folder:\n" +
                               "1. Navigate to the 'pdf' folder in the application directory\n" +
                               "2. Open 'Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf'");
            alert.showAndWait();
        }
    }

    /**
     * Closes the current admin shop window and returns to the main menu window.
     * Sets up the MenuWindowController with the admin profile and controller reference.
     *
     * @param event The ActionEvent triggered by the exit button
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
