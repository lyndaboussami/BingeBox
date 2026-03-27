package groupna.projectNetflix.controllers;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.media.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.*;

public class HomeViewController extends BaseController{
	
    @FXML private StackPane heroSection;
    @FXML private MediaView trailerVideo;
    @FXML private Label heroTitle;
    @FXML private Label heroDesc;
    @FXML private Label selectionTitle;
    @FXML private Label recommendedTitle;

    private MediaPlayer mediaPlayer;

    @FXML private HBox moviesRow;
    @FXML private HBox seriesRow;
    
    @FXML
    public void initialize() {
        setupVideo();
        loadContent();
    }

    private void setupVideo() {
        try {
            var resource = getClass().getResource("/groupna/projectNetflix/assets/Beast (2026) Movie Trailer.mp4");
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true);
                trailerVideo.setMediaPlayer(mediaPlayer);

                trailerVideo.fitWidthProperty().bind(heroSection.widthProperty());
                trailerVideo.fitHeightProperty().bind(heroSection.heightProperty());
            }
        } catch (Exception e) {
            System.out.println("Video failed: " + e.getMessage());
        }
    }

    @FXML
    private void playTrailer() { 
    	if (mediaPlayer != null) mediaPlayer.play(); 
    }

    @FXML
    private void stopTrailer() { 
    	if (mediaPlayer != null) mediaPlayer.stop(); 
    }
    
    private void loadContent() {
        // Mock Data in the future from Database

    	List<Film> movies = DataStore.getMovies();
        List<Serie> series = DataStore.getSeries();
        
        movies.forEach(m -> moviesRow.getChildren().add(createCard(m)));
        series.forEach(s -> seriesRow.getChildren().add(createCard(s)));
    }

    private VBox createCard(Object data) {
        VBox card = new VBox();
        card.getStyleClass().add("movieCard");
        card.setSpacing(10);
        
        //////////////////////////////////////////////////////////////
     // 1. "Hide" the data in the card
        card.setUserData(data); 

        // 2. Link to the MainViewController for the hover effect
        MainViewController mainCtrl = MainViewController.getInstance();
        if (mainCtrl != null) {
            card.setOnMouseEntered(event -> mainCtrl.showDetails(event));
            card.setOnMouseExited(event -> mainCtrl.hideDetails(event));
        }

            // ... (rest of your existing code for ImageViews, Labels, and Clicks)


        StackPane imageContainer = new StackPane();
        
        
        ImageView posterView = new ImageView();
        posterView.setFitWidth(140);
        posterView.setFitHeight(200);
        posterView.setPreserveRatio(false);
        posterView.getStyleClass().add("movie-poster");

        Rectangle clip = new Rectangle(140, 200);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        posterView.setClip(clip);
        
        Label typeIcon = new Label();
        typeIcon.getStyleClass().add("card-type-badge");
        
        if (data instanceof Film) {
            typeIcon.setText("🎬"); // Movie Icon
        } else if (data instanceof Serie) {
            typeIcon.setText("📺"); // Series Icon
        }
        
        StackPane.setAlignment(typeIcon, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(typeIcon, new javafx.geometry.Insets(8));

        imageContainer.getChildren().addAll(posterView, typeIcon);
        
        ResourceBundle bundle = Session.getInstance().getBundle();
        
        String titleKey = (data instanceof Film f) ? "movie." + f.getId() + ".title" : "serie." + ((Serie)data).getId() + ".title";
        String title;
        try {
            title = bundle.getString(titleKey);
        } catch (MissingResourceException e) {
            // If the key is missing, fallback to the object's default title
            title = (data instanceof Film f) ? f.getTitre() : ((Serie)data).getTitre();
        }

        Label titleLabel = new Label(title);
        String imagePath = (data instanceof Film film) ? film.getPathPoster() : ((Serie) data).getPathPoster();

        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                Image img = new Image(getClass().getResourceAsStream(imagePath));
                posterView.setImage(img);
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
        }

        titleLabel.getStyleClass().add("card-text");

        card.getChildren().addAll(imageContainer, titleLabel);

        card.setOnMouseClicked(event -> {
            if (data instanceof Film) {
                MainViewController.getInstance().loadDetailPage("MovieDetailView.fxml", data);
            } else if (data instanceof Serie) {
                MainViewController.getInstance().loadDetailPage("SeriesDetailView.fxml", data);
            }
        });

        return card;
    }
}