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
    private AdminShopController adminShopController;

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
    public void setAdminShopController(AdminShopController adminShopController) {
        this.adminShopController = adminShopController;
    }

    @FXML
    private void addGame(MouseEvent event) {
        // Forzar actualización de los valores editables de los spinners
        if (spinnerPrice.getEditor().getText() != null && !spinnerPrice.getEditor().getText().isEmpty()) {
            try {
                spinnerPrice.getValueFactory().setValue(Double.valueOf(spinnerPrice.getEditor().getText()));
            } catch (NumberFormatException e) {
                // Mantener valor actual si hay error
            }
        }
        if (spinnerStock.getEditor().getText() != null && !spinnerStock.getEditor().getText().isEmpty()) {
            try {
                spinnerStock.getValueFactory().setValue(Integer.valueOf(spinnerStock.getEditor().getText()));
            } catch (NumberFormatException e) {
                // Mantener valor actual si hay error
            }
        }
        
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
            
            // Recargar la tabla en AdminShopController
           /* if (adminShopController != null) {
                adminShopController.reloadGames();
            }*/
            
            if (choice.getResult().equals(ButtonType.OK)) {
                // Si dice OK, limpia los campos para añadir otro juego
                textFieldName.clear();
                comboBoxPlatforms.valueProperty().set(null);
                textFieldCompany.clear();
                spinnerStock.getValueFactory().setValue(0);
                comboBoxGenre.valueProperty().set(null);
                spinnerPrice.getValueFactory().setValue(0.0);
                comboBoxPEGI.valueProperty().set(null);
                datePickerReleaseDate.setValue(LocalDate.now());
            } else {
                // Si dice CANCEL, cierra la ventana
                Stage currentStage = (Stage) buttonAddGame.getScene().getWindow();
                currentStage.close();
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
        
        // Initialize Spinner for Stock (0 to 1000, step 1, initial value 0)
        SpinnerValueFactory<Integer> stockValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 1);
        spinnerStock.setValueFactory(stockValueFactory);
        spinnerStock.setEditable(true);
        
        // Initialize Spinner for Price (0.0 to 1000.0, step 0.01, initial value 0.0)
        SpinnerValueFactory<Double> priceValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, 0.0, 0.01);
        spinnerPrice.setValueFactory(priceValueFactory);
        spinnerPrice.setEditable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCBoxes();
    }
}
