package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Commentaire;
import groupna.projectNetflix.services.CommentaireService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminUserController {

	@FXML private TableView<Commentaire> movieReportsTable;
    @FXML private TableColumn<Commentaire, Integer> colMovieUser, colMovieId;
    @FXML private TableColumn<Commentaire, String> colMovieComment, colMovieReason;
    @FXML private TableColumn<Commentaire, Void> colMovieActions;

    @FXML private TableView<Commentaire> serieReportsTable;
    @FXML private TableColumn<Commentaire, Integer> colSerieUser, colSerieId;
    @FXML private TableColumn<Commentaire, String> colSerieComment, colSerieReason;
    @FXML private TableColumn<Commentaire, Void> colSerieActions;

    @FXML private TextField commentSearchField;
    
    private CommentaireService commentService = new CommentaireService();
    private ObservableList<Commentaire> movieData = FXCollections.observableArrayList();
    private ObservableList<Commentaire> serieData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
    	setupTable(movieReportsTable, colMovieUser, colMovieId, colMovieComment, colMovieReason, colMovieActions, "film");
        setupTable(serieReportsTable, colSerieUser, colSerieId, colSerieComment, colSerieReason, colSerieActions, "serie");
        
        loadInitialData();
        
        commentSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterAll(newVal);
        });
    }


    
    
    private void setupTable(TableView<Commentaire> table, TableColumn<Commentaire, Integer> userCol, 
            TableColumn<Commentaire, Integer> idCol, TableColumn<Commentaire, String> contentCol, 
            TableColumn<Commentaire, String> reasonCol, TableColumn<Commentaire, Void> actionCol, String type) {
		userCol.setCellValueFactory(new PropertyValueFactory<>("id_user"));
		idCol.setCellValueFactory(new PropertyValueFactory<>("id_oeuvre"));
		contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
		reasonCol.setCellValueFactory(new PropertyValueFactory<>("raison"));
	
		actionCol.setCellFactory(param -> new TableCell<>() {
			private final Button keepBtn = new Button("Keep");
			private final Button delBtn = new Button("Delete");
			private final HBox container = new HBox(10, keepBtn, delBtn);
			{
				keepBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
				delBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
				
				keepBtn.setOnAction(e -> handleModeration(getTableRow().getItem(), "Keep", type));
				delBtn.setOnAction(e -> handleModeration(getTableRow().getItem(), "Delete", type));
			}
			@Override protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : container);
			}
		});
	}


    private void loadInitialData() {
    	movieData.setAll(commentService.listerCommentairesSignalesFilms());
        serieData.setAll(commentService.listerCommentairesSignalesSeries());
        
        movieReportsTable.setItems(movieData);
        serieReportsTable.setItems(serieData);
    }

    private void handleModeration(Commentaire comment, String action, String type) {
        if (comment == null) return;
        if ("Delete".equals(action)) {
            handleAdminAction("Delete " + type + " Comment", () -> {
                if ("film".equals(type)) 
                	movieData.remove(comment);
                else serieData.remove(comment);
            });
        } else {
            if ("film".equals(type)) movieData.remove(comment);
            else serieData.remove(comment);
        }
    }

    private void filterAll(String query) {
        String lowerQuery = query.toLowerCase();
        movieReportsTable.setItems(movieData.filtered(c -> matches(c, lowerQuery)));
        serieReportsTable.setItems(serieData.filtered(c -> matches(c, lowerQuery)));
    }

    private boolean matches(Commentaire c, String query) {
        if (query.isEmpty()) return true;
        return c.getContent().toLowerCase().contains(query) || 
               String.valueOf(c.getId_user()).contains(query);
    }
    

    private void handleAdminAction(String action, Runnable onConfirm) {
        String key = String.valueOf((int)(Math.random() * 9000) + 1000);
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("BingeBox Security");
        dialog.setHeaderText(action + " Authorization");
        dialog.setContentText("Enter Pass Key to confirm [" + key + "]:");
        
        dialog.showAndWait().ifPresent(input -> {
            if (input.equals(key)) {
                onConfirm.run();
            } else {
                showError("Invalid Key", "Action cancelled due to incorrect key.");
            }
        });
    }


    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
}