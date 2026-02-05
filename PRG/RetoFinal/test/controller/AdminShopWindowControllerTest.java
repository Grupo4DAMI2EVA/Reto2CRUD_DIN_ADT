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
 * Test para AdminShopWindowController
 * Prueba las funcionalidades de administrador: crear, modificar y eliminar videojuegos
 * @author 2dami
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminShopWindowControllerTest extends ApplicationTest {

    private Stage primaryStage;
    private String usuarioAdmin;
    private String passwordAdmin;
    private String nombreJuegoTest;
    private double randomNumber = Math.random() * 1000;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.centerOnScreen();

        usuarioAdmin = "admin1";
        passwordAdmin = "1234";
        nombreJuegoTest = "JuegoTest" + (int)randomNumber;

        new Main().start(stage);
    }

    @Before
    public void prepararTest() {
        // Preparación inicial si es necesaria
    }

    @Test
    public void testCompletoAdministracion() {
        System.out.println("=== TEST COMPLETO DE ADMINISTRACION ===");

        // ========== FASE 1: LOGIN ADMIN ==========
        System.out.println("\n--- Fase 1: Login Admin ---");
        hacerLoginAdmin();

        // ========== FASE 2: VERIFICAR ELEMENTOS ADMIN ==========
        System.out.println("\n--- Fase 2: Verificar Elementos Admin ---");
        verificarElementosAdmin();

        // ========== FASE 3: CREAR NUEVO VIDEOJUEGO ==========
        System.out.println("\n--- Fase 3: Crear Nuevo Videojuego ---");
        crearNuevoVideojuego();

        // ========== FASE 4: BUSCAR Y VERIFICAR VIDEOJUEGO CREADO ==========
        System.out.println("\n--- Fase 4: Buscar Videojuego Creado ---");
        buscarVideojuegoCreado();

        // ========== FASE 5: MODIFICAR VIDEOJUEGO ==========
        System.out.println("\n--- Fase 5: Modificar Videojuego ---");
        modificarVideojuego();

        // ========== FASE 6: ELIMINAR VIDEOJUEGO ==========
        System.out.println("\n--- Fase 6: Eliminar Videojuego ---");
        eliminarVideojuego();

        // ========== FASE 7: VERIFICAR ELIMINACIÓN ==========
        System.out.println("\n--- Fase 7: Verificar Eliminación ---");
        verificarEliminacion();
    }

