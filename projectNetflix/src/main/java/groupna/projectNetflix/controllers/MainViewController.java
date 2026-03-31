package groupna.projectNetflix.controllers;

import groupna.projectNetflix.utils.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.layout.*;

public class MainViewController {
	@FXML private BorderPane rootPane;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Button homeBtn, moviesBtn, seriesBtn, themeToggle;
    @FXML private HBox navbar;
    @FXML private VBox sidebar;
    @FXML private HBox navLinksContainer;
    @FXML private Label heroTitle, heroDesc, selectionTitle, recommendedTitle;
    private String lastPage = "HomeView.fxml";
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
    
    public void loadPage(String fxml) {

        try {
        	
        	if (!fxml.equals("MovieDetailView.fxml") && 
                    !fxml.equals("SeriesDetailView.fxml") && 
                    !fxml.equals("ProfileView.fxml")) {
                    
                    this.lastPage = fxml;
        	}
        	FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/groupna/projectNetflix/view/" + fxml)
                );

                loader.setResources(Session.getInstance().getBundle());

                Node node = loader.load();

                rootPane.setCenter(node);
                
                if (fxml.equals("HomeView.fxml") || fxml.equals("MoviesView.fxml") || fxml.equals("SeriesView.fxml")) {
                	if (sidebar != null) {
                        sidebar.setVisible(true);
                        sidebar.setManaged(true);
                    }
                    if (navLinksContainer != null) {
                        navLinksContainer.setVisible(true);
                        navLinksContainer.setManaged(true);
                    }
                }

        } catch (Exception e) {
            System.err.println("Error loading FXML: " + fxml);
            e.printStackTrace();
        }
    }
	
    public void goBack() {
        loadPage(lastPage);
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
    private void handleHistory(ActionEvent event) {
        loadPage("HistoryView.fxml");
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
    
    @FXML
    public void showDetails(javafx.scene.input.MouseEvent event) {
        Node card = (Node) event.getSource();
        Object data = card.getUserData();

        if (data == null) return;

        StringBuilder sb = new StringBuilder();

        if (data instanceof groupna.projectNetflix.entities.Film f) {

        	sb.append("🎬 ").append(f.getTitre())
              .append(" (").append(f.getDateDeSortie().getYear()).append(")\n");
            
            sb.append("⭐ ").append(String.format("%.1f", f.getRate()))//--rate--
              .append("/5  |  🕒 ").append(f.getDuree()).append("\n");
            
            if (f.getCat() != null && !f.getCat().isEmpty()) {
                String genres = f.getCat().stream()
                                 .map(groupna.projectNetflix.entities.Categorie::getLabel)
                                 .collect(java.util.stream.Collectors.joining(" • "));
                sb.append("🎫 ").append(genres).append("\n");
            }
            
            sb.append("\n\"").append(truncateResume(f.getResume())).append("\"");
        } 
        else if (data instanceof groupna.projectNetflix.entities.Serie s) {
            int seasonCount = (s.getSaisons() != null) ? s.getSaisons().size() : 0;
            
            sb.append("📺 ").append(s.getTitre())
              .append(" (").append(s.getDateDeSortie().getYear()).append(")\n");
            
            sb.append("⭐ ").append(String.format("%.1f", s.getRate()))//--rating--
              .append("/5  |  📂 ").append(seasonCount).append(" Saisons\n");
            
            if (s.getCat() != null && !s.getCat().isEmpty()) {
                String genres = s.getCat().stream()
                                 .map(groupna.projectNetflix.entities.Categorie::getLabel)
                                 .collect(java.util.stream.Collectors.joining(" • "));
                sb.append("🎫 ").append(genres).append("\n");
            }
            
            sb.append("\n\"").append(truncateResume(s.getResume())).append("\"");
        }

        Tooltip tooltip = new Tooltip(sb.toString());
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(280); 
        
        tooltip.setStyle(
            "-fx-background-color: #0d111a;" + // Slightly darker navy
            "-fx-text-fill: #e6d7c4;" +        // Beige text
            "-fx-font-family: 'Segoe UI', system-ui;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #cfc2b0;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 15, 0, 0, 5);"
        );
        
        tooltip.setShowDelay(Duration.millis(150));
        Tooltip.install(card, tooltip);
    }

    private String truncateResume(String resume) {
        if (resume == null || resume.trim().isEmpty()) return "Aucune description disponible.";

        if (resume.length() > 140) {
            return resume.substring(0, 137).trim() + "...";
        }
        return resume;
    }
    
    public void hideNavigation() {
        if (sidebar != null) {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
        }
        if (navLinksContainer != null) {
            navLinksContainer.setVisible(false);
            navLinksContainer.setManaged(false);
        }
    }

    private boolean isViewingProfile = false;

    @FXML
    private void handleProfileClick(MouseEvent event) {
        if (isViewingProfile) {
            loadPage("HomeView.fxml");
            isViewingProfile = false;
        } else {
            loadPage("ProfileView.fxml");
            isViewingProfile = true;
        }
    }
    
    @FXML
    public void hideDetails(MouseEvent event) {
    }
    @FXML private Button btnManageContent;
    @FXML private Button btnManageUsers;
    @FXML private Button btnAnalytics;

    @FXML
    private void handleAdminNav(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        
        if (clickedButton == btnManageContent) {
            loadPage("AdminMediaCRUD.fxml");
        }
        else if (clickedButton == btnManageUsers) {
            loadPage("AdminUserManagement.fxml");
        }
        else if (clickedButton == btnAnalytics) {
            loadPage("AdminPerformanceView.fxml");
        }
    }
}

    
