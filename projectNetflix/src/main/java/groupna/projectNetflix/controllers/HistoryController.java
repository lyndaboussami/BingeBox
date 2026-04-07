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
            	HBox row;
            	if(item.getContent() instanceof Film f) {
            		row = createHistoryRow(f.getTitre(), f.getPathPoster(), item.getDateVisionnage().toString(), "Movie");
            		
            		row.setOnMouseClicked(event -> {
                        MainViewController.getInstance().setSelectedContent(f);
                        MainViewController.getInstance().loadPage("MovieDetailView.fxml");
                    });
            		historyContainer.getChildren().add(row);
            	}
            	else {
            		Episode e=(Episode) item.getContent();
            		int idSaison = episodeService.recupererIdSaison(e.getId());
                    Saison s = saisonService.getSaisonById(idSaison);
                    int numSaison = s.getNum();
                    
                    int idSerie = saisonService.recupererIdSerie(idSaison);
                    Serie serie = serieService.getSerieById(idSerie);
                    String TitreSerie = serie.getTitre();
                    
                    String detail = TitreSerie + " S" + numSaison + " Ep" + e.getNumero();
                    
                    row = createHistoryRow(detail, e.getPathMiniaure(), item.getDateVisionnage().toString(), "Series");
                    
                    row.setOnMouseClicked(event -> {
                    	MainViewController.getInstance().setSelectedContent(serie);
                        MainViewController.getInstance().loadPage("SeriesDetailView.fxml");
                    });
                    historyContainer.getChildren().add(row);
            	}
            }
        }
    }

    private HBox createHistoryRow(String titleText, String imagePath, String timeAgo, String type) {
        HBox row = new HBox(20);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 15; -fx-background-color: -fx-text-muted; -fx-background-radius: 10; -fx-cursor: hand;");
        
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 15; -fx-background-color: -fx-accent; -fx-background-radius: 10; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 15; -fx-background-color: -fx-text-muted; -fx-background-radius: 10; -fx-cursor: hand;"));

        ImageView poster = new ImageView();
        try {
            poster.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.err.println("Could not load history image: " + imagePath);
        }
        poster.setFitHeight(80);
        poster.setFitWidth(60);
        poster.setPreserveRatio(true);

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