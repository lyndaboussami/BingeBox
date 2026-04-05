package groupna.projectNetflix.controllers;

import groupna.projectNetflix.DAO.CommentaireDAO;
import groupna.projectNetflix.entities.Commentaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminUserController {

    @FXML private TableView<Commentaire> reportsTable;
    @FXML private TableColumn<Commentaire, Integer> colUser;
    @FXML private TableColumn<Commentaire, String> colComment;
    @FXML private TableColumn<Commentaire, Integer> colOeuvre;
    @FXML private TableColumn<Commentaire, String> colReason;
    @FXML private TableColumn<Commentaire, Void> colActions;
    
    @FXML private TextField commentSearchField;

    private ObservableList<Commentaire> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadInitialData();
        
        commentSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterComments(newVal);
        });
    }

    private void setupTable() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        colOeuvre.setCellValueFactory(new PropertyValueFactory<>("id_oeuvre"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("content"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("raison"));

        reportsTable.getStyleClass().add("main-container"); 
    
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button keepBtn = new Button("Keep");
            private final Button delBtn = new Button("Delete");
            private final HBox container = new HBox(10, keepBtn, delBtn);
            {
                keepBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-cursor: hand;");
                delBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-cursor: hand;");
                
                keepBtn.setOnAction(e -> handleKeep(getTableRow().getItem()));
                delBtn.setOnAction(e -> handleDelete(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void loadInitialData() {
        masterData.setAll(CommentaireDAO.findReported());
        
        reportsTable.setItems(masterData);
    }

    private void filterComments(String query) {
        if (query == null || query.isEmpty()) {
            reportsTable.setItems(masterData);
            return;
        }

        reportsTable.setItems(masterData.filtered(c -> 
        	c.getContent().toLowerCase().contains(query.toLowerCase()) ||
        	String.valueOf(c.getId_user()).contains(query)
        ));
    }

    private void handleDelete(Commentaire selected) {
        if (selected != null) {
        	handleAdminAction("Delete Comment", () -> {
                masterData.remove(selected);
                //missing delete from db!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                System.out.println("Deleted comment from user " + selected.getId_user());
        	});
        }
    }

    private void handleKeep(Commentaire selected) {
        if (selected != null) {
            masterData.remove(selected);
            //remove the under review icon in comments and not anymore reported!!!!!!! //selected.setReported(false);
            System.out.println("Comment marked as safe.");
        }
    }

    private void handleAdminAction(String action, Runnable onConfirm) {
        String key = String.valueOf((int)(Math.random() * 9000) + 1000);
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("BingeBox Security");
        dialog.setHeaderText(action + " Authorization");
        dialog.setContentText("Enter Pass Key to confirm [" + key + "]:");
        
        styleDialog(dialog);

        dialog.showAndWait().ifPresent(input -> {
            if (input.equals(key)) {
                onConfirm.run();
            } else {
                showError("Invalid Key", "Action cancelled due to incorrect key.");
            }
        });
    }

    private void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        String path = "/groupna/projectNetflix/css/style.css";
        var res = getClass().getResource(path);
        if (res != null) {
            pane.getStylesheets().add(res.toExternalForm());
            pane.getStyleClass().add("dialog-pane");
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        styleDialog(alert);
        alert.showAndWait();
    }
    
}