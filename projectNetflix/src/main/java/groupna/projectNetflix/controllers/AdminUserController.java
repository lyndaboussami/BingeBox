package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Commentaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminUserController {

    @FXML private TableView<Commentaire> reportsTable;
    @FXML private TableColumn<Commentaire, Integer> colUser;
    @FXML private TableColumn<Commentaire, String> colComment;
    @FXML private TableColumn<Commentaire, Integer> colOeuvre;
    @FXML private TextField commentSearchField;

    private ObservableList<Commentaire> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadMockData();
        
        commentSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterComments(newVal);
        });
    }

    private void setupTable() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("content"));
        colOeuvre.setCellValueFactory(new PropertyValueFactory<>("id_oeuvre"));

        reportsTable.getStyleClass().add("main-container"); 
    }

    private void loadMockData() {
        // Mocking Commentaire(int id_user, int id_oeuvre, String content, boolean reported)
        masterData.add(new Commentaire(101, 50, "This movie is terrible! Don't watch it.", true));
        masterData.add(new Commentaire(102, 51, "Spoiler: Everyone dies at the end lol.", true));
        masterData.add(new Commentaire(105, 50, "Inappropriate language used here.", true));
        masterData.add(new Commentaire(110, 60, "Selling cheap accounts, click my link!", true));

        reportsTable.setItems(masterData);
    }

    private void filterComments(String query) {
        if (query == null || query.isEmpty()) {
            reportsTable.setItems(masterData);
            return;
        }

        ObservableList<Commentaire> filteredList = masterData.filtered(c -> 
            c.getContent().toLowerCase().contains(query.toLowerCase()) ||
            String.valueOf(c.getId_user()).contains(query)
        );
        reportsTable.setItems(filteredList);
    }

    @FXML
    private void handleDeleteComment() {
        Commentaire selected = reportsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {

        	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Comment");
            alert.setHeaderText("Are you sure you want to delete this comment?");
            alert.setContentText("Content: " + selected.getContent());

            if (alert.showAndWait().get() == ButtonType.OK) {
                masterData.remove(selected);
                System.out.println("Deleted comment from user " + selected.getId_user());
            }
        } else {
            showWarning("Please select a comment to delete.");
        }
    }

    @FXML
    private void handleKeepComment() {
        Commentaire selected = reportsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            masterData.remove(selected);
            System.out.println("Comment marked as safe.");
        }
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}