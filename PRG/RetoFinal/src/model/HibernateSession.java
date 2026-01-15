/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author 2dami
 */
public class HibernateSession {
    
    private static SessionFactory sessionFactory;
    
    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al intentar crear SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
     public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
   
    public static void close() {
        getSessionFactory().close();
    }
}
