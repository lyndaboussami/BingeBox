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
import javafx.scene.shape.Rectangle;
import javafx.scene.image.*;

import java.io.File;
import java.io.FileInputStream;
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
            		
            		row = createHistoryRow(f.getTitre(), f.getPathPoster(), 
                            "Watched: " + item.getDateVisionnage().toLocalDateTime().toLocalDate(), 
                            "🎬");
            		            		
            		row.setOnMouseClicked(event -> {
                        MainViewController.getInstance().setSelectedContent(f);
                        MainViewController.getInstance().loadPage("MovieDetailView.fxml");
                    });
            	}
            	else {
            		Episode e = (Episode) item.getContent();
                    int idSaison = episodeService.recupererIdSaison(e.getId());
                    Saison s = saisonService.getSaisonById(idSaison);
                    int numSaison = s.getNum();
                    
                    int idSerie = saisonService.recupererIdSerie(idSaison);
                    Serie serie = serieService.getSerieById(idSerie);
                    String TitreSerie = serie.getTitre();
                    
                    String detail = TitreSerie + " S" + numSaison + " Ep" + e.getNumero();
                    
                    row = createHistoryRow(detail, serie.getPathPoster(), 
                            "Watched: " + item.getDateVisionnage().toLocalDateTime().toLocalDate(), "📺");
                    
                    row.setOnMouseClicked(event -> {
                        MainViewController.getInstance().setSelectedContent(serie);
                        MainViewController.getInstance().loadPage("SeriesDetailView.fxml");
                    });
                }
            	historyContainer.getChildren().add(row);
            }
        }
    }

    private HBox createHistoryRow(String titleText, String imagePath, String timeAgo, String type) {
        HBox row = new HBox(20);
        
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        row.setStyle("-fx-padding: 15; -fx-background-color: transparent; -fx-background-radius: 12; " +
                " -fx-border-width: 0.5; -fx-cursor: hand;");
   
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 15; -fx-background-color: -fx-accent; -fx-background-radius: 12; " +
	                                           "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 4); -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 15; -fx-background-color: transparent; -fx-background-radius: 12; " +
	                                          " -fx-border-width: 0.5;"));
        
        ImageView poster = new ImageView();
        boolean imageLoaded = false;

        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    poster.setImage(new Image(new FileInputStream(file)));
                    imageLoaded = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Image loading failed: " + e.getMessage());
        }

        poster.setFitHeight(90);
        poster.setFitWidth(140);
        poster.setPreserveRatio(false);

        Rectangle clip = new Rectangle(140, 90);
        clip.setArcWidth(15); clip.setArcHeight(15);
        poster.setClip(clip);

        StackPane imageContainer = new StackPane();
        if (!imageLoaded) {
            Label placeholder = new Label(type);
            placeholder.setStyle("-fx-font-size: 30px;");
            imageContainer.getChildren().add(placeholder);
            imageContainer.setStyle("-fx-background-color: #1a1f31; -fx-background-radius: 12;");
            imageContainer.setPrefSize(140, 90);
        } else {
            imageContainer.getChildren().add(poster);
        }
        
        poster.setFitHeight(90);
        poster.setFitWidth(140);
        poster.setPreserveRatio(false);
        
        
        VBox info = new VBox(8);
        
        
        info.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-text-fill: -fx-text-dim; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label subLabel = new Label(type + " • " + timeAgo);
        subLabel.setStyle("-fx-text-fill: -fx-text-dim; -fx-font-size: 13px;");
        
        
        info.getChildren().addAll(titleLabel, subLabel);
        
        Label playIcon = new Label("▶");
        playIcon.setStyle("-fx-text-fill: -fx-accent; -fx-font-size: 24px;");

        row.getChildren().addAll(poster, info, playIcon);
        
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