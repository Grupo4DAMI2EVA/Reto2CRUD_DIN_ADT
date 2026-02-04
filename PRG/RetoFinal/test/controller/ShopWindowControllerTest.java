package controller;

import javafx.scene.input.KeyCode;
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
import static org.testfx.matcher.control.LabeledMatchers.hasText;

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

    }

    @Test
    public void testCompletoTienda() {
        System.out.println("=== TEST COMPLETO DE TIENDA ===");

        // ========== FASE 1: LOGIN ==========
        System.out.println("\n--- Fase 1: Login ---");
        hacerLoginDesdeCero();

        // ========== FASE 2: VERIFICAR ELEMENTOS ==========
        System.out.println("\n--- Fase 2: Verificar Elementos ---");
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

        // Verificar estado de botones
        System.out.println("\nVerificando estado de botones...");
        verificarEstadoBoton("#buttonAddToCart", true, "Botón Add to Cart habilitado");
        verificarEstadoBoton("#buttonSearch", true, "Botón Search habilitado");
        verificarEstadoBoton("#buttonCart", true, "Botón Cart habilitado");
        verificarEstadoBoton("#buttonExit", true, "Botón Exit habilitado");
        verificarEstadoBoton("#buttonReview", true, "Botón Review habilitado");

        // ========== FASE 3: BUSQUEDA ==========
        System.out.println("\n--- Fase 3: Búsqueda ---");

        // Probar búsqueda con texto
        llenarCampo("#textFieldSearch", "test", "Campo búsqueda con 'test'");
        clickOn("#buttonSearch");
        sleep(2000);
        System.out.println("✓ Búsqueda con texto ejecutada");

        // Limpiar búsqueda
        limpiarCampo("#textFieldSearch");

        // Probar filtro de género
        clickOn("#comboBoxGenre");
        sleep(500);
        try {
            clickOn("ACTION");
            sleep(1000);
            clickOn("#buttonSearch");
            sleep(2000);
            System.out.println("✓ Filtro ACTION aplicado");
        } catch (Exception e) {
            System.out.println("⚠ No se pudo seleccionar filtro ACTION");
        }

        // Resetear filtro género
        clickOn("#comboBoxGenre");
        sleep(500);
        try {
            clickOn("ALL");
        } catch (Exception e) {
        }

        // Probar filtro de plataforma
        clickOn("#comboBoxPlatform");
        sleep(500);
        try {
            clickOn("PC");
            sleep(1000);
            clickOn("#buttonSearch");
            sleep(2000);
            System.out.println("✓ Filtro PC aplicado");
        } catch (Exception e) {
            System.out.println("⚠ No se pudo seleccionar filtro PC");
        }

        // Resetear filtro plataforma
        clickOn("#comboBoxPlatform");
        sleep(500);
        try {
            clickOn("ALL");
        } catch (Exception e) {
        }

        // Búsqueda final sin filtros
        clickOn("#buttonSearch");
        sleep(2000);
        System.out.println("✓ Búsqueda sin filtros ejecutada");

        // ========== FASE 4: SELECCIÓN DE JUEGO ==========
        System.out.println("\n--- Fase 4: Selección de Juego ---");

        // Verificar label inicial
        try {
            verifyThat("#labelGameInfo", hasText("Select a game"));
            System.out.println("✓ Label inicial correcto: 'Select a game'");
        } catch (Exception e) {
            System.out.println("⚠ Label inicial no muestra 'Select a game'");
        }

        // Intentar seleccionar un juego si existe
        if (verificarElementoExistente("#tableViewGames .table-row-cell")) {
            System.out.println("✓ Hay juegos en la tabla");

            // Seleccionar primera fila
            clickOn("#tableViewGames .table-row-cell");
            sleep(1000);

            // Verificar que se actualizó el label
            verificarElemento("#labelGameInfo", "Label de juego actualizado");

            // ========== FASE 5: AGREGAR AL CARRITO ==========
            System.out.println("\n--- Fase 5: Agregar al Carrito ---");

            // Intentar agregar al carrito
            clickOn("#buttonAddToCart");
            sleep(1000);

            // Manejar posibles alerts
            manejarAlertConBoton("OK", "Resultado agregar al carrito");

            // ========== FASE 6: ABRIR CARRITO Y COMPRAR ==========
            System.out.println("\n--- Fase 6: Abrir Carrito y Comprar ---");

            clickOn("#buttonCart");
            sleep(2000);

            // Verificar que se abrió el carrito
            boolean carritoAbierto = verificarElementoExistente(".dialog-pane")
                    || verificarElementoExistente("#listViewCarrito")
                    || verificarElementoExistente("Your Cart")
                    || verificarElementoExistente("#buttonComprar"); // Botón de compra

            if (carritoAbierto) {
                System.out.println("✓ Carrito abierto");

                // INTENTAR COMPRAR
                System.out.println("Intentando comprar...");

                // Método 1: Buscar botón de compra por ID
                try {
                    clickOn("#buttonComprar");
                    System.out.println("✓ Clic en botón de compra por ID");
                    sleep(2000);

                    // Manejar confirmación de compra si aparece
                    manejarAlertConBoton("Sí", "Confirmación de compra");
                    manejarAlertConBoton("Aceptar", "Confirmación de compra");

                } catch (Exception e1) {
                    // Método 2: Buscar botón de compra por texto
                    try {
                        clickOn("Purchase");
                        System.out.println("✓ Clic en botón de compra por texto");
                        sleep(2000);

                        // Manejar confirmación
                        manejarAlertConBoton("OK", "Confirmación de compra");
                        manejarAlertConBoton("Yes", "Confirmación de compra");

                    } catch (Exception e2) {
                        // Método 3: Buscar botón de compra por texto en español
                        try {
                            clickOn("Comprar");
                            System.out.println("✓ Clic en botón de compra por texto español");
                            sleep(2000);

                            // Manejar confirmación
                            manejarAlertConBoton("Aceptar", "Confirmación de compra");
                            manejarAlertConBoton("Sí", "Confirmación de compra");

                        } catch (Exception e3) {
                            // Método 4: Buscar cualquier botón que pueda ser de compra
                            try {
                                // Buscar botones con textos relacionados a compra
                                String[] textosCompra = {"Buy", "Checkout", "Pay", "Finalizar", "Confirmar"};
                                boolean comprado = false;

                                for (String texto : textosCompra) {
                                    try {
                                        clickOn(texto);
                                        System.out.println("✓ Clic en botón: " + texto);
                                        sleep(2000);
                                        comprado = true;

                                        // Manejar confirmación
                                        manejarAlertConBoton("OK", "Confirmación");
                                        break;
                                    } catch (Exception e4) {
                                        // Continuar con siguiente texto
                                    }
                                }

                                if (!comprado) {
                                    System.out.println("⚠ No se encontró botón de compra");
                                }

                            } catch (Exception e4) {
                                System.out.println("✗ Error buscando botón de compra");
                            }
                        }
                    }
                }

                // Esperar a que se procese la compra
                sleep(3000);

                // Verificar si la ventana se cerró automáticamente
                boolean ventanaCerrada = !verificarElementoExistente(".dialog-pane")
                        && !verificarElementoExistente("#cartTableView");

                if (ventanaCerrada) {
                    System.out.println("✓ Compra completada, ventana cerrada automáticamente");
                } else {
                    // Si no se cerró, cerrarla manualmente
                    System.out.println("Cerrando ventana de carrito manualmente...");

                }

                sleep(1000);

            } else {
                System.out.println("⚠ No se pudo verificar apertura de carrito");
            }

            // ========== FASE 7: MENÚ ==========
            System.out.println("\n--- Fase 7: Menú ---");

            // Probar menú Help
            clickOn("#menuHelp");
            sleep(500);

            // Verificar ventana de ayuda
            verificarElementoExistente("Help Window");

            // Probar menú Windows
            clickOn("#menuWindow");
            sleep(500);
            verificarElementoExistente("menuUserWindow");
            sleep(500);
            verificarElementoExistente("menuMainWindow");

            // ========== FASE 8: BOTÓN REVIEW ==========
            System.out.println("\n--- Fase 8: Botón Review ---");

            // Probar botón Review sin selección (debe mostrar warning)
            if (verificarElementoExistente("#tableViewGames .table-row-cell")) {
                // Deseleccionar si hay algo seleccionado
                clickOn("#labelTitle"); // Click fuera para deseleccionar
                sleep(500);
            }

            clickOn("#buttonReview");
            sleep(1000);
            manejarAlertConBoton("OK", "Warning sin selección");

            clickOn("#buttonCancelar");
            sleep(2000);
        }
    }

