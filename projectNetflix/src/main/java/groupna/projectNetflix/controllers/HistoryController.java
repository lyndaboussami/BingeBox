package groupna.projectNetflix.controllers;

import groupna.projectNetflix.DAO.HistoryItem;
import groupna.projectNetflix.entities.*;
import groupna.projectNetflix.services.EpisodeService;
import groupna.projectNetflix.services.SaisonService;
import groupna.projectNetflix.services.SerieService;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.*;

import java.util.List;

public class HistoryController extends BaseController {
	private UserService userService=new UserService();
	private SerieService serieService=new SerieService();
	private EpisodeService episodeService=new EpisodeService();
	private SaisonService saisonService=new SaisonService();
    @FXML private VBox historyContainer;
    @FXML private Label emptyLabel;

    //private final FilmService filmService = new FilmService();
    //private final SerieService serieService = new SerieService();
    
	//List<Film> allFilms = DataStore.getMovies();
    //List<Serie> allSeries = DataStore.getSeries();
    

    @FXML
    public void initialize() {
        loadHistory();
    }

    private void loadHistory() {
    	User user=Session.getInstance().getUser();
        historyContainer.getChildren().clear();
        List<HistoryItem> His=userService.recupererHistoriqueComplet(user.getId());
        if (His.isEmpty()) {
            emptyLabel.setVisible(true);
        } else {
            emptyLabel.setVisible(false);
            for(HistoryItem item:His) {
            	if(item.getContent() instanceof Film) {
            		Film f=(Film) item.getContent();
            		historyContainer.getChildren().add(createHistoryRow(f.getTitre(), f.getPathPoster(),item.getDateVisionnage().toString(), "Movie"));
            	}
            	else {
            		Episode e=(Episode) item.getContent();
            		int idSaison=saisonService.getSaisonById(episodeService.recupererIdSaison(e.getId())).getId();
            		int numSaison=saisonService.getSaisonById(episodeService.recupererIdSaison(e.getId())).getNum();
            		String TitreSerie=serieService.getSerieById(saisonService.recupererIdSerie(idSaison)).getTitre();
            		String detail=TitreSerie+"S"+numSaison+"Ep"+e.getNumero();
            		historyContainer.getChildren().add(createHistoryRow(detail, e.getPathMiniaure(), item.getDateVisionnage().toString(), "Series"));
            	}
            
            }
        }
    }

    private HBox createHistoryRow(String titleText, String imagePath, String timeAgo, String type) {
        HBox row = new HBox(20);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 15; -fx-background-color: -fx-text-muted; -fx-background-radius: 10; -fx-cursor: hand;");
        
        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 15; -fx-background-color: -fx-accent; -fx-background-radius: 10; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 15; -fx-background-color: -fx-text-muted; -fx-background-radius: 10; -fx-cursor: hand;"));

        // Poster
        ImageView poster = new ImageView();
        try {
            poster.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            // Fallback if image path is wrong
            System.err.println("Could not load history image: " + imagePath);
        }
        poster.setFitHeight(80);
        poster.setFitWidth(60);
        poster.setPreserveRatio(true);

        // Info
        VBox info = new VBox(5);
        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-text-fill: -fx-text-main; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label typeLabel = new Label(type + " • " + timeAgo);
        typeLabel.setStyle("-fx-text-fill: -fx-button-text; -fx-font-size: 13px;");

        info.getChildren().addAll(titleLabel, typeLabel);
        row.getChildren().addAll(poster, info);

        return row;
    }

    @FXML
    private void clearHistory() {
    	User user=Session.getInstance().getUser();
        historyContainer.getChildren().clear();
        emptyLabel.setVisible(true);
        userService.viderHistorique(user.getId());
    }
}