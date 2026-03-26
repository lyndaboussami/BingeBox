package groupna.projectNetflix.controllers;

import java.util.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.*;

public class MainViewController {
	@FXML private BorderPane rootPane;
    @FXML private Button themeToggle;
    @FXML private StackPane heroSection;
    @FXML private ComboBox<String> languageSelector;
    @FXML private MediaView trailerVideo;
    private MediaPlayer mediaPlayer;
    @FXML private Button homeBtn;
    @FXML private Button moviesBtn;
    @FXML private Button seriesBtn;
    @FXML private Button subscribeBtn;
    @FXML private Label heroTitle;
    @FXML private Label heroDesc;
    @FXML private Label selectionTitle;
    @FXML private Label recommendedTitle;
    @FXML private HBox navbar;

    private double xOffset = 0;
    private double yOffset = 0;
    private void updateLanguage(String langCode) {
        Locale locale = new Locale(langCode);
        ResourceBundle bundle = ResourceBundle.getBundle("groupna.projectNetflix.languages.bundle", locale);

        homeBtn.setText(bundle.getString("navbar.home"));
        moviesBtn.setText(bundle.getString("navbar.movies"));
        seriesBtn.setText(bundle.getString("navbar.series"));

        heroTitle.setText(bundle.getString("hero.title"));
        heroDesc.setText(bundle.getString("hero.description"));

        selectionTitle.setText(bundle.getString("category.selection"));
        recommendedTitle.setText(bundle.getString("category.recommended"));
    }
    
    @FXML
    public void initialize() {
        try {
            var resource = getClass().getResource("/groupna/projectNetflix/assets/Beast (2026) Movie Trailer.mp4");
            if (resource != null) {
                String videoPath = resource.toExternalForm();
                Media media = new Media(videoPath);
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true); 
                trailerVideo.setMediaPlayer(mediaPlayer);
            
                trailerVideo.fitWidthProperty().bind(heroSection.widthProperty());
                trailerVideo.fitHeightProperty().bind(heroSection.heightProperty());
            } else {
                System.out.println("Video file not found in assets!");
            }
        } catch (Exception e) {
            System.err.println("Error loading media: " + e.getMessage());
        }
        languageSelector.setOnAction(e -> {
            String selected = languageSelector.getValue();
            if (selected.contains("Français")) {
                updateLanguage("fr");
            } else if (selected.contains("Tunisian")) {
                updateLanguage("tn");
            } else {
                updateLanguage("en");
            }
        });
        if (navbar != null) {
            navbar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            navbar.setOnMouseDragged(event -> {
                Stage stage = (Stage) navbar.getScene().getWindow();
                if (!stage.isMaximized()) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
        }
        Platform.runLater(() -> {
            Stage stage = (Stage) navbar.getScene().getWindow();
            makeResizable(stage, navbar.getScene().getRoot());
        });
    }
    public void makeResizable(Stage stage, Node root) {
        final int RESIZE_MARGIN = 5;

        root.setOnMouseMoved(event -> {
            double x = event.getX();
            double y = event.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Cursor cursor = Cursor.DEFAULT;

            if (x < RESIZE_MARGIN && y < RESIZE_MARGIN) cursor = Cursor.NW_RESIZE;
            else if (x < RESIZE_MARGIN && y > height - RESIZE_MARGIN) cursor = Cursor.SW_RESIZE;
            else if (x > width - RESIZE_MARGIN && y < RESIZE_MARGIN) cursor = Cursor.NE_RESIZE;
            else if (x > width - RESIZE_MARGIN && y > height - RESIZE_MARGIN) cursor = Cursor.SE_RESIZE;
            else if (x < RESIZE_MARGIN) cursor = Cursor.W_RESIZE;
            else if (x > width - RESIZE_MARGIN) cursor = Cursor.E_RESIZE;
            else if (y < RESIZE_MARGIN) cursor = Cursor.N_RESIZE;
            else if (y > height - RESIZE_MARGIN) cursor = Cursor.S_RESIZE;

            root.setCursor(cursor);
        });

        root.setOnMouseDragged(event -> {
            if (root.getCursor() != Cursor.DEFAULT) {
                double x = event.getScreenX();
                double y = event.getScreenY();

                if (root.getCursor() == Cursor.E_RESIZE || root.getCursor() == Cursor.SE_RESIZE || root.getCursor() == Cursor.NE_RESIZE) {
                    stage.setWidth(event.getX());
                }
                if (root.getCursor() == Cursor.S_RESIZE || root.getCursor() == Cursor.SE_RESIZE || root.getCursor() == Cursor.SW_RESIZE) {
                    stage.setHeight(event.getY());
                }
            }
        });
    }
    @FXML
    private void handleClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }
    
    @FXML
    private void playTrailer(MouseEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            trailerVideo.setOpacity(1.0);
        }
    }

    @FXML
    private void stopTrailer(MouseEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            trailerVideo.setOpacity(0.0);
        }
    }

    //à modifier pour correspondre à chaque film/série
    @FXML
    private void showDetails(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        Label title = (Label) card.getChildren().get(1);
        
        Tooltip info = new Tooltip(
            "Title: " + title.getText() + "\n" +
            "Genre: Drama/Action\n" +
            "Rating: ★★★★☆\n" +
            "Duration: 2h 15m"
        );
        info.setShowDelay(javafx.util.Duration.millis(100));
        Tooltip.install(card, info);
    }

    @FXML
    private void hideDetails(MouseEvent event) {
        VBox card = (VBox) event.getSource();
    }
    
	private void loadPage(String fxml, ActionEvent event) {

	    try {
	        Parent root = FXMLLoader.load(
	            getClass().getResource("/groupna/projectNetflix/view/" + fxml)
	        );

	        Stage stage = (Stage)((Node)event.getSource())
	                        .getScene().getWindow();

	        stage.setScene(new Scene(root));

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	private void handleThemeChange(ActionEvent event) {
	    Parent root = themeToggle.getScene().getRoot();
	    
	    if (root.getStyleClass().contains("light-mode")) {
	        root.getStyleClass().remove("light-mode");
	        themeToggle.setText("🌛");
	    } else {
	        root.getStyleClass().add("light-mode");
	        themeToggle.setText("🔆");
	    }
	}
	
    @FXML
    private void goHome(ActionEvent event){
        loadPage("MainView.fxml",event);
    }

    @FXML
    private void openMovies(ActionEvent event){
        loadPage("MoviesView.fxml",event);
    }

    @FXML
    private void openSeries(ActionEvent event){
        loadPage("SeriesView.fxml",event);

    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        //à ajouter: filter movie list
    }
    
    @FXML
    private void handleProfile(ActionEvent event) {
        loadPage("ProfileView.fxml",event);
    }
    
    @FXML
    private void handleHome(ActionEvent event) {
        //à ajouter: Logic to scroll to top or reset view
    }

    @FXML
    private void handleSeries(ActionEvent event) {
        // à ajouter: Filter view for Series
    }

    @FXML
    private void handleMovies(ActionEvent event) {
        //à ajouter: Filter view for Movies
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        //à ajouter: Show watch history
    }
    
    @FXML
    private void handleSettings(ActionEvent event) {
        loadPage("SettingsView.fxml",event);
    }

    @FXML
    private void handleFavorites() {
    	loadPage("Favoris.fxml",null);
    }
}
