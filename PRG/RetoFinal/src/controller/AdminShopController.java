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

    private Profile profile;
    private Controller cont;
    private Videogame selected;
    @FXML
    private MenuItem menuHelp;
    private ObservableList<Videogame> gamesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // INICIALIZAR la lista de juegos
        gamesList = FXCollections.observableArrayList();
        tableViewGames.setItems(gamesList);
        
        // Configurar las columnas de la tabla
        configureTableColumns();
        
        // Configurar icono de ayuda
        ImageView helpIcon = new ImageView("../images/Help_icon.png");
        menuHelp.setGraphic(helpIcon);
        
        // Configurar listener para selección de fila
        tableViewGames.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> getSelectedTableItem(newValue)
        );
    }
    
    // AÑADIR: Método para configurar columnas
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
    
    // AÑADIR: Método para manejar selección de items
    private void getSelectedTableItem(Videogame newValue) {
        selected = newValue;
        if (selected != null) {
            labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€");
        } else {
            labelGameInfo.setText("");
        }
    }

    public void setUsuario(Profile profile) {
        this.profile = profile;
        labelWelcome.setText("Welcome, " + profile.getUsername());
    }
    
    // AÑADIR: Este método falta y es llamado desde ModifyGameAdminController
    public void reloadGames() {
        loadAllGames();
    }
    
    private void loadAllGames() {
        if (cont != null) {
            gamesList.clear();
            gamesList.addAll(cont.getAllGames());
            tableViewGames.refresh();
        }
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
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete " + selected.getName() + "?");
            confirm.setContentText("Are you sure you want to delete this game?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (cont.deleteGame(selected)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Delete successful!");
                    success.setContentText("The game " + selected.getName() + " was deleted.");
                    success.showAndWait();
                    
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
        tableViewGames.refresh();
    }

    @FXML
    private void helpWindow(ActionEvent event) {
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
    private void exit(MouseEvent event) {
        try {
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
        } catch (IOException ex) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}