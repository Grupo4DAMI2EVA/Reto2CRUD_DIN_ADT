package controller;

import java.util.logging.FileHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Profile;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class ReviewController {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Slider sliderPuntuacion;

    @FXML
    private Label labelPuntuacion;

    @FXML
    private Label labelContadorCaracteres;

    @FXML
    private Label labelVideojuego;

    @FXML
    private TextArea textAreaComentario;

    @FXML
    private Button buttonEnviar;

    @FXML
    private Button buttonCancelar;

    @FXML
    private ImageView estrella1, estrella2, estrella3, estrella4, estrella5;

    private Stage stage;
    private String nombreVideojuego = "";
    private int idUsuario = 0;
    private int idVideojuego = 0;
    private Profile profile;
    private Controller cont;

    // Imágenes para las estrellas
    private Image estrellaLlena;
    private Image estrellaVacia;
    private Image estrellaMedia;
    
    static {
        initializeLogger();
    }
    
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
            logger.info("AdminShopController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }
    
    private void initialize() {
        logger.info("Initializing ReviewController");
        
        try {
            // Configurar el slider
            configurarSlider();

            // Configurar el TextArea con contador de caracteres
            configurarTextArea();

            // Actualizar estrellas visuales inicialmente
            actualizarEstrellasVisuales(sliderPuntuacion.getValue());

            // Deshabilitar botón enviar si no hay comentario
            actualizarEstadoBotonEnviar();
            
            logger.info("ReviewController initialized successfully");
            logger.info("Initial rating: " + sliderPuntuacion.getValue() + 
                       ", Game: " + nombreVideojuego + " (ID: " + idVideojuego + ")");
            
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
                labelPuntuacion.setText(String.format("%.1f", valor));
                actualizarEstrellasVisuales(valor);
                
                logger.info("Rating slider changed - From: " + oldValue + " to: " + newValue + 
                           ", Rounded: " + valor);
            });
            
            logger.info("Slider configured - Range: 0-5, Initial: 2.5, Increment: 0.5");
            
        } catch (Exception e) {
            logger.severe("Error configuring slider: " + e.getMessage());
        }
    }

    private void actualizarEstrellasVisuales(double puntuacion) {
        logger.info("Updating star visuals - Rating: " + puntuacion);
        
        try {
            // Actualizar las estrellas visuales según la puntuación
            ImageView[] estrellas = {estrella1, estrella2, estrella3, estrella4, estrella5};

            for (int i = 0; i < estrellas.length; i++) {
                if (puntuacion >= i + 1) {
                    // Estrella completa
                    if (estrellaLlena != null) {
                        estrellas[i].setImage(estrellaLlena);
                    }
                } else if (puntuacion >= i + 0.5) {
                    // Media estrella
                    if (estrellaMedia != null) {
                        estrellas[i].setImage(estrellaMedia);
                    }
                } else {
                    // Estrella vacía
                    if (estrellaVacia != null) {
                        estrellas[i].setImage(estrellaVacia);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.warning("Error updating star visuals: " + e.getMessage());
        }
    }

    private void configurarTextArea() {
        logger.info("Configuring comment text area");
        
        try {
            // Configurar límite de caracteres
            final int MAX_CARACTERES = 500;

            textAreaComentario.textProperty().addListener((observable, oldValue, newValue) -> {
                // Actualizar contador
                int caracteres = newValue.length();
                labelContadorCaracteres.setText(caracteres + "/" + MAX_CARACTERES + " caracteres");

                // Cambiar color si se acerca al límite
                if (caracteres > MAX_CARACTERES * 0.9) {
                    labelContadorCaracteres.setStyle("-fx-text-fill: #FF5722;");
                    logger.info("Character limit warning - " + caracteres + "/" + MAX_CARACTERES + " characters");
                } else if (caracteres > MAX_CARACTERES * 0.75) {
                    labelContadorCaracteres.setStyle("-fx-text-fill: #FF9800;");
                } else {
                    labelContadorCaracteres.setStyle("-fx-text-fill: #666666;");
                }

                // Limitar caracteres
                if (newValue.length() > MAX_CARACTERES) {
                    textAreaComentario.setText(oldValue);
                    logger.info("Character limit reached - Truncated to " + MAX_CARACTERES + " characters");
                }

                // Actualizar estado del botón enviar
                actualizarEstadoBotonEnviar();
                
                logger.info("Comment updated - Characters: " + caracteres + 
                           ", Previous: " + (oldValue != null ? oldValue.length() : 0));
            });
            
        } catch (Exception e) {
            logger.severe("Error configuring text area: " + e.getMessage());
        }
    }

    private void actualizarEstadoBotonEnviar() {
        try {
            // Habilitar botón solo si hay comentario
            boolean tieneComentario = !textAreaComentario.getText().trim().isEmpty();
            buttonEnviar.setDisable(!tieneComentario);
            
            logger.info("Send button state updated - Enabled: " + tieneComentario + 
                       ", Comment length: " + textAreaComentario.getText().length());
            
        } catch (Exception e) {
            logger.warning("Error updating send button state: " + e.getMessage());
        }
    }

    @FXML
    private void enviarReview() {
        logger.info("Send review button clicked - Game: " + nombreVideojuego + 
                   " (ID: " + idVideojuego + "), User ID: " + idUsuario);
        
        // Validar datos
        if (!validarReview()) {
            logger.warning("Review validation failed");
            return;
        }

        // Obtener datos del review
        double puntuacion = Math.round(sliderPuntuacion.getValue() * 2) / 2.0;
        String comentario = textAreaComentario.getText().trim();
        
        logger.info("Review data - Rating: " + puntuacion + 
                   ", Comment length: " + comentario.length() + " characters");

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
                
                // Aquí iría la lógica para guardar en la base de datos
                guardarReviewEnBD(puntuacion, comentario);

                // Mostrar confirmación
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Valoración Enviada");
                exito.setHeaderText("¡Gracias por tu valoración!");
                exito.setContentText("Tu reseña ha sido publicada correctamente.");
                exito.showAndWait();

                logger.info("Review submitted successfully - Rating: " + puntuacion + 
                           ", Game: " + nombreVideojuego + ", User ID: " + idUsuario);

                // Cerrar ventana
                cancelar();
                
            } else {
                logger.info("User cancelled review submission");
            }
        });
    }

    private boolean validarReview() {
        logger.info("Validating review submission");
        
        // Validar que el comentario no esté vacío
        if (textAreaComentario.getText().trim().isEmpty()) {
            logger.warning("Validation failed - Empty comment");
            mostrarAlerta("Error", "Por favor, escribe un comentario antes de enviar.");
            return false;
        }

        // Validar que el comentario tenga longitud mínima
        int commentLength = textAreaComentario.getText().trim().length();
        if (commentLength < 10) {
            logger.warning("Validation failed - Comment too short: " + commentLength + " characters");
            mostrarAlerta("Error", "El comentario debe tener al menos 10 caracteres.");
            return false;
        }

        // Validar puntuación
        double puntuacion = sliderPuntuacion.getValue();
        if (puntuacion < 0 || puntuacion > 5) {
            logger.warning("Validation failed - Invalid rating: " + puntuacion);
            mostrarAlerta("Error", "La puntuación debe estar entre 0 y 5.");
            return false;
        }

        logger.info("Review validation passed - Rating: " + puntuacion + 
                   ", Comment length: " + commentLength + " characters");
        return true;
    }

    private void guardarReviewEnBD(double puntuacion, String comentario) {
        logger.info("Saving review to database - Game: " + nombreVideojuego + 
                   " (ID: " + idVideojuego + "), User ID: " + idUsuario + 
                   ", Rating: " + puntuacion + ", Comment length: " + comentario.length());
        
        try {
            // Aquí iría el código para guardar en tu base de datos
            // Por ejemplo:
            // Review review = new Review(idUsuario, idVideojuego, puntuacion, comentario);
            // reviewDAO.guardar(review);
            
            logger.info("Review saved to database successfully");
            
            // Para debugging, mostrar en consola también
            System.out.println("=== REVIEW SAVED ===");
            System.out.println("Usuario ID: " + idUsuario);
            System.out.println("Videojuego ID: " + idVideojuego);
            System.out.println("Videojuego: " + nombreVideojuego);
            System.out.println("Puntuación: " + puntuacion);
            System.out.println("Comentario: " + comentario.substring(0, Math.min(50, comentario.length())) + "...");
            
        } catch (Exception e) {
            logger.severe("Error saving review to database: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo guardar la valoración. Inténtalo de nuevo.");
        }
    }

    @FXML
    private void cancelar() {
        logger.info("Cancel button clicked");
        
        // Preguntar confirmación si hay texto escrito
        if (!textAreaComentario.getText().trim().isEmpty()) {
            logger.info("Unsaved comment detected - Asking for confirmation");
            
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Cancelación");
            confirmacion.setHeaderText("¿Descartar valoración?");
            confirmacion.setContentText("Tienes una valoración escrita. ¿Seguro que quieres cancelar?");

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            confirmacion.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    logger.info("User confirmed review cancellation");
                    cerrarVentana();
                } else {
                    logger.info("User cancelled review cancellation");
                }
            });
        } else {
            logger.info("No unsaved comment - Closing window");
            cerrarVentana();
        }
    }

    private void cerrarVentana() {
        logger.info("Closing review window");
        
        try {
            stage = (Stage) buttonCancelar.getScene().getWindow();
            stage.close();
            
            logger.info("Review window closed successfully");
            
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

    // Métodos para configurar datos desde fuera
    public void setVideojuego(String nombre, int idVideojuego) {
        logger.info("Setting video game for review: " + nombre + " (ID: " + idVideojuego + ")");
        this.nombreVideojuego = nombre;
        this.idVideojuego = idVideojuego;
        labelVideojuego.setText("Videojuego: " + nombre);
    }

    public void setUsuario(int idUsuario) {
        logger.info("Setting user ID for review: " + idUsuario);
        this.idUsuario = idUsuario;
    }

    public void setStage(Stage stage) {
        logger.info("Setting stage in ReviewController");
        this.stage = stage;
    }

    // Método para cargar una review existente (para editar)
    public void cargarReviewExistente(double puntuacion, String comentario) {
        logger.info("Loading existing review - Rating: " + puntuacion + 
                   ", Comment length: " + (comentario != null ? comentario.length() : 0));
        
        try {
            sliderPuntuacion.setValue(puntuacion);
            textAreaComentario.setText(comentario);
            actualizarEstrellasVisuales(puntuacion);
            
            logger.info("Existing review loaded successfully");
            
        } catch (Exception e) {
            logger.severe("Error loading existing review: " + e.getMessage());
        }
    }
}