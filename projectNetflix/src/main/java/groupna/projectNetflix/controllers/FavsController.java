package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class FavsController {

	@FXML private FlowPane favsGrid;

    @FXML
    public void initialize() {
    	renderFavorites();
    }
    public void renderFavorites() {
        favsGrid.getChildren().clear();
        var favs = Session.getInstance().getUser().getFavs();

        if (favs.isEmpty()) {
            favsGrid.getChildren().add(new Label("No favorites yet. Start liking some movies!"));
            return;
        }

        for (Oeuvre item : favs) {
        	VBox card;
            if (item instanceof Film film) {
                card = createMovieCard(film); // Use the logic from MoviesController
            } else {
                card = createSeriesCard((Serie) item); // Use the logic from SeriesController
            }
            favsGrid.getChildren().add(card);        }
    }
    
    private VBox createMovieCard(Film movie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("movieCard");

        // Poster with Badge Logic
        StackPane imageStack = new StackPane();
        
        ImageView poster = new ImageView();
        poster.setFitWidth(140);
        poster.setFitHeight(200);
        
        try {
            if (movie.getPathPoster() != null) {
                poster.setImage(new Image(getClass().getResourceAsStream(movie.getPathPoster())));
            }
        } catch (Exception e) {
            System.err.println("Could not load: " + movie.getPathPoster());
        }

        Rectangle clip = new Rectangle(140, 200);
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
    
    private VBox createSeriesCard(Serie serie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("movieCard");

        StackPane stack = new StackPane();
        
        ImageView poster = new ImageView();
        poster.setFitWidth(140);
        poster.setFitHeight(200);
        
        try {
            if (serie.getPathPoster() != null) {
                poster.setImage(new Image(getClass().getResourceAsStream(serie.getPathPoster())));
            }
        } catch (Exception e) {
            System.err.println("Series image failed: " + serie.getPathPoster());
        }

        Rectangle clip = new Rectangle(140, 200);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        poster.setClip(clip);

        Label badge = new Label("📺");
        badge.getStyleClass().add("card-type-badge");
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new javafx.geometry.Insets(8));

        stack.getChildren().addAll(poster, badge);

        Label title = new Label(serie.getTitre());
        title.getStyleClass().add("card-text");
        title.setMaxWidth(140);

        card.getChildren().addAll(stack, title);

        card.setOnMouseClicked(e -> {
            MainViewController.getInstance().loadDetailPage("SeriesDetailView.fxml", serie);
        });

        return card;
    }
}
