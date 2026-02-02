package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
    private int indiceSeleccionado = -1;
    private int cantidadActual = 0;

    private void initialize() {
        
    }
    
    public void setup() {
        // Inicializar la lista de items del carrito
        carritoData = FXCollections.observableArrayList();
        listViewCarrito.setItems(carritoData);

        // Configurar selección de item
        listViewCarrito.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetalleItem(newValue));
    }


    private void mostrarDetalleItem(String item) {
        if (item != null) {
            indiceSeleccionado = listViewCarrito.getSelectionModel().getSelectedIndex();

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
        } else {
            // Si no hay item seleccionado
            indiceSeleccionado = -1;
            labelItemSeleccionado.setText("Selecciona un item");
            labelCantidadActual.setText("0");
            actualizarEstadoBotones();
        }
    }

    @FXML
    private void aumentarCantidad() {
        if (indiceSeleccionado >= 0) {
            cantidadActual++;
            actualizarItemCantidad();
        }
    }

    @FXML
    private void disminuirCantidad() {
        if (indiceSeleccionado >= 0 && cantidadActual > 1) {
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
    }

    @FXML
    private void comprar() {
        // Lógica para realizar la compra
        if (carritoData.isEmpty()) {
            mostrarAlerta("Carrito vacío", "No hay items en el carrito para comprar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Compra");
        alert.setHeaderText("¿Realizar compra?");
        alert.setContentText("Total a pagar: " + labelTotalPagar.getText());

        ButtonType buttonTypeSi = new ButtonType("Sí");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeSi) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Compra Realizada");
                success.setHeaderText("¡Compra exitosa!");
                success.setContentText("Tu compra se ha realizado correctamente.");
                success.showAndWait();

                Stage currentStage = (Stage) buttonComprar.getScene().getWindow();
                currentStage.close();
            }
        });
    }

    @FXML
    private void eliminarItem() {
        if (indiceSeleccionado >= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Eliminar este item del carrito?");
            alert.setContentText("Esta acción no se puede deshacer.");

            ButtonType buttonTypeSi = new ButtonType("Sí");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeSi) {
                    carritoData.remove(indiceSeleccionado);
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
        stage = (Stage) buttonEliminar.getScene().getWindow();
        stage.close();
    }

    public void actualizarTotales() {
        int totalItems = 0;
        double totalPagar = 0.0;

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
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Error procesando item: " + item);
                e.printStackTrace(); // Añadir stack trace para mejor depuración
            }
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

    // Método para agregar items desde otras partes de la aplicación
    public void agregarItemCarrito(String usuario, String videojuego, int cantidad, double precio) {
        String item = String.format("Usuario: %s | Videojuego: %s | Cantidad: %d | Precio: $%.2f",
                usuario, videojuego, cantidad, precio);
        carritoData.add(item);
        actualizarTotales();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
