package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Role;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.RateService;
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
    	
    	languageSelector.setVisible(false);
    	setupWindowControls();
    }
    
    public void unlockFullApp() {
    	
    	User currentUser = Session.getInstance().getUser();
    	
        sidebar.setVisible(true);
        sidebar.setManaged(true);
        
        navLinksContainer.setVisible(true);
        navLinksContainer.setManaged(true);
        
        boolean isAdmin = (currentUser.getRole() == Role.ADMIN);
        
        btnManageContent.setVisible(isAdmin);
        btnManageContent.setManaged(isAdmin);
        btnManageUsers.setVisible(isAdmin);
        btnManageUsers.setManaged(isAdmin);
        btnAnalytics.setVisible(isAdmin);
        btnAnalytics.setManaged(isAdmin);
        
        loadPage("HomeView.fxml");
    }
    	
    private double xOffset = 0;
    private double yOffset = 0;
    
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
    
    private static final int RESIZE_MARGIN = 8;
    private double startX, startY;

    public void makeResizable(Stage stage, Node root) {

        root.setOnMouseMoved(event -> {
            double x = event.getX();
            double y = event.getY();
            double width = root.getBoundsInLocal().getWidth();
            double height = root.getBoundsInLocal().getHeight();

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

        root.setOnMousePressed(event -> {
            startX = event.getScreenX();
            startY = event.getScreenY();
        });

        root.setOnMouseDragged(event -> {
            Cursor cursor = root.getCursor();

            double dx = event.getScreenX() - startX;
            double dy = event.getScreenY() - startY;

            double minWidth = 400;
            double minHeight = 300;

            if (cursor == Cursor.E_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.NE_RESIZE) {
                double newWidth = stage.getWidth() + dx;
                if (newWidth > minWidth) {
                    stage.setWidth(newWidth);
                    startX = event.getScreenX();
                }
            }

            if (cursor == Cursor.S_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.SW_RESIZE) {
                double newHeight = stage.getHeight() + dy;
                if (newHeight > minHeight) {
                    stage.setHeight(newHeight);
                    startY = event.getScreenY();
                }
            }

            if (cursor == Cursor.W_RESIZE || cursor == Cursor.SW_RESIZE || cursor == Cursor.NW_RESIZE) {
                double newWidth = stage.getWidth() - dx;
                if (newWidth > minWidth) {
                    stage.setX(stage.getX() + dx);
                    stage.setWidth(newWidth);
                    startX = event.getScreenX();
                }
            }

            if (cursor == Cursor.N_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.NW_RESIZE) {
                double newHeight = stage.getHeight() - dy;
                if (newHeight > minHeight) {
                    stage.setY(stage.getY() + dy);
                    stage.setHeight(newHeight);
                    startY = event.getScreenY();
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
    public void showDetails(MouseEvent event) {
        Node card = (Node) event.getSource();
        Object data = card.getUserData();
        RateService rating= new RateService();
        if (data == null) return;

        StringBuilder sb = new StringBuilder();

        if (data instanceof Film f) {

        	sb.append("🎬 ").append(f.getTitre())
              .append(" (").append(f.getDateDeSortie().getYear()).append(")\n");
            
            sb.append("⭐ ").append(String.format("%.1f", rating.getMoyenneOeuvre(f.getId(),"film")))
              .append("/5  |  🕒 ").append(f.getDuree()).append("\n");
            
            if (f.getCat() != null && !f.getCat().isEmpty()) {
                String genres = f.getCat().stream()
                                 .map(groupna.projectNetflix.entities.Categorie::getLabel)
                                 .collect(java.util.stream.Collectors.joining(" • "));
                sb.append("🎫 ").append(genres).append("\n");
            }
            
            sb.append("\n\"").append(truncateResume(f.getResume())).append("\"");
        } 
        else if (data instanceof Serie s) {
            int seasonCount = (s.getSaisons() != null) ? s.getSaisons().size() : 0;
            
            sb.append("📺 ").append(s.getTitre())
              .append(" (").append(s.getDateDeSortie().getYear()).append(")\n");
            
            sb.append("⭐ ").append(String.format("%.1f", rating.getMoyenneOeuvre(s.getId(),"serie")))//--rating--
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
            "-fx-background-color: #0d111a;" +
            "-fx-text-fill: #e6d7c4;" +
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

    public void loadSuperAdminCreator() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/groupna/projectNetflix/view/AuthView.fxml")
            );
            Node node = loader.load();
            
            AuthController controller = loader.getController();
            controller.setSuperAdminCreation(true);

            rootPane.setCenter(node);
            hideNavigation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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

    