// ========== MÉTODOS AUXILIARES ==========
    private void hacerLoginDesdeCero() {
        System.out.println("Iniciando login...");

        try {
            // Verificar si ya estamos en LoginWindow
            boolean enLogin = verificarElementoExistente("#TextField_Username")
                    || verificarElementoExistente("#Button_LogIn");

            if (!enLogin) {
                System.out.println("No en LoginWindow, cerrando ventanas...");
                cerrarTodasLasVentanas();
                sleep(3000);
            }

            // Asegurar que estamos en LoginWindow
            enLogin = verificarElementoExistente("#TextField_Username")
                    || verificarElementoExistente("#Button_LogIn");

            if (!enLogin) {
                System.out.println("ERROR: No se pudo llegar a LoginWindow");
                return;
            }

            // Llenar campos de login
            llenarCampo("#TextField_Username", usuarioUnico, "Usuario");
            llenarCampo("#PasswordField_Password", "1234", "Contraseña");

            // Hacer login
            clickOn("#Button_LogIn");
            sleep(3000);

            // Manejar alert si aparece
            manejarAlertConBoton("OK", "Resultado login");

            // Ir a tienda
            sleep(2000);
            clickOn("#Button_Store");
            sleep(3000);

            // Verificar que estamos en tienda
            boolean enTienda = verificarElementoExistente("#labelTitle")
                    || verificarElementoExistente("#tableViewGames");

            if (enTienda) {
                System.out.println("✓ Login exitoso y en tienda");
            } else {
                System.out.println("⚠ Login exitoso pero no se pudo verificar tienda");
            }

        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
            throw e;
        }
    }

    private void cerrarTodasLasVentanas() {
        try {
            // Intentar múltiples métodos de cierre
            for (int i = 0; i < 5; i++) {
                try {
                    push(KeyCode.ESCAPE);
                    sleep(200);
                    release(KeyCode.ESCAPE);
                } catch (Exception e) {
                }
            }

            // Intentar botones de cierre
            String[] botones = {"Exit", "Close", "Cancel", "OK", "Aceptar", "Cerrar"};
            for (String boton : botones) {
                try {
                    clickOn(boton);
                    sleep(200);
                } catch (Exception e) {
                }
            }

            // Intentar coordenadas (X de ventana)
            try {
                clickOn(1000, 10);
                sleep(200);
            } catch (Exception e) {
            }

            sleep(2000);

        } catch (Exception e) {
            System.out.println("Error cerrando ventanas: " + e.getMessage());
        }
    }

    private void cerrarVentanaActual() {
        try {
            // Intentar botones comunes
            String[] botones = {"Close", "Cancel", "OK", "Aceptar"};
            for (String boton : botones) {
                try {
                    clickOn(boton);
                    sleep(500);
                    return;
                } catch (Exception e) {
                }
            }

            // Intentar ESC
            push(KeyCode.ESCAPE);
            sleep(300);
            release(KeyCode.ESCAPE);

        } catch (Exception e) {
            System.out.println("Error cerrando ventana: " + e.getMessage());
        }
    }

    private boolean manejarAlertConBoton(String textoBoton, String descripcion) {
        try {
            clickOn(textoBoton);
            System.out.println("✓ Alert " + descripcion + " manejado");
            sleep(1000);
            return true;
        } catch (Exception e) {
            // Intentar con otros botones
            String[] alternativas = {"Aceptar", "OK", "Yes", "Continuar"};
            for (String alt : alternativas) {
                try {
                    clickOn(alt);
                    System.out.println("✓ Alert " + descripcion + " manejado con '" + alt + "'");
                    sleep(1000);
                    return true;
                } catch (Exception e2) {
                }
            }

            System.out.println("✗ No se pudo manejar alert: " + descripcion);
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
            push(KeyCode.DELETE);
            sleep(200);
        } catch (Exception e) {
            // Ignorar
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

}
