package groupna.projectNetflix.controllers;

import java.io.IOException;
import java.util.List;

import groupna.projectNetflix.DAO.HistoryItem;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.Saison;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.EpisodeService;
import groupna.projectNetflix.services.SaisonService;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class FavsController {
	private UserService userService=new UserService();
	private EpisodeService episodeService = new EpisodeService();
    private SaisonService saisonService = new SaisonService();
	@FXML private FlowPane favsGrid;

    @FXML
    public void initialize() {
    	renderFavorites();
    }
    public void renderFavorites() {
        favsGrid.getChildren().clear();
        User user=Session.getInstance().getUser();
        var favs = userService.recupererFavoris(user.getId());

        if (favs.isEmpty()) {
            favsGrid.getChildren().add(new Label("No favorites yet. Start liking some movies!"));
            return;
        }

        for (Oeuvre item : favs) {
        	VBox card;
            if (item instanceof Film film) {
                card = createMovieCard(film);
            } else {
                card = createSeriesCard((Serie) item);
            }
            favsGrid.getChildren().add(card);        }
    }
    
    private VBox createMovieCard(Film movie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("movieCard");

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
            if (movie.getPathPoster() != null) {
                poster.setImage(new Image(getClass().getResourceAsStream(movie.getPathPoster())));
            }
        } catch (Exception e) {
            System.err.println("Could not load: " + movie.getPathPoster());
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
    
    private VBox createSeriesCard(Serie serie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("movieCard");

        card.setUserData(serie); 

        MainViewController mainCtrl = MainViewController.getInstance();
        if (mainCtrl != null) {
            card.setOnMouseEntered(event -> mainCtrl.showDetails(event));
            card.setOnMouseExited(event -> mainCtrl.hideDetails(event));
        }

        StackPane stack = new StackPane();
        
        ImageView poster = new ImageView();
        poster.setFitWidth(200);
        poster.setFitHeight(300);
        
        try {
            if (serie.getPathPoster() != null) {
                poster.setImage(new Image(getClass().getResourceAsStream(serie.getPathPoster())));
            }
        } catch (Exception e) {
            System.err.println("Series image failed: " + serie.getPathPoster());
        }

        Rectangle clip = new Rectangle(200, 300);
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
        	User user = Session.getInstance().getUser();
            List<HistoryItem> history = userService.recupererHistoriqueComplet(user.getId());
            
            HistoryItem lastWatched = history.stream()
                    .filter(item -> item.getContent() instanceof Episode)
                    .filter(item -> {
                        int idSaison = episodeService.recupererIdSaison(((Episode)item.getContent()).getId());
                        return saisonService.recupererIdSerie(idSaison) == serie.getId();
                    })
                    .findFirst()
                    .orElse(null);
            
            if (lastWatched != null) {
                Episode epToResume = (Episode) lastWatched.getContent();
                int idSaison = episodeService.recupererIdSaison(epToResume.getId());
                List<Episode> seasonEpisodes = episodeService.getEpisodesBySaison(idSaison);
                
                playEpisodeDirectly(epToResume, seasonEpisodes);
                
                } else {
                    Saison firstSaison = serie.getSaisons().keySet().stream()
                        .filter(s -> s.getNum() == 1).findFirst()
                        .orElse(serie.getSaisons().keySet().iterator().next());
                    
                    List<Episode> s1Episodes = serie.getSaisons().get(firstSaison);
                    if (s1Episodes != null && !s1Episodes.isEmpty()) {
                        playEpisodeDirectly(s1Episodes.get(0), s1Episodes);
                    }
                }
        });

        return card;
    }
    private void playEpisodeDirectly(Episode ep, List<Episode> currentSeasonEpisodes) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groupna/projectNetflix/view/VideoPlayerView.fxml"));
            Parent playerView = loader.load();
            VideoPlayerController controller = loader.getController();
            
            int startIndex = currentSeasonEpisodes.indexOf(ep);
            controller.loadSeries(currentSeasonEpisodes, startIndex);
                        
            StackPane mainStack = (StackPane) favsGrid.getScene().getRoot();
            mainStack.getChildren().add(playerView);
            
            controller.setOnCloseRequest(() -> {
                controller.stopVideo();
                ((StackPane) playerView.getParent()).getChildren().remove(playerView);            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
