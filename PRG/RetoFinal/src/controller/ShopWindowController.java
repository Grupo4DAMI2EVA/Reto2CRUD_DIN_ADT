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
 * Controlador principal para la ventana de la tienda de videojuegos. Maneja la interfaz de usuario donde los usuarios pueden buscar, ver, agregar al carrito y gestionar sus juegos favoritos.
 *
 * @author Igor
 * @version 1.0
 */
public class ShopWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;

    // Elementos FXML de la interfaz
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

    // Variables de estado
    private Videogame selected;
    private Profile profile;
    private Controller cont;
    private ObservableList<Videogame> gamesList;
    private ObservableList<Integer> favoriteGameIds;
    private static ObservableList<CartItem> sharedCart;

    /**
     * Bloque estático para inicializar el sistema de logging. Crea el directorio de logs si no existe y configura el FileHandler para escribir logs en un archivo específico.
     */
    static {
        initializeLogger();
    }

    /**
     * Inicializa el sistema de logging de manera sincronizada para evitar múltiples inicializaciones en entornos multi-hilo.
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
     * Método de inicialización llamado automáticamente por JavaFX después de cargar el archivo FXML. Configura la tabla, carga los juegos, establece los listeners y prepara los componentes de la interfaz.
     *
     * @param url Ubicación utilizada para resolver rutas relativas para el objeto raíz
     * @param rb Recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing ShopWindowController");

        try {
            cont = new Controller();
            gamesList = FXCollections.observableArrayList();
            favoriteGameIds = FXCollections.observableArrayList();
            sharedCart = FXCollections.observableArrayList();

            // Configurar las columnas de la tabla
            configureTableColumns();

            // Cargar todos los juegos inicialmente
            tableViewGames.setItems(gamesList);

            // Configurar listener para selección de fila
            tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> getSelectedTableItem()
            );

            // Configurar fábrica de filas para resaltar juegos favoritos
            tableViewGames.setRowFactory(tv -> new TableRow<Videogame>() {
                @Override
                protected void updateItem(Videogame game, boolean empty) {
                    super.updateItem(game, empty);

                    // Primero resetear el estilo
                    setStyle("");

                    // Solo aplicar estilo si no está vacío y el juego no es null
                    if (!empty && game != null) {
                        // Usar una copia local para evitar problemas de concurrencia
                        ObservableList<Integer> favIdsCopy = FXCollections.observableArrayList(favoriteGameIds);

                        if (favIdsCopy.contains(game.getIdVideogame())) {
                            setStyle("-fx-background-color: #ffeb3b; -fx-font-weight: bold;");
                        }
                    }
                }
            });

            // Configurar ComboBox de géneros
            comboBoxGenre.getItems().setAll(GameGenre.values());
            comboBoxGenre.setValue(GameGenre.ALL);

            // Configurar ComboBox de plataformas
            comboBoxPlatform.getItems().setAll(Platform.values());
            comboBoxPlatform.setValue(Platform.ALL);

            // Cargar todos los juegos
            gamesList.setAll(cont.getAllGames());

            logger.info("ShopWindowController initialized successfully");
            logger.info("Loaded " + gamesList.size() + " games, genres and platforms configured");

        } catch (Exception e) {
            logger.severe("Error initializing ShopWindowController: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Establece el perfil del usuario actual en la ventana de la tienda. Actualiza la etiqueta de bienvenida con el nombre de usuario.
     *
     * @param profile Perfil del usuario que inició sesión
     */
    public void setUsuario(Profile profile) {
        logger.info("Setting user profile: " + (profile != null ? profile.getUsername() : "null"));
        this.profile = profile;
        labelWelcome.setText("Welcome, " + this.profile.getUsername() + "!");
        logger.info("User set successfully: " + this.profile.getUsername() + " (ID: " + this.profile.getUserCode() + ")");
    }

    /**
     * Establece el controlador principal de la aplicación.
     *
     * @param cont Controlador principal de la aplicación
     */
    public void setCont(Controller cont) {
        this.cont = cont;
    }

    /**
     * Obtiene el controlador principal de la aplicación.
     *
     * @return Controlador principal de la aplicación
     */
    public Controller getCont() {
        return cont;
    }

    /**
     * Configura las columnas de la tabla de videojuegos. Asocia cada columna con su propiedad correspondiente en la clase Videogame.
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
     * Carga todos los videojuegos disponibles desde la base de datos.
     */
    private void loadAllGames() {
        gamesList.setAll(cont.getAllGames());
    }

    /**
     * Maneja la selección de un elemento en la tabla. Actualiza el videojuego seleccionado y muestra su información.
     */
    private void getSelectedTableItem() {
        selected = tableViewGames.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Game selected: " + selected.getName() + " (ID: " + selected.getIdVideogame() + ")");
            labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€ - Stock: " + selected.getStock());
        }
    }

    /**
     * Realiza una búsqueda de videojuegos basada en los filtros especificados. Los filtros incluyen texto de búsqueda, género y plataforma.
     *
     * @param event Evento de acción del botón de búsqueda
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
     * Agrega el videojuego seleccionado al carrito de compras del usuario. Verifica que el usuario esté autenticado, que haya stock disponible y que el juego no esté ya en el carrito.
     *
     * @param event Evento de acción del botón "Agregar al carrito"
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
     * Obtiene el carrito de compras compartido entre ventanas. Este es un carrito estático que mantiene los items entre diferentes instancias del controlador.
     *
     * @return Lista observable con los items del carrito
     */
    public static ObservableList<CartItem> getSharedCart() {
        return sharedCart;
    }

    /**
     * Abre la ventana del carrito de compras. Carga la interfaz del carrito y pasa los items actuales.
     *
     * @param event Evento de acción del botón del carrito
     */
    @FXML
    private void openCart(ActionEvent event) {
        logger.info("Opening cart window for user: " + (profile != null ? profile.getUsername() : "unknown"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CartWindow.fxml"));
            Parent root = loader.load();
            CartController cartC = loader.getController();

            // PRIMERO llamar setup() para inicializar carritoData
            cartC.setup();

            // Cargar los items del carrito compartido
            loadCartItemsToController(cartC);

            cartC.actualizarTotales();
            cartC.actualizarEstadoBotones();

            Stage stage = new Stage();
            stage.setTitle("Your Cart");
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
     * Método auxiliar para cargar los items del carrito compartido al controlador del carrito.
     *
     * @param cartController Controlador de la ventana del carrito
     */
    private void loadCartItemsToController(CartController cartController) {
        for (CartItem item : sharedCart) {
            if (item.getIdUsuario() == profile.getUserCode()) {
                // Buscar nombre del videojuego
                String gameName = "Unknown Game";
                for (Videogame game : gamesList) {
                    if (game.getIdVideogame() == item.getIdVideojuego()) {
                        gameName = game.getName();
                        break;
                    }
                }

                cartController.agregarItemCarrito(
                        profile.getUsername(),
                        gameName,
                        item.getCantidad(),
                        item.getPrecio()
                );
            }
        }
    }

    /**
     * Refresca la lista de videojuegos aplicando los filtros actuales. Si hay filtros activos, aplica la búsqueda filtrada; de lo contrario, carga todos los juegos.
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
     * Maneja el evento del botón de salida. Cierra la ventana actual y regresa a la ventana principal del menú.
     *
     * @param event Evento de acción del botón de salida
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
     * Maneja el evento del botón de reseñas. Abre una ventana para escribir una reseña sobre el videojuego seleccionado.
     *
     * @param event Evento de acción del botón de reseñas
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

            // Configurar el videojuego seleccionado
            controller.setVideojuego(selected.getName(), selected.getIdVideogame());

            // Configurar usuario si es necesario
            if (profile != null) {
                controller.setUsuario(profile.getUserCode());
                logger.info("Setting user for review: " + profile.getUsername() + " (ID: " + profile.getUserCode() + ")");
            }

            // Crear una NUEVA ventana (Stage) modal
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
     * Alterna el estado de favorito del videojuego seleccionado. Agrega o remueve el juego de la lista de favoritos del usuario.
     *
     * @param event Evento de acción del menú contextual de favoritos
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
     * Muestra los detalles completos del videojuego seleccionado en una ventana de diálogo. Incluye información como compañía, género, plataforma, precio, PEGI, stock y fecha de lanzamiento.
     *
     * @param event Evento de acción del menú contextual de detalles
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
     * Abre la ventana de modificación del perfil de usuario. Permite al usuario actualizar su información personal.
     *
     * @param event Evento de acción del menú "User Window"
     */
    @FXML
    private void menuUserWindow(ActionEvent event) {
        logger.info("Opening user profile modification window for: " + (profile != null ? profile.getUsername() : "unknown"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
            Parent root = loader.load();

            ModifyWindowController controller = loader.getController();

            // Pasar el perfil y controlador
            if (controller != null) {
                if (profile != null) {
                    controller.setProfile(profile);
                }
                if (cont != null) {
                    controller.setCont(cont);
                }
            }

            // Cerrar la ventana actual (StoreWindow)
            Stage currentStage = (Stage) menuBar.getScene().getWindow();
            currentStage.close();

            logger.info("Closing ShopWindow and opening ModifyWindow");

            // Abrir ModifyWindow como ventana principal (NO modal)
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
     * Regresa a la ventana principal del menú desde la tienda. Reemplaza la escena actual con la del menú principal.
     *
     * @param event Evento de acción del menú "Main Window"
     */
    @FXML
    private void menuMainWIndow(ActionEvent event) {
        logger.info("Returning to main menu from ShopWindow");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            Parent root = loader.load();

            MenuWindowController controller = loader.getController();

            // Pasar el perfil y controlador
            if (controller != null) {
                if (profile != null) {
                    controller.setUsuario(profile);
                    logger.info("Passing user to MenuWindow: " + profile.getUsername());
                }
                if (cont != null) {
                    controller.setCont(cont);
                }
            }

            // Obtener la ventana actual
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
     * Abre el manual de usuario en formato PDF. Busca el archivo PDF en varias ubicaciones posibles y lo abre con el visor de PDF predeterminado del sistema.
     *
     * @param event Evento de acción del menú "Help Manual"
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
            alert.setContentText("Error: " + ex.getMessage()
                    + "\n\nPlease open the PDF manually from the 'pdf' folder:\n"
                    + "1. Navigate to the 'pdf' folder in the application directory\n"
                    + "2. Open 'Manual de Usuario - Tienda de Videojuegos.pdf'");
            alert.showAndWait();
        }
    }

    /**
     * Abre el informe del proyecto en formato PDF. Busca el archivo PDF en varias ubicaciones posibles y lo abre con el visor de PDF predeterminado del sistema.
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
            alert.setContentText("Error: " + ex.getMessage()
                    + "\n\nPlease open the PDF manually from the 'pdf' folder:\n"
                    + "1. Navigate to the 'pdf' folder in the application directory\n"
                    + "2. Open 'Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf'");
            alert.showAndWait();
        }
    }
}
