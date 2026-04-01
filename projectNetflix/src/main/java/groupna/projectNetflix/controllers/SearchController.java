package groupna.projectNetflix.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.services.FilmService;
import groupna.projectNetflix.services.SerieService;
import groupna.projectNetflix.utils.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class SearchController extends BaseController{
	@FXML private TextField searchField;
    @FXML private FlowPane resultsPane;
    @FXML private ComboBox<String> yearFilter;
    @FXML private Label resultsCountLabel;

    @FXML private FlowPane genreFilterContainer;
    
    private Set<Oeuvre> allMedia = new HashSet<>();
    
    private final FilmService filmService = new FilmService();
    private final SerieService serieService = new SerieService();
    
	List<Film> movies = filmService.getAllFilms();
    List<Serie> series = serieService.getAllSeries();
    
    @FXML
    public void initialize() {
    	
    	allMedia.addAll(movies);
        allMedia.addAll(series);
        
    	populateDynamicFilters();
    	
    	searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSearchResults();
        });
    	
    	yearFilter.setOnAction(e -> updateSearchResults());
    }

    private void populateDynamicFilters() {
    	if (genreFilterContainer == null) {
            System.err.println("Error: genreFilterContainer was not injected. Check SearchView.fxml fx:id.");
            return; 
        }
        if (allMedia.isEmpty()) return;

        // Extract years, sort them descending, and remove duplicates
        Set<String> uniqueYears = allMedia.stream()
                .map(m -> String.valueOf(m.getDateDeSortie().getYear()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.reverseOrder())));

        ObservableList<String> yearOptions = FXCollections.observableArrayList("All Years");
        yearOptions.addAll(uniqueYears);
        yearFilter.setItems(yearOptions);
        yearFilter.getSelectionModel().selectFirst();

        // Extract all categories used across all films/series
        Set<String> uniqueGenres = allMedia.stream()
                .flatMap(m -> m.getCat().stream())
                .map(Categorie::getLabel)
                .collect(Collectors.toCollection(TreeSet::new));

        genreFilterContainer.getChildren().clear();
        genreFilterContainer.getChildren().add(new Label("Genres:"));
        
        for (String genre : uniqueGenres) {
            ToggleButton genreChip = new ToggleButton(genre);
            genreChip.getStyleClass().add("filter-chip");
            genreChip.setOnAction(e -> updateSearchResults());
            genreFilterContainer.getChildren().add(genreChip);
        }
    }
    
    @FXML
    private void updateSearchResults() {
    	String query = searchField.getText().toLowerCase();
        String selectedYear = yearFilter.getValue();
        
        // Get list of selected genres from the ToggleButtons
        List<String> selectedGenres = genreFilterContainer.getChildren().stream()
                .filter(node -> node instanceof ToggleButton && ((ToggleButton) node).isSelected())
                .map(node -> ((ToggleButton) node).getText())
                .toList();

        // Filter the master list
        List<Oeuvre> filteredResults = allMedia.stream()
            .filter(m -> query.isEmpty() || m.getTitre().toLowerCase().contains(query))
            .filter(m -> selectedYear == null || selectedYear.equals("All Years") || 
                         String.valueOf(m.getDateDeSortie().getYear()).equals(selectedYear))
            .filter(m -> selectedGenres.isEmpty() || 
                         m.getCat().stream().anyMatch(c -> selectedGenres.contains(c.getLabel())))
            .collect(Collectors.toList());

        displayResults(filteredResults);
        if (query.isEmpty()) {
            resultsCountLabel.setText("Explore all titles");
        } else {
            resultsCountLabel.setText("Results for: " + query);
        }
        
    }

    private void displayResults(List<Oeuvre> results) {
        resultsPane.getChildren().clear();
        for (Oeuvre o : results) {
            resultsPane.getChildren().add(createMediaCard(o));
        }
    }
    
    private VBox createMediaCard(Object data) {
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
            //if the key is missing -> object's default title
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
    
    @FXML
    private void clearSearch() {
        searchField.clear();
        yearFilter.getSelectionModel().selectFirst();
    }
}
