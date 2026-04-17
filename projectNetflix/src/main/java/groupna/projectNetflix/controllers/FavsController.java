package groupna.projectNetflix.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
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
    	User user=Session.getInstance().getUser();
        VBox card = new VBox(10);
        card.getStyleClass().add("seriesCard");

        card.setUserData(movie); 

        MainViewController mainCtrl = MainViewController.getInstance();
        List<HistoryItem> history = userService.recupererHistoriqueComplet(user.getId());
        HistoryItem lastWatchedItem = history.stream() 
        	    .filter(item -> item.getContent().equals(movie))
        	    .findFirst()
        	    .orElse(null);
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
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(
	                    "/groupna/projectNetflix/view/VideoPlayerView.fxml"));
	            Parent playerView;
				playerView = loader.load();
				VideoPlayerController controller = loader.getController();
	            if (favsGrid.getScene().getRoot() instanceof StackPane mainStack) {
	                mainStack.getChildren().add(playerView);
	            }try {
	                File file = new File(movie.getPathMovie());

	                if (!file.exists()) {
	                    return;
	                }
	                String fullUrl = file.toURI().toString();
	                double time=0;
	                if(lastWatchedItem!=null) {
	                	time=lastWatchedItem.getTime();
	                }
	                controller.loadVideo(movie.getId(), time, fullUrl);


	            } catch (Exception e2) {
	                System.err.println("Error playing video: " + e2.getMessage());
	                e2.printStackTrace();
	            }
	            controller.setOnCloseRequest(() -> {
	                controller.stopVideo();
	                ((StackPane) playerView.getParent()).getChildren().remove(playerView);
	            });
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        });

        return card;
        }
    
    private VBox createSeriesCard(Serie serie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("seriesCard");

        card.setUserData(serie); 
        User user = Session.getInstance().getUser();
        List<HistoryItem> history = userService.recupererHistoriqueComplet(user.getId());
        HistoryItem lastWatchedItem = history.stream() 
        	    .filter(item -> item.getContent() instanceof Episode)
        	    .filter(item -> {
        	        Episode episode = (Episode) item.getContent();
        	        int idSaison = episodeService.recupererIdSaison(episode.getId());
        	        return saisonService.recupererIdSerie(idSaison) == serie.getId();
        	    })
        	    .findFirst()
        	    .orElse(null);
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
            String path = serie.getPathPoster();
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
        	int index=-1;
        	List<Episode> saison=null;
            if (lastWatchedItem != null) {
                Episode lastEp = (Episode) lastWatchedItem.getContent();
                int idSaison = episodeService.recupererIdSaison(lastEp.getId());
                saison = episodeService.getEpisodesBySaison(idSaison);
                index=saison.indexOf(lastEp);
            } else {
                Saison firstSaison = serie.getSaisons().keySet().stream()
                        .filter(s -> s.getNum() == 1).findFirst()
                        .orElse(serie.getSaisons().keySet().iterator().next());
 
                saison = serie.getSaisons().get(firstSaison);
                if (saison!= null && !saison.isEmpty()) {
                	index=0;
                }
            }
            if(index!=-1 && saison!=null)  {
            	try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/groupna/projectNetflix/view/VideoPlayerView.fxml"));
                    Parent playerView = loader.load();
                    VideoPlayerController controller = loader.getController();
                    if (favsGrid.getScene().getRoot() instanceof StackPane mainStack) {
                        mainStack.getChildren().add(playerView);
                    }
         
                    controller.loadSeries(saison, index);

                    controller.setOnCloseRequest(() -> {
                        controller.stopVideo();
                        ((StackPane) playerView.getParent()).getChildren().remove(playerView);
                    });
                } catch (IOException f) {
                    f.printStackTrace();
                }
            }
            
 
        });
 
        return card;
    }
}
