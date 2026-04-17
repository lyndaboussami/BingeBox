package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.CommentaireService;
import groupna.projectNetflix.services.RateService;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;
import groupna.projectNetflix.entities.Saison;
import groupna.projectNetflix.DAO.HistoryItem;
import groupna.projectNetflix.entities.Commentaire;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Oeuvre;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class SeriesDetailController {
	private UserService userService=new UserService();
    @FXML private Label seriesTitle;
    @FXML private Label seriesDescription;
    @FXML private Label seriesMeta;
    @FXML private ComboBox<String> seasonSelector;
    @FXML private VBox episodeListContainer;

    @FXML private ToggleButton favButton;
    @FXML private Button trailerButton;
    
    @FXML private ImageView heroBlurredPoster;
    @FXML private ImageView seriesSharpPoster;
    
    private RateService rateService = new RateService();
    private CommentaireService service = new CommentaireService();
    
    private int currentRating = 0;
    @FXML private HBox starContainer;
    @FXML private TextField commentField;
    @FXML private VBox commentsContainer;
    
    @FXML
    private void handleBack() {
        MainViewController.getInstance().goBack();
    }
    
    @FXML
    private void handlePlayTrailer() {
    	Serie series = (Serie) MainViewController.getInstance().getSelectedContent();
    	String selectedLabel = seasonSelector.getValue();

    	if (series != null && selectedLabel != null) {
            int selectedNum = Integer.parseInt(selectedLabel.replace("Season ", "").trim());

            Saison currentSaison = series.getSaisons().keySet().stream()
                    .filter(s -> s.getNum() == selectedNum)
                    .findFirst()
                    .orElse(null);

            if (currentSaison != null && currentSaison.getPathTrailer() != null) {
                String path = currentSaison.getPathTrailer();
                openVideoPlayer(path, series.getTitre() + " - " + selectedLabel);
            } else {
                System.out.println("No trailer defined for this season.");
            }
        }
    }
    
    private void openVideoPlayer(String fullUrl, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groupna/projectNetflix/view/VideoPlayerView.fxml"));
            Parent playerView = loader.load();
            VideoPlayerController controller = loader.getController();
            
            controller.loadVideo(0,0.0,fullUrl); 

            StackPane mainStack = (StackPane) seriesTitle.getScene().getRoot();
            mainStack.getChildren().add(playerView);
            controller.setOnCloseRequest(() -> mainStack.getChildren().remove(playerView));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void initialize() {
        Object content = MainViewController.getInstance().getSelectedContent();
        User user =Session.getInstance().getUser();

        if (content instanceof Serie series) {
            if (seriesTitle != null) seriesTitle.setText(series.getTitre().toUpperCase());
            
            currentRating=rateService.getNoteUtilisateur(user.getId(),series.getId(), "series");
    		
        	for (int i = 0; i < starContainer.getChildren().size(); i++) {
                Button star = (Button) starContainer.getChildren().get(i);
                if (i < currentRating) {
                    star.setStyle("-fx-text-fill: #ffcc00; -fx-background-color: transparent; -fx-font-size: 24px;"); // Gold
                } else {
                    star.setStyle("-fx-text-fill: #555; -fx-background-color: transparent; -fx-font-size: 24px;"); // Grey
                }
            }
            
            String path = series.getPathPoster();
            
            if (path != null) {
                try {File file = new File(path); 
                Image img = new Image(file.toURI().toString(), true);
                    seriesSharpPoster.setImage(img);
                    heroBlurredPoster.setImage(img);
                    
                    GaussianBlur blur = new GaussianBlur(30);
                    heroBlurredPoster.setEffect(blur);
                    
                } catch (Exception e) {
                    System.err.println("Could not load image: " + path);
                }
            }
            
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
                
                seasonSelector.getSelectionModel().selectFirst();
                
                String initialValue = seasonSelector.getValue();
                if (initialValue != null) {
                    loadEpisodes(series, initialValue);
                }
                seasonSelector.setOnAction(e -> loadEpisodes(series, seasonSelector.getValue()));
            }
            loadComments(series.getId());
            setupFavLogic(series);
        }
    }

    public void playEpisode(Episode ep, List<Episode> currentSeasonEpisodes) {
    	User user=Session.getInstance().getUser();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groupna/projectNetflix/view/VideoPlayerView.fxml"));
            Parent playerView = loader.load();

            VideoPlayerController controller = loader.getController();
            
            int startIndex = currentSeasonEpisodes.indexOf(ep);
            
            
            StackPane mainStack = (StackPane) seriesTitle.getScene().getRoot();
            mainStack.getChildren().add(playerView);

            controller.loadSeries(currentSeasonEpisodes, startIndex);

            controller.setOnCloseRequest(() -> {
                userService.marquerEpisodeCommeVu(user.getId(), controller.getCurrentIdEpisode(), controller.getTime());
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
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        row.getStyleClass().add("episode-row");
        row.setStyle("-fx-background-color: -fx-card-bg; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;");

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #2a2f41; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: -fx-card-bg; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;"));
        
        User user = Session.getInstance().getUser();
        List<HistoryItem> history = userService.recupererHistoriqueComplet(user.getId());
        
        HistoryItem epHistory = history.stream()
                .filter(item -> item.getContent() instanceof Episode e && e.getId() == ep.getId())
                .findFirst()
                .orElse(null);
        
        Label progressStatus= new Label();
        
        if (epHistory != null) {

        	LocalTime duration = ep.getDuree();
            long totalSeconds = duration.getHour() * 3600 + duration.getMinute() * 60 + duration.getSecond();
            
            double percentage = (epHistory.getTime() / (double) totalSeconds) * 100;
            
        	if (percentage >= 90) {
                progressStatus.setText("✓ WATCHED");
                progressStatus.setStyle("-fx-text-fill: #F5F5DC;");
            } else if (percentage > 2) {
                progressStatus.setText("● " + (int)percentage + "%");
                progressStatus.setStyle("-fx-text-fill: #F5F5DC;");
                
            }
        }else {
        	progressStatus.setText("NEW");
        	progressStatus.setStyle("-fx-text-fill: #D2B48C; -fx-font-weight: bold;");
        }
        
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

        info.getChildren().addAll(progressStatus, title, desc, duration);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button playBtn = new Button("▶");
        playBtn.getStyleClass().add("navButton");

        playBtn.setOnAction(e -> playEpisode(ep, allEpisodesInSeason));
        
        row.setOnMouseClicked(e -> {
            playEpisode(ep, allEpisodesInSeason);
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
            favButton.setStyle("-fx-text-fill: #ff4d4d;");
        } else {
            favButton.setStyle("-fx-text-fill: white;");
        }
    }
    
    @FXML
    private void handleRate(ActionEvent event) {
        User user = Session.getInstance().getUser();
        Serie selected = (Serie) MainViewController.getInstance().getSelectedContent();
        Button clickedStar = (Button) event.getSource();
        currentRating = Integer.parseInt(clickedStar.getUserData().toString());
        
        for (int i = 0; i < starContainer.getChildren().size(); i++) {
            Button star = (Button) starContainer.getChildren().get(i);
            if (i < currentRating) {
                star.setStyle("-fx-text-fill: #ffcc00; -fx-background-color: transparent; -fx-font-size: 24px;");
            } else {
                star.setStyle("-fx-text-fill: #555; -fx-background-color: transparent; -fx-font-size: 24px;");
            }
        }
        rateService.noterContenu(user.getId(), selected.getId(), currentRating, "serie");
    }

    @FXML
    private void handlePostComment() {
        try {
            String text = commentField.getText();
            if (text == null || text.trim().isEmpty()) {
                showError("comment invalide", "you can't post an empty comment");
                return;
            }

            User user = Session.getInstance().getUser();
            Serie series = (Serie) MainViewController.getInstance().getSelectedContent();
            
            Commentaire newComment = new Commentaire(user.getId(), series.getId(), text, false, null, 0);
            
            boolean success = service.posterCommentaire(newComment, "serie");
            if (success) {
                commentsContainer.getChildren().add(0, createCommentNode(newComment));
                commentField.clear();
            }
        } catch (Exception e) {
            showError("Critical Error", "An unexpected error occurred.");
            e.printStackTrace();
        }   
    }
    
    private void loadComments(int seriesId) {
        commentsContainer.getChildren().clear();
        service.recupererCommentairesOeuvre(seriesId, "serie").forEach(comment -> {
            commentsContainer.getChildren().add(createCommentNode(comment));
        });
    }
    
    private VBox createCommentNode(Commentaire comment) {
        VBox commentBox = new VBox(8);
        commentBox.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 10; -fx-background-radius: 5;");
        User user = Session.getInstance().getUser();

        Label userLabel = new Label(userService.recupererUtilisateurParId(comment.getId_user()).toString());
        userLabel.setStyle("-fx-text-fill: -fx-text-muted; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label contentLabel = new Label(comment.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: white;");

        HBox footer = new HBox(10);
        footer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        if (comment.isReported()) {
            Label reportedLabel = new Label("🔺Under Review");
            reportedLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-style: italic; -fx-font-size: 12px;");
            footer.getChildren().add(reportedLabel);
        } else {
            if(comment.getId_user() != user.getId()) {
                Button reportBtn = new Button("Report");
                reportBtn.getStyleClass().add("navButton");
                reportBtn.setStyle("-fx-font-size: 11px; -fx-cursor: hand; -fx-text-fill: -fx-text-muted;");
                reportBtn.setOnAction(e -> handleReportClick(comment, commentBox));
                footer.getChildren().add(reportBtn);
            }
        }

        commentBox.getChildren().addAll(userLabel, contentLabel, footer);
        return commentBox;
    }
    
    private void handleReportClick(Commentaire comment, VBox commentBox) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Comment");
        dialog.setHeaderText("Why are you reporting this comment?");
        dialog.setContentText("Please specify the reason:");

        dialog.showAndWait().ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                boolean success = service.signalerAbus(comment.getId(), "serie", reason);
                
                if (success) {
                    comment.setReported(true);
                    int index = commentsContainer.getChildren().indexOf(commentBox);
                    commentsContainer.getChildren().set(index, createCommentNode(comment));
                }
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}