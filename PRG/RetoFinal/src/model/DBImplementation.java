package model;

import java.util.ArrayList;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

/**
 * Implementation of ClassDAO using Hibernate ORM. Handles all database interactions for users and admins.
 *
 * Author: acer
 */
public class DBImplementation implements ClassDAO {

    /**
     * Logs in a user or admin from the database.
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public Profile logIn(String username, String password) {
        Session session = HibernateSession.getSessionFactory().openSession();

        try {
            // Primero intentamos buscar como User
            String hqlUser = "FROM User u WHERE u.username = :username AND u.password = :password";
            Query<User> queryUser = session.createQuery(hqlUser, User.class);
            queryUser.setParameter("username", username);
            queryUser.setParameter("password", password);

            User user = queryUser.uniqueResult();
            if (user != null) {
                return user;
            }

            // Si no es User, intentamos como Admin
            String hqlAdmin = "FROM Admin a WHERE a.username = :username AND a.password = :password";
            Query<Admin> queryAdmin = session.createQuery(hqlAdmin, Admin.class);
            queryAdmin.setParameter("username", username);
            queryAdmin.setParameter("password", password);

            Admin admin = queryAdmin.uniqueResult();
            if (admin != null) {
                return admin;
            }

            System.out.println("Usuario no encontrado en la base de datos");

        } catch (Exception e) {
            System.out.println("Database query error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    /**
     * Signs up a new user in the database.
     *
     * @param gender
     * @param cardNumber
     * @param username
     * @param password
     * @param email
     * @param name
     * @param telephone
     * @param surname
     * @return
     */
    @Override
    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Verificar si el username ya existe
            String checkHql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
            Query<Long> checkQuery = session.createQuery(checkHql, Long.class);
            checkQuery.setParameter("username", username);
            Long count = checkQuery.uniqueResult();

            if (count > 0) {
                System.out.println("Username ya existe");
                return false;
            }

            // Crear y configurar el User
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setTelephone(telephone);
            newUser.setSurname(surname);
            newUser.setGender(gender);
            newUser.setCardNumber(cardNumber);

            // Guardar en la base de datos
            session.save(newUser);
            transaction.commit();

