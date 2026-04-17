package groupna.projectNetflix.controllers;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.services.SerieService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class SeriesController extends BaseController{
	@FXML private VBox seriesRowsContainer;
	private SerieService serieService=new SerieService();
    @FXML
    public void initialize() {
    	
        List<Serie> allSeries = serieService.getAllSeries();
        Map<String, List<Serie>> seriesByCategory = allSeries.stream()
            .flatMap(s -> s.getCat().stream().map(cat -> Map.entry(cat.getLabel(), s)))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey, 
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
            ));

        seriesByCategory.forEach((catName, list) -> {
            seriesRowsContainer.getChildren().add(createCategoryRow(catName, list));
        });
    }

    private VBox createCategoryRow(String title, List<Serie> seriesList) {
        VBox section = new VBox(10);
        section.getStyleClass().add("category-section");

        Label label = new Label(title);
        label.getStyleClass().add("categoryTitle");

        ScrollPane sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.getStyleClass().add("inner-scroll");

        HBox row = new HBox(15);
        row.setPadding(new javafx.geometry.Insets(0, 0, 10, 0));

        for (Serie s : seriesList) {
            row.getChildren().add(createSeriesCard(s));
        }

        sp.setContent(row);
        section.getChildren().addAll(label, sp);
        return section;
    }

    private VBox createSeriesCard(Serie serie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("seriesCard");

        card.setUserData(serie); 

        MainViewController mainCtrl = MainViewController.getInstance();
        if (mainCtrl != null) {
            card.setOnMouseEntered(event -> mainCtrl.showDetails(event));
            card.setOnMouseExited(event -> mainCtrl.hideDetails(event));
        }
        
        StackPane stack = new StackPane();
        
        ImageView poster = new ImageView();
        poster.setFitWidth(200);
        poster.setFitHeight(300);
        
        try {
            String path = serie.getPathPoster();
            if (path != null && !path.isEmpty()) {
                File file = new File(path);
                
                if (file.exists()) {
                    poster.setImage(new Image(new FileInputStream(file)));
                } else {
                    System.err.println("Fichier introuvable sur le disque : " + path);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        Rectangle clip = new Rectangle(200, 300);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        poster.setClip(clip);

        Label badge = new Label("📺");
        badge.getStyleClass().add("card-type-badge");
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new javafx.geometry.Insets(8));

        stack.getChildren().addAll(poster, badge);

        Label title = new Label(serie.getTitre());
        title.getStyleClass().add("card-text");
        title.setMaxWidth(140);

        card.getChildren().addAll(stack, title);

        card.setOnMouseClicked(e -> {
            MainViewController.getInstance().loadDetailPage("SeriesDetailView.fxml", serie);
        });

        return card;
    }
}
