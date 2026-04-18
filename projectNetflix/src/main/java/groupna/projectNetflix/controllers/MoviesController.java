package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.services.FilmService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoviesController {
	private FilmService filmService=new FilmService();
    @FXML private VBox categoryRowsContainer;

    @FXML
    public void initialize() { 
    	List<Film> allMovies = filmService.getAllFilms();
    	
        Map<String, List<Film>> moviesByCategory = allMovies.stream()
            .flatMap(movie -> movie.getCat().stream().map(cat -> Map.entry(cat.getLabel(), movie)))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey, 
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
            ));

        moviesByCategory.forEach((categoryName, movies) -> {
            categoryRowsContainer.getChildren().add(createCategoryRow(categoryName, movies));
        });
    }

    private VBox createCategoryRow(String title, List<Film> movies) {
        VBox section = new VBox(10);
        section.getStyleClass().add("category-section");

        Label categoryTitle = new Label(title);
        categoryTitle.getStyleClass().add("categoryTitle");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("inner-scroll");

        HBox movieRow = new HBox(15);
        movieRow.setPadding(new javafx.geometry.Insets(0, 0, 10, 0));

        for (Film movie : movies) {
            movieRow.getChildren().add(createMovieCard(movie));
        }

        scrollPane.setContent(movieRow);
        section.getChildren().addAll(categoryTitle, scrollPane);
        return section;
    }

    private VBox createMovieCard(Film movie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("seriesCard");

        card.setUserData(movie); 

        MainViewController mainCtrl = MainViewController.getInstance();
        if (mainCtrl != null) {
            card.setOnMouseEntered(event -> mainCtrl.showDetails(event));
            card.setOnMouseExited(event -> mainCtrl.hideDetails(event));
        }
        
        StackPane imageStack = new StackPane();
        
        ImageView poster = new ImageView();
        poster.setFitWidth(200);
        poster.setFitHeight(300);
        
        try {
            String path = movie.getPathPoster();
            if (path != null && !path.isEmpty()) {
                File file = new File(path);
                
                if (file.exists()) {
                    poster.setImage(new Image(new FileInputStream(file)));
                } else {
                    System.err.println("Fichier introuvable sur le disque : " + path);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        Rectangle clip = new Rectangle(200, 300);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        poster.setClip(clip);

        Label badge = new Label("🎬");
        badge.getStyleClass().add("card-type-badge");
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new javafx.geometry.Insets(8));

        imageStack.getChildren().addAll(poster, badge);

        Label titleLabel = new Label(movie.getTitre());
        titleLabel.getStyleClass().add("card-text");
        titleLabel.setMaxWidth(140);

        card.getChildren().addAll(imageStack, titleLabel);

        card.setOnMouseClicked(e -> {
            MainViewController.getInstance().loadDetailPage("MovieDetailView.fxml", movie);
        });

        return card;
    }
}