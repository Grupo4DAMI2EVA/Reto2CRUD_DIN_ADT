package model;
import javax.persistence.*;

/**
 * Represents a standard user in the system.
 * Extends Profile and adds gender and card number attributes.
 */
@Entity
@Table(name="USER_")
@PrimaryKeyJoinColumn(name="username", referencedColumnName="username")
public class User extends Profile {
    @Column(name="gender",length=50)
    private String gender;
    @Column(name="card_number", length=24)
    private String cardNumber;
    

    public User(String gender, String cardNumber, String username, String password, String email, int userCode, String name, String telephone, String surname) {
        super(username, password, email, userCode, name, telephone, surname);
        this.gender = gender;
        this.cardNumber = cardNumber;
    }

    public User() {
        super();
        this.gender = "";
        this.cardNumber = "";
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public void logIn() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "User{" + "gender=" + gender + ", cardNumber=" + cardNumber + '}';
    }
}
