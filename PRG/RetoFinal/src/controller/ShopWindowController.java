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

public class ShopWindowController implements Initializable {

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

    private Videogame selected;
    private Profile profile;
    private Controller cont;
    private ObservableList<Videogame> gamesList;
    private ObservableList<Integer> favoriteGameIds;
    private static ObservableList<CartItem> sharedCart;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem menuHelp;
    @FXML
    private Label labelTitle;
    @FXML
    private MenuItem menuHelpWindow;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        comboBoxGenre.getItems().setAll(GameGenre.values());
        comboBoxGenre.setValue(GameGenre.ALL);

        comboBoxPlatform.getItems().setAll(Platform.values());
        comboBoxPlatform.setValue(Platform.ALL);

        gamesList.setAll(cont.getAllGames());
    }

    public void setUsuario(Profile profile) {
        this.profile = profile;
        labelWelcome.setText("Welcome, " + this.profile.getUsername() + "!");
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

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

    private void loadAllGames() {
        gamesList.setAll(cont.getAllGames());
    }

    private void getSelectedTableItem() {
        selected = tableViewGames.getSelectionModel().getSelectedItem();
        if (selected != null) {
            labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€ - Stock: " + selected.getStock());
        }
    }

    @FXML
    private void search(ActionEvent event) {

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
    }

    @FXML
    private void addToCart(ActionEvent event) {
        if (selected == null) {
            Alert success = new Alert(Alert.AlertType.WARNING);
            success.setTitle("ERROR!");
            success.setHeaderText("No selection!");
            success.setContentText("Please select a game before attempting add it to your cart.");
            success.showAndWait();
            return;
        }

        if (profile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("User not logged in");
            alert.setContentText("You must be logged in to add games to cart.");
            alert.showAndWait();
            return;
        }

        if (selected.getStock() <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Out of Stock");
            alert.setHeaderText("Game not available");
            alert.setContentText(selected.getName() + " is currently out of stock.");
            alert.showAndWait();
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

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added to Cart");
        alert.setHeaderText("Game added successfully");
        alert.setContentText(selected.getName() + " has been added to your cart!");
        alert.showAndWait();
    }

    public static ObservableList<CartItem> getSharedCart() {
        return sharedCart;
    }

    @FXML
    private void openCart(ActionEvent event) {
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

        } catch (IOException ex) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método auxiliar para cargar items al controlador del carrito
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

    private void refreshGamesList() {

        String name = textFieldSearch.getText();

        GameGenre genre = comboBoxGenre.getValue();
        Platform platform = comboBoxPlatform.getValue();

        boolean hasFilters
                = (name != null)
                || genre != GameGenre.ALL
                || platform != Platform.ALL;

        if (hasFilters) {
            gamesList.setAll(
                    cont.getGamesFiltered(
                            name,
                            genre == GameGenre.ALL ? "" : genre.name(),
                            platform == Platform.ALL ? "" : platform.name()
                    )
            );
        } else {
            gamesList.setAll(cont.getAllGames());
        }
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
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

        } catch (IOException e) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void handleReviewButton(ActionEvent event) {
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No game selected");
            alert.setHeaderText("Please select a game");
            alert.setContentText("You need to select a game from the table before writing a review.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewWindow.fxml"));
            Parent root = loader.load();

            ReviewController controller = loader.getController();

            // Configurar el videojuego seleccionado
            controller.setVideojuego(selected.getName(), selected.getIdVideogame()); // Asume que Videogame tiene getId()

            // Configurar usuario si es necesario
            if (profile != null) {
                controller.setUsuario(profile.getUserCode()); // O profile.getUserId() o similar
            }

            // Crear una NUEVA ventana (Stage) modal
            Stage stage = new Stage();
            stage.setTitle("Review Game: " + selected.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL); // Modal respecto a la ventana principal
            stage.initOwner(((Node) event.getSource()).getScene().getWindow()); // Establecer ventana padre
            stage.setResizable(false); // Opcional: hacerla no redimensionable
            stage.show();

        } catch (IOException e) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, "Error loading ReviewWindow", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open review window");
            alert.setContentText("An error occurred while trying to open the review window.");
            alert.showAndWait();
        }
    }

    @FXML
    private void toggleFavorite(ActionEvent event) {
        if (profile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("User not logged in");
            alert.setContentText("You must be logged in to add games to favorites.");
            alert.showAndWait();
            return;
        }

        if (selected == null) {
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
            favoriteGameIds.remove(Integer.valueOf(gameId));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Favorite Removed");
            alert.setHeaderText("Game removed from favorites");
            alert.setContentText(selected.getName() + " has been removed from your favorites!");
            alert.showAndWait();
        } else {
            // Add to favorites
            favoriteGameIds.add(gameId);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Favorite Added");
            alert.setHeaderText("Game added to favorites");
            alert.setContentText(selected.getName() + " has been added to your favorites!");
            alert.showAndWait();
        }

        // Refresh the table to update highlighting
        tableViewGames.refresh();
    }

    @FXML
    private void viewGameDetails(ActionEvent event) {
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No game selected");
            alert.setHeaderText("Please select a game");
            alert.setContentText("You need to select a game from the table before viewing its details.");
            alert.showAndWait();
            return;
        }

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

    @FXML
    private void helpWindow(ActionEvent event) {
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
        } catch (IOException ex) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void menuUserWindow(ActionEvent event) {
        // Este va a ModifyWindow.fxml (modificar perfil)
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

            // Abrir ModifyWindow como ventana principal (NO modal)
            Stage stage = new Stage();
            stage.setTitle("Modify Profile - " + profile.getUsername());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, "Error loading ModifyWindow", ex);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open Modify Window");
            alert.setContentText("An error occurred while trying to open the modify window.");
            alert.showAndWait();
        }
    }

    @FXML
    private void menuMainWIndow(ActionEvent event) {
        // Este va a MenuWindow.fxml (ventana principal con botones)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            Parent root = loader.load();

            MenuWindowController controller = loader.getController();

            // Pasar el perfil y controlador
            if (controller != null) {
                if (profile != null) {
                    controller.setUsuario(profile);
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

        } catch (IOException ex) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, "Error loading MenuWindow", ex);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open Main Window");
            alert.setContentText("An error occurred while trying to open the main window.");
            alert.showAndWait();
        }
    }
}
