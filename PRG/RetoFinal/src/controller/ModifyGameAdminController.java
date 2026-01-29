package controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.*;

public class ModifyGameAdminController implements Initializable {

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
    private Button buttonModifyGame;
    
    private Controller cont;
    private AdminShopController adminShopController;
    private Videogame videogameToModify;

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public void setAdminShopController(AdminShopController adminShopController) {
        this.adminShopController = adminShopController;
    }

    public void setVideogame(Videogame videogame) {
        this.videogameToModify = videogame;
        loadGameData();
    }

    private void loadGameData() {
        if (videogameToModify != null) {
            textFieldName.setText(videogameToModify.getName());
            textFieldCompany.setText(videogameToModify.getCompanyName());
            comboBoxGenre.setValue(videogameToModify.getGameGenre());
            comboBoxPlatforms.setValue(videogameToModify.getPlatforms());
            comboBoxPEGI.setValue(videogameToModify.getPegi());
            spinnerPrice.getValueFactory().setValue(videogameToModify.getPrice());
            spinnerStock.getValueFactory().setValue(videogameToModify.getStock());
            
            // Convertir Date a LocalDate
            if (videogameToModify.getReleaseDate() != null) {
                // Para java.sql.Date, usar toLocalDate() directamente
                if (videogameToModify.getReleaseDate() instanceof java.sql.Date) {
                    datePickerReleaseDate.setValue(((java.sql.Date) videogameToModify.getReleaseDate()).toLocalDate());
                } else {
                    // Para java.util.Date
                    LocalDate localDate = videogameToModify.getReleaseDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                    datePickerReleaseDate.setValue(localDate);
                }
            }
        }
    }

    @FXML
    private void modifyGame(MouseEvent event) {
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
        
        // Actualizar el objeto videogame con los nuevos valores
        videogameToModify.setName(textFieldName.getText());
        videogameToModify.setCompanyName(textFieldCompany.getText());
        videogameToModify.setGameGenre(comboBoxGenre.getValue());
        videogameToModify.setPlatforms(comboBoxPlatforms.getValue());
        videogameToModify.setPegi(comboBoxPEGI.getValue());
        videogameToModify.setPrice(spinnerPrice.getValue());
        videogameToModify.setStock(spinnerStock.getValue());
        videogameToModify.setReleaseDate(Date.valueOf(datePickerReleaseDate.getValue()));

        if (cont.modifyGame(videogameToModify)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Game modified successfully!");
            success.setHeaderText(videogameToModify.getName() + " was modified successfully.");
            success.setContentText("The game " + videogameToModify.getName() + " was successfully modified in the store.");
            success.showAndWait();

            // Recargar la tabla en AdminShopController
            if (adminShopController != null) {
                adminShopController.reloadGames();
            }

            // Cerrar la ventana
            Stage currentStage = (Stage) buttonModifyGame.getScene().getWindow();
            currentStage.close();
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("ERROR");
            error.setContentText("There was an error while attempting to modify the game. Check the fields to see if anything's wrong.");
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
