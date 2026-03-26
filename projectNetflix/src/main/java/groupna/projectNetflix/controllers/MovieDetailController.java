package groupna.projectNetflix.controllers;

import java.util.stream.Collectors;

import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

public class MovieDetailController {

	@FXML private Label movieTitle;
    @FXML private Label movieDescription;
    @FXML private Label movieMeta;
    @FXML private Label movieCategories;
    @FXML private Label movieCast;

    @FXML private ToggleButton favButton;

    @FXML
    public void initialize() {
        // Retrieve the movie clicked from the Main Controller
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
}
