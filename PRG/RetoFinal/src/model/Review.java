package model;

public class Review {

    private int idUser;
    private int idVideogame;
    private double rating;
    private String comment;

    public Review(int idUser, int idVideogame, double rating, String comment) {
        this.idUser = idUser;
        this.idVideogame = idVideogame;
        this.rating = rating;
        this.comment = comment;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Review{" + "idUser=" + idUser + ", idVideogame=" + idVideogame + ", rating=" + rating + ", comment=" + comment + '}';
    }
}
