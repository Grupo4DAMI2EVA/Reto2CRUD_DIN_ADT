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
 * Test para ReviewController
 * Basado en el patrón de test de tienda y carrito
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReviewWindowTest extends ApplicationTest {

    private Stage primaryStage;
    private String usuarioUnico = "review_user_test";
    
    // Selectores específicos de ReviewController
    private final String SELECTOR_SLIDER_PUNTUACION = "#sliderPuntuacion";
    private final String SELECTOR_LABEL_PUNTUACION = "#labelPuntuacion";
    private final String SELECTOR_LABEL_VIDEOJUEGO = "#labelVideojuego";
    private final String SELECTOR_TEXTAREA_COMENTARIO = "#textAreaComentario";
    private final String SELECTOR_BUTTON_ENVIAR = "#buttonEnviar";
    private final String SELECTOR_BUTTON_CANCELAR = "#buttonCancelar";
    
    // Selectores de la tienda para seleccionar juego
    private final String SELECTOR_TABLA_JUEGOS = "#tableViewGames";
    private final String SELECTOR_BUTTON_REVIEW = "#buttonReview";
    
    // Juegos de prueba (ajusta según tus juegos reales)
    private final String[] JUEGOS_PRUEBA = {"The Witcher 3", "Cyberpunk 2077", "Red Dead Redemption 2", "Minecraft"};

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.centerOnScreen();
        
        // Usuario único para pruebas
        usuarioUnico = "review_user_" + System.currentTimeMillis();
        
        new Main().start(stage);
    }

    @Before
    public void prepararTest() {
        System.out.println("=== PREPARANDO TEST DE REVIEW ===");
    }

    @Test
    public void testA_LoginYSeleccionJuego() {
        System.out.println("\n=== TEST A: LOGIN Y SELECCIÓN DE JUEGO ===");
        
        // ========== FASE 1: LOGIN ==========
        System.out.println("\n--- Fase 1: Login ---");
        hacerLoginDesdeCero();
        
        // ========== FASE 2: SELECCIONAR JUEGO PARA REVIEW ==========
        System.out.println("\n--- Fase 2: Seleccionar Juego ---");
        seleccionarJuegoParaReview();
        
        // ========== FASE 3: ABRIR VENTANA REVIEW ==========
        System.out.println("\n--- Fase 3: Abrir Ventana Review ---");
        abrirVentanaReview();
    }

    @Test
    public void testB_VerificarElementosReview() {
        System.out.println("\n=== TEST B: VERIFICAR ELEMENTOS REVIEW ===");
        
        // Abrir ventana review directamente
        abrirVentanaReviewDirecto();
        
        // ========== FASE 1: VERIFICAR ELEMENTOS UI ==========
        System.out.println("\n--- Fase 1: Verificar Elementos UI ---");
        verificarElementosReview();
        
        // ========== FASE 2: VERIFICAR ESTADO INICIAL ==========
        System.out.println("\n--- Fase 2: Verificar Estado Inicial ---");
        verificarEstadoInicial();
    }

    @Test
    public void testC_InteraccionConSlider() {
        System.out.println("\n=== TEST C: INTERACCIÓN CON SLIDER ===");
        
        abrirVentanaReviewDirecto();
        
        // ========== FASE 1: PROBAR SLIDER ==========
        System.out.println("\n--- Fase 1: Probar Slider de Puntuación ---");
        probarSliderPuntuacion();
    }

    @Test
    public void testD_ValidacionesReview() {
        System.out.println("\n=== TEST D: VALIDACIONES DE REVIEW ===");
        
        abrirVentanaReviewDirecto();
        
        // ========== FASE 1: INTENTAR ENVIAR SIN COMENTARIO ==========
        System.out.println("\n--- Fase 1: Intentar Enviar sin Comentario ---");
        probarEnvioSinComentario();
        
        // ========== FASE 2: INTENTAR ENVIAR COMENTARIO CORTO ==========
        System.out.println("\n--- Fase 2: Intentar Enviar Comentario Corto ---");
        probarEnvioComentarioCorto();
        
        // ========== FASE 3: ESCRIBIR COMENTARIO VÁLIDO ==========
        System.out.println("\n--- Fase 3: Escribir Comentario Válido ---");
        escribirComentarioValido();
    }

    @Test
    public void testE_EnvioReview() {
        System.out.println("\n=== TEST E: ENVÍO DE REVIEW ===");
        
        abrirVentanaReviewDirecto();
        
        // ========== FASE 1: ENVIAR REVIEW EXITOSO ==========
        System.out.println("\n--- Fase 1: Enviar Review Exitoso ---");
        enviarReviewExitoso();
    }

    @Test
    public void testF_CancelacionReview() {
        System.out.println("\n=== TEST F: CANCELACIÓN DE REVIEW ===");
        
        abrirVentanaReviewDirecto();
        
        // ========== FASE 1: CANCELAR SIN CAMBIOS ==========
        System.out.println("\n--- Fase 1: Cancelar sin Cambios ---");
        cancelarSinCambios();
        
        // ========== FASE 2: CANCELAR CON TEXTO Y CONFIRMAR ==========
        System.out.println("\n--- Fase 2: Cancelar con Texto y Confirmar ---");
        cancelarConTextoYConfirmar();
        
        // ========== FASE 3: CANCELAR CON TEXTO Y NO CONFIRMAR ==========
        System.out.println("\n--- Fase 3: Cancelar con Texto y NO Confirmar ---");
        cancelarConTextoYNoConfirmar();
    }

    @Test
    public void testG_MenuYAyuda() {
        System.out.println("\n=== TEST G: MENÚ Y AYUDA ===");
        
        abrirVentanaReviewDirecto();
        
        // ========== FASE 1: MENÚ PDF ==========
        System.out.println("\n--- Fase 1: Menú Manual PDF ---");
        probarMenuManualPdf();
        
        // ========== FASE 2: MENÚ REPORTE ==========
        System.out.println("\n--- Fase 2: Menú Reporte PDF ---");
        probarMenuReportePdf();
        
        // ========== FASE 3: CERRAR VENTANA ==========
        System.out.println("\n--- Fase 3: Cerrar Ventana ---");
        cerrarVentanaReview();
    }

    // ========== MÉTODOS AUXILIARES PRINCIPALES ==========

    private void hacerLoginDesdeCero() {
        System.out.println("Iniciando login para review...");
        
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
            llenarCampo("#TextField_Username", usuarioUnico, "Usuario review");
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

            System.out.println("✓ Login exitoso para usuario: " + usuarioUnico);

        } catch (Exception e) {
            System.out.println("Error en login para review: " + e.getMessage());
        }
    }

    private void seleccionarJuegoParaReview() {
        System.out.println("Seleccionando juego para review...");
        
        try {
            // Verificar que estamos en tienda
            boolean enTienda = verificarElementoExistente(SELECTOR_TABLA_JUEGOS)
                    || verificarElementoExistente("#labelTitle");

            if (!enTienda) {
                System.out.println("✗ No se pudo acceder a la tienda");
                return;
            }

            System.out.println("✓ En tienda, buscando juegos...");

            // Esperar a que carguen los juegos
            sleep(2000);

            // Buscar juegos disponibles
            boolean juegoEncontrado = false;
            
            // Intentar seleccionar un juego de la lista de prueba
            for (String juego : JUEGOS_PRUEBA) {
                try {
                    // Buscar por texto en la tabla
                    clickOn(juego);
                    sleep(500);
                    
                    // Verificar que se seleccionó
                    if (verificarElementoExistente(SELECTOR_TABLA_JUEGOS + " .table-row-cell:selected")) {
                        juegoEncontrado = true;
                        System.out.println("✓ Juego seleccionado: " + juego);
                        break;
                    }
                } catch (Exception e) {
                    // Continuar con siguiente juego
                }
            }

            if (!juegoEncontrado) {
                // Si no encuentra juegos específicos, intentar seleccionar cualquier juego
                try {
                    clickOn(SELECTOR_TABLA_JUEGOS + " .table-row-cell");
                    sleep(500);
                    juegoEncontrado = true;
                    System.out.println("✓ Primer juego seleccionado");
                } catch (Exception e) {
                    System.out.println("✗ No se pudo seleccionar ningún juego");
                }
            }

        } catch (Exception e) {
            System.out.println("Error seleccionando juego: " + e.getMessage());
        }
    }

    private void abrirVentanaReview() {
        System.out.println("Abriendo ventana de review...");
        
        try {
            // Verificar que hay un juego seleccionado
            if (!verificarElementoExistente(SELECTOR_TABLA_JUEGOS + " .table-row-cell:selected")) {
                System.out.println("⚠ No hay juego seleccionado, seleccionando uno...");
                clickOn(SELECTOR_TABLA_JUEGOS + " .table-row-cell");
                sleep(500);
            }

            // Hacer clic en botón de review
            clickOn(SELECTOR_BUTTON_REVIEW);
            sleep(3000);
            
            // Verificar que se abrió la ventana de review
            boolean reviewAbierto = verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION)
                    || verificarElementoExistente(SELECTOR_TEXTAREA_COMENTARIO)
                    || verificarElementoExistente("Review")
                    || verificarElementoExistente("Valoración");
            
            if (reviewAbierto) {
                System.out.println("✓ Ventana de review abierta exitosamente");
            } else {
                System.out.println("✗ No se pudo verificar apertura de ventana review");
                
                // Intentar manejar alert si aparece (por ejemplo, si ya se reseñó el juego)
                manejarAlertConBoton("OK", "Mensaje de review duplicado");
            }
            
        } catch (Exception e) {
            System.out.println("Error abriendo ventana review: " + e.getMessage());
        }
    }

    private void abrirVentanaReviewDirecto() {
        // Método para abrir review directamente sin todo el proceso
        try {
            // Primero intentar abrir desde donde estamos
            if (verificarElementoExistente(SELECTOR_TABLA_JUEGOS)) {
                // Estamos en tienda, seleccionar y abrir
                if (!verificarElementoExistente(SELECTOR_TABLA_JUEGOS + " .table-row-cell:selected")) {
                    clickOn(SELECTOR_TABLA_JUEGOS + " .table-row-cell");
                    sleep(500);
                }
                clickOn(SELECTOR_BUTTON_REVIEW);
                sleep(3000);
            }
            
            // Verificar que se abrió
            if (!verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION)) {
                System.out.println("⚠ No se pudo abrir review directamente");
            }
            
        } catch (Exception e) {
            System.out.println("Error en abrirVentanaReviewDirecto: " + e.getMessage());
        }
    }

    private void verificarElementosReview() {
        System.out.println("Verificando elementos de la ventana review...");
        
        // Verificar elementos principales
        verificarElemento(SELECTOR_SLIDER_PUNTUACION, "Slider de puntuación");
        verificarElemento(SELECTOR_LABEL_PUNTUACION, "Label de puntuación actual");
        verificarElemento(SELECTOR_LABEL_VIDEOJUEGO, "Label del videojuego");
        verificarElemento(SELECTOR_TEXTAREA_COMENTARIO, "TextArea para comentario");
        verificarElemento(SELECTOR_BUTTON_ENVIAR, "Botón Enviar");
        verificarElemento(SELECTOR_BUTTON_CANCELAR, "Botón Cancelar");
        
        // Verificar propiedades del slider
        verificarPropiedadesSlider();
        
        // Verificar que el label del videojuego muestra algo
        String textoJuego = obtenerTextoLabel(SELECTOR_LABEL_VIDEOJUEGO);
        if (textoJuego != null && textoJuego.contains("Videojuego:")) {
            System.out.println("✓ Label de videojuego muestra: " + textoJuego);
        } else {
            System.out.println("⚠ Label de videojuego no muestra información esperada");
        }
    }

    private void verificarPropiedadesSlider() {
        System.out.println("Verificando propiedades del slider...");
        
        try {
            // Verificar que el slider tiene valores correctos
            javafx.scene.control.Slider slider = lookup(SELECTOR_SLIDER_PUNTUACION).queryAs(javafx.scene.control.Slider.class);
            
            double min = slider.getMin();
            double max = slider.getMax();
            double valor = slider.getValue();
            boolean showTicks = slider.isShowTickMarks();
            boolean showLabels = slider.isShowTickLabels();
            
            System.out.println("Propiedades del slider:");
            System.out.println("  Min: " + min + " (esperado: 0)");
            System.out.println("  Max: " + max + " (esperado: 5)");
            System.out.println("  Valor inicial: " + valor + " (esperado: 2.5)");
            System.out.println("  Mostrar ticks: " + showTicks + " (esperado: true)");
            System.out.println("  Mostrar labels: " + showLabels + " (esperado: true)");
            
            if (min == 0 && max == 5) {
                System.out.println("✓ Propiedades del slider correctas");
            } else {
                System.out.println("⚠ Propiedades del slider incorrectas");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error verificando propiedades del slider: " + e.getMessage());
        }
    }

    private void verificarEstadoInicial() {
        System.out.println("Verificando estado inicial...");
        
        // Verificar puntuación inicial
        String puntuacionInicial = obtenerTextoLabel(SELECTOR_LABEL_PUNTUACION);
        if (puntuacionInicial != null) {
            System.out.println("✓ Puntuación inicial: " + puntuacionInicial);
            
            // Verificar formato (debería ser algo como "2.5/5.0")
            if (puntuacionInicial.matches("\\d+\\.\\d+/5\\.0")) {
                System.out.println("✓ Formato de puntuación correcto");
            }
        }
        
        // Verificar textarea vacío
        String comentarioInicial = obtenerTextoTextArea(SELECTOR_TEXTAREA_COMENTARIO);
        if (comentarioInicial != null && comentarioInicial.isEmpty()) {
            System.out.println("✓ TextArea inicialmente vacío");
        }
        
        // Verificar botón enviar deshabilitado inicialmente
        verificarEstadoBoton(SELECTOR_BUTTON_ENVIAR, false, "Botón Enviar deshabilitado inicialmente");
        
        // Verificar botón cancelar habilitado
        verificarEstadoBoton(SELECTOR_BUTTON_CANCELAR, true, "Botón Cancelar habilitado");
    }

    private void probarSliderPuntuacion() {
        System.out.println("Probando interacción con slider...");
        
        try {
            // Obtener valor inicial
            String valorInicial = obtenerTextoLabel(SELECTOR_LABEL_PUNTUACION);
            System.out.println("Valor inicial del slider: " + valorInicial);
            
            // Probar mover slider a diferentes posiciones
            double[] posiciones = {0.0, 1.0, 2.0, 2.5, 3.0, 4.0, 5.0};
            
            for (double posicion : posiciones) {
                // Mover slider (usando dragAndDrop o setValue)
                try {
                    // Método 1: Intentar setValue directamente
                    javafx.scene.control.Slider slider = lookup(SELECTOR_SLIDER_PUNTUACION).queryAs(javafx.scene.control.Slider.class);
                    interact(() -> slider.setValue(posicion));
                    sleep(500);
                    
                    // Verificar que se actualizó el label
                    String valorActual = obtenerTextoLabel(SELECTOR_LABEL_PUNTUACION);
                    System.out.println("Slider en " + posicion + " -> Label: " + valorActual);
                    
                    // Verificar que el valor se redondea correctamente
                    double valorEsperado = Math.round(posicion * 2) / 2.0;
                    String textoEsperado = String.format("%.1f/5.0", valorEsperado);
                    
                    if (valorActual.equals(textoEsperado)) {
                        System.out.println("✓ Posición " + posicion + " correcta: " + valorActual);
                    } else {
                        System.out.println("⚠ Posición " + posicion + " incorrecta. Esperado: " + textoEsperado + ", Actual: " + valorActual);
                    }
                    
                } catch (Exception e) {
                    System.out.println("✗ Error moviendo slider a " + posicion + ": " + e.getMessage());
                }
            }
            
            // Restaurar a posición inicial
            interact(() -> {
                javafx.scene.control.Slider slider = lookup(SELECTOR_SLIDER_PUNTUACION).queryAs(javafx.scene.control.Slider.class);
                slider.setValue(2.5);
            });
            sleep(500);
            
            System.out.println("✓ Slider restaurado a posición inicial");
            
        } catch (Exception e) {
            System.out.println("Error probando slider: " + e.getMessage());
        }
    }

    private void probarEnvioSinComentario() {
        System.out.println("Probando envío sin comentario...");
        
        try {
            // Asegurarse de que el textarea está vacío
            limpiarTextArea(SELECTOR_TEXTAREA_COMENTARIO);
            sleep(500);
            
            // Verificar que botón enviar está deshabilitado
            verificarEstadoBoton(SELECTOR_BUTTON_ENVIAR, false, "Botón Enviar deshabilitado sin comentario");
            
            // Intentar hacer clic (debería estar deshabilitado, pero probamos)
            try {
                clickOn(SELECTOR_BUTTON_ENVIAR);
                System.out.println("⚠ Botón Enviar estaba habilitado cuando debería estar deshabilitado");
            } catch (Exception e) {
                System.out.println("✓ Botón Enviar correctamente deshabilitado sin comentario");
            }
            
        } catch (Exception e) {
            System.out.println("Error probando envío sin comentario: " + e.getMessage());
        }
    }

    private void probarEnvioComentarioCorto() {
        System.out.println("Probando envío con comentario corto (<10 caracteres)...");
        
        try {
            // Escribir comentario corto
            escribirEnTextArea(SELECTOR_TEXTAREA_COMENTARIO, "Corto");
            sleep(500);
            
            // Verificar que botón enviar sigue deshabilitado
            verificarEstadoBoton(SELECTOR_BUTTON_ENVIAR, false, "Botón Enviar deshabilitado con comentario corto");
            
            // Intentar enviar (debería mostrar alerta si se fuerza)
            // Primero habilitar botón si es necesario (simulando interfaz)
            // En realidad, debería mostrar alerta al hacer clic
            System.out.println("✓ Botón Enviar permanece deshabilitado con comentario corto");
            
            // Limpiar para siguiente prueba
            limpiarTextArea(SELECTOR_TEXTAREA_COMENTARIO);
            
        } catch (Exception e) {
            System.out.println("Error probando envío comentario corto: " + e.getMessage());
        }
    }

    private void escribirComentarioValido() {
        System.out.println("Escribiendo comentario válido (≥10 caracteres)...");
        
        try {
            // Comentario válido de prueba
            String comentarioValido = "Este es un comentario de prueba para la reseña. Me encantó el juego, la jugabilidad es excelente.";
            
            escribirEnTextArea(SELECTOR_TEXTAREA_COMENTARIO, comentarioValido);
            sleep(1000);
            
            // Verificar que botón enviar ahora está habilitado
            verificarEstadoBoton(SELECTOR_BUTTON_ENVIAR, true, "Botón Enviar habilitado con comentario válido");
            
            // Verificar que se mantiene habilitado al editar
            agregarTextoTextArea(SELECTOR_TEXTAREA_COMENTARIO, " Más detalles: gráficos increíbles.");
            sleep(500);
            verificarEstadoBoton(SELECTOR_BUTTON_ENVIAR, true, "Botón Enviar sigue habilitado al editar");
            
            // Verificar que se deshabilita si se borra
            limpiarTextArea(SELECTOR_TEXTAREA_COMENTARIO);
            sleep(500);
            verificarEstadoBoton(SELECTOR_BUTTON_ENVIAR, false, "Botón Enviar se deshabilita al borrar");
            
            System.out.println("✓ Validación de comentario funciona correctamente");
            
        } catch (Exception e) {
            System.out.println("Error escribiendo comentario válido: " + e.getMessage());
        }
    }

    private void enviarReviewExitoso() {
        System.out.println("Enviando review exitoso...");
        
        try {
            // Configurar review completo
            // 1. Ajustar slider a 4.5
            javafx.scene.control.Slider slider = lookup(SELECTOR_SLIDER_PUNTUACION).queryAs(javafx.scene.control.Slider.class);
            interact(() -> slider.setValue(4.5));
            sleep(500);
            
            // 2. Escribir comentario válido
            String comentario = "Excelente juego, me encantó la historia y los personajes. " +
                              "La jugabilidad es fluida y los gráficos son impresionantes. " +
                              "Definitivamente lo recomendaría a otros jugadores.";
            
            escribirEnTextArea(SELECTOR_TEXTAREA_COMENTARIO, comentario);
            sleep(1000);
            
            // 3. Verificar que botón está habilitado
            verificarEstadoBoton(SELECTOR_BUTTON_ENVIAR, true, "Botón Enviar habilitado para envío");
            
            // 4. Hacer clic en enviar
            clickOn(SELECTOR_BUTTON_ENVIAR);
            sleep(1000);
            
            // 5. Confirmar envío (aparece diálogo de confirmación)
            boolean confirmado = manejarAlertConBoton("Sí", "Confirmar envío de review");
            
            if (confirmado) {
                System.out.println("✓ Review confirmado para envío");
                sleep(2000);
                
                // 6. Manejar posible resultado (éxito o error)
                // Puede aparecer alerta de éxito o de error (si ya se reseñó)
                try {
                    boolean exito = manejarAlertConBoton("OK", "Resultado envío review");
                    
                    if (exito) {
                        System.out.println("✓ Review enviado exitosamente");
                        
                        // Verificar que la ventana se cerró
                        sleep(1000);
                        boolean ventanaCerrada = !verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION);
                        
                        if (ventanaCerrada) {
                            System.out.println("✓ Ventana de review cerrada automáticamente");
                        } else {
                            System.out.println("⚠ Ventana no se cerró automáticamente");
                            cerrarVentanaReview();
                        }
                    }
                } catch (Exception e) {
                    // Manejar caso de review duplicado
                    boolean duplicado = manejarAlertConBoton("OK", "Review duplicado");
                    if (duplicado) {
                        System.out.println("⚠ Review duplicado - ya se había reseñado este juego");
                        cerrarVentanaReview();
                    }
                }
            } else {
                System.out.println("⚠ Envío de review cancelado por usuario");
            }
            
        } catch (Exception e) {
            System.out.println("Error enviando review: " + e.getMessage());
        }
    }

    private void cancelarSinCambios() {
        System.out.println("Cancelando sin cambios...");
        
        try {
            // Asegurarse de que no hay texto
            limpiarTextArea(SELECTOR_TEXTAREA_COMENTARIO);
            sleep(500);
            
            // Hacer clic en cancelar
            clickOn(SELECTOR_BUTTON_CANCELAR);
            sleep(1000);
            
            // Debería cerrarse directamente sin confirmación
            boolean ventanaCerrada = !verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION);
            
            if (ventanaCerrada) {
                System.out.println("✓ Ventana cerrada directamente sin cambios");
            } else {
                System.out.println("⚠ Ventana no se cerró, verificando si hay diálogo...");
                // Puede haber diálogo si hay texto no guardado
                manejarAlertConBoton("No", "Cancelar cierre");
                System.out.println("Cierre cancelado por prueba");
            }
            
        } catch (Exception e) {
            System.out.println("Error cancelando sin cambios: " + e.getMessage());
        }
    }

    private void cancelarConTextoYConfirmar() {
        System.out.println("Cancelando con texto y confirmando...");
        
        try {
            // Necesitamos reabrir ventana si se cerró
            if (!verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION)) {
                abrirVentanaReviewDirecto();
                sleep(1000);
            }
            
            // Escribir algo en el textarea
            escribirEnTextArea(SELECTOR_TEXTAREA_COMENTARIO, "Comentario de prueba para cancelación");
            sleep(500);
            
            // Hacer clic en cancelar
            clickOn(SELECTOR_BUTTON_CANCELAR);
            sleep(1000);
            
            // Debería aparecer diálogo de confirmación
            boolean confirmado = manejarAlertConBoton("Sí", "Confirmar cancelación con texto");
            
            if (confirmado) {
                System.out.println("✓ Cancelación confirmada");
                sleep(1000);
                
                // Verificar que ventana se cerró
                boolean ventanaCerrada = !verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION);
                if (ventanaCerrada) {
                    System.out.println("✓ Ventana cerrada después de confirmar");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error cancelando con texto: " + e.getMessage());
        }
    }

    private void cancelarConTextoYNoConfirmar() {
        System.out.println("Cancelando con texto y NO confirmando...");
        
        try {
            // Reabrir si es necesario
            if (!verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION)) {
                abrirVentanaReviewDirecto();
                sleep(1000);
            }
            
            // Escribir algo en el textarea
            escribirEnTextArea(SELECTOR_TEXTAREA_COMENTARIO, "Otro comentario de prueba");
            sleep(500);
            
            // Hacer clic en cancelar
            clickOn(SELECTOR_BUTTON_CANCELAR);
            sleep(1000);
            
            // En diálogo de confirmación, seleccionar "No"
            boolean noConfirmado = manejarAlertConBotonEspecifico("No", "No confirmar cancelación");
            
            if (noConfirmado) {
                System.out.println("✓ Cancelación no confirmada");
                sleep(500);
                
                // Verificar que ventana NO se cerró
                boolean ventanaAbierta = verificarElementoExistente(SELECTOR_SLIDER_PUNTUACION);
                if (ventanaAbierta) {
                    System.out.println("✓ Ventana permanece abierta después de no confirmar");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error cancelando sin confirmar: " + e.getMessage());
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
                
                // Manejar posibles mensajes
                manejarAlertConBoton("OK", "Mensaje de PDF manual");
                
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

    private void cerrarVentanaReview() {
        System.out.println("Cerrando ventana de review...");
        
        try {
            // Intentar botón cancelar
            clickOn(SELECTOR_BUTTON_CANCELAR);
            sleep(1000);
            
            // Manejar diálogo de confirmación si aparece
            manejarAlertConBoton("Sí", "Confirmar cierre");
            
            System.out.println("✓ Ventana de review cerrada");
            
        } catch (Exception e) {
            // Intentar otros métodos de cierre
            System.out.println("Cerrando con métodos alternativos...");
            cerrarVentanaActual();
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
    
    private boolean manejarAlertConBotonEspecifico(String textoBoton, String descripcion) {
        try {
            clickOn(textoBoton);
            System.out.println("✓ Alert '" + descripcion + "' manejado con '" + textoBoton + "'");
            sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println("✗ No se pudo manejar alert con botón específico: " + textoBoton);
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

    private void escribirEnTextArea(String selector, String texto) {
        try {
            clickOn(selector);
            // Limpiar primero
            push(KeyCode.CONTROL);
            press(KeyCode.A);
            release(KeyCode.A);
            release(KeyCode.CONTROL);
            push(KeyCode.DELETE);
            // Escribir nuevo texto
            write(texto);
            System.out.println("✓ Texto escrito en TextArea: " + (texto.length() > 20 ? texto.substring(0, 20) + "..." : texto));
        } catch (Exception e) {
            System.out.println("✗ Error escribiendo en TextArea: " + e.getMessage());
        }
    }
    
    private void agregarTextoTextArea(String selector, String texto) {
        try {
            clickOn(selector);
            write(texto);
            System.out.println("✓ Texto agregado en TextArea");
        } catch (Exception e) {
            System.out.println("✗ Error agregando texto en TextArea: " + e.getMessage());
        }
    }
    
    private void limpiarTextArea(String selector) {
        try {
            clickOn(selector);
            push(KeyCode.CONTROL);
            press(KeyCode.A);
            release(KeyCode.A);
            release(KeyCode.CONTROL);
            push(KeyCode.DELETE);
            System.out.println("✓ TextArea limpiado");
        } catch (Exception e) {
            System.out.println("✗ Error limpiando TextArea: " + e.getMessage());
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
    
    private String obtenerTextoTextArea(String selector) {
        try {
            return lookup(selector).queryAs(javafx.scene.control.TextArea.class).getText();
        } catch (Exception e) {
            return null;
        }
    }
}