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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import model.*;

public class AdminShopController implements Initializable {

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
    private Button buttonAdd;
    @FXML
    private Button buttonExit;
    @FXML
    private Button buttonModify;
    @FXML
    private Button buttonDelete;
    @FXML
    private ImageView helpIcon;
    
    private Profile profile;
    private Controller cont;
    private Videogame selected;
    private ObservableList<Videogame> gamesList;
    

    public void setUsuario(Profile profile) {
        this.profile = profile;
        labelWelcome.setText("Welcome, " + profile.getUsername());
    }

    public void setCont(Controller cont) {
        this.cont = cont;
        loadAllGames();
    }

    @FXML
    private void addGame(ActionEvent event) {
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
        } catch (IOException ex) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                stage.show();
            } catch (IOException ex) {
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
            // Confirmación antes de eliminar
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText("Delete game: " + selected.getName());
            confirmation.setContentText("Are you sure you want to delete this game? This action cannot be undone.");
            confirmation.showAndWait();
            
            if (confirmation.getResult().equals(ButtonType.OK)) {
                // Intentar eliminar el juego
                if (cont.deleteGame(selected)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success!");
                    success.setHeaderText("Game deleted successfully");
                    success.setContentText("The game \"" + selected.getName() + "\" has been removed from the store.");
                    success.showAndWait();
                    
                    // Recargar la tabla
                    loadAllGames();
                    selected = null;
                    labelGameInfo.setText("Select a game");
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("ERROR!");
                    error.setHeaderText("Deletion failed");
                    error.setContentText("There was an error deleting the game. Please try again.");
                    error.showAndWait();
                }
            }
        }
    }
    
    @FXML
    private void search(ActionEvent event) {
        String name = textFieldSearch.getText();
        String genre = textFieldGenre.getText();
        String platform = textFieldPlatform.getText();

        gamesList.clear();
        gamesList.addAll(cont.getGamesFiltered(name, genre, platform));
        tableViewGames.setItems(gamesList);
    }
    
    @FXML
    private void helpWindow(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/HelpWindow.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Help Window");
            stage.setScene(new Scene(root));    
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void getSelectedTableItem(ActionEvent event) {
        selected = tableViewGames.getSelectionModel().getSelectedItem();
        if (selected != null) {
            labelGameInfo.setText(selected.getName());
        }
    }

    @FXML
    private void exit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gamesList = FXCollections.observableArrayList();

        // Configurar las columnas de la tabla
        configureTableColumns();

        // Configurar listener para selección de fila
        tableViewGames.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selected = tableViewGames.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€ - Stock: " + selected.getStock());
                    }
                }
        );
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

    public void reloadGames() {
        loadAllGames();
    }
}
