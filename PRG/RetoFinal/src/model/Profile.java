package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Abstract class representing a general profile in the system. 
 * Contains common attributes such as username, password, email, and personal information. 
 * All profile types (User, Admin) extend this class.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)  // Herencia con JOINED
@Table(name = "PROFILE_")
public abstract class Profile implements Serializable {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_code")
    private int userCode;  // PK autoincremental
   
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;  // Único, no PK
   
    @Column(name = "password", nullable = false, length = 50)
    private String password;
   
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;
    
    @Column(name = "name", length = 50)
    private String name;
    
    @Column(name = "telephone", nullable = false, length = 50)
    private String telephone;
    
    @Column(name = "surname", nullable = false, length = 50)
    private String surname;

    // Constructor sin user_code (se genera automático)
    public Profile() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.name = "";
        this.telephone = "";
        this.surname = "";
    }

    public Profile(String username, String password, String email, 
                   String name, String telephone, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.telephone = telephone;
        this.surname = surname;
    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "Profile{" + 
               "username=" + username + 
               ", password=" + password + 
               ", email=" + email + 
               ", userCode=" + userCode + 
               ", name=" + name + 
               ", telephone=" + telephone + 
               ", surname=" + surname + '}';
    }
}