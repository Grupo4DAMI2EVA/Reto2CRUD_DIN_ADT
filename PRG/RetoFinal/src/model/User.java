package model;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "USER_")
@PrimaryKeyJoinColumn(name = "user_code", referencedColumnName = "user_code")  // CAMBIADO
public class User extends Profile {
    
    @Column(name = "gender", length = 50)
    private String gender;
    
    @Column(name = "card_number", length = 24)
    private String cardNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @Transient
    private ArrayList<CartItem> shoppingCart;

    // Constructor SIN userCode (se genera automático)
    public User() {
        super();
        this.gender = "";
        this.cardNumber = "";
    }

    public User(String gender, String cardNumber, String username, String password, 
                String email, String name, String telephone, String surname) {  // SIN userCode
        super(username, password, email, name, telephone, surname);  // Llama al constructor sin userCode
        this.gender = gender;
        this.cardNumber = cardNumber;
    }

    // Getters y setters (sin cambios)
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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
        order.setUser(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setUser(this);
    }

    @Override
    public String toString() {
        return "User{" + 
               "username=" + getUsername() + 
               ", gender=" + gender + 
               ", cardNumber=" + cardNumber + 
               '}';
    }
}