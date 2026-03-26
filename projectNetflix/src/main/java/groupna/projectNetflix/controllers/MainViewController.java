package groupna.projectNetflix.controllers;

import groupna.projectNetflix.utils.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.layout.*;

public class MainViewController {
	@FXML private BorderPane rootPane;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Button homeBtn, moviesBtn, seriesBtn, themeToggle;
    @FXML private HBox navbar;
    @FXML private VBox sidebar;
    @FXML private HBox navLinksContainer;
    @FXML private Label heroTitle, heroDesc, selectionTitle, recommendedTitle;
    
    private static MainViewController instance;
    
    public MainViewController() {
        instance = this;
    }

    public static MainViewController getInstance() {
        return instance;
    }
    
    @FXML
    public void initialize() {
    	
    	if (sidebar != null) {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
        }
    	
    	if (navLinksContainer != null) {
            navLinksContainer.setVisible(false);
            navLinksContainer.setManaged(false);
        }
    	
    	if (navbar != null) {
            loadPage("AuthView.fxml");
        }
    	
    	setupWindowControls();
    }
    
    public void unlockFullApp() {
        sidebar.setVisible(true);
        sidebar.setManaged(true);
        
        navLinksContainer.setVisible(true);
        navLinksContainer.setManaged(true);
        
        loadPage("HomeView.fxml");
    }
    	
    private void setupWindowControls() {
        if (navbar != null) {
            navbar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            navbar.setOnMouseDragged(event -> {
                Stage stage = (Stage) navbar.getScene().getWindow();
                if (!stage.isMaximized()) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
        }
        Platform.runLater(() -> {
            Stage stage = (Stage) navbar.getScene().getWindow();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setMaxWidth(screenBounds.getWidth());
            stage.setMaxHeight(screenBounds.getHeight());
            makeResizable(stage, navbar.getScene().getRoot());
        });
    }   
    
    private double xOffset = 0;
    private double yOffset = 0;

    public void makeResizable(Stage stage, Node root) {
        final int RESIZE_MARGIN = 10;

        root.setOnMouseMoved(event -> {
            double x = event.getX();
            double y = event.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Cursor cursor = Cursor.DEFAULT;

            if (x < RESIZE_MARGIN && y < RESIZE_MARGIN) cursor = Cursor.NW_RESIZE;
            else if (x < RESIZE_MARGIN && y > height - RESIZE_MARGIN) cursor = Cursor.SW_RESIZE;
            else if (x > width - RESIZE_MARGIN && y < RESIZE_MARGIN) cursor = Cursor.NE_RESIZE;
            else if (x > width - RESIZE_MARGIN && y > height - RESIZE_MARGIN) cursor = Cursor.SE_RESIZE;
            else if (x < RESIZE_MARGIN) cursor = Cursor.W_RESIZE;
            else if (x > width - RESIZE_MARGIN) cursor = Cursor.E_RESIZE;
            else if (y < RESIZE_MARGIN) cursor = Cursor.N_RESIZE;
            else if (y > height - RESIZE_MARGIN) cursor = Cursor.S_RESIZE;

            root.setCursor(cursor);
        });

        root.setOnMouseDragged(event -> {
            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();

            if (root.getCursor() != Cursor.DEFAULT) {

                if (root.getCursor() == Cursor.E_RESIZE || root.getCursor() == Cursor.SE_RESIZE || root.getCursor() == Cursor.NE_RESIZE) {
                    stage.setWidth(mouseX - stage.getX());
                }

                if (root.getCursor() == Cursor.S_RESIZE || root.getCursor() == Cursor.SE_RESIZE || root.getCursor() == Cursor.SW_RESIZE) {
                    stage.setHeight(mouseY - stage.getY());
                }

                if (root.getCursor() == Cursor.W_RESIZE || root.getCursor() == Cursor.SW_RESIZE || root.getCursor() == Cursor.NW_RESIZE) {
                    double newWidth = stage.getWidth() - (mouseX - stage.getX());
                    stage.setX(mouseX);
                    stage.setWidth(newWidth);
                }

                if (root.getCursor() == Cursor.N_RESIZE || root.getCursor() == Cursor.NE_RESIZE || root.getCursor() == Cursor.NW_RESIZE) {
                    double newHeight = stage.getHeight() - (mouseY - stage.getY());
                    stage.setY(mouseY);
                    stage.setHeight(newHeight);
                }
            }
        });
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }
    
    private void loadPage(String fxml) {
        try {
        	FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/groupna/projectNetflix/view/" + fxml)
                );

                loader.setResources(Session.getInstance().getBundle());

                Node node = loader.load();

                rootPane.setCenter(node);

        } catch (Exception e) {
            System.err.println("Error loading FXML: " + fxml);
            e.printStackTrace();
        }
    }
	
	@FXML
	private void handleThemeChange(ActionEvent event) {
	    Parent root = themeToggle.getScene().getRoot();
	    
	    if (root.getStyleClass().contains("light-mode")) {
	        root.getStyleClass().remove("light-mode");
	        themeToggle.setText("🌛");
	    } else {
	        root.getStyleClass().add("light-mode");
	        themeToggle.setText("🔆");
	    }
	}
	
    @FXML
    private void goHome(ActionEvent event){
        loadPage("HomeView.fxml");
    }

    @FXML
    private void openMovies(ActionEvent event){
        loadPage("MoviesView.fxml");
    }

    @FXML
    private void openSeries(ActionEvent event){
        loadPage("SeriesView.fxml");

    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        loadPage("SearchView.fxml");
    }
    
    @FXML
    private void handleProfile(ActionEvent event) {
        loadPage("ProfileView.fxml");
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        //à ajouter: Show watch history
    }
    
    @FXML
    private void handleFavorites() {
    	loadPage("FavsView.fxml");
    }
    
    @FXML
    private void handleSettings(ActionEvent event) {
        loadPage("SettingsView.fxml");
    }

    private Object selectedContent;

    public void setSelectedContent(Object content) {
        this.selectedContent = content;
    }

    public Object getSelectedContent() {
        return selectedContent;
    }

    public void loadDetailPage(String fxml, Object content) {
        this.selectedContent = content;
        loadPage(fxml);
    }

}
