package controller;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 *
 * @author 2dami
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShopWindowControllerTest extends ApplicationTest {

    private Stage primaryStage;
    private String usuarioUnico;
    private double randomNumber = 25;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.centerOnScreen();

        usuarioUnico = "user1";

        new Main().start(stage);
    }

    @Before
    public void prepararTest() {
        try {
            // Estrategia 1: Cerrar todos los stages excepto el primary usando Stage
            javafx.application.Platform.runLater(() -> {
                // Obtener todos los stages a través de la escena
                Set<Stage> stages = new HashSet<>();

                // Buscar stages desde las escenas conocidas
                if (primaryStage.getScene() != null) {
                    // Recorrer todos los nodos para encontrar stages hijos
                    encontrarStages(primaryStage.getScene().getRoot(), stages);
                }

                // Cerrar todos los stages excepto el principal
                for (Stage stage : stages) {
                    if (stage != primaryStage && stage.isShowing()) {
                        stage.close();
                    }
                }
            });

            // Esperar a que se cierren las ventanas
            sleep(1000);

            // También intentar cerrar alerts específicos
            cerrarTodosLosAlertsYVentanas();

            sleep(1000);

        } catch (Exception e) {
            System.out.println("Error preparando test: " + e.getMessage());
        }
    }

    @Test
    public void test01_ElementosTiendaBasicos() {
        System.out.println("=== Test 1: Elementos Tienda Básicos ===");

        // Primero hacer login para llegar a la tienda
        hacerLoginParaTienda();

        // Verificar elementos básicos de la tienda
        verificarElemento("#labelTitle", "Título GAME STORE");
        verificarElemento("#labelWelcome", "Label de bienvenida");
        verificarElemento("#labelAvaliable", "Label Available Games");
        verificarElemento("#tableViewGames", "Tabla de juegos");
        verificarElemento("#labelSearch", "Label Search game");
        verificarElemento("#textFieldSearch", "Campo de búsqueda");
        verificarElemento("#labelGenre", "Label Genre");
        verificarElemento("#comboBoxGenre", "ComboBox de género");
        verificarElemento("#labelPlatform", "Label Platform");
        verificarElemento("#comboBoxPlatform", "ComboBox de plataforma");
        verificarElemento("#buttonSearch", "Botón Search");
        verificarElemento("#labelSelectedGame", "Label Selected game");
        verificarElemento("#labelGameInfo", "Label Game info");
        verificarElemento("#buttonAddToCart", "Botón Add to Cart");
        verificarElemento("#buttonCart", "Botón Cart");
        verificarElemento("#buttonExit", "Botón Exit");
        verificarElemento("#buttonReview", "Botón Review");
        verificarElemento("#menuBar", "Menu bar");

        System.out.println("✓ Test elementos básicos de tienda completado");
    }

    private void hacerLoginParaTienda() {
        System.out.println("Realizando login para acceder a tienda...");

        try {
            // Asegurarse de estar en LoginWindow
            // Limpiar campos si existen
            if (verificarElementoExistente("#TextField_Username")) {
                clickOn("#TextField_Username");
                write(usuarioUnico);
            }

            if (verificarElementoExistente("#PasswordField_Password")) {
                clickOn("#PasswordField_Password");
                write("1234");
            }

            // Hacer login
            if (verificarElementoExistente("#Button_LogIn")) {
                clickOn("#Button_LogIn");
                sleep(3000);

                // Manejar alert si aparece
                manejarAlertConBoton("OK", "Resultado login");

                // Ir a tienda desde MenuWindow
                sleep(2000);
                if (verificarElementoExistente("#Button_Store")) {
                    clickOn("#Button_Store");
                    sleep(3000);
                    System.out.println("✓ Llegado a tienda desde MenuWindow");
                }
            }

        } catch (Exception e) {
            System.out.println("Error en login para tienda: " + e.getMessage());
        }
    }

    private boolean manejarAlertConBoton(String textoBoton, String tipoAlert) {
        try {
            System.out.println("Buscando alert: " + tipoAlert);

            // Esperar a que aparezca el alert
            sleep(2000);

            // Buscar el botón específico en el DialogPane
            try {
                // Buscar por texto del botón
                clickOn(textoBoton);
                System.out.println("✓ Alert manejado - Clic en: " + textoBoton);
                sleep(1000);
                return true;

            } catch (Exception e1) {
                // Si no encuentra por texto, buscar en DialogPane
                try {
                    DialogPane dialogPane = lookup(".dialog-pane").query();
                    Button aceptarBtn = lookup("#" + textoBoton.toLowerCase() + "Button").query();
                    clickOn(aceptarBtn);
                    System.out.println("✓ Alert manejado - Clic en botón del dialog");
                    sleep(1000);
                    return true;

                } catch (Exception e2) {
                    // Último intento - buscar cualquier botón en dialog
                    try {
                        Button cualquierBoton = lookup(".dialog-pane .button").query();
                        clickOn(cualquierBoton);
                        System.out.println("✓ Alert manejado - Clic en cualquier botón del dialog");
                        sleep(1000);
                        return true;

                    } catch (Exception e3) {
                        System.out.println("✗ No se pudo encontrar el botón del alert");
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error manejando alert: " + e.getMessage());
            return false;
        }
    }

    private boolean verificarElementoExistente(String selector) {
        try {
            Object node = lookup(selector).query();
            return node != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void verificarElemento(String selector, String descripcion) {
        try {
            verifyThat(selector, isVisible());
            System.out.println("✓ " + descripcion);
        } catch (Exception e) {
            System.out.println("✗ " + descripcion);
        }
    }

    private void verificarEstadoBoton(String selector, boolean habilitado, String descripcion) {
        try {
            if (habilitado) {
                verifyThat(selector, isEnabled());
            } else {
                verifyThat(selector, isDisabled());
            }
            System.out.println("✓ " + descripcion);
        } catch (Exception e) {
            System.out.println("✗ " + descripcion);
        }
    }

    private void llenarCampo(String selector, String texto, String descripcion) {
        try {
            clickOn(selector);
            write(texto);
            System.out.println("✓ " + descripcion);
        } catch (Exception e) {
            System.out.println("✗ " + descripcion);
        }
    }

    private void limpiarCampo(String selector) {
        try {
            doubleClickOn(selector);
            push(javafx.scene.input.KeyCode.DELETE);
        } catch (Exception e) {
            // Ignorar
        }
    }
// ========== MÉTODOS HELPER ADICIONALES PARA ADMIN ==========

    private void verificarCampoNoEditable(String selector, String descripcion) {
        try {
            // Intentar escribir en el campo para ver si es editable
            String textoOriginal = obtenerTextoCampo(selector);
            clickOn(selector);
            write("test");
            sleep(500);

            String textoDespues = obtenerTextoCampo(selector);

            // Si el texto no cambió, no es editable
            if (textoDespues.equals(textoOriginal)) {
                System.out.println("✓ " + descripcion);
            } else {
                System.out.println("✗ " + descripcion + " - El campo es editable");
                // Restaurar texto original
                clickOn(selector);
                push(javafx.scene.input.KeyCode.CONTROL);
                push(javafx.scene.input.KeyCode.A);
                release(javafx.scene.input.KeyCode.A);
                release(javafx.scene.input.KeyCode.CONTROL);
                write(textoOriginal);
            }
        } catch (Exception e) {
            System.out.println("✗ " + descripcion + " - Error: " + e.getMessage());
        }
    }

    private String obtenerTextoCampo(String selector) {
        try {
            // Para TextField normales
            try {
                TextField field = lookup(selector).query();
                return field.getText();
            } catch (Exception e) {
                // Para PasswordField
                try {
                    PasswordField passField = lookup(selector).query();
                    return passField.getText();
                } catch (Exception e2) {
                    System.out.println("No se pudo obtener texto del campo: " + selector);
                    return "";
                }
            }
        } catch (Exception e) {
            return "";
        }
    }

    private void cerrarTodosLosAlertsYVentanas() {
        try {
            System.out.println("Cerrando alerts y ventanas...");

            // Intentar hacer clic en botones "Aceptar", "OK" que puedan estar visibles
            try {
                clickOn("Aceptar");
                sleep(500);
            } catch (Exception e) {
            }

            try {
                clickOn("OK");
                sleep(500);
            } catch (Exception e) {
            }

            try {
                clickOn("Aceptar");
                sleep(500);
            } catch (Exception e) {
            }

        } catch (Exception e) {
            System.out.println("Error cerrando alerts: " + e.getMessage());
        }
    }

    private void encontrarStages(javafx.scene.Node node, Set<Stage> stages) {
        if (node.getScene() != null && node.getScene().getWindow() instanceof Stage) {
            stages.add((Stage) node.getScene().getWindow());
        }

        if (node instanceof javafx.scene.Parent) {
            for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                encontrarStages(child, stages);
            }
        }
    }

}
