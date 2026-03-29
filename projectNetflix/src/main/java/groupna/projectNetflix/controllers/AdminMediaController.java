package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.stream.Collectors;

public class AdminMediaController {

    @FXML private TextField searchField;
    @FXML private FlowPane movieContainer;
    @FXML private FlowPane serieContainer;

    private List<Film> allMovies;
    private List<Serie> allSeries;

    @FXML
    public void initialize() {
        allMovies = DataStore.getMovies();
        allSeries = DataStore.getSeries();
        
        displayMedia(allMovies, allSeries);
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterMedia(newVal);
        });
    }

    private void displayMedia(List<Film> movies, List<Serie> series) {
        movieContainer.getChildren().clear();
        serieContainer.getChildren().clear();

        movies.forEach(movie -> movieContainer.getChildren().add(createMediaCard(movie)));
        series.forEach(serie -> serieContainer.getChildren().add(createMediaCard(serie)));
    }

    private VBox createMediaCard(Oeuvre media) {
        VBox card = new VBox(10);
        card.getStyleClass().add("media-card");
        card.setPrefSize(180, 250);

        Label title = new Label(media.getTitre());
        title.getStyleClass().add("card-title");
        
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> openEditDialog(media));
        
        card.getChildren().addAll(title, editBtn);
        return card;
    }

    private void filterMedia(String query) {
        String lowerCaseQuery = query.toLowerCase();
        
        List<Film> filteredMovies = allMovies.stream()
            .filter(f -> f.getTitre().toLowerCase().contains(lowerCaseQuery))
            .collect(Collectors.toList());
            
        List<Serie> filteredSeries = allSeries.stream()
            .filter(s -> s.getTitre().toLowerCase().contains(lowerCaseQuery))
            .collect(Collectors.toList());

        displayMedia(filteredMovies, filteredSeries);
    }

    private void openEditDialog(Oeuvre media) {
        if (media instanceof Film) {
            showMovieForm((Film) media);
        } else if (media instanceof Serie) {
            showSerieForm((Serie) media);
        }
    }

    @FXML
    private void showMovieForm() {
        showMovieForm(null);
    }
    
    private void showMovieForm(Film film) {
        Dialog<Film> dialog = new Dialog<>();
        dialog.setTitle(film == null ? "Add New Movie" : "Edit Movie");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox grid = new VBox(10);
        TextField title = new TextField(film != null ? film.getTitre() : "");
        TextField duration = new TextField(film != null ? film.getDuree().toString() : "");
        DatePicker date = new DatePicker(film != null ? film.getDateDeSortie() : null);

        grid.getChildren().addAll(new Label("Title:"), title, new Label("Duration (HH:MM):"), duration, new Label("Release Date:"), date);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Logic to update DataStore and refresh UI
                return film; 
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML
    private void showSerieForm() {
        showMovieForm(null);
    }
    
    private void showSerieForm(Serie serie) {
        // Similar to showMovieForm, but adds a section for Seasons management
        System.out.println("Opening Serie/Season Management Form...");
    }
}