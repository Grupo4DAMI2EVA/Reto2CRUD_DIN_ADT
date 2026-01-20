package model;

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
    public boolean addGame(Videogame game) {
        boolean success = false;

        return success;
    }

    @Override
    public boolean modifyGame(Videogame game) {
        boolean success = false;

        return success;
    }

    @Override
    public boolean deleteGame(Videogame game) {
        boolean success = false;

        return success;
    }

    @Override
    public boolean addToCart(Videogame game) {
        boolean success = false;

        return success;
    }
}
