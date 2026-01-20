package model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "REVIEW_")
public class Review implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_code")
    private int reviewCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, referencedColumnName = "user_code")  // CAMBIADO
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_videogame", nullable = false)
    private Videogame videogame;

    @Column(name = "rating")
    private double rating;

    @Column(name = "comment", length = 500)
    private String comment;

    public Review() {
    }

    public Review(User user, Videogame videogame, double rating, String comment) {
        this.user = user;
        this.videogame = videogame;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters y setters
    public int getReviewCode() {
        return reviewCode;
    }

    public void setReviewCode(int reviewCode) {
        this.reviewCode = reviewCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Videogame getVideogame() {
        return videogame;
    }

    public void setVideogame(Videogame videogame) {
        this.videogame = videogame;
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
        return "Review{" + "rating=" + rating + ", comment=" + comment + '}';
    }
}