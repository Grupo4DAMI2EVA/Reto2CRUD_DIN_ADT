package controller;

import java.util.concurrent.TimeoutException;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import main.Main;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class AdminShopControllerTest extends ApplicationTest {

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
    public void tests() {
        performLogin("admin1", "1234");
        try {
            verifyThat("#label_Username", hasText("admin1"));
            clickOn("#Button_Store");
            Thread.sleep(2000);
            clickOn("Grand Theft Auto V");
            verifyThat("#labelGameInfo", hasText("Grand Theft Auto V"));
            clickOn("#buttonDelete");
            Thread.sleep(2000);
            clickOn("OK");
            Thread.sleep(2000);
            clickOn("OK");
            Thread.sleep(2000);

            clickOn("#textFieldSearch");
            write("test");
            clickOn("#buttonSearch");
            Thread.sleep(2000);

            doubleClickOn("#textFieldSearch");
            push(KeyCode.DELETE);
            Thread.sleep(200);

            clickOn("#comboBoxGenre");
            Thread.sleep(500);
            clickOn("ACTION");
            Thread.sleep(1000);
            clickOn("#buttonSearch");
            Thread.sleep(2000);

            clickOn("#comboBoxGenre");
            Thread.sleep(500);
            clickOn("ALL");

            clickOn("#comboBoxPlatform");
            Thread.sleep(500);
            clickOn("PC");
            Thread.sleep(1000);
            clickOn("#buttonSearch");
            Thread.sleep(2000);
            System.out.println("✓ Filtro PC aplicado");

            clickOn("#comboBoxPlatform");
            Thread.sleep(500);
            clickOn("ALL");

            clickOn("#buttonSearch");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
