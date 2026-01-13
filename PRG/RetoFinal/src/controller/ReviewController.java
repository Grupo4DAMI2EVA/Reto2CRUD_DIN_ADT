package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.logging.*;

public class ReviewController {

    // Logger para esta clase
    private static final Logger logger = Logger.getLogger(ReviewController.class.getName());
    
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
    private Image estrellaLlena;
    private Image estrellaVacia;
    private Image estrellaMedia;

    // Inicializar el logger al cargar la clase
    static {
        inicializarLogger();
    }
    
    private static void inicializarLogger() {
        try {
            // Crear carpeta logs si no existe
            java.io.File carpetaLogs = new java.io.File("logs");
            if (!carpetaLogs.exists()) {
                carpetaLogs.mkdirs();
            }
            
            // Configurar FileHandler para escribir en archivo
            FileHandler fileHandler = new FileHandler("logs/reviews_errores.log", true);
            
            // Formato personalizado
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format("[%1$tF %1$tT] [%2$s] %3$s %n",
                            new java.util.Date(record.getMillis()),
                            record.getLevel(),
                            record.getMessage());
                }
            });
            
            // Añadir el manejador al logger
            logger.addHandler(fileHandler);
            
            // Configurar nivel de logging
            logger.setLevel(Level.INFO);
            
            // No mostrar en consola
            logger.setUseParentHandlers(false);
            
            logger.info("Logger de ReviewController inicializado correctamente");
            
        } catch (Exception e) {
            System.err.println("ERROR al inicializar logger de ReviewController: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        try {
            logger.info("Inicializando ReviewController...");
            
            // Cargar imágenes de estrellas
            cargarImagenesEstrellas();
            
            // Configurar el slider
            configurarSlider();

            // Configurar el TextArea con contador de caracteres
            configurarTextArea();

            // Actualizar estrellas visuales inicialmente
            actualizarEstrellasVisuales(sliderPuntuacion.getValue());

            // Deshabilitar botón enviar si no hay comentario
            actualizarEstadoBotonEnviar();
            
            logger.info("ReviewController inicializado correctamente. Videojuego: " + 
                       (nombreVideojuego.isEmpty() ? "No configurado" : nombreVideojuego));
            
        } catch (Exception e) {
            logger.severe("Error crítico al inicializar ReviewController: " + e.getMessage());
            mostrarAlerta("Error de Inicialización", 
                "No se pudo inicializar la ventana de reviews. Por favor, reinicie la aplicación.");
        }
    }

    private void cargarImagenesEstrellas() {
        try {
            logger.fine("Cargando imágenes de estrellas...");
            
            estrellaLlena = new Image(getClass().getResourceAsStream("/images/star_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
            estrellaVacia = new Image(getClass().getResourceAsStream("/images/star_border_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
            estrellaMedia = new Image(getClass().getResourceAsStream("/images/star_half_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
            
            if (estrellaLlena.isError() || estrellaVacia.isError() || estrellaMedia.isError()) {
                logger.warning("Alguna imagen de estrella no se pudo cargar correctamente");
            } else {
                logger.fine("Imágenes de estrellas cargadas correctamente");
            }
            
        } catch (NullPointerException e) {
            logger.severe("No se encontraron las imágenes de estrellas en la ruta especificada: " + e.getMessage());
            mostrarAlerta("Error de Recursos", 
                "No se pudieron cargar las imágenes necesarias. Contacte al administrador.");
            
        } catch (Exception e) {
            logger.severe("Error al cargar imágenes de estrellas: " + e.getMessage());
        }
    }

    private void configurarSlider() {
        try {
            logger.fine("Configurando slider de puntuación...");
            
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
                try {
                    double valor = Math.round(newValue.doubleValue() * 2) / 2.0;
                    labelPuntuacion.setText(String.format("%.1f", valor));
                    actualizarEstrellasVisuales(valor);
                    
                    logger.fine("Slider actualizado - Valor: " + valor);
                    
                } catch (Exception e) {
                    logger.warning("Error al actualizar slider: " + e.getMessage());
                }
            });
            
            logger.fine("Slider configurado correctamente");
            
        } catch (Exception e) {
            logger.severe("Error al configurar slider: " + e.getMessage());
        }
    }

    private void actualizarEstrellasVisuales(double puntuacion) {
        try {
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
            
            logger.finest("Estrellas visuales actualizadas - Puntuación: " + puntuacion);
            
        } catch (NullPointerException e) {
            logger.warning("Error al actualizar estrellas (imágenes no cargadas): " + e.getMessage());
        } catch (Exception e) {
            logger.warning("Error inesperado al actualizar estrellas visuales: " + e.getMessage());
        }
    }

    private void configurarTextArea() {
        try {
            logger.fine("Configurando TextArea de comentario...");
            
            // Configurar límite de caracteres
            final int MAX_CARACTERES = 500;

            textAreaComentario.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
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
                        logger.fine("Límite de caracteres alcanzado (" + MAX_CARACTERES + ")");
                    }

                    // Actualizar estado del botón enviar
                    actualizarEstadoBotonEnviar();
                    
                    logger.finest("Contador de caracteres actualizado: " + caracteres);
                    
                } catch (Exception e) {
                    logger.warning("Error en listener de TextArea: " + e.getMessage());
                }
            });
            
            logger.fine("TextArea configurado correctamente (límite: " + MAX_CARACTERES + " caracteres)");
            
        } catch (Exception e) {
            logger.severe("Error al configurar TextArea: " + e.getMessage());
        }
    }

    private void actualizarEstadoBotonEnviar() {
        try {
            // Habilitar botón solo si hay comentario
            boolean tieneComentario = !textAreaComentario.getText().trim().isEmpty();
            buttonEnviar.setDisable(!tieneComentario);
            
            logger.finest("Estado botón enviar actualizado - Habilitado: " + tieneComentario);
            
        } catch (Exception e) {
            logger.warning("Error al actualizar estado del botón enviar: " + e.getMessage());
        }
    }

    @FXML
    private void enviarReview() {
        try {
            logger.info("Iniciando envío de review - Usuario: " + idUsuario + 
                       ", Videojuego: " + nombreVideojuego);
            
            // Validar datos
            if (!validarReview()) {
                logger.warning("Validación fallida al enviar review");
                return;
            }

            // Obtener datos del review
            double puntuacion = Math.round(sliderPuntuacion.getValue() * 2) / 2.0;
            String comentario = textAreaComentario.getText().trim();

            logger.info("Datos de review preparados - Puntuación: " + puntuacion + 
                       ", Caracteres: " + comentario.length());

            // Confirmar envío
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Envío");
            confirmacion.setHeaderText("¿Enviar valoración?");
            confirmacion.setContentText(String.format("Puntuación: %.1f/5.0\n\n¿Deseas enviar esta valoración?", puntuacion));

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            confirmacion.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            confirmacion.showAndWait().ifPresent(response -> {
                try {
                    if (response == buttonTypeSi) {
                        logger.info("Usuario confirmó envío de review");
                        
                        // Aquí iría la lógica para guardar en la base de datos
                        guardarReviewEnBD(puntuacion, comentario);

                        // Mostrar confirmación
                        Alert exito = new Alert(Alert.AlertType.INFORMATION);
                        exito.setTitle("Valoración Enviada");
                        exito.setHeaderText("¡Gracias por tu valoración!");
                        exito.setContentText("Tu reseña ha sido publicada correctamente.");
                        exito.showAndWait();

                        logger.info("Review enviado exitosamente");
                        
                        // Cerrar ventana
                        cancelar();
                        
                    } else {
                        logger.fine("Usuario canceló el envío de review");
                    }
                } catch (Exception e) {
                    logger.severe("Error al procesar confirmación de review: " + e.getMessage());
                    mostrarAlerta("Error", "No se pudo procesar tu valoración. Intenta nuevamente.");
                }
            });
            
        } catch (Exception e) {
            logger.severe("Error crítico en enviarReview: " + e.getMessage());
            mostrarAlerta("Error del Sistema", 
                "Ocurrió un error al enviar tu valoración. Por favor, inténtalo más tarde.");
        }
    }

    private boolean validarReview() {
        try {
            logger.fine("Validando datos del review...");
            
            // Validar que el comentario no esté vacío
            String comentario = textAreaComentario.getText().trim();
            if (comentario.isEmpty()) {
                logger.warning("Validación fallida: comentario vacío");
                mostrarAlerta("Error", "Por favor, escribe un comentario antes de enviar.");
                return false;
            }

            // Validar que el comentario tenga longitud mínima
            if (comentario.length() < 10) {
                logger.warning("Validación fallida: comentario muy corto (" + comentario.length() + " caracteres)");
                mostrarAlerta("Error", "El comentario debe tener al menos 10 caracteres.");
                return false;
            }

            // Validar puntuación
            double puntuacion = sliderPuntuacion.getValue();
            if (puntuacion < 0 || puntuacion > 5) {
                logger.warning("Validación fallida: puntuación fuera de rango (" + puntuacion + ")");
                mostrarAlerta("Error", "La puntuación debe estar entre 0 y 5.");
                return false;
            }

            // Validar que se haya configurado el videojuego
            if (nombreVideojuego.isEmpty() || idVideojuego == 0) {
                logger.severe("Validación fallida: videojuego no configurado");
                mostrarAlerta("Error", "No se ha seleccionado un videojuego válido.");
                return false;
            }

            // Validar que se haya configurado el usuario
            if (idUsuario == 0) {
                logger.warning("Validación fallida: usuario no configurado o ID=0");
                mostrarAlerta("Error", "No se ha identificado al usuario.");
                return false;
            }

            logger.fine("Validación exitosa - Puntuación: " + puntuacion + 
                       ", Caracteres: " + comentario.length());
            return true;
            
        } catch (Exception e) {
            logger.severe("Error en validarReview: " + e.getMessage());
            mostrarAlerta("Error de Validación", 
                "Ocurrió un error al validar los datos. Verifica la información.");
            return false;
        }
    }

    private void guardarReviewEnBD(double puntuacion, String comentario) {
        try {
            logger.info("Guardando review en BD - UsuarioID: " + idUsuario + 
                       ", VideojuegoID: " + idVideojuego + 
                       ", Puntuación: " + puntuacion);
            
            // Aquí iría el código para guardar en tu base de datos
            // Por ejemplo:
            // Review review = new Review(idUsuario, idVideojuego, puntuacion, comentario);
            // reviewDAO.guardar(review);

            // Simulación de guardado en BD (TODO: Implementar conexión real a BD)
            logger.info("Simulando guardado en BD:");
            logger.info("  Usuario ID: " + idUsuario);
            logger.info("  Videojuego ID: " + idVideojuego);
            logger.info("  Videojuego: " + nombreVideojuego);
            logger.info("  Puntuación: " + puntuacion);
            logger.info("  Comentario (longitud): " + comentario.length() + " caracteres");
            logger.info("  Comentario (preview): " + 
                       (comentario.length() > 50 ? comentario.substring(0, 50) + "..." : comentario));

            // Aquí deberías implementar la conexión real a tu base de datos
            // Por ejemplo:
            // try {
            //     Connection conn = DatabaseManager.getConnection();
            //     PreparedStatement stmt = conn.prepareStatement(
            //         "INSERT INTO reviews (usuario_id, videojuego_id, puntuacion, comentario) VALUES (?, ?, ?, ?)");
            //     stmt.setInt(1, idUsuario);
            //     stmt.setInt(2, idVideojuego);
            //     stmt.setDouble(3, puntuacion);
            //     stmt.setString(4, comentario);
            //     int filasAfectadas = stmt.executeUpdate();
            //     
            //     if (filasAfectadas > 0) {
            //         logger.info("Review guardado exitosamente en BD");
            //     } else {
            //         logger.severe("No se pudo guardar el review en BD (0 filas afectadas)");
            //     }
            // } catch (SQLException e) {
            //     logger.severe("Error de SQL al guardar review: " + e.getMessage());
            //     throw new RuntimeException("Error de base de datos", e);
            // }
            
        } catch (Exception e) {
            logger.severe("Error al guardar review en BD: " + e.getMessage());
            throw new RuntimeException("No se pudo guardar la valoración", e);
        }
    }

    @FXML
    private void cancelar() {
        try {
            logger.info("Solicitud de cancelación de review");
            
            // Preguntar confirmación si hay texto escrito
            if (!textAreaComentario.getText().trim().isEmpty()) {
                logger.fine("Hay texto escrito, solicitando confirmación");
                
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Confirmar Cancelación");
                confirmacion.setHeaderText("¿Descartar valoración?");
                confirmacion.setContentText("Tienes una valoración escrita. ¿Seguro que quieres cancelar?");

                ButtonType buttonTypeSi = new ButtonType("Sí");
                ButtonType buttonTypeNo = new ButtonType("No");
                confirmacion.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                confirmacion.showAndWait().ifPresent(response -> {
                    try {
                        if (response == buttonTypeSi) {
                            logger.info("Usuario confirmó cancelar review");
                            cerrarVentana();
                        } else {
                            logger.fine("Usuario canceló la acción de salir");
                        }
                    } catch (Exception e) {
                        logger.severe("Error al procesar confirmación de cancelación: " + e.getMessage());
                    }
                });
            } else {
                logger.fine("No hay texto escrito, cerrando directamente");
                cerrarVentana();
            }
            
        } catch (Exception e) {
            logger.severe("Error en cancelar: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cancelar la operación.");
        }
    }

    private void cerrarVentana() {
        try {
            logger.info("Cerrando ventana de review");
            
            stage = (Stage) buttonCancelar.getScene().getWindow();
            stage.close();
            
            logger.info("Ventana de review cerrada exitosamente");
            
        } catch (NullPointerException e) {
            logger.severe("Error al cerrar ventana: stage no inicializado");
            mostrarAlerta("Error", "No se pudo cerrar la ventana. Intenta cerrarla manualmente.");
        } catch (Exception e) {
            logger.severe("Error al cerrar ventana: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        try {
            logger.warning("Mostrando alerta: " + titulo + " - " + mensaje);
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
            
        } catch (Exception e) {
            logger.severe("Error al mostrar alerta '" + titulo + "': " + e.getMessage());
            // Si falla mostrar la alerta, al menos lo logueamos
        }
    }

    // Métodos para configurar datos desde fuera
    public void setVideojuego(String nombre, int idVideojuego) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("Nombre de videojuego no puede ser nulo o vacío");
            }
            if (idVideojuego <= 0) {
                throw new IllegalArgumentException("ID de videojuego debe ser mayor a 0");
            }
            
            this.nombreVideojuego = nombre;
            this.idVideojuego = idVideojuego;
            labelVideojuego.setText("Videojuego: " + nombre);
            
            logger.info("Videojuego configurado - Nombre: " + nombre + ", ID: " + idVideojuego);
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error al configurar videojuego: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error inesperado al configurar videojuego: " + e.getMessage());
        }
    }

    public void setUsuario(int idUsuario) {
        try {
            if (idUsuario <= 0) {
                throw new IllegalArgumentException("ID de usuario debe ser mayor a 0");
            }
            
            this.idUsuario = idUsuario;
            logger.info("Usuario configurado - ID: " + idUsuario);
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error al configurar usuario: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error inesperado al configurar usuario: " + e.getMessage());
        }
    }

    public void setStage(Stage stage) {
        try {
            if (stage == null) {
                throw new IllegalArgumentException("Stage no puede ser nulo");
            }
            
            this.stage = stage;
            logger.fine("Stage configurado en ReviewController");
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error al configurar stage: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error inesperado al configurar stage: " + e.getMessage());
        }
    }

    // Método para cargar una review existente (para editar)
    public void cargarReviewExistente(double puntuacion, String comentario) {
        try {
            logger.info("Cargando review existente para edición");
            
            if (puntuacion < 0 || puntuacion > 5) {
                throw new IllegalArgumentException("Puntuación fuera de rango: " + puntuacion);
            }
            
            if (comentario == null) {
                throw new IllegalArgumentException("Comentario no puede ser nulo");
            }
            
            sliderPuntuacion.setValue(puntuacion);
            textAreaComentario.setText(comentario);
            actualizarEstrellasVisuales(puntuacion);
            
            logger.info("Review existente cargado - Puntuación: " + puntuacion + 
                       ", Caracteres: " + comentario.length());
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error de validación al cargar review existente: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error al cargar review existente: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la review para editar.");
        }
    }
    
    // Método para obtener información del review actual (útil para debugging)
    public String obtenerInfoReview() {
        try {
            double puntuacion = Math.round(sliderPuntuacion.getValue() * 2) / 2.0;
            String comentario = textAreaComentario.getText().trim();
            
            return String.format(
                "Review Info:\n" +
                "  Usuario ID: %d\n" +
                "  Videojuego: %s (ID: %d)\n" +
                "  Puntuación: %.1f/5.0\n" +
                "  Comentario: %d caracteres\n" +
                "  Botón enviar: %s",
                idUsuario,
                nombreVideojuego,
                idVideojuego,
                puntuacion,
                comentario.length(),
                buttonEnviar.isDisabled() ? "DESHABILITADO" : "HABILITADO"
            );
            
        } catch (Exception e) {
            return "Error obteniendo info del review: " + e.getMessage();
        }
    }
}