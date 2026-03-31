package groupna.projectNetflix.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Commentaire;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.CommentaireService;
import groupna.projectNetflix.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class MovieDetailController {

	@FXML private Label movieTitle;
    @FXML private Label movieDescription;
    @FXML private Label movieMeta;
    @FXML private Label movieCategories;
    @FXML private Label movieCast;

    @FXML private ToggleButton favButton;

    @FXML private HBox starContainer;
    @FXML private TextField commentField;
    @FXML private VBox commentsContainer;

    @FXML private Button trailerButton;
    
    @FXML private ImageView heroBlurredPoster;
    @FXML private ImageView movieSharpPoster;
    
    private int currentRating = 0;
    
    @FXML
    public void initialize() {
        Object content = MainViewController.getInstance().getSelectedContent();
        
        if (content instanceof Film movie) {

        	String posterPath = movie.getPathPoster();

            if (posterPath != null && !posterPath.isEmpty()) {
                try {
                    Image posterImage = new Image(getClass().getResource(posterPath).toExternalForm(), true);

                    if (posterImage != null) {
                        movieSharpPoster.setImage(posterImage);
                        heroBlurredPoster.setImage(posterImage);

                    }
                } catch (Exception e) {
                    System.err.println("Error loading poster: " + posterPath);
                }
            }
        	
        	
            String categories = movie.getCat().stream()
                    .map(Categorie::getLabel)
                    .collect(Collectors.joining(", "));
            movieCategories.setText(categories);
            
            movieTitle.setText(movie.getTitre().toUpperCase());
           
            String cast = movie.getActeurs().stream()
                    .map(a -> a.getPrenom() + " " + a.getNom())
                    .collect(Collectors.joining(", "));
            movieCast.setText(cast);
            
            movieDescription.setText(movie.getResume());
            movieMeta.setText(movie.getDateDeSortie().getYear() + "  •  " 
            				+ movie.getDuree().getHour()+"h"+String.format("%02d", movie.getDuree().getMinute()));
            setupFavLogic(movie);
            loadComments(movie.getId());
        }
    }
    
    @FXML
    private void handlePlay() {
        Object content = MainViewController.getInstance().getSelectedContent();
        
        if (content instanceof Film movie) {
            String moviePath = movie.getPathMovie();

            if (moviePath == null || moviePath.isEmpty()) {
                System.err.println("Error: No path defined for this movie in the database.");
                return;
            }

            try {
                URL resource = getClass().getResource(moviePath);
                
                if (resource == null) {
                    System.err.println("File not found at path: " + moviePath);
                    return;
                }

                String fullUrl = resource.toExternalForm();
                
                // Open the player window
                openVideoPlayer(fullUrl, movie.getId(), movie.getTitre());

            } catch (Exception e) {
                System.err.println("Error playing video: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
    }
    
    private void openVideoPlayer(String url, int movieId, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groupna/projectNetflix/view/VideoPlayerView.fxml"));
            
            
            Parent playerView = loader.load();
            
            VideoPlayerController controller = loader.getController();
            controller.loadVideo(url, movieId);
            
            StackPane mainStack = (StackPane) movieTitle.getScene().getRoot();
                        
            mainStack.getChildren().add(playerView);
            
            controller.setOnCloseRequest(() -> {
                controller.stopVideo();
                mainStack.getChildren().remove(playerView);            });
            
        } catch (IOException e) {
        	System.err.println("Error loading player: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private void setupFavLogic(Oeuvre currentMedia) {
        User user = Session.getInstance().getUser();

        if (favButton != null) {

        	boolean isAlreadyFav = user.getFavs().contains(currentMedia);
            favButton.setSelected(isAlreadyFav);
            
            updateHeartStyle(isAlreadyFav);

            favButton.setOnAction(e -> {
                boolean selected = favButton.isSelected();
                if (selected) {
                    user.getFavs().add(currentMedia);
                } else {
                    user.getFavs().remove(currentMedia);
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
    
    @FXML
    private void handleRate(ActionEvent event) {
        Button clickedStar = (Button) event.getSource();
        currentRating = Integer.parseInt(clickedStar.getUserData().toString());
        
        for (int i = 0; i < starContainer.getChildren().size(); i++) {
            Button star = (Button) starContainer.getChildren().get(i);
            if (i < currentRating) {
                star.setStyle("-fx-text-fill: #ffcc00; -fx-background-color: transparent; -fx-font-size: 24px;"); // Gold
            } else {
                star.setStyle("-fx-text-fill: #555; -fx-background-color: transparent; -fx-font-size: 24px;"); // Grey
            }
        }
        
        System.out.println("User rated movie: " + currentRating + " stars.");
        //RateService.save(rate) (à ajouter dans service)

    }

    @FXML
    private void handlePostComment() {
    	try {
    		String text = commentField.getText();
            if (text == null || text.trim().isEmpty()) return;

            User user = Session.getInstance().getUser();
            Film movie = (Film) MainViewController.getInstance().getSelectedContent();
            
            Commentaire newComment = new Commentaire(user.getId(), movie.getId(), text, false);
            
            CommentaireService service = new CommentaireService();
            boolean success = service.posterCommentaire(user.getId(), movie.getId(), text, "FILM");
            

            if (success) {
            	commentsContainer.getChildren().add(0, createCommentNode(newComment));
                commentField.clear();
            }
    	} catch (Exception e) {
            showError("Critical Error", "An unexpected error occurred.");
            e.printStackTrace();
        }   
        }
    
    private void loadComments(int movieId) {
        commentsContainer.getChildren().clear();
        CommentaireService service = new CommentaireService();
        
        // Fetch comments from DB
        /*
        service.getCommentairesByMedia(movieId, "FILM").forEach(comment -> {
            commentsContainer.getChildren().add(createCommentNode(comment));
        });*/
    }
    
    private VBox createCommentNode(Commentaire comment) {
        VBox commentBox = new VBox(8);
        commentBox.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 10; -fx-background-radius: 5;");
        
        // à ajouter :a way to get the name from id_user
        Label userLabel = new Label("User #" + comment.getId_user());
        userLabel.setStyle("-fx-text-fill: -fx-text-muted; -fx-font-size: 12px; -fx-font-weight: bold;");

        // Content: The actual text
        Label contentLabel = new Label(comment.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: white;");

        // Report Section
        HBox footer = new HBox(10);
        footer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        if (comment.isReported()) {
            Label reportedLabel = new Label("⚠️ Under Review");
            reportedLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-style: italic; -fx-font-size: 12px;");
            footer.getChildren().add(reportedLabel);
        } else {
            Button reportBtn = new Button("Report");
            reportBtn.getStyleClass().add("navButton");
            reportBtn.setStyle("-fx-font-size: 11px; -fx-cursor: hand; -fx-text-fill: -fx-text-muted;");
            reportBtn.setOnAction(e -> handleReportClick(comment, commentBox));
            footer.getChildren().add(reportBtn);
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
                CommentaireService service = new CommentaireService();
                
                // Pass the user_id and oeuvre_id (primary keys for comment)
                /*
                boolean success = service.markAsReported(comment.getId_user(), comment.getId_oeuvre(), reason);
                
                if (success) {
                    comment.setReported(true);
                    
                    int index = commentsContainer.getChildren().indexOf(commentBox);
                    commentsContainer.getChildren().set(index, createCommentNode(comment));
                }*/
            }
        });
    }
    
    @FXML
    private void handlePlayTrailer() {
        Object content = MainViewController.getInstance().getSelectedContent();
        
        if (content instanceof Film movie) {
            String trailerPath = movie.getPathTrailer(); 

            if (trailerPath == null || trailerPath.isEmpty()) {
                showError("Trailer Unavailable", "No trailer found for this movie.");
                return;
            }

            try {
                URL resource = getClass().getResource(trailerPath);
                if (resource != null) {
                    openVideoPlayer(resource.toExternalForm(), movie.getId(), movie.getTitre() + " - Trailer");
                } else {
                    showError("File Error", "Trailer file not found on disk.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleBack() {
    	MainViewController.getInstance().goBack();
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}
