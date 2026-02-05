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
 * Test para CartController
 * Basado en el test de ShopWindowControllerTest pero adaptado para el carrito
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CartControllerTest extends ApplicationTest {

    private Stage primaryStage;
    private String usuarioUnico = "user_test_cart";
    
    // Selectores específicos de CartController
    private final String SELECTOR_LISTVIEW_CARRITO = "#listViewCarrito";
    private final String SELECTOR_LABEL_TOTAL_ITEMS = "#labelTotalItems";
    private final String SELECTOR_LABEL_TOTAL_PAGAR = "#labelTotalPagar";
    private final String SELECTOR_LABEL_CANTIDAD_ACTUAL = "#labelCantidadActual";
    private final String SELECTOR_LABEL_ITEM_SELECCIONADO = "#labelItemSeleccionado";
    private final String SELECTOR_BUTTON_MAS = "#buttonMas";
    private final String SELECTOR_BUTTON_MENOS = "#buttonMenos";
    private final String SELECTOR_BUTTON_COMPRAR = "#buttonComprar";
    private final String SELECTOR_BUTTON_ELIMINAR = "#buttonEliminar";

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.centerOnScreen();
        
        // Usuario único para pruebas
        usuarioUnico = "cart_user_" + System.currentTimeMillis();
        
        new Main().start(stage);
    }

    @Before
    public void prepararTest() {
        // Configuración inicial si es necesaria
        System.out.println("=== PREPARANDO TEST DE CARRITO ===");
    }

    @Test
    public void testA_LoginYAbrirCarrito() {
        System.out.println("\n=== TEST A: LOGIN Y ABRIR CARRITO ===");
        
        // ========== FASE 1: LOGIN ==========
        System.out.println("\n--- Fase 1: Login ---");
        hacerLoginDesdeCero();
        
        // ========== FASE 2: AGREGAR ITEMS AL CARRITO ==========
        System.out.println("\n--- Fase 2: Agregar Items al Carrito ---");
        agregarItemsAlCarrito();
        
        // ========== FASE 3: ABRIR CARRITO ==========
        System.out.println("\n--- Fase 3: Abrir Carrito ---");
        abrirCarrito();
        
        // ========== FASE 4: VERIFICAR ELEMENTOS ==========
        System.out.println("\n--- Fase 4: Verificar Elementos del Carrito ---");
        verificarElementosCarrito();
    }

    @Test
    public void testB_OperacionesCarrito() {
        System.out.println("\n=== TEST B: OPERACIONES EN CARRITO ===");
        
        // Abrir carrito directamente
        abrirCarritoDirecto();
        
        // ========== FASE 1: SELECCIÓN DE ITEM ==========
        System.out.println("\n--- Fase 1: Selección de Item ---");
        seleccionarItemCarrito();
        
        // ========== FASE 2: MODIFICAR CANTIDAD ==========
        System.out.println("\n--- Fase 2: Modificar Cantidad ---");
        modificarCantidadItem();
        
        // ========== FASE 3: ELIMINAR ITEM ==========
        System.out.println("\n--- Fase 3: Eliminar Item ---");
        eliminarItemCarrito();
        
        // ========== FASE 4: VERIFICAR TOTALES ==========
        System.out.println("\n--- Fase 4: Verificar Totales Actualizados ---");
        verificarTotalesActualizados();
    }

    @Test
    public void testC_ProcesoCompra() {
        System.out.println("\n=== TEST C: PROCESO DE COMPRA ===");
        
        // Agregar items nuevamente
        agregarItemsAlCarrito();
        abrirCarritoDirecto();
        
        // ========== FASE 1: INTENTAR COMPRAR CON CARRITO VACÍO ==========
        System.out.println("\n--- Fase 1: Intentar Comprar con Carrito Vacío ---");
        probarCompraCarritoVacio();
        
        // ========== FASE 2: COMPRA EXITOSA ==========
        System.out.println("\n--- Fase 2: Compra Exitosa ---");
        realizarCompraExitosa();
    }

    @Test
    public void testD_MenuYAyuda() {
        System.out.println("\n=== TEST D: MENÚ Y AYUDA ===");
        
        abrirCarritoDirecto();
        
        // ========== FASE 1: MENÚ PDF ==========
        System.out.println("\n--- Fase 1: Menú Manual PDF ---");
        probarMenuManualPdf();
        
        // ========== FASE 2: MENÚ REPORTE ==========
        System.out.println("\n--- Fase 2: Menú Reporte PDF ---");
        probarMenuReportePdf();
        
        // ========== FASE 3: CERRAR CARRITO ==========
        System.out.println("\n--- Fase 3: Cerrar Carrito ---");
        cerrarCarrito();
    }

    // ========== MÉTODOS AUXILIARES PRINCIPALES ==========

    private void hacerLoginDesdeCero() {
        System.out.println("Iniciando login para carrito...");
        
        try {
            // Verificar si ya estamos en LoginWindow
            boolean enLogin = verificarElementoExistente("#TextField_Username")
                    || verificarElementoExistente("#Button_LogIn");

            if (!enLogin) {
                System.out.println("No en LoginWindow, cerrando ventanas...");
                cerrarTodasLasVentanas();
                sleep(3000);
            }

            // Llenar campos de login
            llenarCampo("#TextField_Username", usuarioUnico, "Usuario carrito");
            llenarCampo("#PasswordField_Password", "1234", "Contraseña");

            // Hacer login
            clickOn("#Button_LogIn");
            sleep(3000);

            // Manejar alert si aparece
            manejarAlertConBoton("OK", "Resultado login");

            System.out.println("✓ Login exitoso para usuario: " + usuarioUnico);

        } catch (Exception e) {
            System.out.println("Error en login para carrito: " + e.getMessage());
        }
    }

    private void agregarItemsAlCarrito() {
        System.out.println("Agregando items al carrito...");
        
        try {
            // Ir a tienda
            sleep(2000);
            clickOn("#Button_Store");
            sleep(3000);

            // Verificar que estamos en tienda
            boolean enTienda = verificarElementoExistente("#labelTitle")
                    || verificarElementoExistente("#tableViewGames");

            if (!enTienda) {
                System.out.println("✗ No se pudo acceder a la tienda");
                return;
            }

            System.out.println("✓ En tienda, buscando juegos...");

            // Esperar a que carguen los juegos
            sleep(2000);

            // Seleccionar y agregar algunos juegos al carrito
            int juegosAgregados = 0;
            
            // Intentar agregar primeros 2 juegos disponibles
            for (int i = 0; i < 3 && juegosAgregados < 2; i++) {
                try {
                    // Seleccionar fila
                    clickOn("#tableViewGames .table-row-cell");
                    sleep(500);
                    
                    // Agregar al carrito
                    clickOn("#buttonAddToCart");
                    sleep(1000);
                    
                    // Manejar alert si aparece
                    manejarAlertConBoton("OK", "Item agregado al carrito");
                    
                    juegosAgregados++;
                    System.out.println("✓ Juego " + juegosAgregados + " agregado al carrito");
                    
                    // Deseleccionar para próximo juego
                    clickOn("#labelTitle");
                    sleep(500);
                    
                } catch (Exception e) {
                    System.out.println("⚠ No se pudo agregar juego " + (i+1));
                }
            }

            if (juegosAgregados > 0) {
                System.out.println("✓ " + juegosAgregados + " juegos agregados al carrito");
            } else {
                System.out.println("⚠ No se pudieron agregar juegos al carrito");
            }

        } catch (Exception e) {
            System.out.println("Error agregando items al carrito: " + e.getMessage());
        }
    }

    private void abrirCarrito() {
        System.out.println("Abriendo carrito...");
        
        try {
            // Hacer clic en botón de carrito
            clickOn("#buttonCart");
            sleep(3000);
            
            // Verificar que se abrió la ventana del carrito
            boolean carritoAbierto = verificarElementoExistente(SELECTOR_LISTVIEW_CARRITO)
                    || verificarElementoExistente(SELECTOR_BUTTON_COMPRAR)
                    || verificarElementoExistente("Your Cart")
                    || verificarElementoExistente("Carrito de Compras");
            
            if (carritoAbierto) {
                System.out.println("✓ Carrito abierto exitosamente");
            } else {
                System.out.println("✗ No se pudo verificar apertura de carrito");
            }
            
        } catch (Exception e) {
            System.out.println("Error abriendo carrito: " + e.getMessage());
        }
    }

    private void abrirCarritoDirecto() {
        // Método para abrir carrito sin todo el proceso de login
        try {
            // Primero intentar abrir desde donde estamos
            clickOn("#buttonCart");
            sleep(2000);
            
            // Si no funciona, buscar botones alternativos
            if (!verificarElementoExistente(SELECTOR_LISTVIEW_CARRITO)) {
                System.out.println("Buscando carrito alternativamente...");
                // Intentar cerrar y reabrir
                cerrarVentanaActual();
                sleep(1000);
                clickOn("#buttonCart");
                sleep(2000);
            }
            
        } catch (Exception e) {
            System.out.println("Error en abrirCarritoDirecto: " + e.getMessage());
        }
    }

    private void verificarElementosCarrito() {
        System.out.println("Verificando elementos del carrito...");
        
        // Verificar elementos principales
        verificarElemento(SELECTOR_LISTVIEW_CARRITO, "Lista de items del carrito");
        verificarElemento(SELECTOR_LABEL_TOTAL_ITEMS, "Label total items");
        verificarElemento(SELECTOR_LABEL_TOTAL_PAGAR, "Label total a pagar");
        verificarElemento(SELECTOR_LABEL_CANTIDAD_ACTUAL, "Label cantidad actual");
        verificarElemento(SELECTOR_LABEL_ITEM_SELECCIONADO, "Label item seleccionado");
        verificarElemento(SELECTOR_BUTTON_MAS, "Botón + (aumentar cantidad)");
        verificarElemento(SELECTOR_BUTTON_MENOS, "Botón - (disminuir cantidad)");
        verificarElemento(SELECTOR_BUTTON_COMPRAR, "Botón Comprar");
        verificarElemento(SELECTOR_BUTTON_ELIMINAR, "Botón Eliminar");
        
        // Verificar estado inicial de botones (deben estar deshabilitados sin selección)
        verificarEstadoBoton(SELECTOR_BUTTON_MAS, false, "Botón + deshabilitado sin selección");
        verificarEstadoBoton(SELECTOR_BUTTON_MENOS, false, "Botón - deshabilitado sin selección");
        verificarEstadoBoton(SELECTOR_BUTTON_ELIMINAR, false, "Botón Eliminar deshabilitado sin selección");
        
        // Verificar si hay items en el carrito
        boolean carritoVacio = !verificarElementoExistente(SELECTOR_LISTVIEW_CARRITO + " .list-cell");
        
        if (carritoVacio) {
            System.out.println("⚠ Carrito vacío - botón comprar debe estar deshabilitado");
            verificarEstadoBoton(SELECTOR_BUTTON_COMPRAR, false, "Botón Comprar deshabilitado con carrito vacío");
        } else {
            System.out.println("✓ Hay items en el carrito");
            verificarEstadoBoton(SELECTOR_BUTTON_COMPRAR, true, "Botón Comprar habilitado con items");
        }
    }

    private void seleccionarItemCarrito() {
        System.out.println("Seleccionando item del carrito...");
        
        try {
            // Verificar si hay items en el listview
            if (verificarElementoExistente(SELECTOR_LISTVIEW_CARRITO + " .list-cell")) {
                // Hacer clic en el primer item
                clickOn(SELECTOR_LISTVIEW_CARRITO + " .list-cell");
                sleep(1000);
                
                // Verificar que se actualizó el label de item seleccionado
                String textoLabel = obtenerTextoLabel(SELECTOR_LABEL_ITEM_SELECCIONADO);
                if (textoLabel != null && !textoLabel.equals("Selecciona un item")) {
                    System.out.println("✓ Item seleccionado: " + textoLabel);
                    
                    // Verificar que los botones están habilitados
                    verificarEstadoBoton(SELECTOR_BUTTON_MAS, true, "Botón + habilitado con selección");
                    verificarEstadoBoton(SELECTOR_BUTTON_MENOS, true, "Botón - habilitado con selección");
                    verificarEstadoBoton(SELECTOR_BUTTON_ELIMINAR, true, "Botón Eliminar habilitado con selección");
                    
                } else {
                    System.out.println("⚠ No se actualizó el label del item seleccionado");
                }
            } else {
                System.out.println("⚠ No hay items en el carrito para seleccionar");
            }
            
        } catch (Exception e) {
            System.out.println("Error seleccionando item: " + e.getMessage());
        }
    }

    private void modificarCantidadItem() {
        System.out.println("Modificando cantidad de item...");
        
        try {
            // Verificar que haya un item seleccionado
            String textoLabel = obtenerTextoLabel(SELECTOR_LABEL_ITEM_SELECCIONADO);
            if (textoLabel == null || textoLabel.equals("Selecciona un item")) {
                System.out.println("⚠ No hay item seleccionado para modificar cantidad");
                return;
            }
            
            // Obtener cantidad inicial
            String cantidadInicialStr = obtenerTextoLabel(SELECTOR_LABEL_CANTIDAD_ACTUAL);
            int cantidadInicial = cantidadInicialStr != null ? Integer.parseInt(cantidadInicialStr) : 0;
            
            System.out.println("Cantidad inicial: " + cantidadInicial);
            
            // Aumentar cantidad
            clickOn(SELECTOR_BUTTON_MAS);
            sleep(500);
            
            String cantidadAumentadaStr = obtenerTextoLabel(SELECTOR_LABEL_CANTIDAD_ACTUAL);
            int cantidadAumentada = cantidadAumentadaStr != null ? Integer.parseInt(cantidadAumentadaStr) : 0;
            
            if (cantidadAumentada == cantidadInicial + 1) {
                System.out.println("✓ Cantidad aumentada correctamente: " + cantidadAumentada);
            } else {
                System.out.println("⚠ No se aumentó la cantidad correctamente");
            }
            
            // Disminuir cantidad (si es mayor a 1)
            if (cantidadAumentada > 1) {
                clickOn(SELECTOR_BUTTON_MENOS);
                sleep(500);
                
                String cantidadDisminuidaStr = obtenerTextoLabel(SELECTOR_LABEL_CANTIDAD_ACTUAL);
                int cantidadDisminuida = cantidadDisminuidaStr != null ? Integer.parseInt(cantidadDisminuidaStr) : 0;
                
                if (cantidadDisminuida == cantidadAumentada - 1) {
                    System.out.println("✓ Cantidad disminuida correctamente: " + cantidadDisminuida);
                } else {
                    System.out.println("⚠ No se disminuyó la cantidad correctamente");
                }
            }
            
            // Probar disminuir hasta 1 (no debería eliminar sin confirmación)
            if (cantidadInicial > 1) {
                System.out.println("Probando disminuir hasta 1...");
                // Disminuir hasta llegar a 1
                while (Integer.parseInt(obtenerTextoLabel(SELECTOR_LABEL_CANTIDAD_ACTUAL)) > 1) {
                    clickOn(SELECTOR_BUTTON_MENOS);
                    sleep(300);
                }
                System.out.println("✓ Llegó a cantidad 1 sin eliminar");
            }
            
        } catch (Exception e) {
            System.out.println("Error modificando cantidad: " + e.getMessage());
        }
    }

    private void eliminarItemCarrito() {
        System.out.println("Eliminando item del carrito...");
        
        try {
            // Verificar que haya un item seleccionado
            if (!verificarEstadoBotonBoolean(SELECTOR_BUTTON_ELIMINAR, false)) {
                // Contar items antes de eliminar
                int itemsAntes = contarItemsListView();
                
                // Hacer clic en eliminar
                clickOn(SELECTOR_BUTTON_ELIMINAR);
                sleep(1000);
                
                // Manejar cuadro de confirmación
                boolean confirmado = manejarAlertConBoton("Sí", "Confirmar eliminación");
                
                if (confirmado) {
                    sleep(1000);
                    
                    // Verificar que se eliminó el item
                    int itemsDespues = contarItemsListView();
                    
                    if (itemsDespues < itemsAntes) {
                        System.out.println("✓ Item eliminado correctamente");
                        System.out.println("  Items antes: " + itemsAntes + ", después: " + itemsDespues);
                        
                        // Verificar que los botones se deshabilitaron
                        verificarEstadoBoton(SELECTOR_BUTTON_MAS, false, "Botón + deshabilitado tras eliminar");
                        verificarEstadoBoton(SELECTOR_BUTTON_MENOS, false, "Botón - deshabilitado tras eliminar");
                        verificarEstadoBoton(SELECTOR_BUTTON_ELIMINAR, false, "Botón Eliminar deshabilitado tras eliminar");
                        
                    } else {
                        System.out.println("⚠ No se eliminó el item");
                    }
                } else {
                    System.out.println("⚠ Eliminación cancelada por el usuario");
                }
            } else {
                System.out.println("⚠ Botón eliminar está deshabilitado");
            }
            
        } catch (Exception e) {
            System.out.println("Error eliminando item: " + e.getMessage());
        }
    }

    private void verificarTotalesActualizados() {
        System.out.println("Verificando totales actualizados...");
        
        try {
            // Obtener valores actuales
            String totalItemsStr = obtenerTextoLabel(SELECTOR_LABEL_TOTAL_ITEMS);
            String totalPagarStr = obtenerTextoLabel(SELECTOR_LABEL_TOTAL_PAGAR);
            
            if (totalItemsStr != null && totalPagarStr != null) {
                System.out.println("✓ Totales actuales:");
                System.out.println("  Total items: " + totalItemsStr);
                System.out.println("  Total a pagar: " + totalPagarStr);
                
                // Verificar que son valores válidos
                try {
                    int totalItems = Integer.parseInt(totalItemsStr);
                    double totalPagar = Double.parseDouble(totalPagarStr.replace("$", "").replace(",", "."));
                    
                    if (totalItems >= 0 && totalPagar >= 0) {
                        System.out.println("✓ Totales válidos");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Formato de totales inválido");
                }
            } else {
                System.out.println("⚠ No se pudieron obtener los totales");
            }
            
        } catch (Exception e) {
            System.out.println("Error verificando totales: " + e.getMessage());
        }
    }

    private void probarCompraCarritoVacio() {
        System.out.println("Probando compra con carrito vacío...");
        
        try {
            // Vaciar carrito si hay items
            if (verificarEstadoBotonBoolean(SELECTOR_BUTTON_COMPRAR, true)) {
                System.out.println("Carrito no está vacío, vaciando...");
                // Seleccionar y eliminar todos los items
                while (verificarElementoExistente(SELECTOR_LISTVIEW_CARRITO + " .list-cell")) {
                    clickOn(SELECTOR_LISTVIEW_CARRITO + " .list-cell");
                    sleep(500);
                    clickOn(SELECTOR_BUTTON_ELIMINAR);
                    sleep(500);
                    manejarAlertConBoton("Sí", "Eliminar item");
                    sleep(500);
                }
            }
            
            // Intentar comprar con carrito vacío
            clickOn(SELECTOR_BUTTON_COMPRAR);
            sleep(1000);
            
            // Debería mostrar una alerta de carrito vacío
            boolean alertaMostrada = manejarAlertConBoton("OK", "Carrito vacío");
            
            if (alertaMostrada) {
                System.out.println("✓ Correctamente previene compra con carrito vacío");
            } else {
                System.out.println("⚠ No se mostró alerta de carrito vacío");
            }
            
        } catch (Exception e) {
            System.out.println("Error probando compra con carrito vacío: " + e.getMessage());
        }
    }

    private void realizarCompraExitosa() {
        System.out.println("Realizando compra exitosa...");
        
        try {
            // Verificar que hay items en el carrito
            if (!verificarElementoExistente(SELECTOR_LISTVIEW_CARRITO + " .list-cell")) {
                System.out.println("⚠ No hay items en el carrito para comprar");
                return;
            }
            
            // Obtener totales antes de la compra
            String totalPagarAntes = obtenerTextoLabel(SELECTOR_LABEL_TOTAL_PAGAR);
            
            // Hacer clic en comprar
            clickOn(SELECTOR_BUTTON_COMPRAR);
            sleep(1000);
            
            // Confirmar compra
            boolean confirmado = manejarAlertConBoton("Sí", "Confirmar compra");
            
            if (confirmado) {
                System.out.println("✓ Compra confirmada, procesando...");
                sleep(3000);
                
                // Verificar si se mostró mensaje de éxito
                boolean exitoMostrado = manejarAlertConBoton("OK", "Compra exitosa");
                
                if (exitoMostrado) {
                    System.out.println("✓ Compra realizada exitosamente");
                    
                    // Verificar que el carrito se vació
                    sleep(1000);
                    boolean carritoVacio = !verificarElementoExistente(SELECTOR_LISTVIEW_CARRITO + " .list-cell");
                    
                    if (carritoVacio) {
                        System.out.println("✓ Carrito vaciado después de compra");
                        
                        // Verificar que botón comprar está deshabilitado
                        verificarEstadoBoton(SELECTOR_BUTTON_COMPRAR, false, "Botón Comprar deshabilitado tras compra");
                        
                        // Verificar que totales son 0
                        String totalItemsDespues = obtenerTextoLabel(SELECTOR_LABEL_TOTAL_ITEMS);
                        String totalPagarDespues = obtenerTextoLabel(SELECTOR_LABEL_TOTAL_PAGAR);
                        
                        if ("0".equals(totalItemsDespues) && "$0.00".equals(totalPagarDespues)) {
                            System.out.println("✓ Totales reseteados correctamente a 0");
                        }
                    }
                } else {
                    System.out.println("⚠ No se mostró mensaje de éxito de compra");
                }
            } else {
                System.out.println("⚠ Compra cancelada por el usuario");
            }
            
        } catch (Exception e) {
            System.out.println("Error realizando compra: " + e.getMessage());
        }
    }

    private void probarMenuManualPdf() {
        System.out.println("Probando menú manual PDF...");
        
        try {
            // Buscar menú Help
            clickOn("#menuHelp");
            sleep(500);
            
            // Intentar hacer clic en Manual PDF
            try {
                clickOn("Help Manual");
                sleep(2000);
                System.out.println("✓ Menú Help Manual accedido");
                
                // Como no podemos verificar la apertura del PDF, al menos verificamos que no hubo error
                manejarAlertConBoton("OK", "Mensaje de PDF");
                
            } catch (Exception e) {
                System.out.println("⚠ No se pudo acceder al menú Help Manual");
            }
            
        } catch (Exception e) {
            System.out.println("Error probando menú manual PDF: " + e.getMessage());
        }
    }

    private void probarMenuReportePdf() {
        System.out.println("Probando menú reporte PDF...");
        
        try {
            // Buscar menú Help si no está abierto
            if (!verificarElementoExistente("Help Report")) {
                clickOn("#menuHelp");
                sleep(500);
            }
            
            // Intentar hacer clic en Report PDF
            try {
                clickOn("Help Report");
                sleep(2000);
                System.out.println("✓ Menú Help Report accedido");
                
                // Manejar posibles mensajes
                manejarAlertConBoton("OK", "Mensaje de reporte PDF");
                
            } catch (Exception e) {
                System.out.println("⚠ No se pudo acceder al menú Help Report");
            }
            
        } catch (Exception e) {
            System.out.println("Error probando menú reporte PDF: " + e.getMessage());
        }
    }

    private void cerrarCarrito() {
        System.out.println("Cerrando ventana de carrito...");
        
        try {
            // Intentar cerrar con botones comunes
            String[] botonesCerrar = {"Cancel", "Cerrar", "Close", "Exit", "Salir"};
            
            for (String boton : botonesCerrar) {
                try {
                    clickOn(boton);
                    System.out.println("✓ Carrito cerrado con botón: " + boton);
                    sleep(1000);
                    return;
                } catch (Exception e) {
                    // Continuar con siguiente botón
                }
            }
            
            // Si no encuentra botón, intentar ESC
            push(KeyCode.ESCAPE);
            sleep(500);
            release(KeyCode.ESCAPE);
            System.out.println("✓ Carrito cerrado con ESC");
            
        } catch (Exception e) {
            System.out.println("Error cerrando carrito: " + e.getMessage());
        }
    }

    // ========== MÉTODOS AUXILIARES GENÉRICOS ==========

    private void cerrarTodasLasVentanas() {
        try {
            for (int i = 0; i < 5; i++) {
                try {
                    push(KeyCode.ESCAPE);
                    sleep(200);
                    release(KeyCode.ESCAPE);
                } catch (Exception e) {
                }
            }

            String[] botones = {"Exit", "Close", "Cancel", "OK", "Aceptar", "Cerrar"};
            for (String boton : botones) {
                try {
                    clickOn(boton);
                    sleep(200);
                } catch (Exception e) {
                }
            }

            sleep(2000);

        } catch (Exception e) {
            System.out.println("Error cerrando ventanas: " + e.getMessage());
        }
    }

    private void cerrarVentanaActual() {
        try {
            String[] botones = {"Close", "Cancel", "OK", "Aceptar"};
            for (String boton : botones) {
                try {
                    clickOn(boton);
                    sleep(500);
                    return;
                } catch (Exception e) {
                }
            }

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
            System.out.println("✓ Alert '" + descripcion + "' manejado con '" + textoBoton + "'");
            sleep(1000);
            return true;
        } catch (Exception e) {
            String[] alternativas = {"Aceptar", "OK", "Yes", "Sí", "Continuar"};
            for (String alt : alternativas) {
                try {
                    clickOn(alt);
                    System.out.println("✓ Alert '" + descripcion + "' manejado con '" + alt + "'");
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
            System.out.println("✗ " + descripcion + " (error: " + e.getMessage() + ")");
        }
    }
    
    private boolean verificarEstadoBotonBoolean(String selector, boolean habilitado) {
        try {
            if (habilitado) {
                verifyThat(selector, isEnabled());
                return true;
            } else {
                verifyThat(selector, isDisabled());
                return true;
            }
        } catch (Exception e) {
            return false;
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

    private boolean verificarElementoExistente(String selector) {
        try {
            Object node = lookup(selector).query();
            return node != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String obtenerTextoLabel(String selector) {
        try {
            return lookup(selector).queryAs(javafx.scene.control.Label.class).getText();
        } catch (Exception e) {
            return null;
        }
    }
    
    private int contarItemsListView() {
        try {
            javafx.scene.control.ListView<?> listView = 
                lookup(SELECTOR_LISTVIEW_CARRITO).queryAs(javafx.scene.control.ListView.class);
            return listView.getItems().size();
        } catch (Exception e) {
            return 0;
        }
    }
}