package model;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Clase Singleton para gestionar la SessionFactory de Hibernate.
 * Inicializa y proporciona acceso a la fábrica de sesiones de forma centralizada.
 * 
 * @author Igor
 * @version 1.0
 */
public class HibernateSession {
    
    /** Instancia única de SessionFactory, inicializada estáticamente. */
    private static SessionFactory sessionFactory;
    
    /**
     * Inicializa la SessionFactory al cargar la clase.
     * Lee la configuración desde hibernate.cfg.xml.
     * 
     * @throws ExceptionInInitializerError si falla la creación de SessionFactory
     */
    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al intentar crear SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Obtiene la SessionFactory configurada.
     * 
     * @return Instancia única de SessionFactory (thread-safe)
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
   
    /**
     * Cierra la SessionFactory y libera sus recursos.
     * Debe llamarse al finalizar la aplicación.
     */
    public static void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            getSessionFactory().close();
        }
    }
}