package model;

import java.util.ArrayList;
import java.util.Date;

public class Videogame {
    
    private int idVideogame;
    private String companyName;
    private Enum gameGenre;
    private String name;
    private Enum platforms;
    private int pegi;
    private double price;
    private int stock;
    private Date releaseDate;
    private ArrayList<Review> reviews;

    public Videogame(int idVideogame, String companyName, Enum gameGenre, String name, Enum platforms, int pegi, double price, int stock, Date releaseDate, ArrayList<Review> reviews) {
        this.idVideogame = idVideogame;
        this.companyName = companyName;
        this.gameGenre = gameGenre;
        this.name = name;
        this.platforms = platforms;
        this.pegi = pegi;
        this.price = price;
        this.stock = stock;
        this.releaseDate = releaseDate;
        this.reviews = reviews;
    }
    
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

    public Enum getGameGenre() {
        return gameGenre;
    }

    public void setGameGenre(Enum gameGenre) {
        this.gameGenre = gameGenre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Enum getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Enum platforms) {
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

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Videogame{" + "idVideogame=" + idVideogame + ", companyName=" + companyName + ", gameGenre=" + gameGenre + ", name=" + name + ", platforms=" + platforms + ", pegi=" + pegi + ", price=" + price + ", stock=" + stock + ", releaseDate=" + releaseDate + ", reviews=" + reviews + '}';
    }
}
