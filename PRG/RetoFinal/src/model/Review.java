package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Represents a review made by a user for a video game.
 * Maps to the "REVIEW_" table in the database.
 * 
 * @author Igor
 * @version 1.0
 */
@Entity
@Table(name = "REVIEW_")
public class Review implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_code")
    private int reviewCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, referencedColumnName = "user_code")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_videogame", nullable = false)
    private Videogame videogame;

    @Column(name = "rating")
    private double rating;

    @Column(name = "comment", length = 500)
    private String comment;

    /**
     * Default constructor required by JPA.
     */
    public Review() {
    }

    /**
     * Creates a new review with specified details.
     */
    public Review(User user, Videogame videogame, double rating, String comment) {
        this.user = user;
        this.videogame = videogame;
        this.rating = rating;
        this.comment = comment;
    }

    /**
     * Gets the unique review code.
     */
    public int getReviewCode() {
        return reviewCode;
    }

    /**
     * Sets the review code.
     */
    public void setReviewCode(int reviewCode) {
        this.reviewCode = reviewCode;
    }

    /**
     * Gets the user who wrote the review.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who wrote the review.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the video game being reviewed.
     */
    public Videogame getVideogame() {
        return videogame;
    }

    /**
     * Sets the video game being reviewed.
     */
    public void setVideogame(Videogame videogame) {
        this.videogame = videogame;
    }

    /**
     * Gets the rating value (typically 0-5 or 0-10).
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the rating value.
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Gets the review comment text.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the review comment text.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns a string representation of the review.
     */
    @Override
    public String toString() {
        return "Review{" + "rating=" + rating + ", comment=" + comment + '}';
    }
}