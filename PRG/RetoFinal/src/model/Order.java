package model;

public class Order {
    
    private int idUser;
    private int idVideogame;
    private int idOrder;
    private double price;
    private int quantity;

    public Order(int idUser, int idVideogame, int idOrder, double price, int quantity) {
        this.idUser = idUser;
        this.idVideogame = idVideogame;
        this.idOrder = idOrder;
        this.price = price;
        this.quantity = quantity;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdVideogame() {
        return idVideogame;
    }

    public void setIdVideogame(int idVideogame) {
        this.idVideogame = idVideogame;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order{" + "idUser=" + idUser + ", idVideogame=" + idVideogame + ", idOrder=" + idOrder + ", price=" + price + ", quantity=" + quantity + '}';
    }
}
