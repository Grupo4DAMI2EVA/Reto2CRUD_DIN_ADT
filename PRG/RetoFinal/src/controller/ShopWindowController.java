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
import javafx.stage.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

/**
 * Main controller for the video game store window. Handles the
 * user interface where users can search, view, add to cart
 * and manage their favorite games.
 *
 * @author Igor
 * @version 1.0
 */
public class ShopWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;

    // FXML UI elements
    @FXML
    private Label labelWelcome;
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
    private Button buttonAddToCart;
    @FXML
    private Button buttonCart;
    @FXML
    private Button buttonExit;
    @FXML
    private Button buttonReview;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem menuHelp;
    @FXML
    private Label labelTitle;
    @FXML
    private Menu menuWindow;
    @FXML
    private MenuItem menuUserWindow;
    @FXML
    private MenuItem menuMainWindow;
    @FXML
    private Label labelAvaliable;
    @FXML
    private ContextMenu tableContextMenu;
    @FXML
    private MenuItem contextMenuFav;
    @FXML
    private MenuItem contextMenuDetails;
    @FXML
    private MenuItem contextMenuCart;
    @FXML
    private Label labelSearch;
    @FXML
    private Label labelGenre;
    @FXML
    private Label labelPlatform;
    @FXML
    private Label labelSelectedGame;
    @FXML
    private MenuItem menuHelpManual;
    @FXML
    private MenuItem menuHelpReport;

    // State variables
    private Videogame selected;
    private Profile profile;
    private Controller cont;
    private ObservableList<Videogame> gamesList;
    private ObservableList<Integer> favoriteGameIds;
    private static ObservableList<CartItem> sharedCart;

    /**
     * Static block to initialize the logging system. Creates the logs
     * directory if it does not exist and configures the FileHandler to write
     * logs to a specific file.
     */
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

            FileHandler fileHandler = new FileHandler("logs/ShopWindow.log", true);

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
     * Initialization method called automatically by JavaFX after loading
     * the FXML file. Configures the table, loads games, sets listeners
     * and prepares the UI components.
     *
     * @param url Location used to resolve relative paths for the root object
     * @param rb Resources used to locate the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing ShopWindowController");

        try {
            cont = new Controller();
            gamesList = FXCollections.observableArrayList();
            favoriteGameIds = FXCollections.observableArrayList();
            sharedCart = FXCollections.observableArrayList();

            // Configure table columns
            configureTableColumns();

            // Load all games initially
            tableViewGames.setItems(gamesList);

            // Configure row selection listener
            tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> getSelectedTableItem()
            );

            // Configure row factory to highlight favorite games
            tableViewGames.setRowFactory(tv -> new TableRow<Videogame>() {
                @Override
                protected void updateItem(Videogame game, boolean empty) {
                    super.updateItem(game, empty);

                    // First reset the style
                    setStyle("");

                    // Only apply style if not empty and game is not null
                    if (!empty && game != null) {
                        // Use a local copy to avoid concurrency issues
                        ObservableList<Integer> favIdsCopy = FXCollections.observableArrayList(favoriteGameIds);

                        if (favIdsCopy.contains(game.getIdVideogame())) {
                            setStyle("-fx-background-color: #ffeb3b; -fx-font-weight: bold;");
                        }
                    }
                }
            });

            // Configure genre ComboBox
            comboBoxGenre.getItems().setAll(GameGenre.values());
            comboBoxGenre.setValue(GameGenre.ALL);

            // Configure platform ComboBox
            comboBoxPlatform.getItems().setAll(Platform.values());
            comboBoxPlatform.setValue(Platform.ALL);

            // Load all games
            gamesList.setAll(cont.getAllGames());

            logger.info("ShopWindowController initialized successfully");
            logger.info("Loaded " + gamesList.size() + " games, genres and platforms configured");

        } catch (Exception e) {
            logger.severe("Error initializing ShopWindowController: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Sets the current user profile in the store window.
     * Updates the welcome label with the username.
     *
     * @param profile Profile of the logged-in user
     */
    public void setUsuario(Profile profile) {
        logger.info("Setting user profile: " + (profile != null ? profile.getUsername() : "null"));
        this.profile = profile;
        labelWelcome.setText("Welcome, " + this.profile.getUsername() + "!");
        logger.info("User set successfully: " + this.profile.getUsername() + " (ID: " + this.profile.getUserCode() + ")");
    }

    /**
     * Sets the main application controller.
     *
     * @param cont Main application controller
     */
    public void setCont(Controller cont) {
        this.cont = cont;
    }

    /**
     * Gets the main application controller.
     *
     * @return Main application controller
     */
    public Controller getCont() {
        return cont;
    }

    /**
     * Configures the video game table columns. Associates each column
     * with its corresponding property in the Videogame class.
     */
    private void configureTableColumns() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("gameGenre"));
        colPlatform.setCellValueFactory(new PropertyValueFactory<>("platforms"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPegi.setCellValueFactory(new PropertyValueFactory<>("pegi"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCompanyName.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        colReleaseDate.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
    }

    /**
     * Loads all available video games from the database.
     */
    private void loadAllGames() {
        gamesList.setAll(cont.getAllGames());
    }

    /**
     * Handles the selection of an item in the table. Updates the selected video
     * game and displays its information.
     */
    private void getSelectedTableItem() {
        selected = tableViewGames.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Game selected: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");
            labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€ - Stock: " + selected.getStock());
        }
    }

    /**
     * Performs a search for video games based on the specified filters.
     * Filters include search text, genre, and platform.
     *
     * @param event Search button action event
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
     * Adds the selected video game to the user shopping cart.
     * Verifies that the user is authenticated, that there is available stock, and
     * that the game is not already in the cart.
     *
     * @param event "Add to cart" button action event
     */
    @FXML
    private void addToCart(ActionEvent event) {
        logger.info("Add to cart button clicked");

        try {
            logger.info("Attempting to add game to cart");

            if (selected == null) {
                logger.warning("Add to cart attempted without game selection");
                Alert success = new Alert(Alert.AlertType.WARNING);
                success.setTitle("ERROR!");
                success.setHeaderText("No selection!");
                success.setContentText("Please select a game before attempting add it to your cart.");
                success.showAndWait();
                return;
            }

            if (profile == null) {
                logger.warning("Add to cart attempted without user logged in");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("User not logged in");
                alert.setContentText("You must be logged in to add games to cart.");
                alert.showAndWait();
                return;
            }

            if (selected.getStock() <= 0) {
                logger.warning("Attempted to add out-of-stock game to cart: " + selected.getName());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Out of Stock");
                alert.setHeaderText("Game not available");
                alert.setContentText(selected.getName() + " is currently out of stock.");
                alert.showAndWait();
                return;
            }
        } catch (Exception e) {
            logger.severe(String.format("Error in addToCart: %s", e.getMessage()));
            showAlert("Error", "Could not add the game to the cart.");
            return;
        }

        // Check if game already in cart
        boolean gameInCart = false;
        for (CartItem item : sharedCart) {
            if (item.getIdVideojuego() == selected.getIdVideogame() && item.getIdUsuario() == profile.getUserCode()) {
                gameInCart = true;
                break;
            }
        }

        if (gameInCart) {
            logger.info("Game already in cart: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Already in Cart");
            alert.setHeaderText("Game already added");
            alert.setContentText(selected.getName() + " is already in your cart.");
            alert.showAndWait();
            return;
        }

        // Add to cart
        CartItem cartItem = new CartItem(
                profile.getUserCode(),
                selected.getIdVideogame(),
                1,
                selected.getPrice()
        );

        sharedCart.add(cartItem);

        logger.info("Game added to cart successfully - User: " + profile.getUsername() + ", Game: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added to Cart");
        alert.setHeaderText("Game added successfully");
        alert.setContentText(selected.getName() + " has been added to your cart!");
        alert.showAndWait();
    }

    /**
     * Gets the shared shopping cart between windows. This is a static cart that
     * maintains items between different instances of the store window.
     *
     * @return Observable list with cart items
     */
    public static ObservableList<CartItem> getSharedCart() {
        return sharedCart;
    }

    /**
     * Opens the shopping cart window. Loads the cart interface and
     * passes the current items.
     *
     * @param event Cart button action event
     */
    @FXML
    private void openCart(ActionEvent event) {
        logger.info("Opening cart window for user: " + (profile != null ? profile.getUsername() : "unknown"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CartWindow.fxml"));
            Parent root = loader.load();
            CartController cartC = loader.getController();

            // PRIMERO llamar setup() para inicializar
            cartC.setup();

            // Configure user and 
            cartC.setUsuario(profile);
            cartC.setCont(cont);

            // Load shared cart items WITH COMPLETE VIDEOGAMES
            loadCartItemsToController(cartC);

            cartC.actualizarTotales();
            cartC.actualizarEstadoBotones();

            Stage stage = new Stage();
            stage.setTitle("Your Cart - " + profile.getUsername());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();

            logger.info("Cart window opened successfully");

        } catch (IOException ex) {
            logger.severe("Error opening cart window: " + ex.getMessage());
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Auxiliary method to load the shared cart items into the cart window
     * controller.
     *
     * @param cartController Cart window controller
     */
    private void loadCartItemsToController(CartController cartController) {
        for (CartItem item : sharedCart) {
            if (item.getIdUsuario() == profile.getUserCode()) {
                // Find the complete video game in the list
                Videogame videojuegoCompleto = null;
                for (Videogame game : gamesList) {
                    if (game.getIdVideogame() == item.getIdVideojuego()) {
                        videojuegoCompleto = game;
                        break;
                    }
                }

                if (videojuegoCompleto != null) {
                    cartController.agregarItemCarrito(
                            profile.getUsername(),
                            videojuegoCompleto,
                            item.getCantidad(),
                            item.getPrecio()
                    );
                }
            }
        }
    }

    /**
     * Refreshes the list of video games applying the current filters. If there
     * are active filters, it applies the filtered search; otherwise, it loads
     * all games.
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
     * Handles the exit button event. Closes the current window and returns
     * to the main menu window.
     *
     * @param event Exit button action event
     */
    @FXML
    private void handleExitButton(ActionEvent event) {
        logger.info("Exit button clicked - Returning to main menu");

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

            logger.info("Successfully returned to Main Menu");

        } catch (IOException e) {
            logger.severe("Error returning to main menu: " + e.getMessage());
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Handles the reviews button event. Opens a window to write a
     * review about the selected video game.
     *
     * @param event Reviews button action event
     */
    @FXML
    private void handleReviewButton(ActionEvent event) {
        logger.info("Review button clicked");

        if (selected == null) {
            logger.warning("Review button clicked without game selection");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No game selected");
            alert.setHeaderText("Please select a game");
            alert.setContentText("You need to select a game from the table before writing a review.");
            alert.showAndWait();
            return;
        }

        try {
            logger.info("Opening review window for game: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewWindow.fxml"));
            Parent root = loader.load();

            ReviewController controller = loader.getController();

            // Configure the complete video game
            controller.setVideojuegoCompleto(selected); // Nuevo método

            // Configure user and 
            if (profile != null) {
                controller.setUsuario(profile);
                controller.setCont(cont);
                logger.info("Setting user for review: " + profile.getUsername() + " (ID: " + profile.getUserCode() + ")");
            }

            // Create a NEW window (Stage) modal
            Stage stage = new Stage();
            stage.setTitle("Review Game: " + selected.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setResizable(false);
            stage.show();

            logger.info("Review window opened successfully for game: " + selected.getName());

        } catch (IOException e) {
            logger.severe("Failed to open review window for game: " + selected.getName() + " - " + e.getMessage());
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, "Error loading ReviewWindow", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open review window");
            alert.setContentText("An error occurred while trying to open the review window.");
            alert.showAndWait();
        }
    }

    /**
     * Toggles the favorite status of the selected video game. Adds or
     * removes the game from the user favorite list.
     *
     * @param event Favorites context menu action event
     */
    @FXML
    private void toggleFavorite(ActionEvent event) {
        logger.info("Toggle favorite action triggered");

        if (profile == null) {
            logger.warning("Attempted to toggle favorite without user logged in");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("User not logged in");
            alert.setContentText("You must be logged in to add games to favorites.");
            alert.showAndWait();
            return;
        }

        if (selected == null) {
            logger.warning("Attempted to toggle favorite without game selected");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No game selected");
            alert.setHeaderText("Please select a game");
            alert.setContentText("You need to select a game from the table before adding it to favorites.");
            alert.showAndWait();
            return;
        }

        int gameId = selected.getIdVideogame();

        if (favoriteGameIds.contains(gameId)) {
            // Remove from favorites
            logger.info("Removing game from favorites - User: " + profile.getUsername() + ", Game: " + selected.getName() + " (ID: " + gameId + ")");
            favoriteGameIds.remove(Integer.valueOf(gameId));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Favorite Removed");
            alert.setHeaderText("Game removed from favorites");
            alert.setContentText(selected.getName() + " has been removed from your favorites!");
            alert.showAndWait();
        } else {
            // Add to favorites
            logger.info("Adding game to favorites - User: " + profile.getUsername() + ", Game: " + selected.getName() + " (ID: " + gameId + ")");
            favoriteGameIds.add(gameId);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Favorite Added");
            alert.setHeaderText("Game added to favorites");
            alert.setContentText(selected.getName() + " has been added to your favorites!");
            alert.showAndWait();
        }

        // Refresh the table to update highlighting
        tableViewGames.refresh();
        logger.info("Favorites updated. Total favorites: " + favoriteGameIds.size());
    }

    /**
     * Shows the complete details of the selected video game in a dialog.
     * Includes information such as company, genre, platform, price,
     * PEGI rating, stock, and release date.
     *
     * @param event Details context menu action event
     */
    @FXML
    private void viewGameDetails(ActionEvent event) {
        logger.info("View game details action triggered");

        if (selected == null) {
            logger.warning("Attempted to view details without game selection");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No game selected");
            alert.setHeaderText("Please select a game");
            alert.setContentText("You need to select a game from the table before viewing its details.");
            alert.showAndWait();
            return;
        }

        logger.info("Showing details for game: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Details - " + selected.getName());
        alert.setHeaderText(selected.getName());

        String details = "Company: " + selected.getCompanyName() + "\n"
                + "Genre: " + selected.getGameGenre() + "\n"
                + "Platform: " + selected.getPlatforms() + "\n"
                + "Price: €" + String.format("%.2f", selected.getPrice()) + "\n"
                + "PEGI Rating: " + selected.getPegi() + "\n"
                + "Stock: " + selected.getStock() + " units\n"
                + "Release Date: " + selected.getReleaseDate();

        alert.setContentText(details);
        alert.showAndWait();
    }

    /**
     * Opens the user profile modification window. Allows the user
     * to update their personal information.
     *
     * @param event "User Window" menu action event
     */
    @FXML
    private void menuUserWindow(ActionEvent event) {
        logger.info("Opening user profile modification window for: " + (profile != null ? profile.getUsername() : "unknown"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
            Parent root = loader.load();

            ModifyWindowController controller = loader.getController();

            // Pasar el perfil y 
            if (controller != null) {
                if (profile != null) {
                    controller.setProfile(profile);
                }
                if (cont != null) {
                    controller.setCont(cont);
                }
            }

            // Close the current window (StoreWindow)
            Stage currentStage = (Stage) menuBar.getScene().getWindow();
            currentStage.close();

            logger.info("Closing ShopWindow and opening ModifyWindow");

            // Open ModifyWindow as main window (NO modal)
            Stage stage = new Stage();
            stage.setTitle("Modify Profile - " + profile.getUsername());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            logger.severe("Error loading ModifyWindow for user: " + (profile != null ? profile.getUsername() : "unknown") + " - " + ex.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open Modify Window");
            alert.setContentText("An error occurred while trying to open the modify window.");
            alert.showAndWait();
        }
    }

    /**
     * Returns to the main menu window from the store. Replaces the
     * current scene with the main menu scene.
     *
     * @param event "Main Window" menu action event
     */
    @FXML
    private void menuMainWIndow(ActionEvent event) {
        logger.info("Returning to main menu from ShopWindow");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            Parent root = loader.load();

            MenuWindowController controller = loader.getController();

            // Pasar el perfil y 
            if (controller != null) {
                if (profile != null) {
                    controller.setUsuario(profile);
                    logger.info("Passing user to MenuWindow: " + profile.getUsername());
                }
                if (cont != null) {
                    controller.setCont(cont);
                }
            }

            // Get the current window
            Stage currentStage = (Stage) menuBar.getScene().getWindow();

            // Reemplazar la escena actual
            currentStage.setTitle("Main Menu");
            currentStage.setScene(new Scene(root));
            currentStage.show();

            logger.info("Successfully returned to Main Menu");

        } catch (IOException ex) {
            logger.severe("Error loading MenuWindow: " + ex.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open Main Window");
            alert.setContentText("An error occurred while trying to open the main window.");
            alert.showAndWait();
        }
    }

    /**
     * Muestra una ventana de alerta con el título y mensaje especificados.
     *
     * @param title Título de la alerta
     * @param message Mensaje a mostrar en la alerta
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
     * Opens the user manual in PDF format. Searches for the PDF file in various
     * possible locations and opens it with the default PDF viewer of the
     * system.
     *
     * @param event "Help Manual" menu action event
     */
    @FXML
    private void manualPdf(ActionEvent event) {
        logger.info("Opening user manual PDF");

        try {
            // Ruta relativa al PDF del manual
            String pdfFileName = "Manual de Usuario - Tienda de Videojuegos.pdf";
            String pdfPath = "pdf/" + pdfFileName;

            // Obtener la ruta absoluta del archivo
            java.io.File pdfFile = new java.io.File(pdfPath);

            if (!pdfFile.exists()) {
                logger.warning("User manual PDF not found at: " + pdfFile.getAbsolutePath());

                // Try to search in different common locations
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

            // Abrir el PDF con el programa predeterminado of the system.
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
            alert.setContentText("Error: " + ex.getMessage()
                    + "\n\nPlease open the PDF manually from the 'pdf' folder:\n"
                    + "1. Navigate to the 'pdf' folder in the application directory\n"
                    + "2. Open 'Manual de Usuario - Tienda de Videojuegos.pdf'");
            alert.showAndWait();
        }
    }

    /**
     * Opens the project report in PDF format. Searches for the PDF file in
     * various possible locations and opens it with the default PDF viewer
     * of the system.
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

                // Try to search in different common locations
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

            // Abrir el PDF con el programa predeterminado of the system.
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
            alert.setContentText("Error: " + ex.getMessage()
                    + "\n\nPlease open the PDF manually from the 'pdf' folder:\n"
                    + "1. Navigate to the 'pdf' folder in the application directory\n"
                    + "2. Open 'Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf'");
            alert.showAndWait();
        }
    }
}





