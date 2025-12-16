package model;
import javax.persistence.*;
/**
 * Represents an admin profile in the system. Extends Profile and adds the currentAccount attribute.
 */
@Entity
@Table(name="ADMIN_")
@PrimaryKeyJoinColumn(name="username", referencedColumnName="username")
public class Admin extends Profile {
    @Column(name="current_account", length=40)
    private String currentAccount;

    public Admin(String currentAccount, String username, String password, String email, int userCode, String name, String telephone, String surname) {
        super(username, password, email, userCode, name, telephone, surname);
        this.currentAccount = currentAccount;
    }

    public Admin() {
        this.currentAccount = "";
    }

    public String getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
    }

    @Override
    public void logIn() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "Admin{" + "currentAccount=" + currentAccount + '}';
    }
}
