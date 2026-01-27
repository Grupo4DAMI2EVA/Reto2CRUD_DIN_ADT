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
    private ComboBox<PEGI> comboBoxPEGI;
    @FXML
    private DatePicker datePickerReleaseDate;
    @FXML
    private Button buttonAddGame;
    private Controller cont;
    private AdminShopController adminShopController;

    // Set controller instance
    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public void setAdminShopController(AdminShopController adminShopController) {
        this.adminShopController = adminShopController;
    }

    @FXML
    private void addGame(MouseEvent event) {
        if (cont.addGame(textFieldCompany.getText(), comboBoxGenre.getValue(), textFieldName.getText(),
                comboBoxPlatforms.getValue(), comboBoxPEGI.getValue(), spinnerPrice.getValue(), spinnerStock.getValue(), Date.valueOf(datePickerReleaseDate.getValue()))) {
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
            
            // Recargar la tabla en AdminShopController
            if (adminShopController != null) {
                adminShopController.reloadGames();
            }
            
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
}
