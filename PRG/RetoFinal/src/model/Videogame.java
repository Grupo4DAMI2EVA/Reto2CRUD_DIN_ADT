package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a video game in the system.
 * Maps to the "VIDEOGAME_" table in the database.
 * Contains game information and relationships with orders and reviews.
 * 
 * @author Igor
 * @version 1.0
 */
@Entity
@Table(name = "VIDEOGAME_")
public class Videogame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "videogame_code")
    private int idVideogame;

    @Column(name = "company_name")
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private GameGenre gameGenre;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platforms;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pegi")
    private PEGI pegi;

    @Column(name = "price")
    private double price;

    @Column(name = "stock")
    private int stock;

    @Temporal(TemporalType.DATE)
    @Column(name = "release_date")
    private Date releaseDate;

    @OneToMany(mappedBy = "videogame", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "videogame", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public Videogame() {
    }

    /**
     * Creates a new video game with specified details.
     */
    public Videogame(String companyName, GameGenre gameGenre, String name, 
                    Platform platforms, PEGI pegi, double price, int stock, Date releaseDate) {
        this.companyName = companyName;
        this.gameGenre = gameGenre;
        this.name = name;
        this.platforms = platforms;
        this.pegi = pegi;
        this.price = price;
        this.stock = stock;
        this.releaseDate = releaseDate;
    }

    /**
     * Gets the unique video game ID.
     */
    public int getIdVideogame() {
        return idVideogame;
    }

    /**
     * Sets the video game ID.
     */
    public void setIdVideogame(int idVideogame) {
        this.idVideogame = idVideogame;
    }

    /**
     * Gets the company name.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the company name.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Gets the game genre.
     */
    public GameGenre getGameGenre() {
        return gameGenre;
    }

    /**
     * Sets the game genre.
     */
    public void setGameGenre(GameGenre gameGenre) {
        this.gameGenre = gameGenre;
    }

    /**
     * Gets the game name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the game name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the platform(s).
     */
    public Platform getPlatforms() {
        return platforms;
    }

    /**
     * Sets the platform(s).
     */
    public void setPlatforms(Platform platforms) {
        this.platforms = platforms;
    }

    /**
     * Gets the PEGI rating.
     */
    public PEGI getPegi() {
        return pegi;
    }

    /**
     * Sets the PEGI rating.
     */
    public void setPegi(PEGI pegi) {
        this.pegi = pegi;
    }

    /**
     * Gets the price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the stock quantity.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets the stock quantity.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Gets the release date.
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the release date.
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Gets the list of orders for this game.
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Sets the list of orders.
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Gets the list of reviews for this game.
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Sets the list of reviews.
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     * Adds an order to this video game.
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setVideogame(this);
    }

    /**
     * Adds a review to this video game.
     */
    public void addReview(Review review) {
        reviews.add(review);
        review.setVideogame(this);
    }

    /**
     * Returns a string representation of the video game.
     */
    @Override
    public String toString() {
        return "Videogame{" + "idVideogame=" + idVideogame + ", name=" + name + 
               ", price=" + price + ", stock=" + stock + '}';
    }
}