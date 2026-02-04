package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Represents an order in the system.
 * Maps to the "Order_" table in the database.
 * Contains information about purchases made by users.
 * 
 * @author TuNombre
 * @version 1.0
 */
@Entity
@Table(name = "Order_")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_code")
    private int orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, referencedColumnName = "user_code")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_videogame", nullable = false)
    private Videogame videogame;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    /**
     * Default constructor required by JPA.
     */
    public Order() {
    }

    /**
     * Creates a new order with specified details.
     */
    public Order(User user, Videogame videogame, double price, int quantity) {
        this.user = user;
        this.videogame = videogame;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Gets the order code (primary key).
     */
    public int getOrderCode() {
        return orderCode;
    }

    /**
     * Sets the order code.
     */
    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    /**
     * Gets the user who placed the order.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who placed the order.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the video game being ordered.
     */
    public Videogame getVideogame() {
        return videogame;
    }

    /**
     * Sets the video game being ordered.
     */
    public void setVideogame(Videogame videogame) {
        this.videogame = videogame;
    }

    /**
     * Gets the price per unit.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price per unit.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the quantity ordered.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity ordered.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns a string representation of the order.
     */
    @Override
    public String toString() {
        return "Order{" + "orderCode=" + orderCode + ", price=" + price + ", quantity=" + quantity + '}';
    }
}