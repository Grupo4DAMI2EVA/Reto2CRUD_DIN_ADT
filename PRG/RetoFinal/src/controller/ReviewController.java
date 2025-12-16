package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ReviewController {
    
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
    
    // Imágenes para las estrellas
    private final Image estrellaLlena = new Image(getClass().getResourceAsStream("/images/star_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
    private final Image estrellaVacia = new Image(getClass().getResourceAsStream("/images/star_border_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
    private final Image estrellaMedia = new Image(getClass().getResourceAsStream("/images/star_half_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
    
    @FXML
    private void initialize() {
        // Configurar el slider
        configurarSlider();
        
        // Configurar el TextArea con contador de caracteres
        configurarTextArea();
        
        // Actualizar estrellas visuales inicialmente
        actualizarEstrellasVisuales(sliderPuntuacion.getValue());
        
        // Deshabilitar botón enviar si no hay comentario
        actualizarEstadoBotonEnviar();
    }
    
    private void configurarSlider() {
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
        });
    }
    
    private void actualizarEstrellasVisuales(double puntuacion) {
        // Actualizar las estrellas visuales según la puntuación
        ImageView[] estrellas = {estrella1, estrella2, estrella3, estrella4, estrella5};
        
        for (int i = 0; i < estrellas.length; i++) {
            if (puntuacion >= i + 1) {
                // Estrella completa
                estrellas[i].setImage(estrellaLlena);
            } else if (puntuacion >= i + 0.5) {
                // Media estrella
                estrellas[i].setImage(estrellaMedia);
            } else {
                // Estrella vacía
                estrellas[i].setImage(estrellaVacia);
            }
        }
    }
    
    private void configurarTextArea() {
        // Configurar límite de caracteres
        final int MAX_CARACTERES = 500;
        
        textAreaComentario.textProperty().addListener((observable, oldValue, newValue) -> {
            // Actualizar contador
            int caracteres = newValue.length();
            labelContadorCaracteres.setText(caracteres + "/" + MAX_CARACTERES + " caracteres");
            
            // Cambiar color si se acerca al límite
            if (caracteres > MAX_CARACTERES * 0.9) {
                labelContadorCaracteres.setStyle("-fx-text-fill: #FF5722;");
            } else if (caracteres > MAX_CARACTERES * 0.75) {
                labelContadorCaracteres.setStyle("-fx-text-fill: #FF9800;");
            } else {
                labelContadorCaracteres.setStyle("-fx-text-fill: #666666;");
            }
            
            // Limitar caracteres
            if (newValue.length() > MAX_CARACTERES) {
                textAreaComentario.setText(oldValue);
            }
            
            // Actualizar estado del botón enviar
            actualizarEstadoBotonEnviar();
        });
    }
    
    private void actualizarEstadoBotonEnviar() {
        // Habilitar botón solo si hay comentario
        boolean tieneComentario = !textAreaComentario.getText().trim().isEmpty();
        buttonEnviar.setDisable(!tieneComentario);
    }
    
    @FXML
    private void enviarReview() {
        // Validar datos
        if (!validarReview()) {
            return;
        }
        
        // Obtener datos del review
        double puntuacion = Math.round(sliderPuntuacion.getValue() * 2) / 2.0;
        String comentario = textAreaComentario.getText().trim();
        
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
                // Aquí iría la lógica para guardar en la base de datos
                guardarReviewEnBD(puntuacion, comentario);
                
                // Mostrar confirmación
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Valoración Enviada");
                exito.setHeaderText("¡Gracias por tu valoración!");
                exito.setContentText("Tu reseña ha sido publicada correctamente.");
                exito.showAndWait();
                
                // Cerrar ventana
                cancelar();
            }
        });
    }
    
    private boolean validarReview() {
        // Validar que el comentario no esté vacío
        if (textAreaComentario.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Por favor, escribe un comentario antes de enviar.");
            return false;
        }
        
        // Validar que el comentario tenga longitud mínima
        if (textAreaComentario.getText().trim().length() < 10) {
            mostrarAlerta("Error", "El comentario debe tener al menos 10 caracteres.");
            return false;
        }
        
        // Validar puntuación
        double puntuacion = sliderPuntuacion.getValue();
        if (puntuacion < 0 || puntuacion > 5) {
            mostrarAlerta("Error", "La puntuación debe estar entre 0 y 5.");
            return false;
        }
        
        return true;
    }
    
    private void guardarReviewEnBD(double puntuacion, String comentario) {
        // Aquí iría el código para guardar en tu base de datos
        // Por ejemplo:
        // Review review = new Review(idUsuario, idVideojuego, puntuacion, comentario);
        // reviewDAO.guardar(review);
        
        System.out.println("Guardando review en BD:");
        System.out.println("Usuario ID: " + idUsuario);
        System.out.println("Videojuego ID: " + idVideojuego);
        System.out.println("Videojuego: " + nombreVideojuego);
        System.out.println("Puntuación: " + puntuacion);
        System.out.println("Comentario: " + comentario);
        
        // Simulación de guardado en BD
        // TODO: Implementar conexión real a BD
    }
    
    @FXML
    private void cancelar() {
        // Preguntar confirmación si hay texto escrito
        if (!textAreaComentario.getText().trim().isEmpty()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Cancelación");
            confirmacion.setHeaderText("¿Descartar valoración?");
            confirmacion.setContentText("Tienes una valoración escrita. ¿Seguro que quieres cancelar?");
            
            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            confirmacion.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);
            
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    cerrarVentana();
                }
            });
        } else {
            cerrarVentana();
        }
    }
    
    private void cerrarVentana() {
        stage = (Stage) buttonCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Métodos para configurar datos desde fuera
    public void setVideojuego(String nombre, int idVideojuego) {
        this.nombreVideojuego = nombre;
        this.idVideojuego = idVideojuego;
        labelVideojuego.setText("Videojuego: " + nombre);
    }
    
    public void setUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // Método para cargar una review existente (para editar)
    public void cargarReviewExistente(double puntuacion, String comentario) {
        sliderPuntuacion.setValue(puntuacion);
        textAreaComentario.setText(comentario);
        actualizarEstrellasVisuales(puntuacion);
    }
}