package groupna.projectNetflix.controllers;

import groupna.projectNetflix.DAO.FilmDAO;
import groupna.projectNetflix.DAO.SerieDAO;
import groupna.projectNetflix.entities.*;
import groupna.projectNetflix.services.EpisodeService;
import groupna.projectNetflix.services.FilmService;
import groupna.projectNetflix.services.SaisonService;
import groupna.projectNetflix.services.SerieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminMediaController {

    @FXML private TextField searchField;
    
    @FXML private TableView<Film> movieTable;
    @FXML private TableColumn<Film, String> colMovieThumb, colMovieTitle, colMovieDate, colMovieDuration, colMovieActions;
    
    @FXML private TableView<Serie> serieTable;
    @FXML private TableColumn<Serie, String> colSerieThumb, colSerieTitle, colSerieDate, colSerieSeasons, colSerieActions;
    
    private ObservableList<Film> masterMovies = FXCollections.observableArrayList();
    private ObservableList<Serie> masterSeries = FXCollections.observableArrayList();
    
    private FilmService filmService=new FilmService();
    private SerieService serieService=new SerieService();
    private EpisodeService episodeService=new EpisodeService();
    private SaisonService saisonService=new SaisonService();
    //@FXML private FlowPane movieContainer;
    //@FXML private FlowPane serieContainer;

    //private List<Film> allMovies;
    //private List<Serie> allSeries;

    @FXML
    public void initialize() {
    	
    	setupTableColumns();
        loadDataFromDatabase();
        
        searchField.textProperty().addListener((obs, old, newVal) -> {
            filterTables(newVal);
        });
        
    	/*
        allMovies = DataStore.getMovies();
        allSeries = DataStore.getSeries();
        
        displayMedia(allMovies, allSeries);
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterMedia(newVal);
        });*/
    }

    /*
    private void displayMedia(List<Film> movies, List<Serie> series) {
        movieContainer.getChildren().clear();
        serieContainer.getChildren().clear();

        movies.forEach(movie -> movieContainer.getChildren().add(createMediaCard(movie)));
        series.forEach(serie -> serieContainer.getChildren().add(createMediaCard(serie)));
    }*/

    /*
    private VBox createMediaCard(Oeuvre media) {
        VBox card = new VBox(10);
        card.getStyleClass().add("media-card");
        card.setPrefSize(180, 250);

        Label title = new Label(media.getTitre());
        title.getStyleClass().add("card-title");
        
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> openEditDialog(media));
        
        card.getChildren().addAll(title, editBtn);
        return card;
    }*/

    private void loadDataFromDatabase() {
    	List<Film> dbMovies = filmService.getAllFilms();
    	List<Serie> dbSeries = serieService.getAllSeries();
    	
    	masterMovies.setAll(dbMovies/*DataStore.getMovies()*/);
        masterSeries.setAll(dbSeries/*DataStore.getSeries()*/);
        
        movieTable.setItems(masterMovies);
        serieTable.setItems(masterSeries);
    }
    
    /*
    private void filterMedia(String query) {
        String lowerCaseQuery = query.toLowerCase();
        
        List<Film> filteredMovies = allMovies.stream()
            .filter(f -> f.getTitre().toLowerCase().contains(lowerCaseQuery))
            .collect(Collectors.toList());
            
        List<Serie> filteredSeries = allSeries.stream()
            .filter(s -> s.getTitre().toLowerCase().contains(lowerCaseQuery))
            .collect(Collectors.toList());

        displayMedia(filteredMovies, filteredSeries);
    }

    private void openEditDialog(Oeuvre media) {
        if (media instanceof Film) {
            showMovieForm((Film) media);
        } else if (media instanceof Serie) {
            showSerieForm((Serie) media);
        }
    }*/

    private void filterTables(String query) {
        String lower = query.toLowerCase();
        
        FilteredList<Film> filteredMovies = new FilteredList<>(masterMovies, p -> 
            p.getTitre().toLowerCase().contains(lower));
        
        FilteredList<Serie> filteredSeries = new FilteredList<>(masterSeries, p -> 
            p.getTitre().toLowerCase().contains(lower));

        movieTable.setItems(filteredMovies);
        serieTable.setItems(filteredSeries);
    }
    
    private void setupTableColumns() {

    	colMovieTitle.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colMovieDate.setCellValueFactory(new PropertyValueFactory<>("dateDeSortie"));
        colMovieDuration.setCellValueFactory(new PropertyValueFactory<>("duree"));

        colMovieThumb.setCellFactory(param -> new TableCell<>() {
            private final ImageView img = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Film f = getTableRow().getItem();
                    String path = f.getPathPoster();
                    
                    try {
                        var stream = getClass().getResourceAsStream(path);
                        if (stream != null) {
                            img.setImage(new Image(stream));
                            img.setFitHeight(50); 
                            img.setPreserveRatio(true);
                            setGraphic(img);
                        } else {
                            System.err.println("Resource not found: " + path);
                            setGraphic(new Label("No Image")); 
                        }
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        colMovieActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(10, editBtn, deleteBtn);
            
            {
                editBtn.getStyleClass().add("admin-edit-btn");
                deleteBtn.getStyleClass().add("admin-delete-btn");

                editBtn.setOnAction(e -> showMovieForm(getTableView().getItems().get(getIndex())));
            
                deleteBtn.setOnAction(e -> handleAdminAction("Delete", () -> {
                    Film f = getTableRow().getItem();
                    filmService.deleteFilm(f.getId());
                    getTableView().getItems().remove(f);
                }));
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

        colSerieTitle.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colSerieDate.setCellValueFactory(new PropertyValueFactory<>("dateDeSortie"));
        
        colSerieSeasons.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) setText(null);
                else setText(String.valueOf(getTableRow().getItem().getSaisons().size()));
            }
        });

        colSerieThumb.setCellFactory(param -> new TableCell<>() {
            private final ImageView img = new ImageView();
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Serie s = getTableRow().getItem();
                    String path = s.getPathPoster();
                    
                    try {
                        var stream = getClass().getResourceAsStream(path);
                        if (stream != null) {
                            img.setImage(new Image(stream));
                            img.setFitHeight(50); 
                            img.setPreserveRatio(true);
                            setGraphic(img);
                        } else {
                            Label placeholder = new Label("No Poster");
                            placeholder.getStyleClass().add("card-text");
                            setGraphic(placeholder);
                        }
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        colSerieActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(10, editBtn, deleteBtn);
            {
                editBtn.getStyleClass().add("admin-edit-btn");
                deleteBtn.getStyleClass().add("admin-delete-btn");

                editBtn.setOnAction(e -> {
                    Serie selectedSerie = getTableView().getItems().get(getIndex());
                    showSerieForm(selectedSerie);
                });
            
                deleteBtn.setOnAction(e -> handleAdminAction("Delete", () -> {
                    Serie s = getTableRow().getItem();
                    SerieDAO.delete(s.getId());
                    getTableView().getItems().remove(s);
                }));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }
    
    private void handleAdminAction(String actionType, Runnable onConfirm) {
        String generatedKey = String.valueOf((int)(Math.random() * 9000) + 1000);
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Admin Verification");
        alert.setHeaderText(actionType + " Action Requested");
        alert.setContentText("To confirm this " + actionType + ", please enter the BingeBox Pass Key: " + generatedKey);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter Pass Key here");
        alert.getDialogPane().setContent(new VBox(10, new Label("Enter Key: " + generatedKey), inputField));

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/groupna/projectNetflix/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (inputField.getText().equals(generatedKey)) {
                    onConfirm.run();
                    showNotification("Success", actionType + " completed successfully.");
                } else {
                    showError("Invalid Key", "The Pass Key entered is incorrect. Action cancelled.");
                }
            }
        });
    }
    
    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Action Failed");
        alert.setContentText(message);
        
        alert.showAndWait();
    }

    
    @FXML
    private void showMovieForm() {
        showMovieForm(null);
    }
    
    private void showMovieForm(Film film) {
        Dialog<Film> dialog = new Dialog<>();
        dialog.setTitle(film == null ? "BingeBox | Add Movie" : "BingeBox | Edit " + film.getTitre());
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/groupna/projectNetflix/css/style.css").toExternalForm());
        
        dialogPane.getStyleClass().add("auth-card");

        ButtonType saveButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: -fx-primary-bg;");

        TextField titleField = new TextField(film != null ? film.getTitre() : "");
        titleField.setPromptText("Enter Movie Title");
        
        TextArea resumeArea = new TextArea(film != null ? film.getResume() : "");
        resumeArea.setPrefRowCount(3); resumeArea.setWrapText(true);

        DatePicker datePicker = new DatePicker(film != null ? film.getDateDeSortie() : LocalDate.now());
        TextField durationField = new TextField(film != null ? film.getDuree().toString() : "01:30");

        TextField posterPath = new TextField(film != null ? film.getPathPoster() : "");
        TextField moviePath = new TextField(film != null ? film.getPathMovie() : "");
        TextField trailerPath = new TextField(film != null ? film.getPathTrailer() : "");

        //categories (tag system)
        ObservableList<Categorie> selectedCats = FXCollections.observableArrayList();
        if (film != null) selectedCats.addAll(film.getCat());
        
        FlowPane catTags = new FlowPane(5, 5);
        catTags.setPrefWidth(300);
        updateTags(selectedCats, catTags);

        ComboBox<Categorie> catCombo = new ComboBox<>();
        catCombo.setPromptText("Select Category");
        catCombo.setEditable(true);
        
        ObservableList<String> allCategoryLabels = FXCollections.observableArrayList();
        for (Categorie c : Categorie.values()) {
            allCategoryLabels.add(c.getLabel());
        }

        ObservableList<Categorie> allCategories = FXCollections.observableArrayList(Categorie.values());
        
        catCombo.setItems(allCategories);
        catCombo.setCellFactory(lv -> new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getLabel());
            }
        });
        catCombo.setButtonCell(new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getLabel());
            }
        });
        
        Button addCatBtn = new Button("+");
        addCatBtn.getStyleClass().add("watchBtn");
        
        addCatBtn.setOnAction(e -> {
            String input = catCombo.getEditor().getText();
            
            if (input != null && !input.trim().isEmpty()) {
                String finalInput = input.trim();
                Categorie matchedCat = null;
                for (Categorie c : Categorie.values()) {
                    if (c.getLabel().equalsIgnoreCase(finalInput) || c.name().equalsIgnoreCase(finalInput)) {
                        matchedCat = c;
                        break;
                    }
                }
                if (matchedCat != null) {
                    if (!selectedCats.contains(matchedCat)) {
                        selectedCats.add(matchedCat);
                        updateTags(selectedCats, catTags);
                        catCombo.getEditor().clear();
                    }
                }
            }
        });

        //artistes
        
        ObservableList<Artiste> selectedActors = FXCollections.observableArrayList();
        ObservableList<Artiste> selectedDirectors = FXCollections.observableArrayList();
        if (film != null) {
            selectedActors.addAll(film.getActeurs());
            selectedDirectors.addAll(film.getDirecteurs());
        }
        
        FlowPane actorTags = new FlowPane(5, 5);
        updateTags(selectedActors, actorTags);
        TextField actorInput = new TextField();
        Button addActorBtn = new Button("+ Actor");
        addActorBtn.getStyleClass().add("watchBtn");

        FlowPane directorTags = new FlowPane(5, 5);
        updateTags(selectedDirectors, directorTags);
        TextField directorInput = new TextField();
        Button addDirectorBtn = new Button("+ Director");
        addDirectorBtn.getStyleClass().add("watchBtn");

        addActorBtn.setOnAction(e -> { 
        	String input = actorInput.getText();
            if(!actorInput.getText().isEmpty()) {
            	
                Artiste newArtist = new Artiste(0,input);
                
                if (selectedActors.stream().noneMatch(a -> a.getFullname().equals(input))) {
                selectedActors.add(newArtist);
                updateTags(selectedActors, actorTags);
                actorInput.clear();
                }
            }
        });
        
        addDirectorBtn.setOnAction(e -> { 
        	String input = directorInput.getText();
            if(!directorInput.getText().isEmpty()) {
            	
            	Artiste newArtist = new Artiste(0,input);
                
                if (selectedDirectors.stream().noneMatch(a -> a.getFullname().equals(input))) {
                selectedDirectors.add(newArtist);
                updateTags(selectedDirectors, actorTags);
                actorInput.clear();
                }
            }
        });
        int r = 0;

        grid.add(createLabel("Title:"), 0, r);        
        grid.add(titleField, 1, r++);

        grid.add(createLabel("Resume:"), 0, r);       
        grid.add(resumeArea, 1, r++);

        grid.add(createLabel("Release Date:"), 0, r);  
        grid.add(datePicker, 1, r++);

        grid.add(createLabel("Duration:"), 0, r);      
        grid.add(durationField, 1, r++);

        grid.add(createLabel("Categories:"), 0, r);   
        grid.add(new VBox(5, new HBox(5, catCombo, addCatBtn), catTags), 1, r++);

        grid.add(createLabel("Actors:"), 0, r);       
        grid.add(new VBox(5, new HBox(5, actorInput, addActorBtn), actorTags), 1, r++);

        grid.add(createLabel("Directors:"), 0, r);    
        grid.add(new VBox(5, new HBox(5, directorInput, addDirectorBtn), directorTags), 1, r++);

        grid.add(createLabel("Poster Path:"), 0, r);   
        grid.add(posterPath, 1, r++);

        grid.add(createLabel("Movie Path:"), 0, r);    
        grid.add(moviePath, 1, r++);

        grid.add(createLabel("Trailer Path:"), 0, r);  
        grid.add(trailerPath, 1, r++);
        
        dialogPane.setContent(new ScrollPane(grid));
        
        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                	return new Film(
                            film == null ? 0 : film.getId(),
                            resumeArea.getText(),
                            new ArrayList<>(selectedCats),
                            titleField.getText(),
                            datePicker.getValue(),
                            new ArrayList<>(selectedActors),
                            new ArrayList<>(selectedDirectors),
                            posterPath.getText(),
                            LocalTime.parse(durationField.getText()),
                            moviePath.getText(),
                            trailerPath.getText()
                        );
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid data format!").show();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newFilm -> {
        	handleAdminAction(film == null ? "Add" : "Update", () -> {
                if (film == null) {
                    filmService.addFilm(newFilm); 
                    masterMovies.add(newFilm);
                }
                /*else {
                    FilmDAO.update(newFilm);
                    int index = masterMovies.indexOf(film);
                    if (index != -1) masterMovies.set(index, newFilm);
                }*/
                movieTable.refresh();
            });
        });
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("label");
        return l;
    }

    private <T> void updateTags(ObservableList<T> items, FlowPane container) {
        container.getChildren().clear();
        for (T item : items) {
        	HBox tag = new HBox(5);
            tag.setAlignment(Pos.CENTER);
            tag.setStyle("-fx-background-color: -fx-accent; -fx-background-radius: 15; -fx-padding: 2 8;");
            
            String displayName;
            if (item instanceof Categorie) {
                displayName = ((Categorie) item).getLabel();
            } else if (item instanceof Artiste) {
                Artiste a = (Artiste) item;
                displayName = a.getFullname();
            } else {
                displayName = item.toString();
            }
            
            Label name = new Label(displayName);
            name.setStyle("-fx-text-fill: -fx-primary-bg; -fx-font-weight: bold;");
            
            Button remove = new Button("×");
            remove.setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-primary-bg; -fx-cursor: hand; -fx-padding: 0;");
            remove.setOnAction(e -> {
                items.remove(item);
                updateTags(items, container);
            });
            
            tag.getChildren().addAll(name, remove);
            container.getChildren().add(tag);
        }
    }
    
    @FXML
    private void showSerieForm() {
        showSerieForm(null);
    }

    private void showSerieForm(Serie serie) {
        Dialog<Serie> dialog = new Dialog<>();
        dialog.setTitle(serie == null ? "BingeBox | Add New Series" : "BingeBox | Edit " + serie.getTitre());
        DialogPane dialogPane = dialog.getDialogPane();
        var css = getClass().getResource("/groupna/projectNetflix/css/style.css");
        if (css != null) dialogPane.getStylesheets().add(css.toExternalForm());
        dialogPane.getStyleClass().add("auth-card");

        ButtonType saveButtonType = new ButtonType("Save Series", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Map<Saison, List<Episode>> tempSaisonMap = (serie != null) 
            ? new LinkedHashMap<>(serie.getSaisons()) : new LinkedHashMap<>();

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: -fx-primary-bg;");

        TextField titleField = new TextField(serie != null ? serie.getTitre() : "");
        TextArea resumeArea = new TextArea(serie != null ? serie.getResume() : "");
        DatePicker datePicker = new DatePicker(serie != null ? serie.getDateDeSortie() : LocalDate.now());
        TextField posterField = new TextField(serie != null ? serie.getPathPoster() : "");

        Button manageSeasonsBtn = new Button("Manage Seasons (" + tempSaisonMap.size() + ")");
        manageSeasonsBtn.getStyleClass().add("watchBtn");
        manageSeasonsBtn.setOnAction(e -> {
            showSeasonManager(tempSaisonMap);
            manageSeasonsBtn.setText("Manage Seasons (" + tempSaisonMap.size() + ")");
        });

        int r = 0;
        grid.add(createLabel("Title:"), 0, r); grid.add(titleField, 1, r++);
        grid.add(createLabel("Resume:"), 0, r); grid.add(resumeArea, 1, r++);
        grid.add(createLabel("Release Date:"), 0, r); grid.add(datePicker, 1, r++);
        grid.add(createLabel("Episodes:"), 0, r); grid.add(manageSeasonsBtn, 1, r++);
        grid.add(createLabel("Poster:"), 0, r); grid.add(posterField, 1, r++);

        dialogPane.setContent(new ScrollPane(grid));
        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                return new Serie(serie == null ? 0 : serie.getId(), resumeArea.getText(), new ArrayList<>(),
                    titleField.getText(), datePicker.getValue(), new ArrayList<>(), new ArrayList<>(),
                     posterField.getText(), tempSaisonMap);
            }
            return null;
        });
        dialog.showAndWait().ifPresent(newSerie -> {
            handleAdminAction(serie == null ? "Add" : "Update", () -> {
                if (serie == null) {
                    SerieDAO.save(newSerie);
                    masterSeries.add(newSerie);
                } 
                /*else {
                    SerieDAO.update(newSerie);
                    int index = masterSeries.indexOf(serie);
                    if (index != -1) masterSeries.set(index, newSerie);
                }*/
                serieTable.refresh();
            });
        });
    }

    private void showSeasonManager(Map<Saison, List<Episode>> saisonMap) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("BingeBox | Seasons Manager");
        
        DialogPane dp = dialog.getDialogPane();
        dp.getStylesheets().add(getClass().getResource("/groupna/projectNetflix/css/style.css").toExternalForm());
        
        dp.getStyleClass().add("auth-card"); 
        dp.setPrefSize(600, 500);
        
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        
        Accordion accordion = new Accordion();

        VBox.setVgrow(accordion, Priority.ALWAYS);
        
        updateSeasonAccordion(accordion, saisonMap);

        Button addSeasonBtn = new Button("+ Add New Season");
        addSeasonBtn.getStyleClass().add("watchBtn");
        addSeasonBtn.setPrefWidth(200);
        
        addSeasonBtn.setOnAction(e -> {
            Saison newSaison = new Saison(0, saisonMap.size() + 1, LocalDate.now(), "Season " + (saisonMap.size() + 1), "", "");
            saisonMap.put(newSaison, new ArrayList<>());
            updateSeasonAccordion(accordion, saisonMap);
        });

        layout.getChildren().addAll(createLabel("Manage Your Seasons"), accordion, addSeasonBtn);
        
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll");
        
        dp.setContent(scrollPane);
        dp.getButtonTypes().add(new ButtonType("Done", ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait();
    }

    private void updateSeasonAccordion(Accordion accordion, Map<Saison, List<Episode>> saisonMap) {
        accordion.getPanes().clear();
        saisonMap.forEach((saison, episodes) -> {
            VBox content = new VBox(10);
            for (Episode ep : episodes) {
                Label epLabel = new Label("E" + ep.getNumero() + ": " + ep.getTitre());
                epLabel.getStyleClass().add("card-text");
                content.getChildren().add(epLabel);
            }
            Button addEpBtn = new Button("+ Add Episode");
            addEpBtn.setOnAction(e -> showEpisodeDialog(episodes, accordion, saisonMap));
            content.getChildren().add(addEpBtn);
            accordion.getPanes().add(new TitledPane("S" + saison.getNum() + ": " + saison.getTitre(), content));
        });
    }

    private void showEpisodeDialog(List<Episode> episodes, Accordion accordion, Map<Saison, List<Episode>> saisonMap) {
        Dialog<Episode> epDialog = new Dialog<>();
        epDialog.setTitle("BingeBox | New Episode");
        
        DialogPane dp = epDialog.getDialogPane();
        
        dp.getStylesheets().add(getClass().getResource("/groupna/projectNetflix/css/style.css").toExternalForm());
        dp.getStyleClass().add("auth-card");
        
        ButtonType saveBtn = new ButtonType("Add Episode", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(15); 
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        
        TextField title = new TextField();
        title.getStyleClass().add("auth-field");
        
        TextArea resume = new TextArea();
        resume.setPrefRowCount(3);
        resume.setWrapText(true);
        
        TextField duration = new TextField("00:45:00");
        duration.getStyleClass().add("auth-field");
        
        TextField path = new TextField();
        path.getStyleClass().add("auth-field");
        
        TextField thumb = new TextField();
        thumb.getStyleClass().add("auth-field");

        grid.add(new Label("Title:"), 0, 0); grid.add(title, 1, 0);
        grid.add(new Label("Resume:"), 0, 1); grid.add(resume, 1, 1);
        grid.add(new Label("Duration:"), 0, 2); grid.add(duration, 1, 2);
        grid.add(new Label("Path:"), 0, 3); grid.add(path, 1, 3);
        grid.add(new Label("Thumb:"), 0, 4); grid.add(thumb, 1, 4);

        dp.setContent(grid);
        
        epDialog.setResultConverter(b -> {
            if (b == saveBtn) {
                try {
                    return new Episode(
                        episodes.size() + 1, 
                        0, 
                        resume.getText(), 
                        title.getText(), 
                        LocalTime.parse(duration.getText()), 
                        path.getText(), 
                        thumb.getText()
                    );
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Time Format (HH:mm:ss)").show();
                    return null;
                }
            }
            return null;
        });
        
        epDialog.showAndWait().ifPresent(newEp -> {
            episodes.add(newEp);
            updateSeasonAccordion(accordion, saisonMap);
        });
    }
}