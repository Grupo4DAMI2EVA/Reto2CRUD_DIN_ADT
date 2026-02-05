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

/**
 * Controller class for managing video game reviews.
 * Handles user interface interactions for creating and submitting reviews.
 * 
 * @author [Your Name]
 */
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
     * Initializes the logger system.
     * Creates log directory and configures file handler with custom formatter.
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

    /**
     * Initializes the controller after FXML loading.
     * Configures UI components and sets up event listeners.
     */
    @FXML
    private void initialize() {
        logger.info("Initializing ReviewController");
        
        try {
            
            configurarSlider();

            
            actualizarEstadoBotonEnviar();

            
            textAreaComentario.textProperty().addListener((observable, oldValue, newValue) -> {
                actualizarEstadoBotonEnviar();
            });
            
            logger.info("ReviewController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing ReviewController: " + e.getMessage());
        }
    }

    /**
     * Sets the user profile for the review.
     *
     * @param profile The user profile to set
     */
    public void setUsuario(Profile profile) {
        logger.info("Setting user profile in ReviewController: " + 
                   (profile != null ? profile.getUsername() + " (ID: " + profile.getUserCode() + ")" : "null"));
        this.profile = profile;
        if (profile != null) {
            this.idUsuario = profile.getUserCode();
        }
    }

    /**
     * Sets the main controller reference.
     *
     * @param cont The main controller to set
     */
    public void setCont(Controller cont) {
        logger.info("Setting controller in ReviewController");
        this.cont = cont;
    }

    /**
     * Gets the main controller reference.
     *
     * @return The main controller
     */
    public Controller getCont() {
        return cont;
    }

    /**
     * Sets the complete videogame object for review.
     *
     * @param videojuego The complete videogame object
     */
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

    /**
     * Sets the videogame information for review (compatibility method).
     *
     * @param nombre The videogame name
     * @param idVideojuego The videogame ID
     */
    public void setVideojuego(String nombre, int idVideojuego) {
        logger.info("Setting game (compatibility method) in ReviewController: " + nombre + " (ID: " + idVideojuego + ")");
        this.nombreVideojuego = nombre;
        this.idVideojuego = idVideojuego;
        if (labelVideojuego != null) {
            labelVideojuego.setText("Videojuego: " + nombre);
        }
    }

    /**
     * Configures the rating slider properties and behavior.
     */
    private void configurarSlider() {
        logger.info("Configuring rating slider");
        
        try {
            
            sliderPuntuacion.setMin(0);
            sliderPuntuacion.setMax(5);
            sliderPuntuacion.setValue(2.5);
            sliderPuntuacion.setBlockIncrement(0.5);
            sliderPuntuacion.setMajorTickUnit(1.0);
            sliderPuntuacion.setMinorTickCount(1);
            sliderPuntuacion.setShowTickLabels(true);
            sliderPuntuacion.setShowTickMarks(true);
            sliderPuntuacion.setSnapToTicks(true);

            
            sliderPuntuacion.valueProperty().addListener((observable, oldValue, newValue) -> {
                double valor = Math.round(newValue.doubleValue() * 2) / 2.0;
                labelPuntuacion.setText(String.format("%.1f/5.0", valor));
            });
            
            logger.info("Slider configured successfully");
            
        } catch (Exception e) {
            logger.severe("Error configuring slider: " + e.getMessage());
        }
    }

    /**
     * Updates the state of the send button based on comment validity.
     */
    private void actualizarEstadoBotonEnviar() {
        try {
            
            String comentario = textAreaComentario.getText().trim();
            boolean tieneComentarioValido = comentario.length() >= 10;
            buttonEnviar.setDisable(!tieneComentarioValido);
            
            logger.info("Send button state updated - Valid comment: " + tieneComentarioValido + 
                       ", Comment length: " + comentario.length());
            
        } catch (Exception e) {
            logger.severe("Error updating send button state: " + e.getMessage());
        }
    }

    /**
     * Handles the send review button action.
     * Validates data, shows confirmation dialog, and saves review to database.
     */
    @FXML
    private void enviarReview() {
        logger.info("Send review button clicked");
        
        
        if (!validarReview()) {
            return;
        }

        
        double puntuacion = Math.round(sliderPuntuacion.getValue() * 2) / 2.0;
        String comentario = textAreaComentario.getText().trim();

        logger.info("Review data prepared - Rating: " + puntuacion + 
                   ", Comment length: " + comentario.length());

        
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
                
                
                boolean exito = guardarReviewEnBD(puntuacion, comentario);

                if (exito) {
                    logger.info("Review saved successfully");
                    
                    
                    Alert exitoAlert = new Alert(Alert.AlertType.INFORMATION);
                    exitoAlert.setTitle("Valoración Enviada");
                    exitoAlert.setHeaderText("¡Gracias por tu valoración!");
                    exitoAlert.setContentText("Tu reseña ha sido publicada correctamente.");
                    exitoAlert.showAndWait();

                    
                    cerrarVentana();
                    
                } else {
                    logger.warning("Failed to save review to database");
                }
            } else {
                logger.info("User cancelled review submission");
            }
        });
    }

    /**
     * Validates the review data before submission.
     *
     * @return true if review is valid, false otherwise
     */
    private boolean validarReview() {
        logger.info("Validating review");
        
        
        if (textAreaComentario.getText().trim().isEmpty()) {
            logger.warning("Validation failed: Empty comment");
            mostrarAlerta("Error", "Por favor, escribe un comentario antes de enviar.");
            return false;
        }

        
        if (textAreaComentario.getText().trim().length() < 10) {
            logger.warning("Validation failed: Comment too short - " + 
                          textAreaComentario.getText().trim().length() + " characters");
            mostrarAlerta("Error", "El comentario debe tener al menos 10 caracteres.");
            return false;
        }

        
        double puntuacion = sliderPuntuacion.getValue();
        if (puntuacion < 0 || puntuacion > 5) {
            logger.warning("Validation failed: Invalid rating - " + puntuacion);
            mostrarAlerta("Error", "La puntuación debe estar entre 0 y 5.");
            return false;
        }

        
        if (videojuegoCompleto == null) {
            logger.warning("Validation failed: No game selected");
            mostrarAlerta("Error", "No se ha seleccionado un videojuego.");
            return false;
        }

        
        if (profile == null) {
            logger.warning("Validation failed: No user profile");
            mostrarAlerta("Error", "No se ha identificado al usuario.");
            return false;
        }

        logger.info("Review validation passed successfully");
        return true;
    }

    /**
     * Saves the review to the database.
     *
     * @param puntuacion The rating score
     * @param comentario The review comment
     * @return true if save was successful, false otherwise
     */
    private boolean guardarReviewEnBD(double puntuacion, String comentario) {
        logger.info("Saving review to database - Rating: " + puntuacion + 
                   ", Comment length: " + comentario.length());
        
        try {
            
            if (profile == null || cont == null || videojuegoCompleto == null) {
                logger.severe("Cannot save review: Incomplete data - Profile: " + 
                             (profile != null) + ", Controller: " + (cont != null) + 
                             ", Game: " + (videojuegoCompleto != null));
                mostrarAlerta("Error", "No se puede guardar la reseña: datos incompletos.");
                return false;
            }

            logger.info("Getting complete user from database: " + profile.getUsername());
            
            
            User usuario = cont.getUserByUsername(profile.getUsername());
            if (usuario == null) {
                logger.severe("User not found in database: " + profile.getUsername());
                mostrarAlerta("Error", "Usuario no encontrado en la base de datos.");
                return false;
            }

            logger.info("User retrieved - ID: " + usuario.getUserCode());
            
            
            logger.info("Checking for existing review - User ID: " + usuario.getUserCode() + 
                       ", Game ID: " + videojuegoCompleto.getIdVideogame());
            
            if (cont.reviewExists(usuario.getUserCode(), videojuegoCompleto.getIdVideogame())) {
                logger.warning("Duplicate review found - User already reviewed this game");
                mostrarAlerta("Reseña duplicada", "Ya has reseñado este videojuego anteriormente.");
                return false;
            }

            logger.info("No duplicate review found - Creating new review");
            
            
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

    /**
     * Handles the cancel button action.
     * Shows confirmation dialog if there is unsaved data.
     */
    @FXML
    private void cancelar() {
        logger.info("Cancel button clicked");
        
        
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

    /**
     * Closes the review window.
     */
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

    /**
     * Shows an alert dialog with the specified title and message.
     *
     * @param titulo The alert title
     * @param mensaje The alert message
     */
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

    /**
     * Loads an existing review for editing.
     *
     * @param puntuacion The existing rating score
     * @param comentario The existing review comment
     */
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
     * Opens the user manual PDF file.
     * Searches for the PDF in multiple possible locations and opens it with the system's default PDF viewer.
     *
     * @param event The action event from the "Help Manual" menu
     */
    @FXML
    private void manualPdf(ActionEvent event) {
        logger.info("Opening user manual PDF");

        try {
            
            String pdfFileName = "Manual de Usuario - Tienda de Videojuegos.pdf";
            String pdfPath = "pdf/" + pdfFileName;

            
            java.io.File pdfFile = new java.io.File(pdfPath);

            if (!pdfFile.exists()) {
                logger.warning("User manual PDF not found at: " + pdfFile.getAbsolutePath());

                
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
     * Opens the project report PDF file.
     * Searches for the PDF in multiple possible locations and opens it with the system's default PDF viewer.
     *
     * @param event The action event from the "Help Report" menu
     */
    @FXML
    private void reportPdf(ActionEvent event) {
        logger.info("Opening project report PDF");

        try {
            
            String pdfFileName = "Proyecto-JavaFX-Sistema-de-Gestion-para-Tienda-de-Videojuegos.pdf";
            String pdfPath = "pdf/" + pdfFileName;

            
            java.io.File pdfFile = new java.io.File(pdfPath);

            if (!pdfFile.exists()) {
                logger.warning("Project report PDF not found at: " + pdfFile.getAbsolutePath());

                
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

            
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(pdfFile);
                    logger.info("Successfully opened project report PDF: " + pdfFile.getName());

                    
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
     * Shows an alert dialog with the specified title and message.
     *
     * @param title The alert title
     * @param message The alert message
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