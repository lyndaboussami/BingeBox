package groupna.projectNetflix.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import groupna.projectNetflix.DAO.DAOStatics;
import groupna.projectNetflix.entities.Categorie;
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

public class HomeViewController{
	private FilmService filmService=new FilmService();
	private SerieService serieService=new SerieService();
    @FXML private StackPane heroSection;
    @FXML private MediaView trailerVideo;
    @FXML private Label heroTitle;
    @FXML private Label heroDesc;
    @FXML private HBox heroMetaBox, topRatedRow, mostWatchedRow;
    @FXML private VBox categoryRowsContainer;
    private MediaPlayer mediaPlayer;

    @FXML private Button heroWatchBtn;
    
    private Film currentHeroMovie;
    
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
        
        List<Object> allMedia = new ArrayList<>();
        allMedia.addAll(movies);
        allMedia.addAll(series);
        
        if (!movies.isEmpty()) {
            List<Film> shuffledMovies = new ArrayList<>(movies);
            Collections.shuffle(shuffledMovies);
            
            setupHero(shuffledMovies.get(0));
        }

        DAOStatics.getTop5MostViewed().forEach((film, views) -> {
            mostWatchedRow.getChildren().add(createCard(film));
        });
        
        DAOStatics.getTop5Rated().forEach((film, rating) -> {
            if (topRatedRow != null) topRatedRow.getChildren().add(createCard(film));
        });
        
        
        Map<String, List<Object>> mediaByCategory = allMedia.stream()
                .filter(m -> getCategoriesOf(m) != null)
                .flatMap(media -> getCategoriesOf(media).stream()
                    .map(cat -> Map.entry(cat.getLabel(), media)))
                .collect(Collectors.groupingBy(
                    Map.Entry::getKey, 
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        mediaByCategory.forEach((categoryName, items) -> {
            categoryRowsContainer.getChildren().add(createCategorySection(categoryName, items));
        });
        
    }
    private List<Categorie> getCategoriesOf(Object media) {
        if (media instanceof Film f) return f.getCat();
        if (media instanceof Serie s) return s.getCat();
        return null;
    }
    
    private VBox createCategorySection(String title, List<Object> mediaItems) {
        VBox section = new VBox(10);
        section.getStyleClass().add("category-section");

        Label categoryTitle = new Label(title);
        categoryTitle.getStyleClass().add("categoryTitle");

        HBox mediaRow = new HBox(20);
        mediaRow.setPadding(new javafx.geometry.Insets(0, 40, 10, 40));

        for (Object item : mediaItems) {
            mediaRow.getChildren().add(createCard(item));
        }

        ScrollPane scrollPane = new ScrollPane(mediaRow);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("inner-scroll");

        section.getChildren().addAll(categoryTitle, scrollPane);
        return section;
    }
    
    private void setupHero(Film movie) {
    	this.currentHeroMovie = movie;
        heroTitle.setText(movie.getTitre().toUpperCase());
        heroDesc.setText(movie.getResume());
        
        heroWatchBtn.setOnAction(event -> {
            if (currentHeroMovie != null) {
                MainViewController.getInstance().loadDetailPage("MovieDetailView.fxml", currentHeroMovie);
            }
        });
        
        heroMetaBox.getChildren().clear();
        heroMetaBox.getChildren().addAll(
            createMetaTag(movie.getDateDeSortie() + ""),
            createMetaTag(movie.getDuree()+ "")
        );

        try {
            String videoPath = movie.getPathTrailer();
            if (videoPath != null && !videoPath.isEmpty()) {
                File file = new File(videoPath);
                
                if (file.exists()) {
                    String uriPath = file.toURI().toString();
                    Media media = new Media(uriPath);
                    
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
                    
                } else {
                    System.err.println("Fichier vidéo introuvable sur le disque : " + videoPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Hero video failed to load: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Label createMetaTag(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("meta-tag");
        return l;
    }
    
    private VBox createCard(Object data) {
        VBox card = new VBox();
        card.getStyleClass().add("seriesCard");
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
                File file = new File(imagePath);
                
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString(), true);
                    posterView.setImage(img);
                } else {
                    System.err.println("Le fichier n'existe pas : " + imagePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
            e.printStackTrace();
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