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
    private Profile profile;
    private Controller cont;
    private ObservableList<Videogame> gamesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cont = new Controller();
        gamesList = FXCollections.observableArrayList();

        // Configurar las columnas de la tabla
        configureTableColumns();

        // Cargar todos los juegos inicialmente
        loadAllGames();

        // Configurar listener para selección de fila
        tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> getSelectedTableItem()
        );
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
        gamesList.clear();
        gamesList.addAll(cont.getAllGames());
        tableViewGames.setItems(gamesList);
    }

    private void getSelectedTableItem() {
        selected = tableViewGames.getSelectionModel().getSelectedItem();
        if (selected != null) {
            labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€ - Stock: " + selected.getStock());
        }
    }

    @FXML
    private void searchGames(ActionEvent event) {
        String name = textFieldSearch.getText();
        String genre = textFieldGenre.getText();
        String platform = textFieldPlatform.getText();

        gamesList.clear();
        gamesList.addAll(cont.getGamesFiltered(name, genre, platform));
        tableViewGames.setItems(gamesList);
    }

    @FXML
    private void addToCart(ActionEvent event) {
        if (selected == null) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("ERROR!");
            success.setHeaderText("No selection!");
            success.setContentText("Please select a game before attempting add it to your cart.");
            success.showAndWait();
        } else {
            // Add cart method here
        }
    }

    @FXML
    private void openCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CartWindow.fxml"));
            Parent root = loader.load();
            CartController cartC = loader.getController();
            cartC.cargarDatosEjemplo();
            cartC.actualizarTotales();
            cartC.actualizarEstadoBotones();
            Stage stage = new Stage();
            stage.setTitle("Your Cart");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshGamesList() {
        String name = textFieldSearch.getText();
        String genre = textFieldGenre.getText();
        String platform = textFieldPlatform.getText();

        // Si hay filtros aplicados, mantenerlos
        if (!name.isEmpty() || !genre.isEmpty() || !platform.isEmpty()) {
            searchGames(null);
        } else {
            loadAllGames();
        }
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
    }

    @FXML
    private void handleReviewButton(ActionEvent event) {
    }
}
