package model;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object interface for database operations. Provides methods to interact with user and admin records in the database.
 */
public interface ClassDAO {

    public Profile logIn(String username, String password);

    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname);

    public boolean dropOutUser(String username, String password);

    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword);

    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender);

    public List comboBoxInsert();

    public boolean userExists(String username);

    public User getUserByUsername(String username);

    public boolean addGame(String companyName, GameGenre gameGenre, String name, Platform platforms, PEGI pegi, double price, int stock, Date releaseDate);

    public boolean modifyGame(Videogame game);

    public boolean deleteGame(Videogame game);

    public boolean addToCart(Videogame game);

    public List<Videogame> getAllGames();

    public List<Videogame> getGamesFiltered(String name, String genre, String platform);
}
