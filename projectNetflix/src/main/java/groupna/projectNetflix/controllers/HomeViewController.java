package groupna.projectNetflix.controllers;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.services.FilmService;
import groupna.projectNetflix.services.SerieService;
import groupna.projectNetflix.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.media.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.*;

public class HomeViewController extends BaseController{
	private FilmService filmService=new FilmService();
	private SerieService serieService=new SerieService();
    @FXML private StackPane heroSection;
    @FXML private MediaView trailerVideo;
    @FXML private Label heroTitle;
    @FXML private Label heroDesc;
    @FXML private HBox heroMetaBox, selectionRow, mostWatchedRow;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        loadContent();
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

    	List<Film> movies = filmService.getAllFilms();
        List<Serie> series = serieService.getAllSeries();
        
        if (!movies.isEmpty()) {
            setupHero(movies.get(movies.size() - 1));
        }

        //Mix first 3 movies and first 2 series
        movies.stream().limit(3).forEach(m -> selectionRow.getChildren().add(createCard(m)));
        series.stream().limit(2).forEach(s -> selectionRow.getChildren().add(createCard(s)));

        // MOST WATCHED: For now, we mix them (Logic: sort by a 'views' property)
        //"newest" as "trending"
        for (int i = movies.size() - 1; i >= 0 && i > movies.size() - 4; i--) {
            mostWatchedRow.getChildren().add(createCard(movies.get(i)));
        }
    }

    private void setupHero(Film movie) {
        heroTitle.setText(movie.getTitre().toUpperCase());
        heroDesc.setText(movie.getResume());
        
        heroMetaBox.getChildren().clear();
        heroMetaBox.getChildren().addAll(
            createMetaTag(movie.getDateDeSortie() + ""),
            createMetaTag(movie.getDuree()+ "")
        );

        try {
            String videoPath = movie.getPathTrailer();
            if (videoPath != null) {
                Media media = new Media(getClass().getResource(videoPath).toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true);
                
                trailerVideo.setMediaPlayer(mediaPlayer);
                
                Rectangle clip = new Rectangle();
                clip.widthProperty().bind(heroSection.widthProperty());
                clip.heightProperty().bind(heroSection.heightProperty());
                
                heroSection.setClip(clip);
                trailerVideo.setPreserveRatio(true);
                
                
                trailerVideo.fitWidthProperty().bind(heroSection.widthProperty());
            }
        } catch (Exception e) {
            System.err.println("Hero video failed to load: " + e.getMessage());
        }
    }
    
    private Label createMetaTag(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("meta-tag");
        return l;
    }
    
    private VBox createCard(Object data) {
        VBox card = new VBox();
        card.getStyleClass().add("movieCard");
        card.setSpacing(10);
        
        card.setUserData(data); 

        MainViewController mainCtrl = MainViewController.getInstance();
        if (mainCtrl != null) {
            card.setOnMouseEntered(event -> mainCtrl.showDetails(event));
            card.setOnMouseExited(event -> mainCtrl.hideDetails(event));
        }

        StackPane imageContainer = new StackPane();
        
        
        ImageView posterView = new ImageView();
        posterView.setFitWidth(200);
        posterView.setFitHeight(300);
        posterView.setPreserveRatio(false);
        posterView.getStyleClass().add("movie-poster");

        Rectangle clip = new Rectangle(200, 300);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        posterView.setClip(clip);
        
        Label typeIcon = new Label();
        typeIcon.getStyleClass().add("card-type-badge");
        
        if (data instanceof Film) {
            typeIcon.setText("🎬");
        } else if (data instanceof Serie) {
            typeIcon.setText("📺");
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