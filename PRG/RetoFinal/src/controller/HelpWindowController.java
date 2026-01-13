package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;

public class HelpWindowController implements Initializable {

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menu;
    @FXML
    private MenuItem item1;
    @FXML
    private MenuItem item2;
    @FXML
    private MenuItem item3;
    @FXML
    private MenuItem item4;
    @FXML
    private TextArea info;

    private Profile profile;
    private Controller cont;

    public void setUsuario(Profile profile) {
        this.profile = profile;
    }

    public void loadAdminItems() {
        item1.setText("Shop Management");
        item2.setText("Add Game");
        item3.setText("Modify Game");
        item4.setText("Delete Game");
        item4.setVisible(true);
        item4.setDisable(false);
    }
    
    @FXML
    private void changeHelpText1() {
        if (profile instanceof Admin) {

        } else {

        }
    }
    
    @FXML
    private void changeHelpText2() {
        if (profile instanceof Admin) {
            info.setText("");
        } else {
            info.setText("");
        }
    }

    @FXML
    private void changeHelpText3() {
        if (profile instanceof Admin) {
            info.setText("");
        } else {
            info.setText("");
        }
    }

    @FXML
    private void changeHelpText4() {
        if (profile instanceof Admin) {
            info.setText("");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (profile instanceof Admin) {
            loadAdminItems();
        }
    }
}
