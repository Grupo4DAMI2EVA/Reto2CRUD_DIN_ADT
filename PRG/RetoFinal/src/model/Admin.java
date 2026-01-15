package model;
import javax.persistence.*;

@Entity
@Table(name = "ADMIN_")
@PrimaryKeyJoinColumn(name = "user_code", referencedColumnName = "user_code")  // CAMBIADO
public class Admin extends Profile {
    
    @Column(name = "current_account", length = 40)
    private String currentAccount;

    public Admin() {
        super();
        this.currentAccount = "";
    }

    // Constructor SIN userCode
    public Admin(String currentAccount, String username, String password, 
                 String email, String name, String telephone, String surname) {  // SIN userCode
        super(username, password, email, name, telephone, surname);
        this.currentAccount = currentAccount;
    }

    public String getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
    }

    @Override
    public String toString() {
        return "Admin{" + 
               "username=" + getUsername() + 
               ", currentAccount=" + currentAccount + 
               '}';
    }
}