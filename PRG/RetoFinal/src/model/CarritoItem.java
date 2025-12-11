package model;

import javafx.beans.property.*;

public class CarritoItem {
    private final IntegerProperty idUsuario;
    private final IntegerProperty idVideojuego;
    private final IntegerProperty cantidad;
    private final DoubleProperty precio;
    
    public CarritoItem(int idUsuario, int idVideojuego, int cantidad, double precio) {
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.idVideojuego = new SimpleIntegerProperty(idVideojuego);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
    }
    
    // Getters y Setters
    public int getIdUsuario() { return idUsuario.get(); }
    public void setIdUsuario(int value) { idUsuario.set(value); }
    public IntegerProperty idUsuarioProperty() { return idUsuario; }
    
    public int getIdVideojuego() { return idVideojuego.get(); }
    public void setIdVideojuego(int value) { idVideojuego.set(value); }
    public IntegerProperty idVideojuegoProperty() { return idVideojuego; }
    
    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int value) { cantidad.set(value); }
    public IntegerProperty cantidadProperty() { return cantidad; }
    
    public double getPrecio() { return precio.get(); }
    public void setPrecio(double value) { precio.set(value); }
    public DoubleProperty precioProperty() { return precio; }
}