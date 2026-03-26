package groupna.projectNetflix.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class SearchController extends BaseController{
	@FXML private TextField searchField;
    @FXML private FlowPane resultsPane;
    @FXML private ComboBox<String> yearFilter;
    @FXML private Label resultsCountLabel;

    @FXML
    public void initialize() {
        // Populate years
        ObservableList<String> years = FXCollections.observableArrayList("All Years", "2026", "2025", "2024", "2023");
        yearFilter.setItems(years);

        // Dynamic Search Listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSearchResults(newValue);
        });
    }

    private void updateSearchResults(String query) {
        if (query.isEmpty()) {
            resultsCountLabel.setText("Explore all titles");
        } else {
            resultsCountLabel.setText("Results for: " + query);
        }
        // Logic to filter your Movie/Series list and rebuild the resultsPane
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        yearFilter.getSelectionModel().selectFirst();
    }
}
