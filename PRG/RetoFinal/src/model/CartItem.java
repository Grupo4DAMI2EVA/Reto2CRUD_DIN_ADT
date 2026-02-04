package model;

import javafx.beans.property.*;

/**
 * Represents an item in the shopping cart. Uses JavaFX properties for data
 * binding with UI components.
 *
 * @author Igor
 * @version 1.0
 */
public class CartItem {

    private final IntegerProperty idUsuario;

    private final IntegerProperty idVideojuego;

    private final IntegerProperty cantidad;

    private final DoubleProperty precio;

    /**
     * Creates a new cart item.
     *
     * @param idUsuario User ID
     * @param idVideojuego Video game ID
     * @param cantidad Quantity
     * @param precio Unit price
     */
    public CartItem(int idUsuario, int idVideojuego, int cantidad, double precio) {
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.idVideojuego = new SimpleIntegerProperty(idVideojuego);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
    }

    // Getters y Setters
    /**
     * Gets the user ID.
     *
     * @return User ID
     */
    public int getIdUsuario() {
        return idUsuario.get();
    }

    /**
     * Sets the user ID.
     *
     * @param value New user ID
     */
    public void setIdUsuario(int value) {
        idUsuario.set(value);
    }

    /**
     * Gets the user ID property for data binding.
     *
     * @return User ID property
     */
    public IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    /**
     * Gets the video game ID.
     *
     * @return Video game ID
     */
    public int getIdVideojuego() {
        return idVideojuego.get();
    }

    /**
     * Sets the video game ID.
     *
     * @param value New video game ID
     */
    public void setIdVideojuego(int value) {
        idVideojuego.set(value);
    }

    /**
     * Gets the video game ID property for data binding.
     *
     * @return Video game ID property
     */
    public IntegerProperty idVideojuegoProperty() {
        return idVideojuego;
    }

    /**
     * Gets the quantity.
     *
     * @return Quantity
     */
    public int getCantidad() {
        return cantidad.get();
    }

    /**
     * Sets the quantity.
     *
     * @param value New quantity
     */
    public void setCantidad(int value) {
        cantidad.set(value);
    }

    /**
     * Gets the quantity property for data binding.
     *
     * @return Quantity property
     */
    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    /**
     * Gets the unit price.
     *
     * @return Unit price
     */
    public double getPrecio() {
        return precio.get();
    }

    /**
     * Sets the unit price.
     *
     * @param value New unit price
     */
    public void setPrecio(double value) {
        precio.set(value);
    }

    /**
     * Gets the price property for data binding.
     *
     * @return Price property
     */
    public DoubleProperty precioProperty() {
        return precio;
    }
}
