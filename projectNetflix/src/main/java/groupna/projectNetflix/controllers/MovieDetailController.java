package groupna.projectNetflix.controllers;

import java.util.stream.Collectors;

import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.CommentaireService;
import groupna.projectNetflix.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private int currentRating = 0;
    
    @FXML
    public void initialize() {
        Object content = MainViewController.getInstance().getSelectedContent();
        
        if (content instanceof Film movie) {

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

        }
    }
    
    @FXML
    private void handlePlay() {
        System.out.println("Starting Video Player...");
        //video player logic à ajouter
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
            
            CommentaireService service = new CommentaireService();
            boolean success = service.posterCommentaire(user.getId(), movie.getId(), text, "FILM");
            
            VBox commentBox = new VBox(5);
            commentBox.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 10; -fx-background-radius: 5;");
            Label userLabel = new Label(user.getNom() + " • " + currentRating + "★");
            userLabel.setStyle("-fx-text-fill: -fx-text-muted; -fx-font-size: 12px; -fx-font-weight: bold;");
            
            Label contentLabel = new Label(text);
            contentLabel.setWrapText(true);
            contentLabel.setStyle("-fx-text-fill: white;");

            if (success) {
            	commentBox.getChildren().addAll(userLabel, contentLabel);
                commentsContainer.getChildren().add(0, commentBox);
                commentField.clear();
            }
    	} catch (Exception e) {
            showError("Critical Error", "An unexpected error occurred.");
            e.printStackTrace();
        }   
        }
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}
