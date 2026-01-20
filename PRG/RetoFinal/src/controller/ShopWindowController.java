package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.DBImplementation;
import model.Profile;
import model.Videogame;

public class ShopWindowController implements Initializable {

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
    private Profile profile;
    private Controller cont;
    private DBImplementation db;
    private ObservableList<Videogame> gamesList;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = new DBImplementation();
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
        labelWelcome.setText("Welcome, " + profile.getUsername() + "!");
        
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
        gamesList.addAll(db.getAllGames());
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
        gamesList.addAll(db.getGamesFiltered(name, genre, platform));
        tableViewGames.setItems(gamesList);
    }

    @FXML
    private void addToCart(ActionEvent event) {
        if (selected == null) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("ERROR!");
            success.setHeaderText("No selection!");
            success.setContentText("Please select a game before attempting deletion of one.");
            success.showAndWait();
        } else {
            // Add cart method here
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
    private void handleCartButton(ActionEvent event) {
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
    }

    @FXML
    private void handleReviewButton(ActionEvent event) {
    }
}
