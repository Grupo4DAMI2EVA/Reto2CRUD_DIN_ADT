package controller;

import java.util.Date;
import java.util.List;
import model.*;

/**
 * Controller class that handles interaction between the GUI and the database.
 * Provides login, signup, deletion, modification, and data retrieval methods.
 *
 * Author: acer
 */
public class Controller {

    ClassDAO dao = new DBImplementation();

    /**
     * Attempts to log in a user or admin.
     *
     * @param username The username
     * @param password The password
     * @return Profile object if login succeeds, null otherwise
     */
    public Profile logIn(String username, String password) {
        return dao.logIn(username, password);
    }

    /**
     * Signs up a new user.
     *
     * @param gender
     * @param cardNumber
     * @param password
     * @param username
     * @param email
     * @param name
     * @param telephone
     * @param surname
     * @return true if signup succeeds, false otherwise
     */
    public boolean signUp(String gender, String cardNumber, String username, String password, String email,
            String name, String telephone, String surname) {
        return dao.signUp(gender, cardNumber, username, password, email, name, telephone, surname);
    }

    /**
     * Deletes a user account.
     *
     * @param username
     * @param password
     * @return
     */
    public boolean dropOutUser(String username, String password) {
        return dao.dropOutUser(username, password);
    }

    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) {
        return dao.dropOutAdmin(usernameToDelete, adminUsername, adminPassword);
    }

    /**
     * Modifies user information.
     *
     * @param password
     * @param email
     * @param name
     * @param telephone
     * @param surname
     * @param username
     * @param gender
     * @return
     */
    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) {
        return dao.modificarUser(password, email, name, telephone, surname, username, gender);
    }

    /**
     * Retrieves a list of usernames for GUI combo boxes.
     *
     * @return
     */
    public List comboBoxInsert() {
        return dao.comboBoxInsert();
    }

    public boolean userExists(String username) {
        return dao.userExists(username);
    }

    public User getUserByUsername(String username) {
        return dao.getUserByUsername(username);
    }

    public boolean addGame(String companyName, GameGenre gameGenre, String name, Platform platforms, PEGI pegi, double price, int stock, Date releaseDate) {
        return dao.addGame(companyName, gameGenre, name, platforms, pegi, price, stock, releaseDate);
    }

    public boolean modifyGame(Videogame game) {
        return dao.modifyGame(game);
    }

    public boolean deleteGame(Videogame game) {
        return dao.deleteGame(game);
    }

    public boolean addToCart(Videogame game) {
        return dao.addToCart(game);
    }

    public List<Videogame> getAllGames() {
        return dao.getAllGames();
    }

    public List<Videogame> getGamesFiltered(String name, String genre, String platform) {
        return dao.getGamesFiltered(name, genre, platform);
    }

    // ... después de los métodos existentes en la clase Controller:
    /**
     * Creates a new order in the database.
     *
     * @param order Order object to create
     * @return true if order creation successful, false otherwise
     */
    public boolean createOrder(Order order) {
        return dao.createOrder(order);
    }

    /**
     * Creates a new review in the database.
     *
     * @param review Review object to create
     * @return true if review creation successful, false otherwise
     */
    public boolean createReview(Review review) {
        return dao.createReview(review);
    }

    /**
     * Checks if a user has already reviewed a specific video game.
     *
     * @param userId User ID
     * @param videogameId Videogame ID
     * @return true if review exists, false otherwise
     */
    public boolean reviewExists(int userId, int videogameId) {
        return dao.reviewExists(userId, videogameId);
    }
}
