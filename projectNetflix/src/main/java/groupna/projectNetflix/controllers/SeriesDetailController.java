package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;
import groupna.projectNetflix.entities.Saison;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Oeuvre;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class SeriesDetailController {
	private UserService userService=new UserService();
    @FXML private Label seriesTitle;
    @FXML private Label seriesDescription;
    @FXML private Label seriesMeta;
    @FXML private ComboBox<String> seasonSelector;
    @FXML private VBox episodeListContainer;

    @FXML private ToggleButton favButton;
    
    @FXML
    public void initialize() {
        Object content = MainViewController.getInstance().getSelectedContent();
        
        if (content instanceof Serie series) {
            if (seriesTitle != null) seriesTitle.setText(series.getTitre().toUpperCase());
            
            if (seriesMeta != null) {
                int totalSeasons = series.getSaisons().size();
                String seasonText = totalSeasons + (totalSeasons > 1 ? " Seasons" : " Season");
                int year = series.getDateDeSortie().getYear();
                seriesMeta.setText(seasonText + " • " + year);
            }

            if (seriesDescription != null) {
                seriesDescription.setText(series.getResume());
            }
            
            if (seasonSelector != null && series.getSaisons() != null) {
                seasonSelector.getItems().clear();
                for (Saison s : series.getSaisons().keySet()) {
                    seasonSelector.getItems().add("Season " + s.getNum());
                }
                
                // Select first season by default
                seasonSelector.getSelectionModel().selectFirst();
                
                String initialValue = seasonSelector.getValue();
                if (initialValue != null) {
                    loadEpisodes(series, initialValue);
                }
                seasonSelector.setOnAction(e -> loadEpisodes(series, seasonSelector.getValue()));
            }
            setupFavLogic(series);
        }
    }

    private void playEpisode(Episode ep, List<Episode> currentSeasonEpisodes) {
    	User user=Session.getInstance().getUser();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groupna/projectNetflix/view/VideoPlayerView.fxml"));
            Parent playerView = loader.load();

            VideoPlayerController controller = loader.getController();
            
            int startIndex = currentSeasonEpisodes.indexOf(ep);
            
            controller.loadSeries(currentSeasonEpisodes, startIndex);
            
            StackPane mainStack = (StackPane) seriesTitle.getScene().getRoot();
            mainStack.getChildren().add(playerView);

            controller.setOnCloseRequest(() -> {
                controller.stopVideo();
                mainStack.getChildren().remove(playerView);
            });

        } catch (IOException e) {
            System.err.println("Could not play episode: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadEpisodes(Serie series, String selectedSeasonLabel) {
    	if (selectedSeasonLabel == null || episodeListContainer == null) {
            return;
        }
    	episodeListContainer.getChildren().clear();

    	try {
            int selectedNum = Integer.parseInt(selectedSeasonLabel.replace("Season ", "").trim());
            
            Saison currentSaison = series.getSaisons().keySet().stream()
                    .filter(s -> s.getNum() == selectedNum)
                    .findFirst()
                    .orElse(null);

            if (currentSaison != null) {
                List<Episode> episodes = series.getSaisons().get(currentSaison);
                if (episodes != null) {
                    for (Episode ep : episodes) {
                        episodeListContainer.getChildren().add(createEpisodeRow(ep,episodes));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading eps: " + selectedSeasonLabel);
        }
    }


    private HBox createEpisodeRow(Episode ep, List<Episode> allEpisodesInSeason) {
    	
        HBox row = new HBox(20);
        row.getStyleClass().add("episode-row");
        row.setStyle("-fx-background-color: -fx-card-bg; -fx-padding: 15; -fx-background-radius: 10;");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        StackPane thumb = new StackPane(new Label("EP " + ep.getNumero()));
        thumb.setPrefSize(180, 100);
        thumb.setStyle("-fx-background-color: #1a1f31; -fx-background-radius: 5;");
        
        VBox info = new VBox(5);
        Label title = new Label(ep.getNumero() + ". " + ep.getTitre());
        title.setStyle("-fx-text-fill: -fx-text-main; -fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label desc = new Label(ep.getResume());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: -fx-text-muted; -fx-font-size: 13px;");
        
        Label duration = new Label(ep.getDuree().toString() + "m");
        duration.setStyle("-fx-text-fill: -fx-accent;");

        info.getChildren().addAll(title, desc, duration);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button playBtn = new Button("▶");
        playBtn.getStyleClass().add("navButton");

        playBtn.setOnAction(e -> playEpisode(ep, allEpisodesInSeason));
        
        row.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) { // Double click to play
                playEpisode(ep, allEpisodesInSeason);
            }
        });
        
        row.getChildren().addAll(thumb, info, playBtn);
        return row;
    }

    private void setupFavLogic(Oeuvre currentMedia) {
        User user = Session.getInstance().getUser();
        if (favButton != null) {

        	boolean isAlreadyFav = userService.recupererFavoris(user.getId()).contains(currentMedia);
            favButton.setSelected(isAlreadyFav);
            
            updateHeartStyle(isAlreadyFav);

            favButton.setOnAction(e -> {
                boolean selected = favButton.isSelected();
                if (selected) {
                    userService.ajouterAuxFavoris(user.getId(), currentMedia.getId(), "serie");
                } else {
                    userService.retirerDesFavoris(user.getId(), currentMedia.getId(), "serie");
                }
                updateHeartStyle(selected);
            });
        }
    }

    private void updateHeartStyle(boolean isFav) {
        if (isFav) {
            favButton.setStyle("-fx-text-fill: #ff4d4d;"); // Red for active
        } else {
            favButton.setStyle("-fx-text-fill: white;"); // White for inactive
            }
        }
    }