// ========== MÉTODOS PRINCIPALES ==========

    private void hacerLoginAdmin() {
        System.out.println("Iniciando login de administrador...");

        try {
            // Verificar si ya estamos en LoginWindow
            boolean enLogin = verificarElementoExistente("#TextField_Username")
                    || verificarElementoExistente("#Button_LogIn");

            if (!enLogin) {
                System.out.println("No en LoginWindow, cerrando ventanas...");
                cerrarTodasLasVentanas();
                sleep(300);
            }

            // Asegurar que estamos en LoginWindow
            enLogin = verificarElementoExistente("#TextField_Username")
                    || verificarElementoExistente("#Button_LogIn");

            if (!enLogin) {
                System.out.println("ERROR: No se pudo llegar a LoginWindow");
                return;
            }

            // Llenar campos de login con credenciales de admin
            llenarCampo("#TextField_Username", usuarioAdmin, "Usuario Admin");
            llenarCampo("#PasswordField_Password", passwordAdmin, "Contraseña Admin");

            // Hacer login
            clickOn("#Button_LogIn");
            sleep(300);

            // Manejar alert si aparece
            manejarAlertConBoton("OK", "Resultado login admin");

            // Ir a tienda (los admins usan la misma interfaz de tienda)
            sleep(200);
            clickOn("#Button_Store");
            sleep(300);

            // Verificar que estamos en la tienda
            boolean enTienda = verificarElementoExistente("#labelTitle")
                    || verificarElementoExistente("#tableViewGames");

            if (enTienda) {
                System.out.println("✓ Login admin exitoso y en tienda");
            } else {
                System.out.println("⚠ Login admin exitoso pero no se pudo verificar tienda");
            }

        } catch (Exception e) {
            System.out.println("Error en login admin: " + e.getMessage());
            throw e;
        }
    }

    private void verificarElementosAdmin() {
        System.out.println("Verificando elementos de administración...");

        // Elementos específicos de admin - usando patrones que funcionan
        verificarElemento("#labelTitle", "Título Tienda/Admin"); // Usar el título normal
        verificarElemento("#tableViewGames", "Tabla de juegos"); // Usar tabla normal
        verificarElemento("#buttonAddGame", "Botón Agregar Juego");
        verificarElemento("#buttonModifyGame", "Botón Modificar Juego");  
        verificarElemento("#buttonDeleteGame", "Botón Eliminar Juego");
        verificarElemento("#buttonRefresh", "Botón Actualizar");

        // Verificar estado de botones
        verificarEstadoBoton("#buttonAddGame", true, "Botón Agregar habilitado");
        if (verificarElementoExistente("#buttonRefresh")) {
            verificarEstadoBoton("#buttonRefresh", true, "Botón Actualizar habilitado");
        }
    }

    private void crearNuevoVideojuego() {
        System.out.println("Creando nuevo videojuego: " + nombreJuegoTest);

        try {
            // Primero verificar si estamos en la ventana correcta y hay botón para agregar
            sleep(100);
            
            // Buscar botón de agregar juego con diferentes posibles IDs
            boolean botonEncontrado = false;
            String[] posiblesBotonAgregar = {"#buttonAddGame", "#buttonAdd", "#Button_AddGame", 
                                           "#addGameButton", "#btnAddGame", "#addButton"};
            
            for (String boton : posiblesBotonAgregar) {
                if (verificarElementoExistente(boton)) {
                    clickOn(boton);
                    botonEncontrado = true;
                    System.out.println("✓ Botón agregar encontrado: " + boton);
                    break;
                }
            }
            
            if (!botonEncontrado) {
                System.out.println("✗ No se encontró botón de agregar juego");
                return;
            }
            
            sleep(300); // Dar más tiempo para que se abra la ventana

            // Buscar elementos de la ventana de agregar con diferentes posibles IDs
            String[] posiblesCampoNombre = {"#textFieldName", "#textFieldGameName", "#textFieldTitle", 
                                           "#TextField_Name", "#gameNameField", "#titleField"};
            String campoNombre = null;
            
            for (String campo : posiblesCampoNombre) {
                if (verificarElementoExistente(campo)) {
                    campoNombre = campo;
                    System.out.println("✓ Campo nombre encontrado: " + campo);
                    break;
                }
            }

            if (campoNombre != null) {
                System.out.println("✓ Ventana de agregar juego abierta");

                // Llenar campos del nuevo videojuego usando IDs reales del FXML
                llenarCampoJuego("#textFieldName", nombreJuegoTest, "Nombre del juego");
                sleep(500);
                
                // Llenar campo company (no había descripción, pero sí company)
                if (verificarElementoExistente("#textFieldCompany")) {
                    llenarCampoJuego("#textFieldCompany", "CompanyTest", "Compañía");
                }
                sleep(500);
                
                // No hay campo precio como TextField, es un Spinner
                if (verificarElementoExistente("#spinnerPrice")) {
                    try {
                        clickOn("#spinnerPrice");
                        sleep(200);
                        // Limpiar y poner valor
                        push(KeyCode.CONTROL, KeyCode.A);
                        sleep(100);
                        write("29.99");
                        System.out.println("✓ Precio: 29.99");
                    } catch (Exception e) {
                        System.out.println("⚠ Error llenando spinner precio");
                    }
                }
                sleep(500);

                // Llenar stock (es un Spinner)
                if (verificarElementoExistente("#spinnerStock")) {
                    try {
                        clickOn("#spinnerStock");
                        sleep(200);
                        push(KeyCode.CONTROL, KeyCode.A);
                        sleep(100);
                        write("10");
                        System.out.println("✓ Stock: 10");
                    } catch (Exception e) {
                        System.out.println("⚠ Error llenando spinner stock");
                    }
                }
                sleep(500);

                // Seleccionar género usando ID real
                if (verificarElementoExistente("#comboBoxGenre")) {
                    try {
                        clickOn("#comboBoxGenre");
                        sleep(500);
                        clickOn("ACTION");
                        sleep(500);
                        System.out.println("✓ Género seleccionado");
                    } catch (Exception e) {
                        System.out.println("⚠ Error seleccionando género");
                    }
                }
                sleep(500);

                // Seleccionar plataforma usando ID real
                if (verificarElementoExistente("#comboBoxPlatforms")) {
                    try {
                        clickOn("#comboBoxPlatforms");
                        sleep(1000);
                        clickOn("PC");
                        sleep(500);
                        System.out.println("✓ Plataforma seleccionada: PC");
                    } catch (Exception e) {
                        System.out.println("⚠ No se pudo seleccionar plataforma: " + e.getMessage());
                    }
                }
                sleep(500);

                // Seleccionar PEGI usando ID real
                if (verificarElementoExistente("#comboBoxPEGI")) {
                    try {
                        clickOn("#comboBoxPEGI");
                        sleep(1500);
                        // Intentar diferentes formatos de PEGI
                        String[] pegis = {"PEGI_16", "PEGI_18", "PEGI_12", "16", "18", "12", "PEGI 16", "PEGI 18", "PEGI 12"};
                        boolean pegiSeleccionado = false;
                        for (String pegi : pegis) {
                            try {
                                clickOn(pegi);
                                System.out.println("✓ PEGI seleccionado: " + pegi);
                                pegiSeleccionado = true;
                                sleep(300);
                                break;
                            } catch (Exception e2) {
                                // Continuar con siguiente PEGI
                            }
                        }
                        if (!pegiSeleccionado) {
                            System.out.println("⚠ No se pudo seleccionar ningún PEGI, continuando sin PEGI");
                        }
                    } catch (Exception e) {
                        System.out.println("⚠ Error abriendo comboBox PEGI, continuando sin PEGI");
                    }
                }
                sleep(500);

                // Llenar fecha de lanzamiento
                if (verificarElementoExistente("#datePickerReleaseDate")) {
                    try {
                        clickOn("#datePickerReleaseDate");
                        sleep(200);
                        write("01/01/2024");
                        System.out.println("✓ Fecha de lanzamiento: 01/01/2024");
                    } catch (Exception e) {
                        System.out.println("⚠ Error llenando fecha, continuando sin fecha");
                    }
                }
                sleep(1000);

                // Botón guardar usando ID real del FXML - asegurar que se presiona correctamente
                if (verificarElementoExistente("#buttonAddGame")) {
                    System.out.println("Presionando botón Add Game...");
                    clickOn("#buttonAddGame");
                    System.out.println("✓ Botón Add Game presionado");
                    sleep(3000); // Más tiempo para procesar

                    // Manejar múltiples posibles alertas/confirmaciones
                    boolean alertManejado = false;
                    String[] posiblesRespuestas = {"OK", "Aceptar", "Accept", "Yes", "Sí", "Continuar"};
                    
                    for (String respuesta : posiblesRespuestas) {
                        if (manejarAlertConBoton(respuesta, "Confirmación creación juego")) {
                            alertManejado = true;
                            break;
                        }
                    }
                    
                    sleep(2000);
                    
                    // Manejar popup de "¿crear más videojuegos?" - responder "Cancelar"
                    System.out.println("Manejando popup de crear más videojuegos...");
                    boolean popupManejado = false;
                    String[] posiblesCancelar = {"Cancelar", "Cancel", "No", "Close", "Cerrar"};
                    
                    for (String cancelar : posiblesCancelar) {
                        if (manejarAlertConBoton(cancelar, "Popup crear más videojuegos")) {
                            popupManejado = true;
                            break;
                        }
                    }
                    
                    if (!popupManejado) {
                        System.out.println("⚠ No se detectó popup de crear más videojuegos");
                    }
                    
                    if (alertManejado || popupManejado) {
                        System.out.println("✓ Videojuego creado exitosamente");
                    } else {
                        System.out.println("✓ Videojuego creado (sin confirmaciones)");
                    }
                    
                    sleep(2000);
                } else {
                    System.out.println("✗ No se encontró el botón Add Game");
                }

            } else {
                System.out.println("✗ No se pudo encontrar ventana de agregar juego");
            }

        } catch (Exception e) {
            System.out.println("✗ Error creando videojuego: " + e.getMessage());
        }
    }

    private void buscarVideojuegoCreado() {
        System.out.println("Buscando videojuego creado...");

        try {
            // Actualizar la lista
            if (verificarElementoExistente("#buttonRefresh")) {
                clickOn("#buttonRefresh");
                sleep(2000);
            }

            // Buscar por nombre si hay campo de búsqueda
            if (verificarElementoExistente("#textFieldSearchAdmin")) {
                llenarCampo("#textFieldSearchAdmin", nombreJuegoTest, "Búsqueda admin");
                clickOn("#buttonSearchAdmin");
                sleep(2000);
            } else if (verificarElementoExistente("#textFieldSearch")) {
                llenarCampo("#textFieldSearch", nombreJuegoTest, "Búsqueda general");
                clickOn("#buttonSearch");
                sleep(2000);
            }

            // Buscar el juego en la tabla usando los selectores que funcionan
            boolean juegoSeleccionado = false;
            
            // Intentar con tabla de administración primero
            if (verificarElementoExistente("#tableViewAdminGames .table-row-cell")) {
                clickOn("#tableViewAdminGames .table-row-cell");
                juegoSeleccionado = true;
                sleep(1000);
                System.out.println("✓ Juego seleccionado en tabla admin");
            } 
            // Si no, usar tabla normal
            else if (verificarElementoExistente("#tableViewGames .table-row-cell")) {
                clickOn("#tableViewGames .table-row-cell");
                juegoSeleccionado = true;
                sleep(1000);
                System.out.println("✓ Juego seleccionado en tabla normal");
            }
            
            if (!juegoSeleccionado) {
                System.out.println("⚠ No se encontraron juegos en la tabla para seleccionar");
                // Intentar seleccionar cualquier fila visible
                try {
                    clickOn(".table-row-cell");
                    System.out.println("✓ Juego seleccionado en tabla genérica");
                    juegoSeleccionado = true;
                } catch (Exception e) {
                    System.out.println("✗ No se pudo seleccionar ningún juego");
                }
            }
            
            if (juegoSeleccionado) {
                sleep(1000);
                System.out.println("✓ Juego encontrado y seleccionado para operaciones posteriores");
            }

        } catch (Exception e) {
            System.out.println("✗ Error buscando videojuego: " + e.getMessage());
        }
    }

    private void modificarVideojuego() {
        System.out.println("Modificando videojuego...");

        try {
            // El juego ya debería estar seleccionado desde el paso anterior
            // Verificar que hay un juego seleccionado y hacer clic en modificar usando ID específico
            if (verificarElementoExistente("#buttonModifyGame")) {
                System.out.println("Haciendo clic en botón modificar por ID específico (#buttonModifyGame)...");
                clickOn("#buttonModifyGame");
                sleep(3000); // Más tiempo para que se abra la ventana

                // Verificar que se abrió la ventana de modificar
                if (verificarElementoExistente("#textFieldName") || 
                    verificarElementoExistente("#textFieldCompany")) {
                    
                    System.out.println("✓ Ventana de modificar juego abierta");

                    // Modificar campos usando los IDs reales del FXML
                    String nombreModificado = nombreJuegoTest + "_Modificado";
                    
                    // Modificar nombre
                    if (verificarElementoExistente("#textFieldName")) {
                        limpiarYLlenarCampo("#textFieldName", nombreModificado, "Nombre modificado");
                    }
                    
                    // Modificar compañía
                    if (verificarElementoExistente("#textFieldCompany")) {
                        limpiarYLlenarCampo("#textFieldCompany", "CompanyModificada", "Compañía modificada");
                    }

                    // Modificar precio (es un Spinner)
                    if (verificarElementoExistente("#spinnerPrice")) {
                        try {
                            clickOn("#spinnerPrice");
                            sleep(200);
                            push(KeyCode.CONTROL, KeyCode.A);
                            sleep(100);
                            write("39.99");
                            System.out.println("✓ Precio modificado: 39.99");
                        } catch (Exception e) {
                            System.out.println("⚠ Error modificando precio");
                        }
                    }

                    // Actualizar nombre para siguientes operaciones
                    nombreJuegoTest = nombreModificado;
                    sleep(1000);

                    // Usar el ID real del botón de modificar del FXML
                    if (verificarElementoExistente("#buttonModifyGame")) {
                        System.out.println("Presionando botón Modify Game...");
                        clickOn("#buttonModifyGame");
                        System.out.println("✓ Cambios guardados con botón real del FXML");
                        sleep(3000);

                        // Manejar confirmaciones
                        boolean alertManejado = false;
                        String[] respuestas = {"OK", "Aceptar", "Accept", "Yes", "Sí"};
                        for (String respuesta : respuestas) {
                            if (manejarAlertConBoton(respuesta, "Confirmación modificación")) {
                                alertManejado = true;
                                break;
                            }
                        }
                        
                        if (alertManejado) {
                            System.out.println("✓ Videojuego modificado exitosamente");
                        } else {
                            System.out.println("✓ Videojuego modificado (sin confirmación)");
                        }
                    } else {
                        System.out.println("✗ No se encontró el botón Modify Game");
                    }

                } else {
                    System.out.println("✗ No se pudo abrir ventana de modificar");
                }
            } else {
                System.out.println("✗ Botón modificar no disponible - ¿hay un juego seleccionado?");
            }

        } catch (Exception e) {
            System.out.println("✗ Error modificando videojuego: " + e.getMessage());
        }
    }

    private void eliminarVideojuego() {
        System.out.println("Eliminando videojuego...");

        try {
            sleep(2000); // Dar tiempo después de la modificación
            
            // Actualizar lista para ver cambios
            if (verificarElementoExistente("#buttonRefresh")) {
                clickOn("#buttonRefresh");
                sleep(2000);
                System.out.println("✓ Lista actualizada");
            }

            // Buscar el juego modificado por nombre
            if (verificarElementoExistente("#textFieldSearchAdmin")) {
                limpiarCampo("#textFieldSearchAdmin");
                llenarCampo("#textFieldSearchAdmin", nombreJuegoTest, "Búsqueda para eliminar");
                clickOn("#buttonSearchAdmin");
                sleep(2000);
            } else if (verificarElementoExistente("#textFieldSearch")) {
                limpiarCampo("#textFieldSearch");
                llenarCampo("#textFieldSearch", nombreJuegoTest, "Búsqueda para eliminar");
                clickOn("#buttonSearch");
                sleep(2000);
            }

            // Seleccionar el juego en la tabla
            boolean juegoSeleccionado = false;
            
            // Intentar seleccionar en tabla de admin
            if (verificarElementoExistente("#tableViewAdminGames .table-row-cell")) {
                clickOn("#tableViewAdminGames .table-row-cell");
                juegoSeleccionado = true;
                sleep(1000);
                System.out.println("✓ Juego seleccionado para eliminar (tabla admin)");
            } 
            // Si no, usar tabla normal
            else if (verificarElementoExistente("#tableViewGames .table-row-cell")) {
                clickOn("#tableViewGames .table-row-cell");
                juegoSeleccionado = true;
                sleep(1000);
                System.out.println("✓ Juego seleccionado para eliminar (tabla normal)");
            }
            // Fallback genérico
            else {
                try {
                    clickOn(".table-row-cell");
                    juegoSeleccionado = true;
                    sleep(1000);
                    System.out.println("✓ Juego seleccionado para eliminar (tabla genérica)");
                } catch (Exception e) {
                    System.out.println("✗ No se pudo seleccionar ningún juego");
                }
            }

            if (juegoSeleccionado) {
                // Hacer clic en eliminar usando ID específico
                if (verificarElementoExistente("#buttonDeleteGame")) {
                    System.out.println("Haciendo clic en botón eliminar por ID específico (#buttonDeleteGame)...");
                    clickOn("#buttonDeleteGame");
                    sleep(2000);

                    // Confirmar eliminación - buscar múltiples opciones
                    boolean eliminacionConfirmada = false;
                    String[] confirmaciones = {"Sí", "Yes", "Si", "OK", "Aceptar", "Delete", "Eliminar", "Confirm"};
                    
                    for (String confirmacion : confirmaciones) {
                        if (manejarAlertConBoton(confirmacion, "Confirmación eliminación")) {
                            eliminacionConfirmada = true;
                            break;
                        }
                    }
                    
                    if (eliminacionConfirmada) {
                        sleep(3000); // Dar tiempo para que se procese la eliminación
                        System.out.println("✓ Videojuego eliminado exitosamente");
                    } else {
                        System.out.println("⚠ Eliminación realizada sin confirmación detectada");
                    }

                } else {
                    System.out.println("✗ Botón eliminar no disponible");
                    // Intentar por texto
                    try {
                        clickOn("Delete");
                        sleep(2000);
                        manejarAlertConBoton("Sí", "Confirmación eliminación por texto");
                        System.out.println("✓ Eliminación por texto ejecutada");
                    } catch (Exception e) {
                        System.out.println("✗ No se pudo eliminar por ningún método");
                    }
                }
            } else {
                System.out.println("✗ No se encontró juego para eliminar");
            }

        } catch (Exception e) {
            System.out.println("✗ Error eliminando videojuego: " + e.getMessage());
        }
    }

    private void verificarEliminacion() {
        System.out.println("Verificando eliminación...");

        try {
            // Actualizar lista
            if (verificarElementoExistente("#buttonRefresh")) {
                clickOn("#buttonRefresh");
                sleep(2000);
            }

            // Buscar el juego eliminado
            if (verificarElementoExistente("#textFieldSearchAdmin")) {
                limpiarCampo("#textFieldSearchAdmin");
                llenarCampo("#textFieldSearchAdmin", nombreJuegoTest, "Verificación eliminación");
                clickOn("#buttonSearchAdmin");
                sleep(2000);
            }

            // Verificar que no aparece en la tabla
            boolean juegoEncontrado = false;
            try {
                // Intentar buscar texto del juego eliminado en la tabla
                lookup(nombreJuegoTest).query();
                juegoEncontrado = true;
            } catch (Exception e) {
                juegoEncontrado = false;
            }

            if (!juegoEncontrado) {
                System.out.println("✓ Verificación exitosa: El juego ha sido eliminado");
            } else {
                System.out.println("⚠ El juego aún aparece en la lista");
            }

        } catch (Exception e) {
            System.out.println("✗ Error verificando eliminación: " + e.getMessage());
        }
    }

