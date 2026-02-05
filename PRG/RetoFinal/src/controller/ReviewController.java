package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.event.ActionEvent;

public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Slider sliderPuntuacion;

    @FXML
    private Label labelPuntuacion;

    @FXML
    private Label labelVideojuego;

    @FXML
    private TextArea textAreaComentario;

    @FXML
    private Button buttonEnviar;

    @FXML
    private Button buttonCancelar;

    private Stage stage;
    private String nombreVideojuego = "";
    private int idUsuario = 0;
    private int idVideojuego = 0;
    private Profile profile;
    private Controller cont;
    private Videogame videojuegoCompleto;
    
    static {
        initializeLogger();
    }

    /**
     * Inicializa el sistema de logging de manera sincronizada para evitar
     * múltiples inicializaciones en entornos multi-hilo.
     */
    private static synchronized void initializeLogger() {
        if (loggerInitialized) {
            return;
        }

        try {
            java.io.File logsFolder = new java.io.File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }

            FileHandler fileHandler = new FileHandler("logs/ReviewWindow.log", true);

            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    if (record.getLevel() == Level.INFO || record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
                        return String.format("[%1$tF %1$tT] [%2$s] %3$s %n",
                                new java.util.Date(record.getMillis()),
                                record.getLevel(),
                                record.getMessage());
                    }
                    return "";
                }
            });

            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false);

            loggerInitialized = true;
            logger.info("ReviewController logger initialized");

        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    @FXML
    private void initialize() {
        logger.info("Initializing ReviewController");
        
        try {
            // Configurar el slider
            configurarSlider();

            // Deshabilitar botón enviar inicialmente
            actualizarEstadoBotonEnviar();

            // Configurar listener para habilitar botón cuando haya texto
            textAreaComentario.textProperty().addListener((observable, oldValue, newValue) -> {
                actualizarEstadoBotonEnviar();
            });
            
            logger.info("ReviewController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing ReviewController: " + e.getMessage());
        }
    }

    public void setUsuario(Profile profile) {
        logger.info("Setting user profile in ReviewController: " + 
                   (profile != null ? profile.getUsername() + " (ID: " + profile.getUserCode() + ")" : "null"));
        this.profile = profile;
        if (profile != null) {
            this.idUsuario = profile.getUserCode();
        }
    }

    public void setCont(Controller cont) {
        logger.info("Setting controller in ReviewController");
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    // Método para establecer el videojuego completo
    public void setVideojuegoCompleto(Videogame videojuego) {
        logger.info("Setting complete game in ReviewController: " + 
                   (videojuego != null ? videojuego.getName() + " (ID: " + videojuego.getIdVideogame() + ")" : "null"));
        this.videojuegoCompleto = videojuego;
        this.nombreVideojuego = videojuego != null ? videojuego.getName() : "";
        this.idVideojuego = videojuego != null ? videojuego.getIdVideogame() : 0;
        if (labelVideojuego != null) {
            labelVideojuego.setText("Videojuego: " + nombreVideojuego);
        }
    }

    // Método original para compatibilidad
    public void setVideojuego(String nombre, int idVideojuego) {
        logger.info("Setting game (compatibility method) in ReviewController: " + nombre + " (ID: " + idVideojuego + ")");
        this.nombreVideojuego = nombre;
        this.idVideojuego = idVideojuego;
        if (labelVideojuego != null) {
            labelVideojuego.setText("Videojuego: " + nombre);
        }
    }

    private void configurarSlider() {
        logger.info("Configuring rating slider");
        
        try {
            // Configurar propiedades del slider
            sliderPuntuacion.setMin(0);
            sliderPuntuacion.setMax(5);
            sliderPuntuacion.setValue(2.5);
            sliderPuntuacion.setBlockIncrement(0.5);
            sliderPuntuacion.setMajorTickUnit(1.0);
            sliderPuntuacion.setMinorTickCount(1);
            sliderPuntuacion.setShowTickLabels(true);
            sliderPuntuacion.setShowTickMarks(true);
            sliderPuntuacion.setSnapToTicks(true);

            // Actualizar label cuando cambia el slider
            sliderPuntuacion.valueProperty().addListener((observable, oldValue, newValue) -> {
                double valor = Math.round(newValue.doubleValue() * 2) / 2.0;
                labelPuntuacion.setText(String.format("%.1f/5.0", valor));
            });
            
            logger.info("Slider configured successfully");
            
        } catch (Exception e) {
            logger.severe("Error configuring slider: " + e.getMessage());
        }
    }

    private void actualizarEstadoBotonEnviar() {
        try {
            // Habilitar botón solo si hay comentario (al menos 10 caracteres)
            String comentario = textAreaComentario.getText().trim();
            boolean tieneComentarioValido = comentario.length() >= 10;
            buttonEnviar.setDisable(!tieneComentarioValido);
            
            logger.info("Send button state updated - Valid comment: " + tieneComentarioValido + 
                       ", Comment length: " + comentario.length());
            
        } catch (Exception e) {
            logger.severe("Error updating send button state: " + e.getMessage());
        }
    }

    @FXML
    private void enviarReview() {
        logger.info("Send review button clicked");
        
        // Validar datos
        if (!validarReview()) {
            return;
        }

        // Obtener datos del review
        double puntuacion = Math.round(sliderPuntuacion.getValue() * 2) / 2.0;
        String comentario = textAreaComentario.getText().trim();

        logger.info("Review data prepared - Rating: " + puntuacion + 
                   ", Comment length: " + comentario.length());

        // Confirmar envío
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Envío");
        confirmacion.setHeaderText("¿Enviar valoración?");
        confirmacion.setContentText(String.format("Puntuación: %.1f/5.0\n\n¿Deseas enviar esta valoración?", puntuacion));

        ButtonType buttonTypeSi = new ButtonType("Sí");
        ButtonType buttonTypeNo = new ButtonType("No");
        confirmacion.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == buttonTypeSi) {
                logger.info("User confirmed review submission");
                
                // Guardar en la base de datos
                boolean exito = guardarReviewEnBD(puntuacion, comentario);

                if (exito) {
                    logger.info("Review saved successfully");
                    
                    // Mostrar confirmación
                    Alert exitoAlert = new Alert(Alert.AlertType.INFORMATION);
                    exitoAlert.setTitle("Valoración Enviada");
                    exitoAlert.setHeaderText("¡Gracias por tu valoración!");
                    exitoAlert.setContentText("Tu reseña ha sido publicada correctamente.");
                    exitoAlert.showAndWait();

                    // Cerrar ventana
                    cerrarVentana();
                    
                } else {
                    logger.warning("Failed to save review to database");
                }
            } else {
                logger.info("User cancelled review submission");
            }
        });
    }

    private boolean validarReview() {
        logger.info("Validating review");
        
        // Validar que el comentario no esté vacío
        if (textAreaComentario.getText().trim().isEmpty()) {
            logger.warning("Validation failed: Empty comment");
            mostrarAlerta("Error", "Por favor, escribe un comentario antes de enviar.");
            return false;
        }

        // Validar que el comentario tenga longitud mínima
        if (textAreaComentario.getText().trim().length() < 10) {
            logger.warning("Validation failed: Comment too short - " + 
                          textAreaComentario.getText().trim().length() + " characters");
            mostrarAlerta("Error", "El comentario debe tener al menos 10 caracteres.");
            return false;
        }

        // Validar puntuación
        double puntuacion = sliderPuntuacion.getValue();
        if (puntuacion < 0 || puntuacion > 5) {
            logger.warning("Validation failed: Invalid rating - " + puntuacion);
            mostrarAlerta("Error", "La puntuación debe estar entre 0 y 5.");
            return false;
        }

        // Validar que se tenga el videojuego
        if (videojuegoCompleto == null) {
            logger.warning("Validation failed: No game selected");
            mostrarAlerta("Error", "No se ha seleccionado un videojuego.");
            return false;
        }

        // Validar que se tenga el usuario
        if (profile == null) {
            logger.warning("Validation failed: No user profile");
            mostrarAlerta("Error", "No se ha identificado al usuario.");
            return false;
        }

        logger.info("Review validation passed successfully");
        return true;
    }

    private boolean guardarReviewEnBD(double puntuacion, String comentario) {
        logger.info("Saving review to database - Rating: " + puntuacion + 
                   ", Comment length: " + comentario.length());
        
        try {
            // Verificar que tenemos todos los datos necesarios
            if (profile == null || cont == null || videojuegoCompleto == null) {
                logger.severe("Cannot save review: Incomplete data - Profile: " + 
                             (profile != null) + ", Controller: " + (cont != null) + 
                             ", Game: " + (videojuegoCompleto != null));
                mostrarAlerta("Error", "No se puede guardar la reseña: datos incompletos.");
                return false;
            }

            logger.info("Getting complete user from database: " + profile.getUsername());
            
            // Obtener el usuario completo de la base de datos
            User usuario = cont.getUserByUsername(profile.getUsername());
            if (usuario == null) {
                logger.severe("User not found in database: " + profile.getUsername());
                mostrarAlerta("Error", "Usuario no encontrado en la base de datos.");
                return false;
            }

            logger.info("User retrieved - ID: " + usuario.getUserCode());
            
            // Verificar si el usuario ya ha reseñado este videojuego
            logger.info("Checking for existing review - User ID: " + usuario.getUserCode() + 
                       ", Game ID: " + videojuegoCompleto.getIdVideogame());
            
            if (cont.reviewExists(usuario.getUserCode(), videojuegoCompleto.getIdVideogame())) {
                logger.warning("Duplicate review found - User already reviewed this game");
                mostrarAlerta("Reseña duplicada", "Ya has reseñado este videojuego anteriormente.");
                return false;
            }

            logger.info("No duplicate review found - Creating new review");
            
            // Crear y guardar la reseña
            Review review = new Review(usuario, videojuegoCompleto, puntuacion, comentario);
            boolean resultado = cont.createReview(review);

            if (resultado) {
                logger.info("Review saved successfully in database:");
                logger.info("User: " + usuario.getUsername());
                logger.info("Videojuego: " + videojuegoCompleto.getName());
                logger.info("Puntuación: " + puntuacion);
                logger.info("Comentario length: " + comentario.length());
                return true;
            } else {
                logger.severe("Failed to save review in database");
                mostrarAlerta("Error", "No se pudo guardar la reseña en la base de datos.");
                return false;
            }

        } catch (Exception e) {
            logger.severe("Error saving review to database: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al guardar la reseña: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void cancelar() {
        logger.info("Cancel button clicked");
        
        // Preguntar confirmación si hay texto escrito
        if (!textAreaComentario.getText().trim().isEmpty()) {
            logger.info("Unsaved review exists - Asking for confirmation");
            
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Cancelación");
            confirmacion.setHeaderText("¿Descartar valoración?");
            confirmacion.setContentText("Tienes una valoración escrita. ¿Seguro que quieres cancelar?");

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            confirmacion.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    logger.info("User confirmed cancellation");
                    cerrarVentana();
                } else {
                    logger.info("User cancelled the cancellation");
                }
            });
        } else {
            logger.info("No unsaved review - Closing directly");
            cerrarVentana();
        }
    }

    private void cerrarVentana() {
        logger.info("Closing review window");
        
        try {
            Stage currentStage = (Stage) buttonCancelar.getScene().getWindow();
            currentStage.close();
            
            logger.info("Review window closed");
            
        } catch (Exception e) {
            logger.severe("Error closing review window: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        logger.info("Showing alert - Title: " + titulo + ", Message: " + mensaje);
        
        try {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        } catch (Exception e) {
            logger.severe("Error showing alert: " + e.getMessage());
        }
    }

    // Método para cargar una review existente (para editar)
    public void cargarReviewExistente(double puntuacion, String comentario) {
        logger.info("Loading existing review - Rating: " + puntuacion + 
                   ", Comment length: " + (comentario != null ? comentario.length() : 0));
        
        try {
            sliderPuntuacion.setValue(puntuacion);
            textAreaComentario.setText(comentario);
            labelPuntuacion.setText(String.format("%.1f/5.0", puntuacion));
            
            logger.info("Existing review loaded successfully");
            
        } catch (Exception e) {
            logger.severe("Error loading existing review: " + e.getMessage());
        }
    }
    
    /**
     * Abre el manual de usuario en formato PDF. Busca el archivo PDF en varias
     * ubicaciones posibles y lo abre con el visor de PDF predeterminado del
     * sistema.
     *
     * @param event Evento de acción del menú "Help Manual"
     */
    @FXML
    private void manualPdf(ActionEvent event) {
        logger.info("Opening user manual PDF");

        try {
            // Ruta relativa al PDF del manual
            String pdfFileName = "Manual de Usuario - Tienda de Videojuegos.pdf";
            String pdfPath = "pdf/" + pdfFileName;

            // Obtener la ruta absoluta del archivo
            java.io.File pdfFile = new java.io.File(pdfPath);

            if (!pdfFile.exists()) {
                logger.warning("User manual PDF not found at: " + pdfFile.getAbsolutePath());

                // Intentar buscar en diferentes ubicaciones comunes
                String[] possiblePaths = {
                    pdfPath,
                    "src/pdf/" + pdfFileName,
                    "resources/pdf/" + pdfFileName,
                    "../pdf/" + pdfFileName,
                    "./pdf/" + pdfFileName
                };

                boolean found = false;
                for (String path : possiblePaths) {
                    pdfFile = new java.io.File(path);
                    if (pdfFile.exists()) {
                        found = true;
                        logger.info("Found manual PDF at: " + pdfFile.getAbsolutePath());
                        break;
                    }
                }

                if (!found) {
                    showAlert("File Not Found",
                            "User manual PDF not found. Please ensure 'Manual de Usuario - Tienda de Videojuegos.pdf' exists in the 'pdf' folder.");
                    return;
                }
            }

            // Abrir el PDF con el programa predeterminado del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(pdfFile);
                    logger.info("Successfully opened user manual PDF: " + pdfFile.getName());
                } else {
                    throw new IOException("OPEN action not supported on this platform");
                }
            } else {
                throw new IOException("Desktop not supported on this platform");
            }

        } catch (IOException ex) {
            logger.severe("Error opening user manual PDF: " + ex.getMessage());

            // Mostrar instrucciones alternativas
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Opening PDF");
            alert.setHeaderText("Could not open user manual automatically");
            alert.setContentText("Error: " + ex.getMessage()
                    + "\n\nPlease open the PDF manually from the 'pdf' folder:\n"
                    + "1. Navigate to the 'pdf' folder in the application directory\n"
                    + "2. Open 'Manual de Usuario - Tienda de Videojuegos.pdf'");
            alert.showAndWait();
        }
    }

    /**
     * Abre el informe del proyecto en formato PDF. Busca el archivo PDF en
     * varias ubicaciones posibles y lo abre con el visor de PDF predeterminado
     * del sistema.
     *
     * @param event Evento de acción del menú "Help Report"
     */
    @FXML
    private void reportPdf(ActionEvent event) {
        logger.info("Opening project report PDF");

        try {
            // Ruta relativa al PDF del informe
            String pdfFileName = "Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf";
            String pdfPath = "pdf/" + pdfFileName;

            // Obtener la ruta absoluta del archivo
            java.io.File pdfFile = new java.io.File(pdfPath);

            if (!pdfFile.exists()) {
                logger.warning("Project report PDF not found at: " + pdfFile.getAbsolutePath());

                // Intentar buscar en diferentes ubicaciones comunes
                String[] possiblePaths = {
                    pdfPath,
                    "src/pdf/" + pdfFileName,
                    "resources/pdf/" + pdfFileName,
                    "../pdf/" + pdfFileName,
                    "./pdf/" + pdfFileName
                };

                boolean found = false;
                for (String path : possiblePaths) {
                    pdfFile = new java.io.File(path);
                    if (pdfFile.exists()) {
                        found = true;
                        logger.info("Found report PDF at: " + pdfFile.getAbsolutePath());
                        break;
                    }
                }

                if (!found) {
                    showAlert("File Not Found",
                            "Project report PDF not found. Please ensure 'Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf' exists in the 'pdf' folder.");
                    return;
                }
            }

            // Abrir el PDF con el programa predeterminado del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(pdfFile);
                    logger.info("Successfully opened project report PDF: " + pdfFile.getName());

                    // Mostrar confirmación
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("PDF Opened");
                    info.setHeaderText("Project report opened successfully");
                    info.setContentText("The project report PDF has been opened in your default PDF viewer.");
                    info.showAndWait();

                } else {
                    throw new IOException("OPEN action not supported on this platform");
                }
            } else {
                throw new IOException("Desktop not supported on this platform");
            }

        } catch (IOException ex) {
            logger.severe("Error opening project report PDF: " + ex.getMessage());

            // Mostrar instrucciones alternativas
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Opening PDF");
            alert.setHeaderText("Could not open project report automatically");
            alert.setContentText("Error: " + ex.getMessage()
                    + "\n\nPlease open the PDF manually from the 'pdf' folder:\n"
                    + "1. Navigate to the 'pdf' folder in the application directory\n"
                    + "2. Open 'Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf'");
            alert.showAndWait();
        }
    }
    
    /**
     * Muestra una ventana de alerta con el título y mensaje especificados.
     *
     * @param title Título de la alerta
     * @param message Mensaje a mostrar en la alerta
     */
    private void showAlert(String title, String message) {
        try {
            logger.info("Showing alert: " + title + " - " + message);

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

        } catch (Exception e) {
            logger.severe("Error showing alert: " + e.getMessage());
        }
    }
}