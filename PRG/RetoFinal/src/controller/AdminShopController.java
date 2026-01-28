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

    public void setUsuario(Profile profile) {
        this.profile = profile;
        labelWelcome.setText("Welcome, " + profile.getUsername());
    }

    public void setCont(Controller cont) {
        this.cont = cont;
        loadAllGames();
    }

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

    @FXML
    private void deleteGame(ActionEvent event) {
        if (selected == null) {
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("ERROR!");
            error.setHeaderText("No selection!");
            error.setContentText("Please select a game before attempting deletion of one.");
            error.showAndWait();
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

    private void getSelectedTableItem(ActionEvent event) {
        selected = tableViewGames.getSelectionModel().getSelectedItem();
        if (selected != null) {
            labelGameInfo.setText(selected.getName());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ImageView helpIcon = new ImageView("../images/Help_icon.png");
        menuHelp.setGraphic(helpIcon);
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
        } catch (IOException ex) {
            Logger.getLogger(AdminShopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
