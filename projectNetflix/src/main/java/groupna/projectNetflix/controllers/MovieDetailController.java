package groupna.projectNetflix.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

import groupna.projectNetflix.DAO.HistoryItem;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Commentaire;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.CommentaireService;
import groupna.projectNetflix.services.RateService;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class MovieDetailController {
	private UserService userService=new UserService();
	private RateService rateService=new RateService();
	@FXML private Label movieTitle;
    @FXML private Label movieDescription;
    @FXML private Label movieMeta;
    @FXML private Label movieCategories;
    @FXML private Label movieCast;

    @FXML private ToggleButton favButton;
    @FXML private Button playButton;

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
        User user =Session.getInstance().getUser();
        
        if (content instanceof Film movie) {
        	currentRating=rateService.getNoteUtilisateur(user.getId(),movie.getId(), "film");
    		
        	for (int i = 0; i < starContainer.getChildren().size(); i++) {
                Button star = (Button) starContainer.getChildren().get(i);
                if (i < currentRating) {
                    star.setStyle("-fx-text-fill: #ffcc00; -fx-background-color: transparent; -fx-font-size: 24px;"); // Gold
                } else {
                    star.setStyle("-fx-text-fill: #555; -fx-background-color: transparent; -fx-font-size: 24px;"); // Grey
                }
            }

        	String posterPath = movie.getPathPoster();

            if (posterPath != null && !posterPath.isEmpty()) {
                try {
                	File file = new File(posterPath);
                	String imagePath = file.toURI().toString(); 

                	Image posterImage = new Image(imagePath, true);
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
                    .map(a -> a.getFullname())
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
        if (content instanceof Film) {
            Film film = (Film) content;
            int idFilm = film.getId();
            String moviePath = film.getPathMovie(); // Ex: "C:/Users/bsouh/Desktop/..."
            
            User user = Session.getInstance().getUser();
            Optional<HistoryItem> alreadyWatched = userService.recupererHistoriqueComplet(user.getId())
                .stream()
                .filter(a -> a.getContent() instanceof Film && a.getContent().equals(film))
                .findFirst();
            
            double time = alreadyWatched.isPresent() ? alreadyWatched.get().getTime() : 0.0;

            if (moviePath == null || moviePath.isEmpty()) {
                System.err.println("Error: No path defined for this movie.");
                return;
            }

            try {
                File file = new File(moviePath);

                if (!file.exists()) {
                    System.err.println("Le fichier est introuvable sur le disque dur : " + moviePath);
                    return;
                }
                String fullUrl = file.toURI().toString();
                openVideoPlayer(idFilm, time, fullUrl, film.getTitre());

            } catch (Exception e) {
                System.err.println("Error playing video: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void openVideoPlayer(int idFilm,double time,String url, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groupna/projectNetflix/view/VideoPlayerView.fxml"));
            
            User user=Session.getInstance().getUser();
            Parent playerView = loader.load();
            
            VideoPlayerController controller = loader.getController();
            controller.loadVideo(idFilm,time,url);
            
            StackPane mainStack = (StackPane) movieTitle.getScene().getRoot();
                        
            mainStack.getChildren().add(playerView);
            
            controller.setOnCloseRequest(() -> {
                controller.stopVideo();
                userService.marquerFilmCommeVu(user.getId(), idFilm, controller.getTime());
                mainStack.getChildren().remove(playerView);            });
            
        } catch (IOException e) {
        	System.err.println("Error loading player: " + e.getMessage());
            e.printStackTrace();
        }
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
                    userService.ajouterAuxFavoris(user.getId(),currentMedia.getId(), "film");
                } else {
                    userService.retirerDesFavoris(user.getId(), currentMedia.getId(), "film");
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
    	Film selected=(Film) MainViewController.getInstance().getSelectedContent();
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
        rateService.noterContenu(user.getId(), ((Film) selected).getId(), currentRating, "film");
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
            Film movie = (Film) MainViewController.getInstance().getSelectedContent();
            
            Commentaire newComment = new Commentaire(user.getId(), movie.getId(), text, false,null,0);
            
            CommentaireService service = new CommentaireService();
            boolean success = service.posterCommentaire(newComment, "film");
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
                
        service.recupererCommentairesOeuvre(movieId, "film").forEach(comment -> {
            commentsContainer.getChildren().add(createCommentNode(comment));
        });
    }
    
    private VBox createCommentNode(Commentaire comment) {
        VBox commentBox = new VBox(8);
        commentBox.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 10; -fx-background-radius: 5;");
        User user=Session.getInstance().getUser();

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
        	if(comment.getId_user()!=user.getId()) {
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
                CommentaireService service = new CommentaireService();
                
                boolean success = service.signalerAbus(comment.getId(),"film", reason);
                
                if (success) {
                    comment.setReported(true);
                    
                    int index = commentsContainer.getChildren().indexOf(commentBox);
                    commentsContainer.getChildren().set(index, createCommentNode(comment));
                }
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
               File file = new java.io.File(trailerPath);

                if (file.exists()) {
                    String fullUrl = file.toURI().toString();
                    openVideoPlayer(0, 0.0, fullUrl, movie.getTitre() + " - Trailer");
                } else {
                    System.err.println("Trailer introuvable au chemin : " + trailerPath);
                    showError("File Error", "Trailer file not found at: " + trailerPath);
                }
            } catch (Exception e) {
                System.err.println("Error playing trailer: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /*@FXML
    private void handlePlayTrailer() {
        Object content = MainViewController.getInstance().getSelectedContent();
        
        if (content instanceof Film movie) {
            String trailerPath = movie.getPathTrailer(); 

            if (trailerPath == null || trailerPath.isEmpty()) {
                showError("Trailer Unavailable", "No trailer found for this movie.");
                return;
            }

            try {
                String resource = getClass().getResource(trailerPath).toExternalForm();
                if (resource != null) {
                    openVideoPlayer(0,0.0,resource, movie.getTitre() + " - Trailer");
                } else {
                    showError("File Error", "Trailer file not found on disk.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
    
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
