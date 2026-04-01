package groupna.projectNetflix.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminPerformanceController {

    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Number> viewsBarChart;
    @FXML private LineChart<String, Number> registrationLineChart;

    @FXML
    public void initialize() {
        loadCategoryData();
        loadViewsData();
        loadRegistrationData();
    }

    private void loadCategoryData() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Action", 45),
            new PieChart.Data("Drama", 30),
            new PieChart.Data("Comedy", 15),
            new PieChart.Data("Sci-Fi", 10)
        );
        categoryPieChart.setData(pieData);
    }

    private void loadViewsData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Views 2026");
        series.getData().add(new XYChart.Data<>("Inception", 1250));
        series.getData().add(new XYChart.Data<>("The Dark Knight", 980));
        series.getData().add(new XYChart.Data<>("Interstellar", 850));
        series.getData().add(new XYChart.Data<>("Avatar", 720));
        series.getData().add(new XYChart.Data<>("The Matrix", 650));
        
        viewsBarChart.getData().add(series);
    }

    private void loadRegistrationData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Registrations");
        series.getData().add(new XYChart.Data<>("23/03", 5));
        series.getData().add(new XYChart.Data<>("24/03", 12));
        series.getData().add(new XYChart.Data<>("25/03", 8));
        series.getData().add(new XYChart.Data<>("26/03", 20));
        series.getData().add(new XYChart.Data<>("27/03", 15));
        series.getData().add(new XYChart.Data<>("28/03", 25));
        series.getData().add(new XYChart.Data<>("29/03", 18));
        
        registrationLineChart.getData().add(series);
    }
}