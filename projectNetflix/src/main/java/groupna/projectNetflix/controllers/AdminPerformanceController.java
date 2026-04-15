package groupna.projectNetflix.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.DAO.*;

import java.time.LocalDate;
import java.util.Map;

public class AdminPerformanceController {

    @FXML private PieChart movieCategoryChart, seriesCategoryChart;
    @FXML private LineChart<String, Number> userTrafficChart;
    @FXML private BarChart<String, Number> viewsBarChart;
    
    @FXML private TableView<Map.Entry<Film, Double>> topRatedTable;
    @FXML private TableColumn<Map.Entry<Film, Double>, String> colRatedTitle;
    @FXML private TableColumn<Map.Entry<Film, Double>, Double> colRatedStars;

    @FXML private Label lblTotalMovies, lblTotalSeries, lblTotalUsers;
    
    @FXML private TableView<Map.Entry<Serie, Double>> topRatedSeriesTable;
    @FXML private TableColumn<Map.Entry<Serie, Double>, String> colSeriesTitle;
    @FXML private TableColumn<Map.Entry<Serie, Double>, Double> colSeriesStars;
    
    private final UserService userService = new UserService();
    
    @FXML
    public void initialize() {
        setupTableColumns();
        refreshCharts();
    }
    
    private void setupTableColumns() {
    	colRatedTitle.setCellValueFactory(data -> 
	        new javafx.beans.property.SimpleStringProperty(data.getValue().getKey().getTitre()));
	    colRatedStars.setCellValueFactory(data -> 
	        new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getValue()));
	
	    colSeriesTitle.setCellValueFactory(data -> 
	        new javafx.beans.property.SimpleStringProperty(data.getValue().getKey().getTitre()));
	    colSeriesStars.setCellValueFactory(data -> 
	        new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getValue()));
	}

    private void refreshCharts() {
    	
    	lblTotalMovies.setText(String.valueOf(DAOStatics.getTotalMovies()));
        lblTotalSeries.setText(String.valueOf(DAOStatics.getTotalSeries()));
        lblTotalUsers.setText(String.valueOf(DAOStatics.getTotalUsers()));
    	
        userTrafficChart.getData().clear();
        
        XYChart.Series<String, Number> signupSeries = new XYChart.Series<>();
        signupSeries.setName("New Sign-ups");
        Map<LocalDate, Integer> signupData = userService.NbrIscrisPerDate();
        signupData.forEach((date, count) -> {
            signupSeries.getData().add(new XYChart.Data<>(date.toString(), count));
        });
        
        
        XYChart.Series<String, Number> loginSeries = new XYChart.Series<>();
        loginSeries.setName("Daily Logins");
        Map<LocalDate, Integer> loginData = DAOStatics.getStatsLoginsSeptDerniersJours();
        loginData.forEach((date, count) -> {
            loginSeries.getData().add(new XYChart.Data<>(date.toString(), count));
        });
        
        userTrafficChart.getData().addAll(signupSeries, loginSeries);
        
        
        Map<Categorie, Integer> movieStats = DAOStatics.getMoviesCountByCategory();
        movieCategoryChart.getData().clear();
        movieStats.forEach((cat, count) -> 
            movieCategoryChart.getData().add(new PieChart.Data(cat.getLabel(), count)));

        Map<Categorie, Integer> seriesStats = DAOStatics.getSeriesCountByCategory();
        seriesCategoryChart.getData().clear();
        seriesStats.forEach((cat, count) -> 
            seriesCategoryChart.getData().add(new PieChart.Data(cat.getLabel(), count)));

        Map<Film, Double> ratedMovies = DAOStatics.getTop5Rated();
        topRatedTable.setItems(FXCollections.observableArrayList(ratedMovies.entrySet()));

        Map<Serie, Double> ratedSeries = DAOStatics.getTop5RatedSeries();
        topRatedSeriesTable.setItems(FXCollections.observableArrayList(ratedSeries.entrySet()));
        
        viewsBarChart.getData().clear();
        XYChart.Series<String, Number> viewSeries = new XYChart.Series<>();
        viewSeries.setName("Views");
        
        Map<Film, Integer> viewedData = DAOStatics.getTop5MostViewed();
        viewedData.forEach((film, count) -> {
            viewSeries.getData().add(new XYChart.Data<>(film.getTitre(), count));
        });
        viewsBarChart.getData().add(viewSeries);
    }
}