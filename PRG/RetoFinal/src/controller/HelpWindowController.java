package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;

public class HelpWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(ShopWindowController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menu;
    @FXML
    private MenuItem item1;
    @FXML
    private MenuItem item2;
    @FXML
    private MenuItem item3;
    @FXML
    private MenuItem item4;
    @FXML
    private TextArea info;

    private Profile profile;
    
    static {
        initializeLogger();
    }
    
    private static synchronized void initializeLogger() {
        if (loggerInitialized) {
            return;
        }
        
        try {
            java.io.File logsFolder = new java.io.File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }
            
            FileHandler fileHandler = new FileHandler("logs/LogInWindow.log", true);
            
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    if (record.getLevel() == Level.INFO || record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
                        return String.format("[%1$tF %1$tT] [%2$s] %3$s %n",
                                new java.util.Date(record.getMillis()),
                                record.getLevel(),
                                record.getMessage());
                    }
                    return "";
                }
            });
            
            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false);
            
            loggerInitialized = true;
            logger.info("AdminShopController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setUsuario(Profile profile) {
        logger.info("Setting user profile in HelpWindowController: " + 
                   (profile != null ? profile.getUsername() + 
                    " (Type: " + (profile instanceof Admin ? "Admin" : "User") + ")" : "null"));
        this.profile = profile;
        
        // Cargar items apropiados según tipo de usuario
        if (profile instanceof Admin) {
            loadAdminItems();
        }
    }

    public void loadAdminItems() {
        logger.info("Loading admin-specific help menu items");
        
        try {
            item1.setText("Shop Management");
            item2.setText("Add Game");
            item3.setText("Modify Game");
            item4.setVisible(true);
            item4.setDisable(false);
            item4.setText("Delete Game");
            
            logger.info("Admin help menu items loaded successfully");
            
        } catch (Exception e) {
            logger.severe("Error loading admin help menu items: " + e.getMessage());
        }
    }

    @FXML
    private void changeHelpText1() {
        String userType = profile instanceof Admin ? "Admin" : "User";
        logger.info("Help menu item 1 clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown") + 
                   ", Type: " + userType);
        
        try {
            if (profile instanceof Admin) {
                logger.info("Displaying admin shop management help");
                info.setText("Bienvenido a Store Management. Esta página permite gestionar el catálogo de videojuegos.\n"
                        + "\n"
                        + "Operaciones disponibles:\n"
                        + "\n"
                        + "Selecciona un videojuego: Haz clic en cualquier fila de la tabla para seleccionar el juego\n"
                        + "Verifica los detalles: El juego seleccionado aparecerá en la sección 'Selected game'\n"
                        + "Añadir juego: Usa el botón 'Add Game' para abrirse una nueva ventana y añadir un juego nuevo al catálogo\n"
                        + "Modificar juego: Usa el botón 'Modify Game' (o en el menu de click derecho) para editar los detalles del juego seleccionado\n"
                        + "Eliminar juego: Usa el botón 'Delete Game' (o en el menu de click derecho) para eliminar el juego seleccionado del catálogo\n"
                        + "\n"
                        + "Filtros disponibles:\n"
                        + "Search game: Busca por nombre del videojuego\n"
                        + "Genre: Filtra por género (Acción, RPG, etc.)\n"
                        + "Platform: Filtra por plataforma (PC, PlayStation, Xbox, etc.)\n"
                        + "\n"
                        + "Importante:\n"
                        + "Debes seleccionar un videojuego antes de poder modificarlo o eliminarlo. La eliminación es permanente.");
            } else {
                logger.info("Displaying user shop help");
                info.setText("Bienvenido a Game Store - Tienda de Videojuegos\n"
                    + "===============================================\n\n"
                    + "🛍️ COMPRA DE VIDEOJUEGOS:\n"
                    + "• Selecciona un juego: Haz clic en cualquier fila de la tabla\n"
                    + "• Ver detalles: El juego seleccionado aparece en 'Selected game'\n"
                    + "• Añadir al carrito: Botón 'Add to Cart' para agregar a tu carrito\n"
                    + "• Escribir reseña: Botón 'Review' para opinar sobre el juego\n"
                    + "• Ver carrito: Botón 'Cart' o menú Cart → View Cart\n\n"
                    
                    + "⭐ GESTIÓN DE FAVORITOS:\n"
                    + "• Añadir a favoritos: Menú contextual (clic derecho) → Add to Favorites\n"
                    + "• Ver favoritos: Menú View → Favorites\n"
                    + "• Los juegos favoritos aparecen resaltados en amarillo\n"
                    + "• Quitar de favoritos: Clic derecho → Remove from Favorites\n\n"
                    
                    + "🔍 BÚSQUEDA AVANZADA:\n"
                    + "• Search game: Busca por nombre exacto o parcial\n"
                    + "• Genre: Filtra por género (Acción, RPG, Estrategia, Deportes, etc.)\n"
                    + "• Platform: Filtra por plataforma (PC, PS5, Xbox, Nintendo Switch)\n"
                    + "• Mostrar todos: View → All Games para ver catálogo completo\n\n"
                    
                    + "📋 NAVEGACIÓN POR MENÚ:\n"
                    + "• File → Main Menu: Volver al menú principal con opciones de usuario\n"
                    + "• File → Modify Profile: Editar tu información personal\n"
                    
                    + "❓ MENÚ HELP:\n"
                    + "• Help → Help: Mostrar esta guía de ayuda\n"
                    + "• Help → About: Información sobre la tienda\n\n"
                    
                    + "🎮 DETALLES DEL JUEGO (clic derecho):\n"
                    + "• Add to Favorites: Marcar como favorito\n"
                    + "• View Details: Ver información completa del juego\n"
                    + "• Add to Cart: Añadir directamente al carrito\n"
                    + "• Write Review: Escribir una reseña\n\n"
                    
                    + "💰 TU CUENTA:\n"
                    + "• Nombre de usuario visible en 'Welcome, [usuario]!'\n"
                    + "• Stock disponible se muestra para cada juego\n\n"
                    
                    + "⚠️ NOTAS IMPORTANTES:\n"
                    + "• Necesitas estar logueado para añadir al carrito o favoritos\n"
                    + "• Revisa el stock disponible antes de comprar\n"
                    + "• Los juegos sin stock no se pueden añadir al carrito\n"
                    + "• Puedes tener múltiples copias del mismo juego en el carrito\n"
                    + "• Las reseñas ayudan a otros usuarios a decidir\n");
            }
            
            logger.info("Help text 1 displayed successfully for " + userType);
            
        } catch (Exception e) {
            logger.severe("Error displaying help text 1: " + e.getMessage());
        }
    }

    @FXML
    private void changeHelpText2() {
        String userType = profile instanceof Admin ? "Admin" : "User";
        logger.info("Help menu item 2 clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown") + 
                   ", Type: " + userType);
        
        try {
            if (profile instanceof Admin) {
                logger.info("Displaying admin add game help");
                info.setText("En esta parte, tienes que rellenar todos los campos del juego.\n"
                        + "Name: Nombre de juego."
                        + "Platforms: Uno de las plataformas en el que se puede jugar al juego."
                        + "Company: La compañia que ha creado el juego."
                        + "Stock: La cantidad de copias del juego que se va a añadir."
                        + "Genre: Uno de los generos del juego."
                        + "Price: El precio del juego. El uso de decimales es perimitido."
                        + "PEGI: El PEGI del juego. Puede ser ninguno si el juego no lo tiene aun."
                        + "Release Date: El dia en el que un juego haya salido/vaya a salir en."
                        + "Usa el boton 'Add Game' para añadir el juego.");
            } else {
                logger.info("Displaying empty help text for user");
                info.setText("");
            }
            
            logger.info("Help text 2 displayed successfully for " + userType);
            
        } catch (Exception e) {
            logger.severe("Error displaying help text 2: " + e.getMessage());
        }
    }

    @FXML
    private void changeHelpText3() {
        String userType = profile instanceof Admin ? "Admin" : "User";
        logger.info("Help menu item 3 clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown") + 
                   ", Type: " + userType);
        
        try {
            if (profile instanceof Admin) {
                logger.info("Displaying admin modify game help (currently empty)");
                info.setText("");
            } else {
                logger.info("Displaying empty help text for user");
                info.setText("");
            }
            
            logger.info("Help text 3 displayed successfully for " + userType);
            
        } catch (Exception e) {
            logger.severe("Error displaying help text 3: " + e.getMessage());
        }
    }

    @FXML
    private void changeHelpText4() {
        String userType = profile instanceof Admin ? "Admin" : "User";
        logger.info("Help menu item 4 clicked - User: " + 
                   (profile != null ? profile.getUsername() : "unknown") + 
                   ", Type: " + userType);
        
        try {
            if (profile instanceof Admin) {
                logger.info("Displaying admin delete game help (currently empty)");
                info.setText("");
            }
            // Nota: Para usuarios normales, este item no es visible
            
            logger.info("Help text 4 displayed successfully for " + userType);
            
        } catch (Exception e) {
            logger.severe("Error displaying help text 4: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing HelpWindowController");
        
        try {
            // Configurar según tipo de usuario si ya se ha establecido el perfil
            if (profile instanceof Admin) {
                logger.info("User is Admin - Loading admin help configuration");
                loadAdminItems();
            } else if (profile != null) {
                logger.info("User is Regular User - Standard help configuration");
            } else {
                logger.info("No user profile set yet");
            }
            
            logger.info("HelpWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing HelpWindowController: " + e.getMessage());
        }
    }
}