package controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.*;

/**
 *
 * @author Usuario
 */
public class AddGamesAdminController implements Initializable {

    /**
     * Field for the new game's name.
     */
    @FXML
    private TextField textFieldName;
    /**
     * Combobox for the new game's platform.
     */
    @FXML
    private ComboBox<Platform> comboBoxPlatforms;
    /**
     * Field for the new game's company's name.
     */
    @FXML
    private TextField textFieldCompany;
    /**
     * Spinner for the amount of stock to be added for the new game.
     */
    @FXML
    private Spinner<Integer> spinnerStock;
    /**
     * Combobox for the new game's genre.
     */
    @FXML
    private ComboBox<GameGenre> comboBoxGenre;
    /**
     * Spinner for the price of the new game.
     */
    @FXML
    private Spinner<Double> spinnerPrice;
    /**
     * Combobox for the new game's PEGI Rating.
     */
    @FXML
    private ComboBox<PEGI> comboBoxPEGI;
    /**
     * DatePicker for the release date of the new game.
     */
    @FXML
    private DatePicker datePickerReleaseDate;
    @FXML
    private Button buttonAddGame;
    /**
     * The controller used for calling database methods.
     */
    private Controller cont;

    /**
     * Method called from a different controller to set up the controller ahead of time.
     *
     * @param cont
     */
    public void setCont(Controller cont) {
        this.cont = cont;
    }

    /**
     * The main method that is used cofr compiling the info to add a game, asking the admin fi they want to add more games if successful, or showing an error message when something fails.
     *
     * @param event
     */
    @FXML
    private void addGame(MouseEvent event) {
        if (cont.addGame(textFieldCompany.getText(), comboBoxGenre.getValue(), textFieldName.getText(),
                comboBoxPlatforms.getValue(), comboBoxPEGI.getValue(), spinnerPrice.getValue(), spinnerStock.getValue(), Date.valueOf(datePickerReleaseDate.getValue()))) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Game added successfully!");
            success.setHeaderText(textFieldName.getText() + " was added successfully.");
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
                comboBoxPEGI.valueProperty().set(null);
                datePickerReleaseDate.setValue(LocalDate.now());
            }
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("ERROR");
            error.setContentText("There was an error while attempting to add the game. Check the fields to see if anything's wrong.");
            error.showAndWait();
        }
    }

    /**
     * Method to set up the values for the Enum comboboxes.
     */
    private void setupCBoxes() {
        comboBoxGenre.getItems().addAll(GameGenre.values());
        comboBoxPlatforms.getItems().addAll(Platform.values());
        comboBoxPEGI.getItems().addAll(PEGI.values());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCBoxes();
    }
}
