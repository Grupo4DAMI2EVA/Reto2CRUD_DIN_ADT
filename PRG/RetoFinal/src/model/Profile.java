package model;
import javax.persistence.*;
/**
 * Abstract class representing a general profile in the system. Contains common attributes such as username, password, email, and personal information. All profile types (User, Admin) extend this class.
 *
 * @author acer
 */
@MappedSuperclass
public abstract class Profile {
   @Id
   @Column(name="username",nullable=false,length=50) 
    private String username;
   @Column(name="password",nullable=false,length=50)
    private String password;
   @Column(name="email",nullable=false,unique=true,length=50)
    private String email;
    
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_code")
    private int userCode;
    @Column(name="name",length=50)
    private String name;
    @Column(name="telephone",nullable=false,length=50)
    private String telephone;
    @Column(name="surname",nullable=false,length=50)
    private String surname;

    /**
     * Constructs a profile with the specified attributes.
     *
     * @param username
     * @param password
     * @param email
     * @param userCode
     * @param name
     * @param telephone
     * @param surname
     */
    public Profile(String username, String password, String email, int userCode, String name, String telephone, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userCode = userCode;
        this.name = name;
        this.telephone = telephone;
        this.surname = surname;
    }

    /**
     * Default constructor initializing attributes with default values.
     */
    public Profile() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.userCode = 0;
        this.name = "";
        this.telephone = "";
        this.surname = "";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getUserCode() {
        return userCode;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getSurname() {
        return surname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "Profile{" + "username=" + username + ", password=" + password + ", email=" + email
                + ", userCode=" + userCode + ", name=" + name + ", telephone=" + telephone + ", surname=" + surname + '}';
    }

    /**
     * Performs login logic for the profile. Must be implemented by subclasses.
     */
    public abstract void logIn();
}
