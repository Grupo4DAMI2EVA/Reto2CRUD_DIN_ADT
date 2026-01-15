package main;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main extends Application {

    private static void CreateTableHibernate()
    {
        try {
        
        SessionFactory  sessionFactory = model.HibernateSession.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.close();
        
        System.out.println("Tablas creadas/validadas");
    } catch (Exception e) {
        System.err.println("Error al crear tablas: " + e.getMessage());
        // Decidir si continuar o salir
        System.exit(1);  // Sale si no puede crear tablas
    }
    }

    /**
     * Starts the JavaFX application by loading the login window.
     *
     * @param stage the primary stage for this application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LogInWindow.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Login Application");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        CreateTableHibernate();
        
        launch(args);
        
    }
    
    
}
