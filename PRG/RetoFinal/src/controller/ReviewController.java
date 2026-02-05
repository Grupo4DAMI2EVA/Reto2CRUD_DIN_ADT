package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;

public class ReviewController {

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

    @FXML
    private void initialize() {
        // Configurar el slider
        configurarSlider();

        // Deshabilitar botón enviar inicialmente
        actualizarEstadoBotonEnviar();

        // Configurar listener para habilitar botón cuando haya texto
        textAreaComentario.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarEstadoBotonEnviar();
        });
    }

    public void setUsuario(Profile profile) {
        this.profile = profile;
        if (profile != null) {
            this.idUsuario = profile.getUserCode();
        }
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    // Método para establecer el videojuego completo
    public void setVideojuegoCompleto(Videogame videojuego) {
        this.videojuegoCompleto = videojuego;
        this.nombreVideojuego = videojuego.getName();
        this.idVideojuego = videojuego.getIdVideogame();
        if (labelVideojuego != null) {
            labelVideojuego.setText("Videojuego: " + nombreVideojuego);
        }
    }

    // Método original para compatibilidad
    public void setVideojuego(String nombre, int idVideojuego) {
        this.nombreVideojuego = nombre;
        this.idVideojuego = idVideojuego;
        if (labelVideojuego != null) {
            labelVideojuego.setText("Videojuego: " + nombre);
        }
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
            labelPuntuacion.setText(String.format("%.1f/5.0", valor));
        });
    }

    private void actualizarEstadoBotonEnviar() {
        // Habilitar botón solo si hay comentario (al menos 10 caracteres)
        String comentario = textAreaComentario.getText().trim();
        boolean tieneComentarioValido = comentario.length() >= 10;
        buttonEnviar.setDisable(!tieneComentarioValido);
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
                // Guardar en la base de datos
                boolean exito = guardarReviewEnBD(puntuacion, comentario);

                if (exito) {
                    // Mostrar confirmación
                    Alert exitoAlert = new Alert(Alert.AlertType.INFORMATION);
                    exitoAlert.setTitle("Valoración Enviada");
                    exitoAlert.setHeaderText("¡Gracias por tu valoración!");
                    exitoAlert.setContentText("Tu reseña ha sido publicada correctamente.");
                    exitoAlert.showAndWait();

                    // Cerrar ventana
                    cerrarVentana();
                }
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

    private boolean guardarReviewEnBD(double puntuacion, String comentario) {
        try {
            // Verificar que tenemos todos los datos necesarios
            if (profile == null || cont == null || videojuegoCompleto == null) {
                mostrarAlerta("Error", "No se puede guardar la reseña: datos incompletos.");
                return false;
            }

            // Obtener el usuario completo de la base de datos
            User usuario = cont.getUserByUsername(profile.getUsername());
            if (usuario == null) {
                mostrarAlerta("Error", "Usuario no encontrado en la base de datos.");
                return false;
            }

            // Verificar si el usuario ya ha reseñado este videojuego
            if (cont.reviewExists(usuario.getUserCode(), videojuegoCompleto.getIdVideogame())) {
                mostrarAlerta("Reseña duplicada", "Ya has reseñado este videojuego anteriormente.");
                return false;
            }

            // Crear y guardar la reseña
            Review review = new Review(usuario, videojuegoCompleto, puntuacion, comentario);
            boolean resultado = cont.createReview(review);

            if (resultado) {
                System.out.println("Reseña guardada exitosamente:");
                System.out.println("Usuario: " + usuario.getUsername());
                System.out.println("Videojuego: " + videojuegoCompleto.getName());
                System.out.println("Puntuación: " + puntuacion);
                System.out.println("Comentario: " + comentario);
                return true;
            } else {
                mostrarAlerta("Error", "No se pudo guardar la reseña en la base de datos.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error al guardar la reseña: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al guardar la reseña: " + e.getMessage());
            return false;
        }
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
        Stage currentStage = (Stage) buttonCancelar.getScene().getWindow();
        currentStage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para cargar una review existente (para editar)
    public void cargarReviewExistente(double puntuacion, String comentario) {
        sliderPuntuacion.setValue(puntuacion);
        textAreaComentario.setText(comentario);
        labelPuntuacion.setText(String.format("%.1f/5.0", puntuacion));
    }
}
