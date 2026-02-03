package controller;

import java.util.concurrent.TimeoutException;
import javafx.stage.Stage;
import main.Main;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class AddGamesAdminControllerTest extends ApplicationTest {

    public AddGamesAdminControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FxToolkit.showStage();
    }

    private void performLogin(String username, String password) {
        try {
            clickOn("#TextField_Username");
            write(username);
            clickOn("#PasswordField_Password");
            write(password);
            clickOn("#Button_LogIn");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddGame() {
        performLogin("admin1", "1234");
        try {
            verifyThat("#label_Username", hasText("admin1"));
            clickOn("#buttonAdd");
            Thread.sleep(2000);
            clickOn("#comboBoxPlatforms");
            clickOn("");
            clickOn("#textFieldCompany");
            write("e");
            clickOn("#spinnerStock");
            write("10");
            clickOn("#comboBoxGenre");
            clickOn("");
            clickOn("#spinnerPrice");
            write("10.40");
            clickOn("#comboBoxPEGI");
            write("e");
            clickOn("#datePickerReleaseDate");
            write("e");
            clickOn("#buttonAddGame");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
