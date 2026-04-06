package groupna.projectNetflix.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.DAO.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public class AdminPerformanceController {

    @FXML private PieChart movieCategoryChart, seriesCategoryChart;
    @FXML private LineChart<String, Number> loginLineChart;
    @FXML private TableView<Map.Entry<Film, Double>> topRatedTable;
    @FXML private TableColumn<Map.Entry<Film, Double>, String> colRatedTitle;
    @FXML private TableColumn<Map.Entry<Film, Double>, Double> colRatedStars;

    @FXML private TableView<Map.Entry<Film, Integer>> topViewedTable;
    @FXML private TableColumn<Map.Entry<Film, Integer>, String> colViewedTitle;
    @FXML private TableColumn<Map.Entry<Film, Integer>, Integer> colViewedCount;

    @FXML private Label lblTotalMovies, lblTotalSeries, lblTotalUsers;
    
    //private final FilmDAO filmDAO = new FilmDAO();
    //private final SerieDAO serieDAO = new SerieDAO();
    private final DAOStatics statsDAO = new DAOStatics(); 

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshCharts();
        //testInterfaceWithMockData(); // Test without DAO
    }
    
    /*
    //juste bech ntesti hata lin nzidou l fazet li bech nestaamelhom
    private void testInterfaceWithMockData() {
        // 1. Test Movie Categories (PieChart)
        movieCategoryChart.getData().setAll(
            new PieChart.Data("Action", 45),
            new PieChart.Data("Drama", 30),
            new PieChart.Data("Sci-Fi", 25)
        );

        // 2. Test Series Categories (PieChart)
        seriesCategoryChart.getData().setAll(
            new PieChart.Data("Comedy", 50),
            new PieChart.Data("Thriller", 20),
            new PieChart.Data("Documentary", 30)
        );

        // 3. Test Login History (LineChart)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Logins");
        series.getData().add(new XYChart.Data<>("Mon", 120));
        series.getData().add(new XYChart.Data<>("Tue", 150));
        series.getData().add(new XYChart.Data<>("Wed", 110));
        series.getData().add(new XYChart.Data<>("Thu", 190));
        loginLineChart.getData().add(series);

     // 4. Test Top Rated Table
        ObservableList<Film> mockRated = FXCollections.observableArrayList();
        
        // Using your constructor: (id, resume, categories, titre, date, actors, directors, poster, duration, moviePath, trailerPath)
        Film f1 = new Film(1, "Resume...", new ArrayList<>(), "Inception", LocalDate.now(), null, null, null, LocalTime.of(2, 28), null, null);
        
        Film f2 = new Film(2, "Resume...", new ArrayList<>(), "The Matrix", LocalDate.now(), null, null, null, LocalTime.of(2, 16), null, null);
        
        mockRated.addAll(f1, f2);
        topRatedTable.setItems(mockRated);

        // 5. Test Top Viewed Table
        ObservableList<Film> mockViewed = FXCollections.observableArrayList();
        Film f3 = new Film(3, "Resume...", new ArrayList<>(), "Avatar", LocalDate.now(), null, null, null, LocalTime.of(2, 42), null, null);
        
        mockViewed.add(f3);
        topViewedTable.setItems(mockViewed);
    }
	*/
    private void setupTableColumns() {
        colRatedTitle.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getKey().getTitre()));
        
        colRatedStars.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getValue()));

        colViewedTitle.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getKey().getTitre()));
        
        colViewedCount.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getValue()));
    }

    private void refreshCharts() {
    	
    	lblTotalMovies.setText(String.valueOf(DAOStatics.getTotalMovies()));
        lblTotalSeries.setText(String.valueOf(DAOStatics.getTotalSeries()));
        lblTotalUsers.setText(String.valueOf(DAOStatics.getTotalUsers()));
    	
        Map<Categorie, Integer> movieStats = DAOStatics.getMoviesCountByCategory();
        movieCategoryChart.getData().clear();
        movieStats.forEach((cat, count) -> 
            movieCategoryChart.getData().add(new PieChart.Data(cat.getLabel(), count)));

        Map<Categorie, Integer> seriesStats = DAOStatics.getSeriesCountByCategory();
        seriesCategoryChart.getData().clear();
        seriesStats.forEach((cat, count) -> 
            seriesCategoryChart.getData().add(new PieChart.Data(cat.getLabel(), count)));

        XYChart.Series<String, Number> loginSeries = new XYChart.Series<>();
        loginSeries.setName("User Traffic");
        /*
        Map<String, Integer> loginData = statsDAO.getDailyLoginCounts();
        loginData.forEach((date, count) -> 
            loginSeries.getData().add(new XYChart.Data<>(date, count)));
        */
        loginLineChart.getData().setAll(loginSeries);

        Map<Film, Double> ratedData = DAOStatics.getTop5Rated();
        topRatedTable.setItems(FXCollections.observableArrayList(ratedData.entrySet()));
        

        Map<Film, Integer> viewedData = DAOStatics.getTop5MostViewed();
        topViewedTable.setItems(FXCollections.observableArrayList(viewedData.entrySet()));
    }
}