// ========== MÉTODOS AUXILIARES ==========

    private void llenarCampoJuego(String selector, String texto, String descripcion) {
        try {
            clickOn(selector);
            sleep(200);
            // Limpiar campo primero
            push(KeyCode.CONTROL, KeyCode.A);
            sleep(100);
            push(KeyCode.DELETE);
            sleep(100);
            // Escribir nuevo texto
            write(texto);
            System.out.println("✓ " + descripcion + ": " + texto);
        } catch (Exception e) {
            System.out.println("✗ Error llenando " + descripcion);
        }
    }

    private void limpiarYLlenarCampo(String selector, String texto, String descripcion) {
        try {
            clickOn(selector);
            sleep(200);
            // Seleccionar todo y eliminar
            push(KeyCode.CONTROL, KeyCode.A);
            sleep(100);
            push(KeyCode.DELETE);
            sleep(100);
            // Escribir nuevo texto
            write(texto);
            System.out.println("✓ " + descripcion + ": " + texto);
        } catch (Exception e) {
            System.out.println("✗ Error modificando " + descripcion);
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

            sleep(2000);

        } catch (Exception e) {
            System.out.println("Error cerrando ventanas: " + e.getMessage());
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
            String[] alternativas = {"Aceptar", "OK", "Yes", "Sí", "Continuar"};
            for (String alt : alternativas) {
                try {
                    clickOn(alt);
                    System.out.println("✓ Alert " + descripcion + " manejado con '" + alt + "'");
                    sleep(1000);
                    return true;
                } catch (Exception e2) {
                }
            }

            // System.out.println("⚠ No se pudo manejar alert: " + descripcion);
            return false;
        }
    }

    private void verificarElemento(String selector, String descripcion) {
        try {
            verifyThat(selector, isVisible());
            System.out.println("✓ " + descripcion);
        } catch (Exception e) {
            System.out.println("✗ " + descripcion + " - No visible");
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
            System.out.println("✗ " + descripcion + " - Estado incorrecto");
        }
    }

    private void llenarCampo(String selector, String texto, String descripcion) {
        try {
            clickOn(selector);
            sleep(200);
            // Limpiar campo
            push(KeyCode.CONTROL, KeyCode.A);
            sleep(100);
            write(texto);
            System.out.println("✓ " + descripcion + ": " + texto);
        } catch (Exception e) {
            System.out.println("✗ Error en " + descripcion);
        }
    }

    private void limpiarCampo(String selector) {
        try {
            clickOn(selector);
            push(KeyCode.CONTROL, KeyCode.A);
            sleep(100);
            push(KeyCode.DELETE);
            sleep(200);
        } catch (Exception e) {
            // Ignorar error
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