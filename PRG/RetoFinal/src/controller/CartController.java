package controller;

import java.util.logging.FileHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class CartController {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
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

    private Stage stage;
    private ObservableList<String> carritoData;
    private int indiceSeleccionado = -1;
    private int cantidadActual = 0;
    
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
            
            FileHandler fileHandler = new FileHandler("logs/CartWindow.log", true);
            
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
        
    }
    
    public void setup() {
        logger.info("Setting up CartController");
        
        try {
            // Inicializar la lista de items del carrito
            carritoData = FXCollections.observableArrayList();
            listViewCarrito.setItems(carritoData);

            // Configurar selección de item
            listViewCarrito.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> mostrarDetalleItem(newValue));
            
            logger.info("CartController setup completed successfully");
            logger.info("Carrito items: " + carritoData.size());
            
        } catch (Exception e) {
            logger.severe("Error setting up CartController: " + e.getMessage());
        }
    }


    private void mostrarDetalleItem(String item) {
        if (item != null) {
            logger.info("Item selected in cart: " + item);
            
            indiceSeleccionado = listViewCarrito.getSelectionModel().getSelectedIndex();
            logger.info("Selected index: " + indiceSeleccionado);

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

                logger.info("Item details - User: " + usuario + 
                           ", Game: " + videojuego + 
                           ", Quantity: " + cantidadActual + 
                           ", Price: " + precio);

                // Habilitar botones
                actualizarEstadoBotones();
            } else {
                logger.warning("Invalid item format in cart: " + item);
            }
        } else {
            // Si no hay item seleccionado
            logger.info("Cart item selection cleared");
            indiceSeleccionado = -1;
            labelItemSeleccionado.setText("Selecciona un item");
            labelCantidadActual.setText("0");
            actualizarEstadoBotones();
        }
    }

    @FXML
    private void aumentarCantidad() {
        logger.info("Increase quantity button clicked");
        
        if (indiceSeleccionado >= 0) {
            cantidadActual++;
            logger.info("Increasing quantity to: " + cantidadActual + " for index: " + indiceSeleccionado);
            actualizarItemCantidad();
        } else {
            logger.warning("Attempted to increase quantity without item selected");
        }
    }

    @FXML
    private void disminuirCantidad() {
        logger.info("Decrease quantity button clicked");
        
        if (indiceSeleccionado >= 0 && cantidadActual > 1) {
            cantidadActual--;
            logger.info("Decreasing quantity to: " + cantidadActual + " for index: " + indiceSeleccionado);
            actualizarItemCantidad();
        } else if (cantidadActual == 1) {
            logger.info("Quantity is 1 - Asking if user wants to remove item");
            
            // Si la cantidad es 1, preguntar si eliminar
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar item");
            alert.setHeaderText("¿Eliminar este item del carrito?");
            alert.setContentText("La cantidad llegaría a 0. ¿Deseas eliminarlo completamente?");

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    logger.info("User confirmed item removal (from decrease quantity)");
                    eliminarItem();
                } else {
                    logger.info("User cancelled item removal (from decrease quantity)");
                }
            });
        } else {
            logger.warning("Attempted to decrease quantity without item selected");
        }
    }

    private void actualizarItemCantidad() {
        if (indiceSeleccionado >= 0) {
            String itemOriginal = carritoData.get(indiceSeleccionado);
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
                
                logger.info("Item quantity updated - New quantity: " + cantidadActual + 
                           ", Index: " + indiceSeleccionado);
                
            } else {
                logger.warning("Cannot update quantity - Invalid item format: " + itemOriginal);
            }
        }
    }

    public void actualizarEstadoBotones() {
        boolean hayItemSeleccionado = (indiceSeleccionado >= 0);

        // Habilitar/deshabilitar botones + y -
        buttonMas.setDisable(!hayItemSeleccionado);
        buttonMenos.setDisable(!hayItemSeleccionado || cantidadActual <= 1);

        // Habilitar/deshabilitar botón eliminar
        buttonEliminar.setDisable(!hayItemSeleccionado);

        // Habilitar/deshabilitar botón comprar
        buttonComprar.setDisable(carritoData.isEmpty());
        
        logger.info("Button states updated - Items in cart: " + carritoData.size() + 
                   ", Selected: " + hayItemSeleccionado + 
                   ", Current quantity: " + cantidadActual);
    }

    @FXML
    private void comprar() {
        logger.info("Buy button clicked");
        
        // Lógica para realizar la compra
        if (carritoData.isEmpty()) {
            logger.warning("Attempted to buy with empty cart");
            mostrarAlerta("Carrito vacío", "No hay items en el carrito para comprar.");
            return;
        }

        logger.info("Starting purchase process - Items in cart: " + carritoData.size() + 
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
                logger.info("User confirmed purchase - Processing transaction");
                
                // Aquí iría la lógica real de compra (conexión a BD, etc.)
                // Por ahora solo mostramos mensaje de éxito
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Compra Realizada");
                success.setHeaderText("¡Compra exitosa!");
                success.setContentText("Tu compra se ha realizado correctamente.");
                success.showAndWait();

                logger.info("Purchase completed successfully - Cart cleared");
                
                Stage currentStage = (Stage) buttonComprar.getScene().getWindow();
                currentStage.close();
                
                logger.info("Cart window closed after purchase");
                
            } else {
                logger.info("User cancelled purchase");
            }
        });
    }

    @FXML
    private void eliminarItem() {
        logger.info("Delete item button clicked");
        
        if (indiceSeleccionado >= 0) {
            String itemToDelete = carritoData.get(indiceSeleccionado);
            logger.info("Confirming deletion of item: " + itemToDelete + ", Index: " + indiceSeleccionado);
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Eliminar este item del carrito?");
            alert.setContentText("Esta acción no se puede deshacer.");

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    logger.info("User confirmed item deletion - Removing item from cart");
                    carritoData.remove(indiceSeleccionado);
                    actualizarTotales();
                    limpiarSeleccion();
                    
                    logger.info("Item removed successfully - Remaining items: " + carritoData.size());
                    
                } else {
                    logger.info("User cancelled item deletion");
                }
            });
        } else {
            logger.warning("Attempted to delete item without selection");
            mostrarAlerta("Seleccionar item", "Por favor, seleccione un item para eliminar.");
        }
    }

    private void limpiarSeleccion() {
        logger.info("Clearing cart selection");
        
        listViewCarrito.getSelectionModel().clearSelection();
        indiceSeleccionado = -1;
        cantidadActual = 0;
        labelCantidadActual.setText("0");
        labelItemSeleccionado.setText("Selecciona un item");
        actualizarEstadoBotones();
        
        logger.info("Selection cleared successfully");
    }

    @FXML
    private void cancelar() {
        logger.info("Cancel button clicked - Closing cart window");
        
        stage = (Stage) buttonEliminar.getScene().getWindow();
        stage.close();
        
        logger.info("Cart window closed");
    }

    public void actualizarTotales() {
        logger.info("Updating cart totals");
        
        int totalItems = 0;
        double totalPagar = 0.0;
        int itemsProcessed = 0;

        for (String item : carritoData) {
            try {
                String[] partes = item.split("\\|");
                if (partes.length >= 4) {
                    // Extraer cantidad
                    String cantidadParte = partes[2].trim();
                    String cantidadStr = cantidadParte.split(":")[1].trim();
                    int cantidad = Integer.parseInt(cantidadStr);

                    // Extraer precio - manejar formato "Precio: $19,99"
                    String precioParte = partes[3].trim();
                    // Buscar el símbolo de moneda y extraer el número
                    int startIndex = precioParte.indexOf('$');
                    if (startIndex == -1) {
                        startIndex = precioParte.indexOf('€');
                    }
                    if (startIndex == -1) {
                        startIndex = precioParte.indexOf(':');
                    }
                    
                    if (startIndex != -1) {
                        String precioStr = precioParte.substring(startIndex + 1).trim();
                        // Reemplazar coma por punto para parseo correcto
                        precioStr = precioStr.replace(",", ".");
                        double precio = Double.parseDouble(precioStr);

                        totalItems += cantidad;
                        totalPagar += precio * cantidad;
                        itemsProcessed++;
                    }
                }
            } catch (Exception e) {
                logger.warning("Error processing cart item: " + item + " - " + e.getMessage());
                // e.printStackTrace(); // Comentado para logs más limpios
            }
        }

        labelTotalItems.setText(String.valueOf(totalItems));
        labelTotalPagar.setText(String.format("$%.2f", totalPagar));
        actualizarEstadoBotones();
        
        logger.info("Cart totals updated - Total items: " + totalItems + 
                   ", Total to pay: $" + totalPagar + 
                   ", Items processed: " + itemsProcessed + "/" + carritoData.size());
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

    // Método para agregar items desde otras partes de la aplicación
    public void agregarItemCarrito(String usuario, String videojuego, int cantidad, double precio) {
        logger.info("Adding item to cart - User: " + usuario + 
                   ", Game: " + videojuego + 
                   ", Quantity: " + cantidad + 
                   ", Price: $" + precio);
        
        String item = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                usuario, videojuego, cantidad, precio);
        carritoData.add(item);
        actualizarTotales();
        
        logger.info("Item added successfully - Total items in cart: " + carritoData.size());
    }

    public void setStage(Stage stage) {
        logger.info("Setting stage in CartController");
        this.stage = stage;
    }
}