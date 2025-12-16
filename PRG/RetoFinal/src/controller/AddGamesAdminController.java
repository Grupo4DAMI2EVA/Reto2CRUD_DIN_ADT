package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AddGamesAdminController implements Initializable {

    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldPlatform;
    @FXML
    private TextField textFieldCompany;
    @FXML
    private Spinner<Integer> spinnerStock;
    @FXML
    private ComboBox<?> comboBoxGenre;
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
        //DB stuff not ready yet
        
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Game added successfully!");
        success.setHeaderText(textFieldName.getText()+" was added successfully.");
        //Game title in the content and header
        success.setContentText("The game "+textFieldName.getText()+" was successfully added to the list of games in the store.");
        success.showAndWait();

        Stage currentStage = (Stage) buttonAddGame.getScene().getWindow();
        currentStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
