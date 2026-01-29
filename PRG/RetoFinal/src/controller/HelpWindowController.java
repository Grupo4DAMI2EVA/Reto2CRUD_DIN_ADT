package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;
import java.util.logging.*;

public class HelpWindowController implements Initializable {

    private static final Logger logger = Logger.getLogger(HelpWindowController.class.getName());
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
    private Controller cont;

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
            
            FileHandler fileHandler = new FileHandler("logs/help_window.log", true);
            
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    if (record.getLevel() == Level.INFO || record.getLevel() == Level.SEVERE) {
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
            logger.info("HelpWindowController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    public void setUser(Profile profile) {
        try {
            this.profile = profile;
            logger.info(String.format("User profile set: %s, Type: %s", 
                       profile != null ? profile.getUsername() : "null",
                       profile != null ? profile.getClass().getSimpleName() : "null"));
        } catch (Exception e) {
            logger.severe(String.format("Error setting user profile: %s", e.getMessage()));
        }
    }

    public void setController(Controller cont) {
        try {
            this.cont = cont;
            logger.info("Main controller set for HelpWindowController");
        } catch (Exception e) {
            logger.severe(String.format("Error setting main controller: %s", e.getMessage()));
        }
    }

    public void loadAdminItems() {
        try {
            item1.setText("Shop Management");
            item2.setText("Add Game");
            item3.setText("Modify Game");
            item4.setVisible(true);
            item4.setDisable(false);
            item4.setText("Delete Game");
        } catch (Exception e) {
            logger.severe(String.format("Error loading admin items: %s", e.getMessage()));
        }
    }

    public void loadAdminItems() {
        try {
            item1.setText("Shop Management");
            item2.setText("Add Game");
            item3.setText("Modify Game");
            item4.setText("Delete Game");
            item4.setVisible(true);
            item4.setDisable(false);
            
            logger.info("Admin menu items loaded");
            
        } catch (Exception e) {
            logger.severe(String.format("Error loading admin menu items: %s", e.getMessage()));
        }
    }
    
    public void loadUserItems() {
        try {
            item1.setText("Buy Games");
            item2.setText("View Cart");
            item3.setText("Account Settings");
            item4.setVisible(false);
            item4.setDisable(true);
            
            logger.info("User menu items loaded");
            
        } catch (Exception e) {
            logger.severe(String.format("Error loading user menu items: %s", e.getMessage()));
        }
    }
    
    @FXML
    private void changeHelpText1() {
        try {
            logger.info("Changing help text for menu item 1");
            
            if (profile instanceof Admin) {
                showAdminHelpText1();
            } else {
                showUserHelpText1();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error changing help text 1: %s", e.getMessage()));
        }
    }

    private void showAdminHelpText1() {
        info.setText("SHOP MANAGEMENT\n\n" +
                   "As an Administrator, you have access to:\n" +
                   "1. View all available games in the store\n" +
                   "2. Manage game inventory and stock\n" +
                   "3. Monitor sales and user activity\n" +
                   "4. Generate reports on store performance\n\n" +
                   "To access the shop management panel, navigate to:\n" +
                   "Menu → Admin Panel → Shop Management");
        logger.info("Admin shop management help displayed");
    }

    private void showUserHelpText1() {
        info.setText("BUY GAMES\n\n" +
                   "As a User, you can:\n" +
                   "1. Browse available games in the store\n" +
                   "2. Search games by title, genre, or platform\n" +
                   "3. View detailed information about each game\n" +
                   "4. Add games to your shopping cart\n\n" +
                   "To buy games:\n" +
                   "1. Go to the Shop section\n" +
                   "2. Select the games you want\n" +
                   "3. Click 'Add to Cart'\n" +
                   "4. Proceed to checkout when ready");
        logger.info("User buy games help displayed");
    }
    
    @FXML
    private void changeHelpText2() {
        try {
            logger.info("Changing help text for menu item 2");
            
            if (profile instanceof Admin) {
                showAdminHelpText2();
            } else {
                showUserHelpText2();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error changing help text 2: %s", e.getMessage()));
        }
    }

    private void showAdminHelpText2() {
        info.setText("ADD GAME\n\n" +
                   "To add a new game to the store:\n\n" +
                   "1. Go to Admin Panel → Add Game\n" +
                   "2. Fill in all required information:\n" +
                   "   - Game Title\n" +
                   "   - Platform (PC, PS5, Xbox, etc.)\n" +
                   "   - Developer Company\n" +
                   "   - Stock Quantity\n" +
                   "   - Genre\n" +
                   "   - Price\n" +
                   "   - PEGI Rating\n" +
                   "   - Release Date\n\n" +
                   "3. Click 'Add Game' to save\n\n" +
                   "Note: All fields are required for proper game registration.");
        logger.info("Admin add game help displayed");
    }

    private void showUserHelpText2() {
        info.setText("VIEW CART\n\n" +
                   "Your shopping cart contains all items you have selected for purchase.\n\n" +
                   "To manage your cart:\n" +
                   "1. View all items in your cart\n" +
                   "2. Adjust quantities using + and - buttons\n" +
                   "3. Remove unwanted items\n" +
                   "4. View total cost\n\n" +
                   "To proceed to checkout:\n" +
                   "1. Ensure all items are correct\n" +
                   "2. Click 'Buy' button\n" +
                   "3. Confirm your purchase\n\n" +
                   "Note: You must be logged in to complete purchases.");
        logger.info("User view cart help displayed");
    }

    @FXML
    private void changeHelpText3() {
        try {
            logger.info("Changing help text for menu item 3");
            
            if (profile instanceof Admin) {
                showAdminHelpText3();
            } else {
                showUserHelpText3();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error changing help text 3: %s", e.getMessage()));
        }
    }

    private void showAdminHelpText3() {
        info.setText("MODIFY GAME\n\n" +
                   "To modify an existing game:\n\n" +
                   "1. Go to Admin Panel → Shop Management\n" +
                   "2. Select the game you want to modify from the list\n" +
                   "3. Click 'Modify' button\n" +
                   "4. Update the information you want to change:\n" +
                   "   - Price adjustments\n" +
                   "   - Stock quantity updates\n" +
                   "   - Game details corrections\n\n" +
                   "5. Click 'Save Changes' to update\n\n" +
                   "Important: Modifying prices will affect all future purchases.");
        logger.info("Admin modify game help displayed");
    }

    private void showUserHelpText3() {
        info.setText("ACCOUNT SETTINGS\n\n" +
                   "Manage your account information:\n\n" +
                   "Available options:\n" +
                   "1. Change Password\n" +
                   "2. Update Personal Information\n" +
                   "3. View Purchase History\n" +
                   "4. Delete Account\n\n" +
                   "To change your password:\n" +
                   "1. Go to Account Settings\n" +
                   "2. Select 'Change Password'\n" +
                   "3. Enter current and new password\n" +
                   "4. Confirm changes\n\n" +
                   "Security: Always use strong passwords and never share your login details.");
        logger.info("User account settings help displayed");
    }

    @FXML
    private void changeHelpText4() {
        try {
            logger.info("Changing help text for menu item 4");
            
            if (profile instanceof Admin) {
                showAdminHelpText4();
            } else {
                handleUserHelpText4();
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error changing help text 4: %s", e.getMessage()));
        }
    }

    private void showAdminHelpText4() {
        info.setText("DELETE GAME\n\n" +
                   "To remove a game from the store:\n\n" +
                   "1. Go to Admin Panel → Shop Management\n" +
                   "2. Select the game you want to delete from the list\n" +
                   "3. Click 'Delete' button\n" +
                   "4. Confirm the deletion in the pop-up window\n\n" +
                   "WARNING: This action is permanent and cannot be undone!\n\n" +
                   "Considerations before deleting:\n" +
                   "1. Check if any users have this game in their cart\n" +
                   "2. Review purchase history for this game\n" +
                   "3. Consider archiving instead of deleting\n\n" +
                   "Note: Deleted games will no longer be available for purchase.");
        logger.info("Admin delete game help displayed");
    }

    private void handleUserHelpText4() {
        logger.info("Menu item 4 not available for regular users");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("Initializing HelpWindowController");
            
            initializeDefaultHelpText();
            initializeMenuBasedOnUserType();
            
            logger.info("HelpWindowController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing HelpWindowController: %s", e.getMessage()));
        }
    }

    private void initializeDefaultHelpText() {
        info.setText("Welcome to the Help System\n\n" +
                   "Select a menu item on the left to view help information for that feature.\n\n" +
                   "The available help topics depend on your user type (Admin or Regular User).");
    }

    private void initializeMenuBasedOnUserType() {
        if (profile instanceof Admin) {
            loadAdminItems();
            logger.info("Admin interface initialized");
        } else {
            loadUserItems();
            logger.info("User interface initialized");
        }
    }

    public void refreshHelpContent() {
        try {
            logger.info("Refreshing help content");
            
            if (profile instanceof Admin) {
                loadAdminItems();
                changeHelpText1();
                logger.info("Admin help content refreshed");
            } else {
                loadUserItems();
                changeHelpText1();
                logger.info("User help content refreshed");
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error refreshing help content: %s", e.getMessage()));
        }
    }

    public void clearHelpText() {
        try {
            logger.info("Clearing help text");
            
            info.clear();
            info.setText("Select a menu item to view help information.");
            
            logger.info("Help text cleared");
            
        } catch (Exception e) {
            logger.severe(String.format("Error clearing help text: %s", e.getMessage()));
        }
    }
}