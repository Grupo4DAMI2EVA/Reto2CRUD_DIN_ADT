package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Profile;

public class AdminShopController implements Initializable {

    private Label label_Username;
    @FXML
    private Label labelWelcome;
    @FXML
    private Label labelBalance;
    @FXML
    private TableView<?> tableViewGames;
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
    private Button buttonAdd;
    @FXML
    private Button buttonExit;
    @FXML
    private Button buttonModify;
    @FXML
    private Button buttonDelete;

    private Profile profile;
    private Controller cont;

    public void setUsuario(Profile profile) {
        this.profile = profile;
        label_Username.setText(profile.getUsername());
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    @FXML
    private void addGame(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddGamesAdminController.fxml"));
            Parent root = fxmlLoader.load();
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
    private void modifyGame(MouseEvent event) {

    }

    @FXML
    private void deleteGame(MouseEvent event) {

    }

    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
