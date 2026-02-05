package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import java.util.List;

public class CartController {

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

    @FXML
    private void initialize() {
        // Inicializar las listas en initialize() que se llama automáticamente
        carritoData = FXCollections.observableArrayList();
        itemsCarrito = FXCollections.observableArrayList();
        juegosCarrito = FXCollections.observableArrayList();
        listViewCarrito.setItems(carritoData);

        // Configurar listener para selección
        listViewCarrito.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetalleItem(newValue));
    }
    
    public void setup() {
        // Si necesitas hacer algo adicional después de initialize()
        actualizarEstadoBotones();
    }
    
    public void setUsuario(Profile profile) {
        this.profile = profile;
    }
    
    public void setCont(Controller cont) {
        this.cont = cont;
        if (cont != null) {
            this.todosLosJuegos = cont.getAllGames();
        }
    }
    
    // Método para agregar items al carrito
    public void agregarItemCarrito(String usuario, Videogame videojuego, int cantidad, double precio) {
        // Verificar si el juego ya está en el carrito
        for (int i = 0; i < itemsCarrito.size(); i++) {
            CartItem item = itemsCarrito.get(i);
            if (item.getIdVideojuego() == videojuego.getIdVideogame()) {
                // Si ya existe, actualizar cantidad
                int nuevaCantidad = item.getCantidad() + cantidad;
                item.setCantidad(nuevaCantidad);
                
                // Actualizar la vista
                String itemActualizado = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                        usuario, videojuego.getName(), nuevaCantidad, precio);
                carritoData.set(i, itemActualizado);
                
                actualizarTotales();
                return;
            }
        }
        
        // Si no existe, agregar nuevo item
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
        
        actualizarTotales();
        actualizarEstadoBotones();
    }
    
    // Método de compatibilidad
    public void agregarItemCarrito(String usuario, String videojuegoNombre, int cantidad, double precio) {
        String item = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                usuario, videojuegoNombre, cantidad, precio);
        carritoData.add(item);
        actualizarTotales();
        actualizarEstadoBotones();
    }

    private void mostrarDetalleItem(String item) {
        if (item != null) {
            // Obtener el índice de la selección actual
            indiceSeleccionado = listViewCarrito.getSelectionModel().getSelectedIndex();
            
            // Verificar que el índice sea válido
            if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
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

                    // Habilitar botones
                    actualizarEstadoBotones();
                }
            }
        } else {
            // Si no hay item seleccionado
            indiceSeleccionado = -1;
            cantidadActual = 0;
            labelItemSeleccionado.setText("Selecciona un item");
            labelCantidadActual.setText("0");
            actualizarEstadoBotones();
        }
    }

    @FXML
    private void aumentarCantidad() {
        // Verificar que haya un item seleccionado válido
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            cantidadActual++;
            actualizarItemCantidad();
        } else {
            mostrarAlerta("Seleccionar item", "Por favor, seleccione un item del carrito.");
        }
    }

    @FXML
    private void disminuirCantidad() {
        // Verificar que haya un item seleccionado válido
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            if (cantidadActual > 1) {
                cantidadActual--;
                actualizarItemCantidad();
            } else if (cantidadActual == 1) {
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
                        eliminarItem();
                    }
                });
            }
        } else {
            mostrarAlerta("Seleccionar item", "Por favor, seleccione un item del carrito.");
        }
    }

    private void actualizarItemCantidad() {
        // Verificar que el índice sea válido
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            // Obtener el item seleccionado
            CartItem item = itemsCarrito.get(indiceSeleccionado);
            Videogame videojuego = juegosCarrito.get(indiceSeleccionado);
            
            // Actualizar la cantidad en el item
            item.setCantidad(cantidadActual);
            
            // Actualizar la vista
            String nuevoItem = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                    profile.getUsername(), videojuego.getName(), cantidadActual, item.getPrecio());
            carritoData.set(indiceSeleccionado, nuevoItem);
            
            // Actualizar la interfaz
            labelCantidadActual.setText(String.valueOf(cantidadActual));
            actualizarTotales();
            actualizarEstadoBotones();
        } else {
            // Si el índice no es válido, limpiar la selección
            limpiarSeleccion();
        }
    }

    public void actualizarEstadoBotones() {
        boolean hayItemSeleccionado = (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size());
        boolean carritoVacio = carritoData.isEmpty();

        // Habilitar/deshabilitar botones + y -
        buttonMas.setDisable(!hayItemSeleccionado);
        buttonMenos.setDisable(!hayItemSeleccionado || cantidadActual <= 1);

        // Habilitar/deshabilitar botón eliminar
        buttonEliminar.setDisable(!hayItemSeleccionado);

        // Habilitar/deshabilitar botón comprar
        buttonComprar.setDisable(carritoVacio);
    }

    @FXML
    private void eliminarItem() {
        if (indiceSeleccionado >= 0 && indiceSeleccionado < itemsCarrito.size()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Eliminar este item del carrito?");
            alert.setContentText("Esta acción no se puede deshacer.");

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    // Eliminar de todas las listas
                    carritoData.remove(indiceSeleccionado);
                    if (indiceSeleccionado < itemsCarrito.size()) {
                        itemsCarrito.remove(indiceSeleccionado);
                    }
                    if (indiceSeleccionado < juegosCarrito.size()) {
                        juegosCarrito.remove(indiceSeleccionado);
                    }
                    actualizarTotales();
                    limpiarSeleccion();
                }
            });
        } else {
            mostrarAlerta("Seleccionar item", "Por favor, seleccione un item para eliminar.");
        }
    }

    private void limpiarSeleccion() {
        listViewCarrito.getSelectionModel().clearSelection();
        indiceSeleccionado = -1;
        cantidadActual = 0;
        labelCantidadActual.setText("0");
        labelItemSeleccionado.setText("Selecciona un item");
        actualizarEstadoBotones();
    }

    @FXML
    private void cancelar() {
        Stage currentStage = (Stage) buttonEliminar.getScene().getWindow();
        currentStage.close();
    }

    public void actualizarTotales() {
        int totalItems = 0;
        double totalPagar = 0.0;

        for (CartItem item : itemsCarrito) {
            totalItems += item.getCantidad();
            totalPagar += item.getPrecio() * item.getCantidad();
        }

        labelTotalItems.setText(String.valueOf(totalItems));
        labelTotalPagar.setText(String.format("$%.2f", totalPagar));
        actualizarEstadoBotones();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void comprar() {
        if (carritoData.isEmpty()) {
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
                procesarCompra();
            }
        });
    }
    
    private void procesarCompra() {
        try {
            // 1. Obtener el usuario completo de la base de datos
            User usuario = cont.getUserByUsername(profile.getUsername());
            if (usuario == null) {
                mostrarAlerta("Error", "Usuario no encontrado en la base de datos.");
                return;
            }
            
            // 2. Validar stock antes de procesar
            if (!validarStockDisponible()) {
                return;
            }
            
            // 3. Procesar cada item del carrito
            boolean exitoTotal = true;
            StringBuilder errores = new StringBuilder();
            
            for (int i = 0; i < itemsCarrito.size(); i++) {
                CartItem item = itemsCarrito.get(i);
                Videogame videojuego = juegosCarrito.get(i);
                
                try {
                    // Crear el Order para la base de datos
                    Order order = new Order(usuario, videojuego, item.getPrecio(), item.getCantidad());
                    
                    // Guardar en la base de datos
                    boolean exito = cont.createOrder(order);
                    
                    if (exito) {
                        // Actualizar stock del videojuego
                        int nuevoStock = videojuego.getStock() - item.getCantidad();
                        videojuego.setStock(nuevoStock);
                        
                        // Actualizar en la base de datos
                        cont.modifyGame(videojuego);
                        
                        System.out.println("Compra procesada: " + videojuego.getName() + 
                                         " x" + item.getCantidad());
                    } else {
                        exitoTotal = false;
                        errores.append("- Error al procesar: ").append(videojuego.getName()).append("\n");
                    }
                    
                } catch (Exception e) {
                    exitoTotal = false;
                    errores.append("- Error: ").append(e.getMessage()).append("\n");
                    e.printStackTrace();
                }
            }
            
            if (exitoTotal) {
                // Mostrar éxito
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Compra Realizada");
                success.setHeaderText("¡Compra exitosa!");
                success.setContentText("Tu compra se ha realizado correctamente.\n" +
                                     "Se procesaron " + itemsCarrito.size() + " items.\n" +
                                     "Total pagado: " + labelTotalPagar.getText());
                success.showAndWait();
                
                // Limpiar carrito
                limpiarCarritoCompleto();
                
                // Cerrar ventana
                Stage currentStage = (Stage) buttonComprar.getScene().getWindow();
                currentStage.close();
            } else {
                mostrarAlerta("Compra parcial", 
                    "Algunos items no se pudieron procesar:\n" + errores.toString());
            }
            
        } catch (Exception e) {
            mostrarAlerta("Error en la compra", "Ocurrió un error al procesar la compra: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validarStockDisponible() {
        StringBuilder erroresStock = new StringBuilder();
        
        for (int i = 0; i < itemsCarrito.size(); i++) {
            CartItem item = itemsCarrito.get(i);
            Videogame videojuego = juegosCarrito.get(i);
            
            if (item.getCantidad() > videojuego.getStock()) {
                erroresStock.append("- ").append(videojuego.getName())
                           .append(": Stock disponible: ").append(videojuego.getStock())
                           .append(", Cantidad solicitada: ").append(item.getCantidad())
                           .append("\n");
            }
        }
        
        if (erroresStock.length() > 0) {
            mostrarAlerta("Stock insuficiente", 
                "Los siguientes items no tienen stock suficiente:\n\n" + erroresStock.toString());
            return false;
        }
        
        return true;
    }
    
    private void limpiarCarritoCompleto() {
        carritoData.clear();
        itemsCarrito.clear();
        juegosCarrito.clear();
        limpiarSeleccion();
        actualizarTotales();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}