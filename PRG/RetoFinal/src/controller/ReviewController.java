package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.logging.*;

public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class.getName());
    private static boolean loggerInitialized = false;
    
    @FXML
    private Slider sliderRating;
    @FXML
    private Label labelRating;
    @FXML
    private Label labelCharacterCount;
    @FXML
    private Label labelGame;
    @FXML
    private TextArea textAreaComment;
    @FXML
    private Button buttonSubmit;
    @FXML
    private Button buttonCancel;
    @FXML
    private ImageView star1, star2, star3, star4, star5;

    private Stage stage;
    private String gameName = "";
    private int userId = 0;
    private int gameId = 0;

    private Image starFull;
    private Image starEmpty;
    private Image starHalf;

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
            
            Handler fileHandler = new FileHandler("logs/reviews.log", true);
            
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
            logger.info("ReviewController logger initialized");
            
        } catch (Exception e) {
            System.err.println("ERROR initializing logger: " + e.getMessage());
            loggerInitialized = true;
        }
    }

    @FXML
    private void initialize() {
        try {
            logger.info("Initializing ReviewController");
            
            loadStarImages();
            configureSlider();
            configureTextArea();
            updateVisualStars(sliderRating.getValue());
            updateSubmitButtonState();
            
            logger.info("ReviewController initialized successfully. Game: " + 
                       (gameName.isEmpty() ? "Not configured" : gameName));
            
        } catch (Exception e) {
            logger.severe("Critical error initializing ReviewController: " + e.getMessage());
            showAlert("Initialization Error", 
                "Could not initialize the review window. Please restart the application.");
        }
    }

    private void loadStarImages() {
        try {
            logger.info("Loading star images");
            
            starFull = new Image(getClass().getResourceAsStream("/images/star_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
            starEmpty = new Image(getClass().getResourceAsStream("/images/star_border_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
            starHalf = new Image(getClass().getResourceAsStream("/images/star_half_32dp_FFC107_FILL0_wght400_GRAD0_opsz40.png"));
            
            if (starFull.isError() || starEmpty.isError() || starHalf.isError()) {
                logger.warning("Some star images could not be loaded correctly");
            } else {
                logger.info("Star images loaded successfully");
            }
            
        } catch (NullPointerException e) {
            logger.severe("Star images not found in specified path: " + e.getMessage());
            showAlert("Resource Error", 
                "Could not load necessary images. Contact the administrator.");
            
        } catch (Exception e) {
            logger.severe("Error loading star images: " + e.getMessage());
        }
    }

    private void configureSlider() {
        try {
            logger.info("Configuring rating slider");
            
            sliderRating.setMin(0);
            sliderRating.setMax(5);
            sliderRating.setValue(2.5);
            sliderRating.setBlockIncrement(0.5);
            sliderRating.setMajorTickUnit(1.0);
            sliderRating.setMinorTickCount(1);
            sliderRating.setShowTickLabels(true);
            sliderRating.setShowTickMarks(true);
            sliderRating.setSnapToTicks(true);

            sliderRating.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double value = Math.round(newValue.doubleValue() * 2) / 2.0;
                    labelRating.setText(String.format("%.1f", value));
                    updateVisualStars(value);
                    
                    logger.info("Slider updated - Value: " + value);
                    
                } catch (Exception e) {
                    logger.warning("Error updating slider: " + e.getMessage());
                }
            });
            
            logger.info("Slider configured successfully");
            
        } catch (Exception e) {
            logger.severe("Error configuring slider: " + e.getMessage());
        }
    }

    private void updateVisualStars(double rating) {
        try {
            ImageView[] stars = {star1, star2, star3, star4, star5};

            for (int i = 0; i < stars.length; i++) {
                if (rating >= i + 1) {
                    stars[i].setImage(starFull);
                } else if (rating >= i + 0.5) {
                    stars[i].setImage(starHalf);
                } else {
                    stars[i].setImage(starEmpty);
                }
            }
            
            logger.info("Visual stars updated - Rating: " + rating);
            
        } catch (NullPointerException e) {
            logger.warning("Error updating stars (images not loaded): " + e.getMessage());
        } catch (Exception e) {
            logger.warning("Unexpected error updating visual stars: " + e.getMessage());
        }
    }

    private void configureTextArea() {
        try {
            logger.info("Configuring comment TextArea");
            
            final int MAX_CHARACTERS = 500;

            textAreaComment.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    int characters = newValue.length();
                    labelCharacterCount.setText(characters + "/" + MAX_CHARACTERS + " characters");

                    if (characters > MAX_CHARACTERS * 0.9) {
                        labelCharacterCount.setStyle("-fx-text-fill: #FF5722;");
                    } else if (characters > MAX_CHARACTERS * 0.75) {
                        labelCharacterCount.setStyle("-fx-text-fill: #FF9800;");
                    } else {
                        labelCharacterCount.setStyle("-fx-text-fill: #666666;");
                    }

                    if (newValue.length() > MAX_CHARACTERS) {
                        textAreaComment.setText(oldValue);
                        logger.info("Character limit reached (" + MAX_CHARACTERS + ")");
                    }

                    updateSubmitButtonState();
                    
                    logger.info("Character counter updated: " + characters);
                    
                } catch (Exception e) {
                    logger.warning("Error in TextArea listener: " + e.getMessage());
                }
            });
            
            logger.info("TextArea configured successfully (limit: " + MAX_CHARACTERS + " characters)");
            
        } catch (Exception e) {
            logger.severe("Error configuring TextArea: " + e.getMessage());
        }
    }

    private void updateSubmitButtonState() {
        try {
            boolean hasComment = !textAreaComment.getText().trim().isEmpty();
            buttonSubmit.setDisable(!hasComment);
            
            logger.info("Submit button state updated - Enabled: " + hasComment);
            
        } catch (Exception e) {
            logger.warning("Error updating submit button state: " + e.getMessage());
        }
    }

    @FXML
    private void submitReview() {
        try {
            logger.info("Starting review submission - User: " + userId + 
                       ", Game: " + gameName);
            
            if (!validateReview()) {
                logger.warning("Validation failed when submitting review");
                return;
            }

            double rating = Math.round(sliderRating.getValue() * 2) / 2.0;
            String comment = textAreaComment.getText().trim();

            logger.info("Review data prepared - Rating: " + rating + 
                       ", Characters: " + comment.length());

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Submission");
            confirmation.setHeaderText("Submit Rating?");
            confirmation.setContentText(String.format("Rating: %.1f/5.0\n\nDo you want to submit this rating?", rating));

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            confirmation.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            confirmation.showAndWait().ifPresent(response -> {
                try {
                    if (response == buttonTypeYes) {
                        logger.info("User confirmed review submission");
                        
                        saveReviewToDB(rating, comment);

                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Rating Submitted");
                        success.setHeaderText("Thank you for your rating!");
                        success.setContentText("Your review has been published successfully.");
                        success.showAndWait();

                        logger.info("Review submitted successfully");
                        
                        cancel();
                        
                    } else {
                        logger.info("User cancelled review submission");
                    }
                } catch (Exception e) {
                    logger.severe("Error processing review confirmation: " + e.getMessage());
                    showAlert("Error", "Could not process your rating. Please try again.");
                }
            });
            
        } catch (Exception e) {
            logger.severe("Critical error in submitReview: " + e.getMessage());
            showAlert("System Error", 
                "An error occurred while submitting your rating. Please try again later.");
        }
    }

    private boolean validateReview() {
        try {
            logger.info("Validating review data");
            
            String comment = textAreaComment.getText().trim();
            if (comment.isEmpty()) {
                logger.warning("Validation failed: empty comment");
                showAlert("Error", "Please write a comment before submitting.");
                return false;
            }

            if (comment.length() < 10) {
                logger.warning("Validation failed: comment too short (" + comment.length() + " characters)");
                showAlert("Error", "The comment must have at least 10 characters.");
                return false;
            }

            double rating = sliderRating.getValue();
            if (rating < 0 || rating > 5) {
                logger.warning("Validation failed: rating out of range (" + rating + ")");
                showAlert("Error", "The rating must be between 0 and 5.");
                return false;
            }

            if (gameName.isEmpty() || gameId == 0) {
                logger.severe("Validation failed: game not configured");
                showAlert("Error", "A valid game has not been selected.");
                return false;
            }

            if (userId == 0) {
                logger.warning("Validation failed: user not configured or ID=0");
                showAlert("Error", "User has not been identified.");
                return false;
            }

            logger.info("Validation successful - Rating: " + rating + 
                       ", Characters: " + comment.length());
            return true;
            
        } catch (Exception e) {
            logger.severe("Error in validateReview: " + e.getMessage());
            showAlert("Validation Error", 
                "An error occurred while validating data. Please check the information.");
            return false;
        }
    }

    private void saveReviewToDB(double rating, String comment) {
        try {
            logger.info("Saving review to DB - UserID: " + userId + 
                       ", GameID: " + gameId + 
                       ", Rating: " + rating);
            
            logger.info("Simulating save to DB:");
            logger.info("  User ID: " + userId);
            logger.info("  Game ID: " + gameId);
            logger.info("  Game: " + gameName);
            logger.info("  Rating: " + rating);
            logger.info("  Comment (length): " + comment.length() + " characters");
            logger.info("  Comment (preview): " + 
                       (comment.length() > 50 ? comment.substring(0, 50) + "..." : comment));

            // TODO: Implement real database connection
            // Example:
            // Connection conn = DatabaseManager.getConnection();
            // PreparedStatement stmt = conn.prepareStatement(
            //     "INSERT INTO reviews (user_id, game_id, rating, comment) VALUES (?, ?, ?, ?)");
            // stmt.setInt(1, userId);
            // stmt.setInt(2, gameId);
            // stmt.setDouble(3, rating);
            // stmt.setString(4, comment);
            // int rowsAffected = stmt.executeUpdate();
            
        } catch (Exception e) {
            logger.severe("Error saving review to DB: " + e.getMessage());
            throw new RuntimeException("Could not save the rating", e);
        }
    }

    @FXML
    private void cancel() {
        try {
            logger.info("Review cancellation requested");
            
            if (!textAreaComment.getText().trim().isEmpty()) {
                logger.info("Text exists, requesting confirmation");
                
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Cancellation");
                confirmation.setHeaderText("Discard Rating?");
                confirmation.setContentText("You have a rating written. Are you sure you want to cancel?");

                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");
                confirmation.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                confirmation.showAndWait().ifPresent(response -> {
                    try {
                        if (response == buttonTypeYes) {
                            logger.info("User confirmed cancel review");
                            closeWindow();
                        } else {
                            logger.info("User cancelled exit action");
                        }
                    } catch (Exception e) {
                        logger.severe("Error processing cancellation confirmation: " + e.getMessage());
                    }
                });
            } else {
                logger.info("No text written, closing directly");
                closeWindow();
            }
            
        } catch (Exception e) {
            logger.severe("Error in cancel: " + e.getMessage());
            showAlert("Error", "Could not cancel the operation.");
        }
    }

    private void closeWindow() {
        try {
            logger.info("Closing review window");
            
            stage = (Stage) buttonCancel.getScene().getWindow();
            stage.close();
            
            logger.info("Review window closed successfully");
            
        } catch (NullPointerException e) {
            logger.severe("Error closing window: stage not initialized");
            showAlert("Error", "Could not close the window. Try closing it manually.");
        } catch (Exception e) {
            logger.severe("Error closing window: " + e.getMessage());
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

    public void setGame(String name, int gameId) {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Game name cannot be null or empty");
            }
            if (gameId <= 0) {
                throw new IllegalArgumentException("Game ID must be greater than 0");
            }
            
            this.gameName = name;
            this.gameId = gameId;
            labelGame.setText("Game: " + name);
            
            logger.info("Game configured - Name: " + name + ", ID: " + gameId);
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error configuring game: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Unexpected error configuring game: " + e.getMessage());
        }
    }

    public void setUser(int userId) {
        try {
            if (userId <= 0) {
                throw new IllegalArgumentException("User ID must be greater than 0");
            }
            
            this.userId = userId;
            logger.info("User configured - ID: " + userId);
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error configuring user: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Unexpected error configuring user: " + e.getMessage());
        }
    }

    public void setStage(Stage stage) {
        try {
            if (stage == null) {
                throw new IllegalArgumentException("Stage cannot be null");
            }
            
            this.stage = stage;
            logger.info("Stage configured in ReviewController");
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error configuring stage: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Unexpected error configuring stage: " + e.getMessage());
        }
    }

    public void loadExistingReview(double rating, String comment) {
        try {
            logger.info("Loading existing review for editing");
            
            if (rating < 0 || rating > 5) {
                throw new IllegalArgumentException("Rating out of range: " + rating);
            }
            
            if (comment == null) {
                throw new IllegalArgumentException("Comment cannot be null");
            }
            
            sliderRating.setValue(rating);
            textAreaComment.setText(comment);
            updateVisualStars(rating);
            
            logger.info("Existing review loaded - Rating: " + rating + 
                       ", Characters: " + comment.length());
            
        } catch (IllegalArgumentException e) {
            logger.severe("Validation error loading existing review: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error loading existing review: " + e.getMessage());
            showAlert("Error", "Could not load the review for editing.");
        }
    }
    
    public String getReviewInfo() {
        try {
            double rating = Math.round(sliderRating.getValue() * 2) / 2.0;
            String comment = textAreaComment.getText().trim();
            
            return String.format(
                "Review Info:\n" +
                "  User ID: %d\n" +
                "  Game: %s (ID: %d)\n" +
                "  Rating: %.1f/5.0\n" +
                "  Comment: %d characters\n" +
                "  Submit button: %s",
                userId,
                gameName,
                gameId,
                rating,
                comment.length(),
                buttonSubmit.isDisabled() ? "DISABLED" : "ENABLED"
            );
            
        } catch (Exception e) {
            return "Error getting review info: " + e.getMessage();
        }
    }
}