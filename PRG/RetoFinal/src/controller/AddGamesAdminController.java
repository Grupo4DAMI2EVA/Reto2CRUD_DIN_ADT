package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.*;

public class AddGamesAdminController implements Initializable {

    @FXML
    private TextField textFieldName;
    @FXML
    private ComboBox<Platform> comboBoxPlatforms;
    @FXML
    private TextField textFieldCompany;
    @FXML
    private Spinner<Integer> spinnerStock;
    @FXML
    private ComboBox<GameGenre> comboBoxGenre;
    @FXML
    private Spinner<Double> spinnerPrice;
    @FXML
    private TextField textFieldPEGI;
    @FXML
    private DatePicker datePickerReleaseDate;
    @FXML
    private Button buttonAddGame;
    private Controller cont;

    // Set controller instance
    public void setCont(Controller cont) {
        this.cont = cont;
    }

    @FXML
    private void addGame(MouseEvent event) {
        //DB stuff and checks not ready yet

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Game added successfully!");
        success.setHeaderText(textFieldName.getText() + " was added successfully.");
        //Game title in the content and header
        success.setContentText("The game " + textFieldName.getText() + " was successfully added to the list of games in the store.");
        success.showAndWait();

        Alert choice = new Alert(Alert.AlertType.CONFIRMATION);
        choice.setTitle("Add more?");
        choice.setHeaderText("Do you want to add more games?");
        choice.showAndWait();
        if (choice.getResult().equals(ButtonType.CLOSE)) {
            Stage currentStage = (Stage) buttonAddGame.getScene().getWindow();
            currentStage.close();
        } else {
            textFieldName.clear();
            comboBoxPlatforms.valueProperty().set(null);
            textFieldCompany.clear();
            spinnerStock.getValueFactory().setValue(0);
            comboBoxGenre.valueProperty().set(null);
            spinnerPrice.getValueFactory().setValue(0.0);
            textFieldPEGI.clear();
            datePickerReleaseDate.setValue(LocalDate.now());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
