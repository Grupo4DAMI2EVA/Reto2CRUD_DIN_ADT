package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

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

    @FXML
    private void addGame(MouseEvent event) {
        //DB stuff not ready yet
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