            System.out.println("Usuario registrado exitosamente: " + username);
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on signup: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Deletes a standard user from the database.
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public boolean dropOutUser(String username, String password) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Verificar que el usuario existe y la contraseña es correcta
            String hql = "FROM User u WHERE u.username = :username";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);

            User user = query.uniqueResult();

            if (user == null) {
                System.out.println("Usuario no encontrado: " + username);
                return false;
            }

            if (!user.getPassword().equals(password)) {
                System.out.println("Contraseña incorrecta para usuario: " + username);
                return false;
            }

            // Eliminar el usuario (se eliminarán automáticamente las relaciones por cascade)
            session.delete(user);
            transaction.commit();

            System.out.println("Usuario eliminado exitosamente: " + username);
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Deletes a user selected by admin from the database.
     *
     * @param usernameToDelete
     * @param adminUsername
     * @param adminPassword
     * @return
     */
    @Override
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Verificar que el admin existe y la contraseña es correcta
            String hqlAdmin = "FROM Admin a WHERE a.username = :username";
            Query<Admin> queryAdmin = session.createQuery(hqlAdmin, Admin.class);
            queryAdmin.setParameter("username", adminUsername);

            Admin admin = queryAdmin.uniqueResult();

            if (admin == null) {
                System.out.println("Admin no encontrado: " + adminUsername);
                return false;
            }

            if (!admin.getPassword().equals(adminPassword)) {
                System.out.println("Contraseña incorrecta para admin: " + adminUsername);
                return false;
            }

            // Buscar el usuario a eliminar (puede ser User o Admin)
            Profile profileToDelete = null;

            // Primero buscar si es User
            String hqlUser = "FROM User u WHERE u.username = :username";
            Query<User> queryUser = session.createQuery(hqlUser, User.class);
            queryUser.setParameter("username", usernameToDelete);
            User user = queryUser.uniqueResult();

            if (user != null) {
                profileToDelete = user;
            } else {
                // Si no es User, buscar si es Admin (pero no el que está logueado)
                String hqlOtherAdmin = "FROM Admin a WHERE a.username = :username";
                Query<Admin> queryOtherAdmin = session.createQuery(hqlOtherAdmin, Admin.class);
                queryOtherAdmin.setParameter("username", usernameToDelete);
                Admin otherAdmin = queryOtherAdmin.uniqueResult();

                if (otherAdmin != null && !otherAdmin.getUsername().equals(adminUsername)) {
                    profileToDelete = otherAdmin;
                }
            }

            if (profileToDelete == null) {
                System.out.println("Usuario a eliminar no encontrado: " + usernameToDelete);
                return false;
            }

            // No permitir que un admin se elimine a sí mismo
            if (profileToDelete.getUsername().equals(adminUsername)) {
                System.out.println("Un admin no puede eliminarse a sí mismo");
                return false;
            }

            // Eliminar el perfil
            session.delete(profileToDelete);
            transaction.commit();

            System.out.println("Usuario eliminado por admin: " + usernameToDelete);
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on deleting admin selection: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Modifies the information of a user in the database.
     *
     * @param password
     * @param email
     * @param name
     * @param telephone
     * @param username
     * @param gender
     * @param surname
     * @return
     */
    @Override
    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Buscar el usuario
            String hql = "FROM User u WHERE u.username = :username";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);

            User user = query.uniqueResult();

            if (user == null) {
                System.out.println("Usuario no encontrado en la base de datos: " + username);
                return false;
            }

            // Actualizar los campos
            user.setPassword(password);
            user.setEmail(email);
            user.setName(name);
            user.setTelephone(telephone);
            user.setSurname(surname);
            user.setGender(gender);

            // Guardar cambios
            session.update(user);
            transaction.commit();

            System.out.println("Usuario modificado exitosamente: " + username);
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on modifying user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Retrieves a list of usernames from the database.
     *
     * @return
     */
    @Override
    public List<String> comboBoxInsert() {
        Session session = HibernateSession.getSessionFactory().openSession();

        try {
            // Usar SQL nativo para obtener solo los usernames
            String sql = "SELECT DISTINCT username FROM PROFILE_";
            Query<String> query = session.createNativeQuery(sql, String.class);

            List<String> result = query.getResultList();
            System.out.println("Usernames encontrados: " + result.size());

            return result;

        } catch (Exception e) {
            System.out.println("Database error on retrieving usernames: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Método adicional para verificar si un username existe
     *
     * @param username
     * @return
     */
    @Override
    public boolean userExists(String username) {
        Session session = HibernateSession.getSessionFactory().openSession();

        try {
            String hql = "SELECT COUNT(p) FROM Profile p WHERE p.username = :username";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("username", username);

            Long count = query.uniqueResult();
            return count > 0;

        } catch (Exception e) {
            System.out.println("Error verificando existencia de usuario: " + e.getMessage());
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Método adicional para obtener un usuario por username
     *
     * @param username
     * @return
     */
    @Override
    public User getUserByUsername(String username) {
        Session session = HibernateSession.getSessionFactory().openSession();

        try {
            String hql = "FROM User u WHERE u.username = :username";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);

            return query.uniqueResult();

        } catch (Exception e) {
            System.out.println("Error obteniendo usuario: " + e.getMessage());
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public boolean addGame(String companyName, GameGenre gameGenre, String name, Platform platforms, PEGI pegi, double price, int stock, Date releaseDate) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Verificar si el juego ya existe
            String checkHql = "SELECT COUNT(v) FROM Videogame v WHERE v.name = :name";
            Query<Long> checkQuery = session.createQuery(checkHql, Long.class);
            checkQuery.setParameter("name", name);
            Long count = checkQuery.uniqueResult();

            if (count > 0) {
                System.out.println("Game already exists");
                return false;
            }

            // Crear y configurar el Juego
            Videogame videogame = new Videogame(companyName, gameGenre, name, platforms, pegi, price, stock, releaseDate);

            // Guardar en la base de datos
            session.save(videogame);
            transaction.commit();

            System.out.println("Game added correctly: " + videogame);
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on signup: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public boolean modifyGame(Videogame game) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            // Verificar si el juego ya existe
            String checkHql = "SELECT COUNT(u) FROM VIDEOGAME_ v WHERE v.name = :name";
            Query<Long> checkQuery = session.createQuery(checkHql, Long.class);
            checkQuery.setParameter("name", game.getName());
            Long count = checkQuery.uniqueResult();

            if (count > 0) {
                System.out.println("Game already exists.");
                return false;
            }

            transaction = session.beginTransaction();
            session.update(game);
            transaction.commit();

            System.out.println("Game " + game.getName() + " modified correctly!");
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on modifying game: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public boolean deleteGame(Videogame game) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            
            // Cargar el videojuego desde la base de datos para asegurar que está asociado a la sesión
            Videogame videogameToDelete = session.get(Videogame.class, game.getIdVideogame());
            
            if (videogameToDelete != null) {
                session.delete(videogameToDelete);
                transaction.commit();
                System.out.println("Game deleted successfully: " + videogameToDelete.getName());
                return true;
            } else {
                System.out.println("Game not found in database");
                return false;
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on deleting game: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public boolean addToCart(Videogame game) {
        boolean success = false;

        return success;
    }

    /**
     * Retrieves all videogames from the database.
     *
     * @return List of all videogames
     */
    @Override
    public List<Videogame> getAllGames() {
        Session session = HibernateSession.getSessionFactory().openSession();
        List<Videogame> games = new ArrayList<>();

        try {
            String hql = "FROM Videogame v ORDER BY v.name ASC";
            Query<Videogame> query = session.createQuery(hql, Videogame.class);

            games = query.getResultList();
            System.out.println("Total de juegos encontrados: " + games.size());

        } catch (Exception e) {
            System.out.println("Database error on retrieving games: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return games;
    }

    /**
     * Retrieves videogames filtered by criteria
     *
     * @param name Game name or part of it (can be null or empty)
     * @param genre Game genre (can be null or empty)
     * @param platform Platform (can be null or empty)
     * @return List of filtered videogames
     */
    @Override
    public List<Videogame> getGamesFiltered(String name, String genre, String platform) {
        Session session = HibernateSession.getSessionFactory().openSession();
        List<Videogame> games = new ArrayList<>();

        try {
            StringBuilder hql = new StringBuilder("FROM Videogame v WHERE 1=1");

            // Construir la consulta dinámicamente basado en los filtros
            if (name != null && !name.trim().isEmpty()) {
                hql.append(" AND LOWER(v.name) LIKE LOWER(:name)");
            }
            if (genre != null && !genre.trim().isEmpty()) {
                hql.append(" AND v.gameGenre = :genre");
            }
            if (platform != null && !platform.trim().isEmpty()) {
                hql.append(" AND v.platforms = :platform");
            }

            hql.append(" ORDER BY v.name ASC");

            Query<Videogame> query = session.createQuery(hql.toString(), Videogame.class);

            // Establecer parámetros si existen
            if (name != null && !name.trim().isEmpty()) {
                query.setParameter("name", "%" + name.trim() + "%");
            }
            if (genre != null && !genre.trim().isEmpty()) {
                try {
                    GameGenre genreEnum = GameGenre.valueOf(genre.trim().toUpperCase());
                    query.setParameter("genre", genreEnum);
                } catch (IllegalArgumentException e) {
                    System.out.println("Género no válido: " + genre);
                    // Si el género no es válido, retornar lista vacía o manejar según tu lógica
                    return new ArrayList<>();
                }
            }
            if (platform != null && !platform.trim().isEmpty()) {
                try {
                    Platform platformEnum = Platform.valueOf(platform.trim().toUpperCase());
                    query.setParameter("platform", platformEnum);
                } catch (IllegalArgumentException e) {
                    System.out.println("Plataforma no válida: " + platform);
                    // Si la plataforma no es válida, retornar lista vacía o manejar según tu lógica
                    return new ArrayList<>();
                }
            }

            games = query.getResultList();
            System.out.println("Juegos encontrados con filtros: " + games.size());

        } catch (Exception e) {
            System.out.println("Database error on retrieving filtered games: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return games;
    }

    /**
     * Creates a new admin in the database with validation.
     *
     * @param username
     * @param password
     * @param email
     * @param name
     * @param telephone
     * @param surname
     * @param currentAccount
     * @return true if admin was created successfully, false otherwise
     */
    @Override
    public boolean createAdmin(String username, String password, String email, String name, String telephone, String surname, String currentAccount) {
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Verificar si el username ya existe
            String checkHql = "SELECT COUNT(p) FROM Profile p WHERE p.username = :username";
            Query<Long> checkQuery = session.createQuery(checkHql, Long.class);
            checkQuery.setParameter("username", username);
            Long count = checkQuery.uniqueResult();

            if (count > 0) {
                System.out.println("Username ya existe: " + username);
                return false;
            }

            // Crear y configurar el Admin
            Admin newAdmin = new Admin();
            newAdmin.setUsername(username);
            newAdmin.setPassword(password);
            newAdmin.setEmail(email);
            newAdmin.setName(name);
            newAdmin.setTelephone(telephone);
            newAdmin.setSurname(surname);
            newAdmin.setCurrentAccount(currentAccount);

            // Guardar en la base de datos
            session.save(newAdmin);
            transaction.commit();

            System.out.println("Admin registrado exitosamente: " + username);
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Database error on creating admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
