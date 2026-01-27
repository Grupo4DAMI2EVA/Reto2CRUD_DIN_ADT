package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.*;

public class CartController {

    private static final Logger logger = Logger.getLogger(CartController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private ListView<String> listViewCart;
    @FXML
    private Label labelTotalItems;
    @FXML
    private Label labelTotalPrice;
    @FXML
    private Label labelCurrentQuantity;
    @FXML
    private Label labelSelectedItem;
    @FXML
    private Button buttonPlus;
    @FXML
    private Button buttonMinus;
    @FXML
    private Button buttonBuy;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonCancel;

    private Stage stage;
    private ObservableList<String> cartData;
    private int selectedIndex = -1;
    private int currentQuantity = 0;

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
            
            FileHandler fileHandler = new FileHandler("logs/cart.log", true);
            
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
            logger.info("CartController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    @FXML
    private void initialize() {
        try {
            logger.info("Initializing CartController");
            
            initializeCartData();
            setupListViewListener();
            loadExampleData();
            updateTotals();
            updateButtonStates();
            
            logger.info("CartController initialized successfully");
            
        } catch (Exception e) {
            logger.severe(String.format("Error initializing CartController: %s", e.getMessage()));
            showAlert("Initialization Error", 
                "Could not initialize the cart. Please restart the application.");
        }
    }

    private void initializeCartData() {
        cartData = FXCollections.observableArrayList();
        listViewCart.setItems(cartData);
    }

    private void setupListViewListener() {
        listViewCart.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showItemDetails(newValue));
    }

    private void loadExampleData() {
        try {
            logger.info("Loading example data to cart");
            
            cartData.add("User: 1 | Game: FIFA 23 | Quantity: 2 | Price: $59.99");
            cartData.add("User: 1 | Game: Call of Duty | Quantity: 1 | Price: $69.99");
            cartData.add("User: 2 | Game: Minecraft | Quantity: 3 | Price: $24.99");
            cartData.add("User: 3 | Game: GTA V | Quantity: 1 | Price: $39.99");
            
            logger.info(String.format("Example data loaded: %d items", cartData.size()));
            
        } catch (Exception e) {
            logger.severe(String.format("Error loading example data: %s", e.getMessage()));
        }
    }

    private void showItemDetails(String item) {
        try {
            if (item != null) {
                selectedIndex = listViewCart.getSelectionModel().getSelectedIndex();
                logger.info(String.format("Item selected - Index: %d", selectedIndex));
                
                extractItemDetails(item);
                updateButtonStates();
                
            } else {
                clearItemSelection();
            }
            
        } catch (NumberFormatException e) {
            logger.severe(String.format("Format error extracting quantity from item: %s", e.getMessage()));
            showAlert("Format Error", "The format of the selected item is incorrect.");
            
        } catch (Exception e) {
            logger.severe(String.format("Error in showItemDetails: %s", e.getMessage()));
            showAlert("Error", "Could not process the selected item.");
        }
    }

    private void extractItemDetails(String item) {
        int pos1 = item.indexOf("|");
        int pos2 = item.indexOf("|", pos1 + 1);
        int pos3 = item.indexOf("|", pos2 + 1);
        
        if (pos1 == -1 || pos2 == -1 || pos3 == -1) {
            logger.severe("Invalid item format");
            return;
        }
        
        String part2 = item.substring(pos1 + 1, pos2).trim();
        String part3 = item.substring(pos2 + 1, pos3).trim();
        
        extractGameName(part2);
        extractQuantity(part3);
    }

    private void extractGameName(String gamePart) {
        int colonPos = gamePart.indexOf(":");
        if (colonPos != -1) {
            String gameName = gamePart.substring(colonPos + 1).trim();
            labelSelectedItem.setText(gameName);
        }
    }

    private void extractQuantity(String quantityPart) {
        int colonPos = quantityPart.indexOf(":");
        if (colonPos != -1) {
            String quantityStr = quantityPart.substring(colonPos + 1).trim();
            currentQuantity = Integer.parseInt(quantityStr);
            labelCurrentQuantity.setText(String.valueOf(currentQuantity));
            
            logger.info(String.format("Details extracted - Quantity: %d", currentQuantity));
        }
    }

    private void clearItemSelection() {
        selectedIndex = -1;
        labelSelectedItem.setText("Select an item");
        labelCurrentQuantity.setText("0");
        updateButtonStates();
    }

    @FXML
    private void increaseQuantity() {
        try {
            if (selectedIndex >= 0) {
                logger.info(String.format("Increasing quantity - Index: %d", selectedIndex));
                
                currentQuantity++;
                updateItemQuantity();
                
                logger.info(String.format("Quantity increased to: %d", currentQuantity));
            } else {
                logger.severe("Attempt to increase quantity without selected item");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error in increaseQuantity: %s", e.getMessage()));
        }
    }

    @FXML
    private void decreaseQuantity() {
        try {
            if (selectedIndex >= 0 && currentQuantity > 1) {
                handleDecreaseQuantity();
            } else if (currentQuantity == 1) {
                handleQuantityAtOne();
            } else {
                logger.severe("Attempt to decrease quantity without selected item");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error in decreaseQuantity: %s", e.getMessage()));
        }
    }

    private void handleDecreaseQuantity() {
        logger.info(String.format("Decreasing quantity - Index: %d", selectedIndex));
        
        currentQuantity--;
        updateItemQuantity();
        
        logger.info(String.format("Quantity decreased to: %d", currentQuantity));
    }

    private void handleQuantityAtOne() {
        logger.info("Quantity at 1 - Asking if delete item");
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete this item from cart?");
        alert.setContentText("Quantity would reach 0. Do you want to delete it completely?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeYes) {
                logger.info("User confirmed delete item");
                deleteItem();
            }
        });
    }

    private void updateItemQuantity() {
        try {
            if (selectedIndex >= 0) {
                String originalItem = cartData.get(selectedIndex);
                String newItem = createUpdatedItem(originalItem);
                
                if (newItem != null) {
                    cartData.set(selectedIndex, newItem);
                    listViewCart.refresh();
                    labelCurrentQuantity.setText(String.valueOf(currentQuantity));
                    updateTotals();
                    updateButtonStates();
                    
                    logger.info(String.format("Quantity updated - New quantity: %d", currentQuantity));
                } else {
                    logger.severe("Invalid item format when updating quantity");
                }
            } else {
                logger.severe("Attempt to update quantity without selected item");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error in updateItemQuantity: %s", e.getMessage()));
            showAlert("Error", "Could not update item quantity.");
        }
    }

    private String createUpdatedItem(String originalItem) {
        int pos1 = originalItem.indexOf("|");
        int pos2 = originalItem.indexOf("|", pos1 + 1);
        int pos3 = originalItem.indexOf("|", pos2 + 1);
        
        if (pos1 != -1 && pos2 != -1 && pos3 != -1) {
            String part1 = originalItem.substring(0, pos1).trim();
            String part2 = originalItem.substring(pos1 + 1, pos2).trim();
            String part4 = originalItem.substring(pos3 + 1).trim();
            
            return String.format("%s | %s | Quantity: %d | %s", 
                   part1, part2, currentQuantity, part4);
        }
        return null;
    }

    private void updateButtonStates() {
        try {
            boolean hasItemSelected = (selectedIndex >= 0);

            buttonPlus.setDisable(!hasItemSelected);
            buttonMinus.setDisable(!hasItemSelected || currentQuantity <= 1);
            buttonDelete.setDisable(!hasItemSelected);
            buttonBuy.setDisable(cartData.isEmpty());
            
        } catch (Exception e) {
            logger.severe(String.format("Error in updateButtonStates: %s", e.getMessage()));
        }
    }

    @FXML
    private void buy() {
        try {
            if (cartData.isEmpty()) {
                logger.severe("Attempt to buy with empty cart");
                showAlert("Empty Cart", "There are no items in the cart to buy.");
                return;
            }

            logger.info(String.format("Starting purchase process - Items: %d", cartData.size()));

            if (confirmPurchase()) {
                processPurchase();
            } else {
                logger.info("User canceled purchase");
            }
            
        } catch (Exception e) {
            logger.severe(String.format("Error in buy: %s", e.getMessage()));
            showAlert("Purchase Error", 
                "An error occurred processing the purchase. Please try again.");
        }
    }

    private boolean confirmPurchase() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Purchase");
        alert.setHeaderText("Make purchase?");
        alert.setContentText(String.format("Total to pay: %s", labelTotalPrice.getText()));

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    private void processPurchase() {
        logger.info(String.format("User confirmed purchase - Total: %s", labelTotalPrice.getText()));
        
        showSuccessAlert("Purchase Completed", "Purchase successful!", 
            "Your purchase has been completed successfully.");

        logger.info("Clearing cart after purchase");
        cartData.clear();
        updateTotals();
        clearSelection();
        
        logger.info("Purchase completed successfully");
    }

    private void showSuccessAlert(String title, String header, String content) {
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle(title);
        success.setHeaderText(header);
        success.setContentText(content);
        success.showAndWait();
    }

    @FXML
    private void deleteItem() {
        try {
            if (selectedIndex >= 0) {
                String itemToDelete = cartData.get(selectedIndex);
                logger.info(String.format("Requesting to delete item: %s", itemToDelete));

                if (confirmDeleteItem()) {
                    removeItemFromCart();
                }
            } else {
                logger.severe("Attempt to delete without selected item");
                showAlert("Select Item", "Please select an item to delete.");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error in deleteItem: %s", e.getMessage()));
            showAlert("Error", "Could not delete item from cart.");
        }
    }

    private boolean confirmDeleteItem() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete this item from cart?");
        alert.setContentText("This action cannot be undone.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    private void removeItemFromCart() {
        logger.info("User confirmed to delete the item");
        cartData.remove(selectedIndex);
        updateTotals();
        clearSelection();
        
        logger.info("Item deleted successfully");
    }

    private void clearSelection() {
        try {
            listViewCart.getSelectionModel().clearSelection();
            selectedIndex = -1;
            currentQuantity = 0;
            labelCurrentQuantity.setText("0");
            labelSelectedItem.setText("Select an item");
            updateButtonStates();
            
        } catch (Exception e) {
            logger.severe(String.format("Error in clearSelection: %s", e.getMessage()));
        }
    }

    @FXML
    private void cancel() {
        try {
            logger.info("Closing cart window");
            
            stage = (Stage) buttonDelete.getScene().getWindow();
            stage.close();
            
            logger.info("Cart window closed");
            
        } catch (Exception e) {
            logger.severe(String.format("Error closing cart window: %s", e.getMessage()));
            showAlert("Error", "Could not close the window.");
        }
    }

    private void updateTotals() {
        try {
            CartTotals totals = calculateTotals();

            labelTotalItems.setText(String.valueOf(totals.totalItems));
            labelTotalPrice.setText(String.format("$%.2f", totals.totalPrice));
            updateButtonStates();
            
            logger.info(String.format("Totals updated - Items: %d, Total: $%.2f", 
                       totals.totalItems, totals.totalPrice));
            
        } catch (Exception e) {
            logger.severe(String.format("Error in updateTotals: %s", e.getMessage()));
            showAlert("Calculation Error", 
                "Could not calculate totals. Please check the items.");
        }
    }

    private CartTotals calculateTotals() {
        CartTotals totals = new CartTotals();
        
        for (String item : cartData) {
            try {
                ItemDetails details = extractItemDetailsForTotals(item);
                totals.totalItems += details.quantity;
                totals.totalPrice += details.price * details.quantity;
            } catch (Exception e) {
                logger.severe(String.format("Error processing item: %s", item));
            }
        }
        
        return totals;
    }

    private ItemDetails extractItemDetailsForTotals(String item) {
        int pos1 = item.indexOf("|");
        int pos2 = item.indexOf("|", pos1 + 1);
        int pos3 = item.indexOf("|", pos2 + 1);
        
        if (pos1 == -1 || pos2 == -1 || pos3 == -1) {
            throw new IllegalArgumentException("Invalid item format");
        }
        
        String part3 = item.substring(pos2 + 1, pos3).trim();
        String part4 = item.substring(pos3 + 1).trim();
        
        return new ItemDetails(extractQuantityFromString(part3), extractPriceFromString(part4));
    }

    private int extractQuantityFromString(String quantityPart) {
        int colonPos = quantityPart.indexOf(":");
        if (colonPos == -1) {
            throw new IllegalArgumentException("Quantity format invalid");
        }
        String quantityStr = quantityPart.substring(colonPos + 1).trim();
        return Integer.parseInt(quantityStr);
    }

    private double extractPriceFromString(String pricePart) {
        int dollarPos = pricePart.indexOf("$");
        if (dollarPos == -1) {
            throw new IllegalArgumentException("Price format invalid");
        }
        String priceStr = pricePart.substring(dollarPos + 1).trim();
        return Double.parseDouble(priceStr);
    }

    private void showAlert(String title, String message) {
        try {
            logger.info(String.format("Showing alert: %s - %s", title, message));
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
        } catch (Exception e) {
            logger.severe(String.format("Error showing alert: %s", e.getMessage()));
        }
    }

    public void addItemToCart(String user, String game, int quantity, double price) {
        try {
            validateItemParameters(user, game, quantity, price);

            String item = createCartItem(user, game, quantity, price);
            
            cartData.add(item);
            updateTotals();
            
            logger.info(String.format("Item added - User: %s, Game: %s, Quantity: %d, Price: $%.2f",
                       user, game, quantity, price));
            
        } catch (IllegalArgumentException e) {
            logger.severe(String.format("Validation error adding item: %s", e.getMessage()));
            throw e;
            
        } catch (Exception e) {
            logger.severe(String.format("Error adding item to cart: %s", e.getMessage()));
            showAlert("Error", "Could not add item to cart.");
        }
    }

    private void validateItemParameters(String user, String game, int quantity, double price) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User cannot be null or empty");
        }
        if (game == null || game.trim().isEmpty()) {
            throw new IllegalArgumentException("Game cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }

    private String createCartItem(String user, String game, int quantity, double price) {
        return String.format("User: %s | Game: %s | Quantity: %d | Price: $%.2f",
                user, game, quantity, price);
    }

    public void setStage(Stage stage) {
        try {
            this.stage = stage;
        } catch (Exception e) {
            logger.severe(String.format("Error setting stage: %s", e.getMessage()));
        }
    }
    
    public String getCartStatus() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("Cart - Items: %d\n", cartData.size()));
        for (int i = 0; i < cartData.size(); i++) {
            status.append(String.format("  %d: %s\n", i, cartData.get(i)));
        }
        return status.toString();
    }

    // Helper classes for better organization
    private static class CartTotals {
        int totalItems = 0;
        double totalPrice = 0.0;
    }

    private static class ItemDetails {
        int quantity;
        double price;
        
        ItemDetails(int quantity, double price) {
            this.quantity = quantity;
            this.price = price;
        }
    }
}