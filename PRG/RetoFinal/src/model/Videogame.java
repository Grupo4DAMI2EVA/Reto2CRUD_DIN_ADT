package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Column(name = "pegi")
    private int pegi;

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

    public Videogame() {
    }

    public Videogame(String companyName, GameGenre gameGenre, String name, Platform platforms, int pegi, double price, int stock, Date releaseDate) {
        this.companyName = companyName;
        this.gameGenre = gameGenre;
        this.name = name;
        this.platforms = platforms;
        this.pegi = pegi;
        this.price = price;
        this.stock = stock;
        this.releaseDate = releaseDate;
    }

    // Getters y setters
    public int getIdVideogame() {
        return idVideogame;
    }

    public void setIdVideogame(int idVideogame) {
        this.idVideogame = idVideogame;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public GameGenre getGameGenre() {
        return gameGenre;
    }

    public void setGameGenre(GameGenre gameGenre) {
        this.gameGenre = gameGenre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Platform getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Platform platforms) {
        this.platforms = platforms;
    }

    public int getPegi() {
        return pegi;
    }

    public void setPegi(int pegi) {
        this.pegi = pegi;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    // Métodos helper
    public void addOrder(Order order) {
        orders.add(order);
        order.setVideogame(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setVideogame(this);
    }

    @Override
    public String toString() {
        return "Videogame{" + "idVideogame=" + idVideogame + ", name=" + name + ", price=" + price + ", stock=" + stock + '}';
    }
}