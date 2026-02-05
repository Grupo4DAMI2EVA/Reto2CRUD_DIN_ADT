package controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.event.ActionEvent;

/**
 * Controller class for managing the shopping cart functionality.
 * Handles adding, removing, and modifying items in the cart, as well as processing purchases.
 * 
 * @author [Your Name]
 */
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
    private ObservableList<CartItem> itemsCarrito;
    private ObservableList<Videogame> juegosCarrito;
    private int indiceSeleccionado = -1;
    private int cantidadActual = 0;
    
    private Profile profile;
    private Controller cont;
    private List<Videogame> todosLosJuegos;
    
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

    /**
     * Initializes the controller after FXML loading.
     * Sets up the observable lists and configures selection listeners.
     */
    @FXML
    private void initialize() {
        logger.info("Initializing CartController");
        
        try {
            
            carritoData = FXCollections.observableArrayList();
            itemsCarrito = FXCollections.observableArrayList();
            juegosCarrito = FXCollections.observableArrayList();
            listViewCarrito.setItems(carritoData);

            
            listViewCarrito.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> mostrarDetalleItem(newValue));
            
            logger.info("CartController initialized successfully");
            logger.info("Lists initialized - carritoData: " + carritoData.size() + 
                       ", itemsCarrito: " + itemsCarrito.size() + 
                       ", juegosCarrito: " + juegosCarrito.size());
            
        } catch (Exception e) {
            logger.severe("Error initializing CartController: " + e.getMessage());
        }
    }
    
    /**
     * Performs additional setup after initialization.
     * Updates button states and performs final configuration.
     */
    public void setup() {
        logger.info("CartController setup called");
        
        try {
            
            actualizarEstadoBotones();
            
            logger.info("CartController setup completed successfully");
            
        } catch (Exception e) {
            logger.severe("Error in CartController setup: " + e.getMessage());
        }
    }
    
    /**
     * Sets the user profile for the cart.
     *
     * @param profile The user profile to set
     */
    public void setUsuario(Profile profile) {
        logger.info("Setting user profile in CartController: " + 
                   (profile != null ? profile.getUsername() + " (ID: " + profile.getUserCode() + ")" : "null"));
        this.profile = profile;
    }
    
    /**
     * Sets the main controller reference and loads all games.
     *
     * @param cont The main controller to set
     */
    public void setCont(Controller cont) {
        logger.info("Setting controller in CartController");
        this.cont = cont;
        if (cont != null) {
            this.todosLosJuegos = cont.getAllGames();
            logger.info("Loaded all games for cart reference: " + 
                       (todosLosJuegos != null ? todosLosJuegos.size() : 0) + " games");
        }
    }
    
    /**
     * Adds an item to the shopping cart.
     *
     * @param usuario The username
     * @param videojuego The videogame object
     * @param cantidad The quantity to add
     * @param precio The price per unit
     */
    public void agregarItemCarrito(String usuario, Videogame videojuego, int cantidad, double precio) {
        logger.info("Adding item to cart - User: " + usuario + 
                   ", Game: " + videojuego.getName() + 
                   " (ID: " + videojuego.getIdVideogame() + 
                   "), Quantity: " + cantidad + 
                   ", Price: $" + precio);
        
        try {
            
            for (int i = 0; i < itemsCarrito.size(); i++) {
                CartItem item = itemsCarrito.get(i);
                if (item.getIdVideojuego() == videojuego.getIdVideogame()) {
                    
                    int nuevaCantidad = item.getCantidad() + cantidad;
                    item.setCantidad(nuevaCantidad);
                    
                    logger.info("Item already in cart - Updating quantity from " + 
                               item.getCantidad() + " to " + nuevaCantidad + 
                               " for game: " + videojuego.getName());
                    
                    
                    String itemActualizado = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                            usuario, videojuego.getName(), nuevaCantidad, precio);
                    carritoData.set(i, itemActualizado);
                    
                    actualizarTotales();
                    return;
                }
            }
            
            
            String item = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                    usuario, videojuego.getName(), cantidad, precio);
            carritoData.add(item);
            
            CartItem cartItem = new CartItem(
                    profile.getUserCode(),
                    videojuego.getIdVideogame(),
                    cantidad,
                    precio
            );
            itemsCarrito.add(cartItem);
            juegosCarrito.add(videojuego);
            
            logger.info("New item added to cart - Total items now: " + itemsCarrito.size());
            
            actualizarTotales();
            actualizarEstadoBotones();
            
        } catch (Exception e) {
            logger.severe("Error adding item to cart: " + e.getMessage());
        }
    }
    
    /**
     * Adds an item to the shopping cart (compatibility method).
     *
     * @param usuario The username
     * @param videojuegoNombre The videogame name
     * @param cantidad The quantity to add
     * @param precio The price per unit
     */
    public void agregarItemCarrito(String usuario, String videojuegoNombre, int cantidad, double precio) {
        logger.info("Adding item to cart (compatibility method) - User: " + usuario + 
                   ", Game: " + videojuegoNombre + 
                   ", Quantity: " + cantidad + 
                   ", Price: $" + precio);
        
        try {
            String item = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                    usuario, videojuegoNombre, cantidad, precio);
            carritoData.add(item);
            actualizarTotales();
            actualizarEstadoBotones();
            
            logger.info("Item added via compatibility method - Cart data size: " + carritoData.size());
            
        } catch (Exception e) {
            logger.severe("Error in compatibility method agregarItemCarrito: " + e.getMessage());
        }
    }

    /**
     * Displays details of the selected cart item.
     *
     * @param item The selected item string
     */
    private void mostrarDetalleItem(String item) {
        if (item != null) {
            logger.info("Item selected in cart: " + item);
            
            
            indiceSeleccionado = listViewCarrito.getSelectionModel().getSelectedIndex();
            
            
            if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
                logger.info("Valid selection - Index: " + indiceSeleccionado + 
                           ", Items in cart: " + itemsCarrito.size());
                
                
                String[] partes = item.split("\\|");
                if (partes.length >= 4) {
                    String usuario = partes[0].trim().split(":")[1].trim();
                    String videojuego = partes[1].trim().split(":")[1].trim();
                    String cantidadStr = partes[2].trim().split(":")[1].trim();
                    String precio = partes[3].trim();

                    
                    labelItemSeleccionado.setText(videojuego);
                    cantidadActual = Integer.parseInt(cantidadStr);
                    labelCantidadActual.setText(String.valueOf(cantidadActual));

                    logger.info("Item details extracted - Game: " + videojuego + 
                               ", Quantity: " + cantidadActual + 
                               ", Price: " + precio);
                    
                    
                    actualizarEstadoBotones();
                } else {
                    logger.warning("Invalid item format in cart: " + item);
                }
            } else {
                logger.warning("Invalid selection index: " + indiceSeleccionado + 
                             " (itemsCarrito size: " + itemsCarrito.size() + ")");
                limpiarSeleccion();
            }
        } else {
            
            logger.info("No item selected in cart");
            indiceSeleccionado = -1;
            cantidadActual = 0;
            labelItemSeleccionado.setText("Selecciona un item");
            labelCantidadActual.setText("0");
            actualizarEstadoBotones();
        }
    }

    /**
     * Handles the increase quantity button action.
     * Increases the quantity of the selected item by 1.
     */
    @FXML
    private void aumentarCantidad() {
        logger.info("Increase quantity button clicked");
        
        
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            cantidadActual++;
            logger.info("Increasing quantity to: " + cantidadActual + 
                       " for item at index: " + indiceSeleccionado);
            actualizarItemCantidad();
        } else {
            logger.warning("Attempted to increase quantity without valid selection");
            mostrarAlerta("Seleccionar item", "Por favor, seleccione un item del carrito.");
        }
    }

    /**
     * Handles the decrease quantity button action.
     * Decreases the quantity of the selected item by 1, or removes it if quantity reaches 0.
     */
    @FXML
    private void disminuirCantidad() {
        logger.info("Decrease quantity button clicked");
        
        
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            if (cantidadActual > 1) {
                cantidadActual--;
                logger.info("Decreasing quantity to: " + cantidadActual + 
                           " for item at index: " + indiceSeleccionado);
                actualizarItemCantidad();
            } else if (cantidadActual == 1) {
                logger.info("Quantity is 1 - Asking if user wants to remove item");
                
                
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
            }
        } else {
            logger.warning("Attempted to decrease quantity without valid selection");
            mostrarAlerta("Seleccionar item", "Por favor, seleccione un item del carrito.");
        }
    }

    /**
     * Updates the quantity of the selected item in the cart.
     */
    private void actualizarItemCantidad() {
        
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            try {
                
                CartItem item = itemsCarrito.get(indiceSeleccionado);
                Videogame videojuego = juegosCarrito.get(indiceSeleccionado);
                
                logger.info("Updating quantity - Game: " + videojuego.getName() + 
                           ", Old quantity: " + item.getCantidad() + 
                           ", New quantity: " + cantidadActual);
                
                
                item.setCantidad(cantidadActual);
                
                
                String nuevoItem = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                        profile.getUsername(), videojuego.getName(), cantidadActual, item.getPrecio());
                carritoData.set(indiceSeleccionado, nuevoItem);
                
                
                labelCantidadActual.setText(String.valueOf(cantidadActual));
                actualizarTotales();
                actualizarEstadoBotones();
                
                logger.info("Quantity updated successfully");
                
            } catch (Exception e) {
                logger.severe("Error updating item quantity: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo actualizar la cantidad del item.");
            }
        } else {
            
            logger.warning("Invalid index when updating quantity: " + indiceSeleccionado);
            limpiarSeleccion();
        }
    }

    /**
     * Updates the enabled/disabled state of all buttons based on cart state.
     */
    public void actualizarEstadoBotones() {
        try {
            boolean hayItemSeleccionado = (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size());
            boolean carritoVacio = carritoData.isEmpty();

            
            buttonMas.setDisable(!hayItemSeleccionado);
            buttonMenos.setDisable(!hayItemSeleccionado || cantidadActual <= 1);

            
            buttonEliminar.setDisable(!hayItemSeleccionado);

            
            buttonComprar.setDisable(carritoVacio);
            
            logger.info("Button states updated - Selected: " + hayItemSeleccionado + 
                       ", Cart empty: " + carritoVacio + 
                       ", Current quantity: " + cantidadActual + 
                       ", Items in cart: " + itemsCarrito.size());
            
        } catch (Exception e) {
            logger.severe("Error updating button states: " + e.getMessage());
        }
    }

    /**
     * Handles the delete item button action.
     * Removes the selected item from the cart after confirmation.
     */
    @FXML
    private void eliminarItem() {
        logger.info("Delete item button clicked");
        
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            try {
                CartItem item = itemsCarrito.get(indiceSeleccionado);
                logger.info("Confirming deletion of item at index: " + indiceSeleccionado + 
                           " - Game ID: " + item.getIdVideojuego() + 
                           ", Quantity: " + item.getCantidad());
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar Eliminación");
                alert.setHeaderText("¿Eliminar este item del carrito?");
                alert.setContentText("Esta acción no se puede deshacer.");

                ButtonType buttonTypeSi = new ButtonType("Sí");
                ButtonType buttonTypeNo = new ButtonType("No");
                alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                alert.showAndWait().ifPresent(response -> {
                    if (response == buttonTypeSi) {
                        logger.info("User confirmed item deletion");
                        
                        
                        carritoData.remove(indiceSeleccionado);
                        if (indiceSeleccionado < itemsCarrito.size()) {
                            itemsCarrito.remove(indiceSeleccionado);
                        }
                        if (indiceSeleccionado < juegosCarrito.size()) {
                            juegosCarrito.remove(indiceSeleccionado);
                        }
                        
                        logger.info("Item removed - Remaining items: " + itemsCarrito.size());
                        
                        actualizarTotales();
                        limpiarSeleccion();
                        
                    } else {
                        logger.info("User cancelled item deletion");
                    }
                });
            } catch (Exception e) {
                logger.severe("Error deleting item: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo eliminar el item.");
            }
        } else {
            logger.warning("Attempted to delete item without valid selection");
            mostrarAlerta("Seleccionar item", "Por favor, seleccione un item para eliminar.");
        }
    }

    /**
     * Clears the current selection and resets display fields.
     */
    private void limpiarSeleccion() {
        logger.info("Clearing cart selection");
        
        try {
            listViewCarrito.getSelectionModel().clearSelection();
            indiceSeleccionado = -1;
            cantidadActual = 0;
            labelCantidadActual.setText("0");
            labelItemSeleccionado.setText("Selecciona un item");
            actualizarEstadoBotones();
            
            logger.info("Selection cleared successfully");
            
        } catch (Exception e) {
            logger.severe("Error clearing selection: " + e.getMessage());
        }
    }

    /**
     * Handles the cancel button action.
     * Closes the cart window.
     */
    @FXML
    private void cancelar() {
        logger.info("Cancel button clicked - Closing cart window");
        
        try {
            Stage currentStage = (Stage) buttonEliminar.getScene().getWindow();
            currentStage.close();
            
            logger.info("Cart window closed");
            
        } catch (Exception e) {
            logger.severe("Error closing cart window: " + e.getMessage());
        }
    }

    /**
     * Updates the total items count and total price display.
     */
    public void actualizarTotales() {
        logger.info("Updating cart totals");
        
        try {
            int totalItems = 0;
            double totalPagar = 0.0;

            for (CartItem item : itemsCarrito) {
                totalItems += item.getCantidad();
                totalPagar += item.getPrecio() * item.getCantidad();
            }

            labelTotalItems.setText(String.valueOf(totalItems));
            labelTotalPagar.setText(String.format("$%.2f", totalPagar));
            actualizarEstadoBotones();
            
            logger.info("Cart totals updated - Total items: " + totalItems + 
                       ", Total to pay: $" + totalPagar + 
                       ", Cart items: " + itemsCarrito.size());
            
        } catch (Exception e) {
            logger.severe("Error updating cart totals: " + e.getMessage());
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
     * Handles the buy button action.
     * Processes the purchase of all items in the cart.
     */
    @FXML
    private void comprar() {
        logger.info("Buy button clicked - Items in cart: " + itemsCarrito.size() + 
                   ", Total: " + labelTotalPagar.getText());
        
        if (carritoData.isEmpty()) {
            logger.warning("Attempted to buy with empty cart");
            mostrarAlerta("Carrito vacío", "No hay items en el carrito para comprar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Compra");
        alert.setHeaderText("¿Realizar compra?");
        alert.setContentText("Total a pagar: " + labelTotalPagar.getText() + 
                           "\n\nEsta acción procesará " + itemsCarrito.size() + " items.");

        ButtonType buttonTypeSi = new ButtonType("Sí");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeSi) {
                logger.info("User confirmed purchase - Processing " + itemsCarrito.size() + " items");
                procesarCompra();
            } else {
                logger.info("User cancelled purchase");
            }
        });
    }
    
    /**
     * Processes the purchase of all items in the cart.
     * Creates orders in the database and updates stock levels.
     */
    private void procesarCompra() {
        logger.info("Starting purchase processing for user: " + 
                   (profile != null ? profile.getUsername() : "unknown"));
        
        try {
            
            logger.info("Retrieving user from database: " + profile.getUsername());
            User usuario = cont.getUserByUsername(profile.getUsername());
            if (usuario == null) {
                logger.severe("User not found in database: " + profile.getUsername());
                mostrarAlerta("Error", "Usuario no encontrado en la base de datos.");
                return;
            }
            
            logger.info("User retrieved successfully - User ID: " + usuario.getUserCode());
            
            
            logger.info("Validating stock for " + itemsCarrito.size() + " items");
            if (!validarStockDisponible()) {
                return;
            }
            
            
            boolean exitoTotal = true;
            StringBuilder errores = new StringBuilder();
            int itemsProcesados = 0;
            
            logger.info("Processing " + itemsCarrito.size() + " cart items");
            
            for (int i = 0; i < itemsCarrito.size(); i++) {
                CartItem item = itemsCarrito.get(i);
                Videogame videojuego = juegosCarrito.get(i);
                
                try {
                    logger.info("Processing item " + (i+1) + "/" + itemsCarrito.size() + 
                               " - Game: " + videojuego.getName() + 
                               " x" + item.getCantidad());
                    
                    
                    Order order = new Order(usuario, videojuego, item.getPrecio(), item.getCantidad());
                    
                    
                    logger.info("Creating order in database...");
                    boolean exito = cont.createOrder(order);
                    
                    if (exito) {
                        
                        int nuevoStock = videojuego.getStock() - item.getCantidad();
                        videojuego.setStock(nuevoStock);
                        
                        logger.info("Order created successfully - Updating stock from " + 
                                   videojuego.getStock() + " to " + nuevoStock);
                        
                        
                        cont.modifyGame(videojuego);
                        
                        itemsProcesados++;
                        logger.info("Item processed successfully");
                        
                    } else {
                        exitoTotal = false;
                        String errorMsg = "Error al procesar: " + videojuego.getName();
                        errores.append("- ").append(errorMsg).append("\n");
                        logger.warning(errorMsg);
                    }
                    
                } catch (Exception e) {
                    exitoTotal = false;
                    String errorMsg = "Error: " + e.getMessage() + " for game: " + videojuego.getName();
                    errores.append("- ").append(errorMsg).append("\n");
                    logger.severe(errorMsg);
                    e.printStackTrace();
                }
            }
            
            if (exitoTotal) {
                
                logger.info("Purchase completed successfully - " + itemsProcesados + " items processed");
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Compra Realizada");
                success.setHeaderText("¡Compra exitosa!");
                success.setContentText("Tu compra se ha realizado correctamente.\n" +
                                     "Se procesaron " + itemsCarrito.size() + " items.\n" +
                                     "Total pagado: " + labelTotalPagar.getText());
                success.showAndWait();
                
                
                logger.info("Clearing cart after successful purchase");
                limpiarCarritoCompleto();
                
                
                Stage currentStage = (Stage) buttonComprar.getScene().getWindow();
                currentStage.close();
                
                logger.info("Cart window closed after purchase");
                
            } else {
                logger.warning("Partial purchase - " + itemsProcesados + "/" + itemsCarrito.size() + 
                             " items processed successfully");
                mostrarAlerta("Compra parcial", 
                    "Algunos items no se pudieron procesar:\n" + errores.toString());
            }
            
        } catch (Exception e) {
            logger.severe("Critical error processing purchase: " + e.getMessage());
            mostrarAlerta("Error en la compra", "Ocurrió un error al procesar la compra: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Validates that all items in the cart have sufficient stock.
     *
     * @return true if all items have sufficient stock, false otherwise
     */
    private boolean validarStockDisponible() {
        logger.info("Validating stock availability");
        
        StringBuilder erroresStock = new StringBuilder();
        int itemsConStockInsuficiente = 0;
        
        for (int i = 0; i < itemsCarrito.size(); i++) {
            CartItem item = itemsCarrito.get(i);
            Videogame videojuego = juegosCarrito.get(i);
            
            if (item.getCantidad() > videojuego.getStock()) {
                itemsConStockInsuficiente++;
                String errorMsg = videojuego.getName() + 
                                ": Stock disponible: " + videojuego.getStock() + 
                                ", Cantidad solicitada: " + item.getCantidad();
                erroresStock.append("- ").append(errorMsg).append("\n");
                logger.warning("Insufficient stock - " + errorMsg);
            }
        }
        
        if (erroresStock.length() > 0) {
            logger.warning("Stock validation failed - " + itemsConStockInsuficiente + 
                          " items with insufficient stock");
            mostrarAlerta("Stock insuficiente", 
                "Los siguientes items no tienen stock suficiente:\n\n" + erroresStock.toString());
            return false;
        }
        
        logger.info("Stock validation passed - All items have sufficient stock");
        return true;
    }
    
    /**
     * Clears all items from the shopping cart.
     */
    private void limpiarCarritoCompleto() {
        logger.info("Clearing entire cart");
        
        try {
            int itemsCount = itemsCarrito.size();
            carritoData.clear();
            itemsCarrito.clear();
            juegosCarrito.clear();
            limpiarSeleccion();
            actualizarTotales();
            
            logger.info("Cart cleared - Removed " + itemsCount + " items");
            
        } catch (Exception e) {
            logger.severe("Error clearing cart: " + e.getMessage());
        }
    }

    /**
     * Sets the stage for this controller.
     *
     * @param stage The stage to set
     */
    public void setStage(Stage stage) {
        logger.info("Setting stage in CartController");
        this.stage = stage;
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
}