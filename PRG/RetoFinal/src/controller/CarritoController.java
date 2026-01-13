package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.*;

public class CarritoController {

    // Logger para esta clase
    private static final Logger logger = Logger.getLogger(CarritoController.class.getName());
    
    @FXML
    private ListView<String> listViewCarrito;

    @FXML
    private Label labelTotalItems;

    @FXML
    private Label labelTotalPagar;

    @FXML
    private Label labelCantidadActual;

    @FXML
    private Label labelItemSeleccionado;

    @FXML
    private Button buttonMas;

    @FXML
    private Button buttonMenos;

    @FXML
    private Button buttonComprar;

    @FXML
    private Button buttonEliminar;

    @FXML
    private Button buttonCancelar;

    private Stage stage;
    private ObservableList<String> carritoData;
    private int indiceSeleccionado = -1;
    private int cantidadActual = 0;

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
            FileHandler fileHandler = new FileHandler("logs/carrito_errores.log", true);
            
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
            
            // Solo registrar WARNING y SEVERE
            logger.setLevel(Level.WARNING);
            
            // No mostrar en consola
            logger.setUseParentHandlers(false);
            
            logger.info("Logger de CarritoController inicializado correctamente");
            
        } catch (Exception e) {
            System.err.println("ERROR al inicializar logger: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        try {
            logger.info("Inicializando CarritoController...");
            
            // Inicializar la lista de items del carrito
            carritoData = FXCollections.observableArrayList();
            listViewCarrito.setItems(carritoData);

            // Cargar datos de ejemplo
            cargarDatosEjemplo();

            // Actualizar totales
            actualizarTotales();

            // Deshabilitar botones inicialmente
            actualizarEstadoBotones();

            // Configurar selección de item
            listViewCarrito.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> mostrarDetalleItem(newValue));
            
            logger.info("CarritoController inicializado correctamente");
            
        } catch (Exception e) {
            logger.severe("Error al inicializar CarritoController: " + e.getMessage());
            mostrarAlerta("Error de Inicialización", 
                "No se pudo inicializar el carrito. Por favor, reinicie la aplicación.");
        }
    }

    private void cargarDatosEjemplo() {
        try {
            logger.info("Cargando datos de ejemplo al carrito");
            
            // Datos de ejemplo para probar
            carritoData.add("Usuario: 1 | Videojuego: FIFA 23 | Cantidad: 2 | Precio: $59.99");
            carritoData.add("Usuario: 1 | Videojuego: Call of Duty | Cantidad: 1 | Precio: $69.99");
            carritoData.add("Usuario: 2 | Videojuego: Minecraft | Cantidad: 3 | Precio: $24.99");
            carritoData.add("Usuario: 3 | Videojuego: GTA V | Cantidad: 1 | Precio: $39.99");
            
            logger.info("Datos de ejemplo cargados: " + carritoData.size() + " items");
            
        } catch (Exception e) {
            logger.warning("Error al cargar datos de ejemplo: " + e.getMessage());
        }
    }

    private void mostrarDetalleItem(String item) {
        try {
            if (item != null) {
                indiceSeleccionado = listViewCarrito.getSelectionModel().getSelectedIndex();
                logger.info("Item seleccionado - Índice: " + indiceSeleccionado + ", Item: " + item);

                // Extraer información del item seleccionado
                String[] partes = item.split("\\|");
                if (partes.length >= 4) {
                    String usuario = partes[0].trim().split(":")[1].trim();
                    String videojuego = partes[1].trim().split(":")[1].trim();
                    String cantidadStr = partes[2].trim().split(":")[1].trim();
                    String precio = partes[3].trim();

                    // Actualizar información mostrada
                    labelItemSeleccionado.setText(videojuego);
                    cantidadActual = Integer.parseInt(cantidadStr);
                    labelCantidadActual.setText(String.valueOf(cantidadActual));

                    logger.fine("Detalles extraídos - Usuario: " + usuario + 
                              ", Videojuego: " + videojuego + 
                              ", Cantidad: " + cantidadActual);
                } else {
                    logger.warning("Formato de item inválido: " + item);
                }
                
                // Habilitar botones
                actualizarEstadoBotones();
                
            } else {
                // Si no hay item seleccionado
                logger.fine("No hay item seleccionado");
                indiceSeleccionado = -1;
                labelItemSeleccionado.setText("Selecciona un item");
                labelCantidadActual.setText("0");
                actualizarEstadoBotones();
            }
            
        } catch (NumberFormatException e) {
            logger.severe("Error de formato al extraer cantidad del item: " + item + 
                         " - Error: " + e.getMessage());
            mostrarAlerta("Error de Formato", "El formato del item seleccionado es incorrecto.");
            
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.severe("Error al procesar item: formato inesperado - " + item);
            mostrarAlerta("Error de Formato", "No se pudo procesar el item seleccionado.");
            
        } catch (Exception e) {
            logger.severe("Error inesperado en mostrarDetalleItem: " + e.getMessage());
        }
    }

    @FXML
    private void aumentarCantidad() {
        try {
            if (indiceSeleccionado >= 0) {
                logger.info("Aumentando cantidad - Índice: " + indiceSeleccionado + 
                          ", Cantidad actual: " + cantidadActual);
                
                cantidadActual++;
                actualizarItemCantidad();
                
                logger.fine("Cantidad aumentada a: " + cantidadActual);
            } else {
                logger.warning("Intento de aumentar cantidad sin item seleccionado");
            }
        } catch (Exception e) {
            logger.severe("Error en aumentarCantidad: " + e.getMessage());
        }
    }

    @FXML
    private void disminuirCantidad() {
        try {
            if (indiceSeleccionado >= 0 && cantidadActual > 1) {
                logger.info("Disminuyendo cantidad - Índice: " + indiceSeleccionado + 
                          ", Cantidad actual: " + cantidadActual);
                
                cantidadActual--;
                actualizarItemCantidad();
                
                logger.fine("Cantidad disminuida a: " + cantidadActual);
                
            } else if (cantidadActual == 1) {
                // Si la cantidad es 1, preguntar si eliminar
                logger.info("Cantidad en 1 - Preguntando si eliminar item");
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Eliminar item");
                alert.setHeaderText("¿Eliminar este item del carrito?");
                alert.setContentText("La cantidad llegaría a 0. ¿Deseas eliminarlo completamente?");

                ButtonType buttonTypeSi = new ButtonType("Sí");
                ButtonType buttonTypeNo = new ButtonType("No");
                alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                alert.showAndWait().ifPresent(response -> {
                    if (response == buttonTypeSi) {
                        logger.info("Usuario confirmó eliminar item");
                        eliminarItem();
                    } else {
                        logger.fine("Usuario canceló eliminación");
                    }
                });
            } else {
                logger.warning("Intento de disminuir cantidad sin item seleccionado o cantidad inválida");
            }
        } catch (Exception e) {
            logger.severe("Error en disminuirCantidad: " + e.getMessage());
        }
    }

    private void actualizarItemCantidad() {
        try {
            if (indiceSeleccionado >= 0) {
                String itemOriginal = carritoData.get(indiceSeleccionado);
                logger.fine("Actualizando cantidad - Item original: " + itemOriginal);
                
                String[] partes = itemOriginal.split("\\|");

                if (partes.length >= 4) {
                    // Reconstruir el item con la nueva cantidad
                    String nuevoItem = partes[0].trim() + " | "
                            + partes[1].trim() + " | "
                            + "Cantidad: " + cantidadActual + " | "
                            + partes[3].trim();

                    carritoData.set(indiceSeleccionado, nuevoItem);
                    listViewCarrito.refresh();
                    labelCantidadActual.setText(String.valueOf(cantidadActual));
                    actualizarTotales();
                    actualizarEstadoBotones();
                    
                    logger.info("Cantidad actualizada - Nuevo item: " + nuevoItem);
                } else {
                    logger.warning("Formato de item inválido al actualizar cantidad: " + itemOriginal);
                }
            } else {
                logger.warning("Intento de actualizar cantidad sin item seleccionado");
            }
        } catch (Exception e) {
            logger.severe("Error en actualizarItemCantidad: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo actualizar la cantidad del item.");
        }
    }

    private void actualizarEstadoBotones() {
        try {
            boolean hayItemSeleccionado = (indiceSeleccionado >= 0);

            // Habilitar/deshabilitar botones + y -
            buttonMas.setDisable(!hayItemSeleccionado);
            buttonMenos.setDisable(!hayItemSeleccionado || cantidadActual <= 1);

            // Habilitar/deshabilitar botón eliminar
            buttonEliminar.setDisable(!hayItemSeleccionado);

            // Habilitar/deshabilitar botón comprar
            buttonComprar.setDisable(carritoData.isEmpty());
            
            logger.fine("Estado de botones actualizado - " +
                       "Hay selección: " + hayItemSeleccionado + 
                       ", Carrito vacío: " + carritoData.isEmpty());
            
        } catch (Exception e) {
            logger.severe("Error en actualizarEstadoBotones: " + e.getMessage());
        }
    }

    @FXML
    private void comprar() {
        try {
            // Lógica para realizar la compra
            if (carritoData.isEmpty()) {
                logger.warning("Intento de comprar con carrito vacío");
                mostrarAlerta("Carrito vacío", "No hay items en el carrito para comprar.");
                return;
            }

            logger.info("Iniciando proceso de compra - Items: " + carritoData.size() + 
                       ", Total: " + labelTotalPagar.getText());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Compra");
            alert.setHeaderText("¿Realizar compra?");
            alert.setContentText("Total a pagar: " + labelTotalPagar.getText());

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    logger.info("Usuario confirmó la compra");
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Compra Realizada");
                    success.setHeaderText("¡Compra exitosa!");
                    success.setContentText("Tu compra se ha realizado correctamente.");
                    success.showAndWait();

                    // Limpiar carrito después de comprar
                    logger.info("Limpiando carrito después de compra exitosa");
                    carritoData.clear();
                    actualizarTotales();
                    limpiarSeleccion();
                    
                    logger.info("Compra completada exitosamente");
                    
                } else {
                    logger.fine("Usuario canceló la compra");
                }
            });
            
        } catch (Exception e) {
            logger.severe("Error en comprar: " + e.getMessage());
            mostrarAlerta("Error en Compra", 
                "Ocurrió un error al procesar la compra. Por favor, intente nuevamente.");
        }
    }

    @FXML
    private void eliminarItem() {
        try {
            if (indiceSeleccionado >= 0) {
                String itemAEliminar = carritoData.get(indiceSeleccionado);
                logger.info("Solicitando eliminar item: " + itemAEliminar);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar Eliminación");
                alert.setHeaderText("¿Eliminar este item del carrito?");
                alert.setContentText("Esta acción no se puede deshacer.");

                ButtonType buttonTypeSi = new ButtonType("Sí");
                ButtonType buttonTypeNo = new ButtonType("No");
                alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                alert.showAndWait().ifPresent(response -> {
                    if (response == buttonTypeSi) {
                        logger.info("Usuario confirmó eliminar el item");
                        carritoData.remove(indiceSeleccionado);
                        actualizarTotales();
                        limpiarSeleccion();
                        
                        logger.info("Item eliminado exitosamente");
                    } else {
                        logger.fine("Usuario canceló la eliminación");
                    }
                });
            } else {
                logger.warning("Intento de eliminar sin item seleccionado");
                mostrarAlerta("Seleccionar item", "Por favor, seleccione un item para eliminar.");
            }
        } catch (Exception e) {
            logger.severe("Error en eliminarItem: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo eliminar el item del carrito.");
        }
    }

    private void limpiarSeleccion() {
        try {
            logger.fine("Limpiando selección");
            
            listViewCarrito.getSelectionModel().clearSelection();
            indiceSeleccionado = -1;
            cantidadActual = 0;
            labelCantidadActual.setText("0");
            labelItemSeleccionado.setText("Selecciona un item");
            actualizarEstadoBotones();
            
        } catch (Exception e) {
            logger.severe("Error en limpiarSeleccion: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        try {
            logger.info("Cerrando ventana del carrito");
            
            stage = (Stage) buttonEliminar.getScene().getWindow();
            stage.close();
            
            logger.info("Ventana del carrito cerrada");
            
        } catch (Exception e) {
            logger.severe("Error al cerrar ventana del carrito: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cerrar la ventana.");
        }
    }

    private void actualizarTotales() {
        try {
            int totalItems = 0;
            double totalPagar = 0.0;
            
            logger.fine("Calculando totales...");

            for (String item : carritoData) {
                try {
                    String[] partes = item.split("\\|");
                    if (partes.length >= 4) {
                        // Extraer cantidad
                        String cantidadParte = partes[2].trim();
                        String cantidadStr = cantidadParte.split(":")[1].trim();
                        int cantidad = Integer.parseInt(cantidadStr);

                        // Extraer precio
                        String precioParte = partes[3].trim();
                        String precioStr = precioParte.split("\\$")[1].trim();
                        double precio = Double.parseDouble(precioStr);

                        totalItems += cantidad;
                        totalPagar += precio * cantidad;
                    }
                } catch (NumberFormatException e) {
                    logger.warning("Error de formato en item (ignorado): " + item + 
                                 " - Error: " + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    logger.warning("Formato inesperado en item (ignorado): " + item);
                } catch (Exception e) {
                    logger.warning("Error procesando item (ignorado): " + item + 
                                 " - Error: " + e.getMessage());
                }
            }

            labelTotalItems.setText(String.valueOf(totalItems));
            labelTotalPagar.setText(String.format("$%.2f", totalPagar));
            actualizarEstadoBotones();
            
            logger.info("Totales actualizados - Items: " + totalItems + 
                       ", Total a pagar: $" + String.format("%.2f", totalPagar));
            
        } catch (Exception e) {
            logger.severe("Error en actualizarTotales: " + e.getMessage());
            mostrarAlerta("Error de Cálculo", 
                "No se pudieron calcular los totales. Por favor, verifique los items.");
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
        }
    }

    // Método para agregar items desde otras partes de la aplicación
    public void agregarItemCarrito(String usuario, String videojuego, int cantidad, double precio) {
        try {
            if (usuario == null || usuario.trim().isEmpty()) {
                throw new IllegalArgumentException("Usuario no puede ser nulo o vacío");
            }
            if (videojuego == null || videojuego.trim().isEmpty()) {
                throw new IllegalArgumentException("Videojuego no puede ser nulo o vacío");
            }
            if (cantidad <= 0) {
                throw new IllegalArgumentException("Cantidad debe ser mayor a 0");
            }
            if (precio <= 0) {
                throw new IllegalArgumentException("Precio debe ser mayor a 0");
            }

            String item = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                    usuario, videojuego, cantidad, precio);
            
            carritoData.add(item);
            actualizarTotales();
            
            logger.info("Item agregado al carrito - Usuario: " + usuario + 
                       ", Videojuego: " + videojuego + 
                       ", Cantidad: " + cantidad + 
                       ", Precio: $" + precio);
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error de validación al agregar item: " + e.getMessage());
            throw e; // Relanzar para que lo maneje quien llama
            
        } catch (Exception e) {
            logger.severe("Error inesperado al agregar item al carrito: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo agregar el item al carrito.");
        }
    }

    public void setStage(Stage stage) {
        try {
            this.stage = stage;
            logger.fine("Stage configurado en CarritoController");
        } catch (Exception e) {
            logger.severe("Error al configurar stage: " + e.getMessage());
        }
    }
    
    // Método para obtener el estado actual del carrito (útil para debugging)
    public String obtenerEstadoCarrito() {
        StringBuilder estado = new StringBuilder();
        estado.append("Carrito - Items: ").append(carritoData.size()).append("\n");
        for (int i = 0; i < carritoData.size(); i++) {
            estado.append("  ").append(i).append(": ").append(carritoData.get(i)).append("\n");
        }
        return estado.toString();
    }
}