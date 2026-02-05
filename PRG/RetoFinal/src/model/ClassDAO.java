package model;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object interface for database operations. Provides methods to
 * interact with user and admin records in the database.
 *
 * @author Igor
 * @version 1.0
 */
public interface ClassDAO {

    /**
     * Authenticates a user with username and password.
     *
     * @param username User's username
     * @param password User's password
     * @return Profile object if authentication successful, null otherwise
     */
    public Profile logIn(String username, String password);

    /**
     * Registers a new user in the system.
     *
     * @param gender User's gender
     * @param cardNumber Credit card number
     * @param username Desired username
     * @param password Desired password
     * @param email User's email
     * @param name User's first name
     * @param telephone User's phone number
     * @param surname User's last name
     * @return true if registration successful, false otherwise
     */
    public boolean signUp(String gender, String cardNumber, String username, String password,
            String email, String name, String telephone, String surname);

    /**
     * Deletes a user account (self-deletion).
     *
     * @param username User's username
     * @param password User's password
     * @return true if deletion successful, false otherwise
     */
    public boolean dropOutUser(String username, String password);

    /**
     * Deletes a user account by an administrator.
     *
     * @param usernameToDelete Username of the account to delete
     * @param adminUsername Administrator's username
     * @param adminPassword Administrator's password
     * @return true if deletion successful, false otherwise
     */
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword);

    /**
     * Modifies user profile information.
     *
     * @param password New password
     * @param email New email
     * @param name New first name
     * @param telephone New phone number
     * @param surname New last name
     * @param username User's username (identifier)
     * @param gender User's gender
     * @return true if modification successful, false otherwise
     */
    public boolean modificarUser(String password, String email, String name,
            String telephone, String surname, String username, String gender);

    /**
     * Retrieves data for combo box population.
     *
     * @return List of items for combo box
     */
    public List comboBoxInsert();

    /**
     * Checks if a username already exists in the database.
     *
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean userExists(String username);

    /**
     * Retrieves a user by their username.
     *
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username);

    /**
     * Adds a new video game to the database.
     *
     * @param companyName Game's company
     * @param gameGenre Game's genre
     * @param name Game's name
     * @param platforms Supported platforms
     * @param pegi PEGI rating
     * @param price Game's price
     * @param stock Available stock
     * @param releaseDate Release date
     * @return true if addition successful, false otherwise
     */
    public boolean addGame(String companyName, GameGenre gameGenre, String name,
            Platform platforms, PEGI pegi, double price, int stock, Date releaseDate);

    /**
     * Modifies an existing video game.
     *
     * @param game Video game object with updated information
     * @return true if modification successful, false otherwise
     */
    public boolean modifyGame(Videogame game);

    /**
     * Deletes a video game from the database.
     *
     * @param game Video game to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteGame(Videogame game);

    /**
     * Adds a video game to the shopping cart.
     *
     * @param game Video game to add to cart
     * @return true if addition successful, false otherwise
     */
    public boolean addToCart(Videogame game);

    /**
     * Retrieves all video games from the database.
     *
     * @return List of all video games
     */
    public List<Videogame> getAllGames();

    /**
     * Retrieves video games filtered by criteria.
     *
     * @param name Game name filter (partial match)
     * @param genre Game genre filter
     * @param platform Platform filter
     * @return List of filtered video games
     */
    public List<Videogame> getGamesFiltered(String name, String genre, String platform);

    /**
     * Creates a new administrator account.
     *
     * @param username Admin username
     * @param password Admin password
     * @param email Admin email
     * @param name Admin first name
     * @param telephone Admin phone number
     * @param surname Admin last name
     * @param currentAccount Account type information
     * @return true if creation successful, false otherwise
     */
    public boolean createAdmin(String username, String password, String email,
            String name, String telephone, String surname, String currentAccount);

    // ... dentro de la interfaz ClassDAO, después de los métodos existentes:
    /**
     * Creates a new order in the database.
     *
     * @param order Order object to create
     * @return true if creation successful, false otherwise
     */
    public boolean createOrder(Order order);

    /**
     * Creates a new review in the database.
     *
     * @param review Review object to create
     * @return true if creation successful, false otherwise
     */
    public boolean createReview(Review review);

    /**
     * Checks if a user has already reviewed a specific video game.
     *
     * @param userId User ID
     * @param videogameId Videogame ID
     * @return true if review exists, false otherwise
     */
    public boolean reviewExists(int userId, int videogameId);
}
