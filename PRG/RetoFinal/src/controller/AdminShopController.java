package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import model.*;

/**
 * Controller class for the Admin window.
 *
 * @author Jagoba
 */
public class AdminShopController implements Initializable {

    /**
     * Label used to display the admin's username.
     */
    private Label label_Username;
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
    private TextField textFieldGenre;
    /**
     * Text field used for filtering games by platform.
     */
    @FXML
    private TextField textFieldPlatform;
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
    /**
     * The list of games in the table.
     */
    private ObservableList<Videogame> gamesList;

    /**
     * Method called from a different controller to set up the Admin profile ahead of time.
     *
     * @param profile The admin profile
     */
    public void setUsuario(Profile profile) {
        this.profile = profile;
        label_Username.setText(profile.getUsername());
    }

    /**
     * Method called from a different controller to set up the controller ahead of time.
     *
     * @param cont The controller instance
     */
    public void setCont(Controller cont) {
        this.cont = cont;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ImageView helpIcon = new ImageView("../images/Help_icon.png");
        menuHelp.setGraphic(helpIcon);
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

    /**
     * Method to load all videogames from the database, immediately displaying them in the table once loaded.
     */
    private void loadAllGames() {
        gamesList.clear();
        gamesList.addAll(cont.getAllGames());
        tableViewGames.setItems(gamesList);
    }

    /**
     * Initializes the TableView columns to prevent any possible issues.
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
     * Used for two things: If any filters are applied, it calls the Search method, otherwise it calls the method to reload the game list.
     */
    private void refreshGamesList() {
        String name = textFieldSearch.getText();
        String genre = textFieldGenre.getText();
        String platform = textFieldPlatform.getText();

        // Si hay filtros aplicados, mantenerlos
        if (!name.isEmpty() || !genre.isEmpty() || !platform.isEmpty()) {
            search(null);
        } else {
            loadAllGames();
        }
    }

    /**
     * Opens the Add Game window, setting up the controller ahead of time.
     *
     * @param event
     */
    @FXML
    private void addGame(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddGamesWindow.fxml"));
            Parent root = fxmlLoader.load();
            AddGamesAdminController aCont = fxmlLoader.getController();
            aCont.setCont(cont);
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

    /**
     * Calls for the method to modify the selected game.
     *
     * @param event
     */
    @FXML
    private void modifyGame(ActionEvent event) {
        if (selected == null) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("ERROR!");
            success.setHeaderText("No selection!");
            success.setContentText("Please select a game before attempting a modification.");
            success.showAndWait();
        } else {
            if (selected == null) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("ERROR!");
                success.setHeaderText("No selection!");
                success.setContentText("Please select a game before attempting deletion of one.");
                success.showAndWait();
            } else {
                if (cont.modifyGame(selected)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Modify successful!");
                    success.setContentText("The game " + selected.getName() + " was modified correctly.");
                    success.showAndWait();
                } else {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("ERROR!");
                    success.setHeaderText("An issue occurred");
                    success.setContentText("The game was failed to be modified. Check the fields and try again.");
                    success.showAndWait();
                }
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
        if (selected == null) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("ERROR!");
            success.setHeaderText("No selection!");
            success.setContentText("Please select a game before attempting deletion of one.");
            success.showAndWait();
        } else {
            if (cont.deleteGame(selected)) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Delte successful!");
                success.setContentText("The game " + selected.getName() + " was deleted.");
                success.showAndWait();
                selected = null;
            } else {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("ERROR!");
                success.setHeaderText("An issue occurred");
                success.setContentText("The game was failed to be modified. Check the fields and try again.");
                success.showAndWait();
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
        String name = textFieldSearch.getText();
        String genre = textFieldGenre.getText();
        String platform = textFieldPlatform.getText();

        gamesList.clear();
        gamesList.addAll(cont.getGamesFiltered(name, genre, platform));
        tableViewGames.setItems(gamesList);
    }

    /**
     * Opens the Help window, setting up the admin profile ahead of time.
     *
     * @param event
     */
    @FXML
    private void helpWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/HelpWindow.fxml"));
            HelpWindowController hCont = fxmlLoader.getController();
            hCont.setUsuario(profile);
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

    /**
     * Updates the selected videogame label and displays certain information about the game.
     */
    @FXML
    private void getSelectedTableItem() {
        selected = tableViewGames.getSelectionModel().getSelectedItem();
        if (selected != null) {
            labelGameInfo.setText(selected.getName() + " - " + selected.getPrice() + "€ - Stock: " + selected.getStock());
        }
    }

    /**
     * Closes the curret window and opens the menu window.
     *
     * @param event
     */
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
        } catch (IOException ex) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
