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
            
            Handler fileHandler = new FileHandler("logs/cart.log", true);
            
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
            
            cartData = FXCollections.observableArrayList();
            listViewCart.setItems(cartData);

            loadExampleData();
            updateTotals();
            updateButtonStates();

            listViewCart.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> showItemDetails(newValue));
            
            logger.info("CartController initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing CartController: " + e.getMessage());
            showAlert("Initialization Error", 
                "Could not initialize the cart. Please restart the application.");
        }
    }

    private void loadExampleData() {
        try {
            logger.info("Loading example data to cart");
            
            cartData.add("User: 1 | Game: FIFA 23 | Quantity: 2 | Price: $59.99");
            cartData.add("User: 1 | Game: Call of Duty | Quantity: 1 | Price: $69.99");
            cartData.add("User: 2 | Game: Minecraft | Quantity: 3 | Price: $24.99");
            cartData.add("User: 3 | Game: GTA V | Quantity: 1 | Price: $39.99");
            
            logger.info("Example data loaded: " + cartData.size() + " items");
            
        } catch (Exception e) {
            logger.severe("Error loading example data: " + e.getMessage());
        }
    }

    // Manual version without split - EASIER TO UNDERSTAND
    private void showItemDetails(String item) {
        try {
            if (item != null) {
                selectedIndex = listViewCart.getSelectionModel().getSelectedIndex();
                logger.info("Item selected - Index: " + selectedIndex);
                
                // Find the first |
                int pos1 = item.indexOf("|");
                if (pos1 == -1) {
                    logger.severe("Invalid item format - no | found");
                    return;
                }
                String part1 = item.substring(0, pos1).trim(); // "User: 1"
                
                // Find the second |
                int pos2 = item.indexOf("|", pos1 + 1);
                if (pos2 == -1) {
                    logger.severe("Invalid item format - second | not found");
                    return;
                }
                String part2 = item.substring(pos1 + 1, pos2).trim(); // "Game: FIFA 23"
                
                // Find the third |
                int pos3 = item.indexOf("|", pos2 + 1);
                if (pos3 == -1) {
                    logger.severe("Invalid item format - third | not found");
                    return;
                }
                String part3 = item.substring(pos2 + 1, pos3).trim(); // "Quantity: 2"
                
                // What remains after the third |
                String part4 = item.substring(pos3 + 1).trim(); // "Price: $59.99"
                
                // Now extract the game name from part2
                int colonPos = part2.indexOf(":");
                if (colonPos != -1) {
                    String gameName = part2.substring(colonPos + 1).trim();
                    labelSelectedItem.setText(gameName);
                }
                
                // Extract quantity from part3
                colonPos = part3.indexOf(":");
                if (colonPos != -1) {
                    String quantityStr = part3.substring(colonPos + 1).trim();
                    currentQuantity = Integer.parseInt(quantityStr);
                    labelCurrentQuantity.setText(String.valueOf(currentQuantity));
                    
                    logger.info("Details extracted - Quantity: " + currentQuantity);
                }
                
                updateButtonStates();
                
            } else {
                selectedIndex = -1;
                labelSelectedItem.setText("Select an item");
                labelCurrentQuantity.setText("0");
                updateButtonStates();
            }
            
        } catch (NumberFormatException e) {
            logger.severe("Format error extracting quantity from item: " + e.getMessage());
            showAlert("Format Error", "The format of the selected item is incorrect.");
            
        } catch (Exception e) {
            logger.severe("Error in showItemDetails: " + e.getMessage());
            showAlert("Error", "Could not process the selected item.");
        }
    }

    @FXML
    private void increaseQuantity() {
        try {
            if (selectedIndex >= 0) {
                logger.info("Increasing quantity - Index: " + selectedIndex);
                
                currentQuantity++;
                updateItemQuantity();
                
                logger.info("Quantity increased to: " + currentQuantity);
            } else {
                logger.severe("Attempt to increase quantity without selected item");
            }
        } catch (Exception e) {
            logger.severe("Error in increaseQuantity: " + e.getMessage());
        }
    }

    @FXML
    private void decreaseQuantity() {
        try {
            if (selectedIndex >= 0 && currentQuantity > 1) {
                logger.info("Decreasing quantity - Index: " + selectedIndex);
                
                currentQuantity--;
                updateItemQuantity();
                
                logger.info("Quantity decreased to: " + currentQuantity);
                
            } else if (currentQuantity == 1) {
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
            } else {
                logger.severe("Attempt to decrease quantity without selected item");
            }
        } catch (Exception e) {
            logger.severe("Error in decreaseQuantity: " + e.getMessage());
        }
    }

    private void updateItemQuantity() {
        try {
            if (selectedIndex >= 0) {
                String originalItem = cartData.get(selectedIndex);
                
                // Manual split for the item
                int pos1 = originalItem.indexOf("|");
                int pos2 = originalItem.indexOf("|", pos1 + 1);
                int pos3 = originalItem.indexOf("|", pos2 + 1);
                
                if (pos1 != -1 && pos2 != -1 && pos3 != -1) {
                    String part1 = originalItem.substring(0, pos1).trim();
                    String part2 = originalItem.substring(pos1 + 1, pos2).trim();
                    String part4 = originalItem.substring(pos3 + 1).trim();
                    
                    // Build new item with updated quantity
                    String newItem = part1 + " | " + part2 + " | Quantity: " + 
                                    currentQuantity + " | " + part4;

                    cartData.set(selectedIndex, newItem);
                    listViewCart.refresh();
                    labelCurrentQuantity.setText(String.valueOf(currentQuantity));
                    updateTotals();
                    updateButtonStates();
                    
                    logger.info("Quantity updated - New quantity: " + currentQuantity);
                } else {
                    logger.severe("Invalid item format when updating quantity");
                }
            } else {
                logger.severe("Attempt to update quantity without selected item");
            }
        } catch (Exception e) {
            logger.severe("Error in updateItemQuantity: " + e.getMessage());
            showAlert("Error", "Could not update item quantity.");
        }
    }

    private void updateButtonStates() {
        try {
            boolean hasItemSelected = (selectedIndex >= 0);

            buttonPlus.setDisable(!hasItemSelected);
            buttonMinus.setDisable(!hasItemSelected || currentQuantity <= 1);
            buttonDelete.setDisable(!hasItemSelected);
            buttonBuy.setDisable(cartData.isEmpty());
            
        } catch (Exception e) {
            logger.severe("Error in updateButtonStates: " + e.getMessage());
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

            logger.info("Starting purchase process - Items: " + cartData.size());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Purchase");
            alert.setHeaderText("Make purchase?");
            alert.setContentText("Total to pay: " + labelTotalPrice.getText());

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeYes) {
                    logger.info("User confirmed purchase - Total: " + labelTotalPrice.getText());
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Purchase Completed");
                    success.setHeaderText("Purchase successful!");
                    success.setContentText("Your purchase has been completed successfully.");
                    success.showAndWait();

                    logger.info("Clearing cart after purchase");
                    cartData.clear();
                    updateTotals();
                    clearSelection();
                    
                    logger.info("Purchase completed successfully");
                    
                } else {
                    logger.info("User canceled purchase");
                }
            });
            
        } catch (Exception e) {
            logger.severe("Error in buy: " + e.getMessage());
            showAlert("Purchase Error", 
                "An error occurred processing the purchase. Please try again.");
        }
    }

    @FXML
    private void deleteItem() {
        try {
            if (selectedIndex >= 0) {
                String itemToDelete = cartData.get(selectedIndex);
                logger.info("Requesting to delete item: " + itemToDelete);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Delete this item from cart?");
                alert.setContentText("This action cannot be undone.");

                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                alert.showAndWait().ifPresent(response -> {
                    if (response == buttonTypeYes) {
                        logger.info("User confirmed to delete the item");
                        cartData.remove(selectedIndex);
                        updateTotals();
                        clearSelection();
                        
                        logger.info("Item deleted successfully");
                    }
                });
            } else {
                logger.severe("Attempt to delete without selected item");
                showAlert("Select Item", "Please select an item to delete.");
            }
        } catch (Exception e) {
            logger.severe("Error in deleteItem: " + e.getMessage());
            showAlert("Error", "Could not delete item from cart.");
        }
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
            logger.severe("Error in clearSelection: " + e.getMessage());
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
            logger.severe("Error closing cart window: " + e.getMessage());
            showAlert("Error", "Could not close the window.");
        }
    }

    private void updateTotals() {
        try {
            int totalItems = 0;
            double totalPrice = 0.0;

            for (String item : cartData) {
                try {
                    // Manual split for each item
                    int pos1 = item.indexOf("|");
                    int pos2 = item.indexOf("|", pos1 + 1);
                    int pos3 = item.indexOf("|", pos2 + 1);
                    
                    if (pos1 != -1 && pos2 != -1 && pos3 != -1) {
                        String part3 = item.substring(pos2 + 1, pos3).trim(); // "Quantity: 2"
                        String part4 = item.substring(pos3 + 1).trim(); // "Price: $59.99"
                        
                        // Extract quantity
                        int colonPos = part3.indexOf(":");
                        if (colonPos != -1) {
                            String quantityStr = part3.substring(colonPos + 1).trim();
                            int quantity = Integer.parseInt(quantityStr);
                            
                            // Extract price
                            colonPos = part4.indexOf("$");
                            if (colonPos != -1) {
                                String priceStr = part4.substring(colonPos + 1).trim();
                                double price = Double.parseDouble(priceStr);
                                
                                totalItems += quantity;
                                totalPrice += price * quantity;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.severe("Error processing item: " + item);
                }
            }

            labelTotalItems.setText(String.valueOf(totalItems));
            labelTotalPrice.setText(String.format("$%.2f", totalPrice));
            updateButtonStates();
            
            logger.info("Totals updated - Items: " + totalItems + ", Total: $" + String.format("%.2f", totalPrice));
            
        } catch (Exception e) {
            logger.severe("Error in updateTotals: " + e.getMessage());
            showAlert("Calculation Error", 
                "Could not calculate totals. Please check the items.");
        }
    }

    private void showAlert(String title, String message) {
        try {
            logger.info("Showing alert: " + title + " - " + message);
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
        } catch (Exception e) {
            logger.severe("Error showing alert: " + e.getMessage());
        }
    }

    public void addItemToCart(String user, String game, int quantity, double price) {
        try {
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

            String item = String.format("User: %s | Game: %s | Quantity: %d | Price: $%.2f",
                    user, game, quantity, price);
            
            cartData.add(item);
            updateTotals();
            
            logger.info("Item added - User: " + user + ", Game: " + game + 
                       ", Quantity: " + quantity + ", Price: $" + price);
            
        } catch (IllegalArgumentException e) {
            logger.severe("Validation error adding item: " + e.getMessage());
            throw e;
            
        } catch (Exception e) {
            logger.severe("Error adding item to cart: " + e.getMessage());
            showAlert("Error", "Could not add item to cart.");
        }
    }

    public void setStage(Stage stage) {
        try {
            this.stage = stage;
        } catch (Exception e) {
            logger.severe("Error setting stage: " + e.getMessage());
        }
    }
    
    public String getCartStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Cart - Items: ").append(cartData.size()).append("\n");
        for (int i = 0; i < cartData.size(); i++) {
            status.append("  ").append(i).append(": ").append(cartData.get(i)).append("\n");
        }
        return status.toString();
    }
}