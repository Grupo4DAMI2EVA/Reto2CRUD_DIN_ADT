    package controller;

import java.util.concurrent.TimeoutException;
import javafx.stage.Stage;
import main.Main;
import org.junit.After;
import org.junit.Before;
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

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        FxToolkit.showStage();
    }
    
    private void performLogin(String username, String password) {
        clickOn("#TextField_Username");

        write(username);

        clickOn("#PasswordField_Password");
        write(password);

        clickOn("#Button_LogIn");

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testShop() {
         performLogin("admin1", "1234");
         
         verifyThat("#label_Username", hasText("admin1"));
         
         clickOn("#Button_Store");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}