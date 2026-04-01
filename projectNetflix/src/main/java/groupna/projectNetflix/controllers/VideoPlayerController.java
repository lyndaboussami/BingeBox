package groupna.projectNetflix.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;

import groupna.projectNetflix.DAO.HistoryItem;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.entities.Visualisable;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;

public class VideoPlayerController {
    @FXML private MediaView mediaView;
    @FXML private Button playPauseBtn;
    @FXML private Slider progressBar;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;

    private MediaPlayer mediaPlayer;
    private int currentMovieId=-1;

    @FXML private StackPane playerRoot;
    private Runnable onCloseRequest;
    
    
    private List<Episode> playlist;
    private int currentIndex;
    private Timeline bingeTimer;
    @FXML private VBox nextEpisodeOverlay;
    @FXML private Label countdownLabel;
    private UserService userService=new UserService();
    // Temporary memory (Static Map) - This replaces DB logic for now
    //private static final Map<Integer, Double> localWatchHistory = new HashMap<>();

    
    
    private void initializePlayer(String url) {
    	if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            Platform.runLater(() -> {
                if (playerRoot != null) {
                    mediaView.fitWidthProperty().bind(playerRoot.widthProperty());
                    mediaView.fitHeightProperty().bind(playerRoot.heightProperty());
                }
            });
            mediaView.setPreserveRatio(true);

            //volume
            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));

            //slider/time labels
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                updateUI(newTime);
            });

            //slider interactions (drag/click)
            progressBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (progressBar.isValueChanging()) {
                    double total = mediaPlayer.getTotalDuration().toMillis();
                    mediaPlayer.seek(Duration.millis((newVal.doubleValue() / 100) * total));
                }
            });

            progressBar.setOnMouseClicked(event -> {
                double total = mediaPlayer.getTotalDuration().toMillis();
                double targetPercent = event.getX() / progressBar.getWidth();
                mediaPlayer.seek(Duration.millis(targetPercent * total));
            });

        } catch (Exception e) {
            System.err.println("Error initializing Media Player: " + e.getMessage());
        }		
	}
    
    public void loadVideo(Visualisable movie,String c,int b) {
    	if (movie instanceof Film) {
    	    Film currentFilm = (Film) movie;
    	    currentMovieId=currentFilm.getId();
    	    String url = getClass().getResource(currentFilm.getPathMovie()).toExternalForm();
    	    initializePlayer(url);
    	    
    	    User user = Session.getInstance().getUser();
    	    Optional<HistoryItem> alreadyWatched = userService.recupererHistoriqueComplet(user.getId())
    	        .stream()
    	        .filter(a -> a.getContent().equals(currentFilm))
    	        .findFirst();
    	    if (alreadyWatched.isPresent() && alreadyWatched.get().getTime() > 5.0) {
    	        showResumeDialog(alreadyWatched.get().getTime());
    	    } else {
    	        mediaPlayer.play();
    	    }
    	}else {
    		initializePlayer(c);
    		mediaPlayer.play();
    	}
        mediaPlayer.setOnEndOfMedia(() -> {
             playPauseBtn.setText("↺");
          });
        }
    

    private void playCurrentEpisode() {
        if (currentIndex < 0 || currentIndex >= playlist.size()) return;

        Episode ep = playlist.get(currentIndex);
        String url = getClass().getResource(ep.getPathEp()).toExternalForm();

        initializePlayer(url);
        nextEpisodeOverlay.setVisible(false);
        User user = Session.getInstance().getUser();
        Optional<HistoryItem> alreadyWatched = userService.recupererHistoriqueComplet(user.getId())
            .stream()
            .filter(a -> {
                Object content = a.getContent();
                if (content instanceof Episode ) {
                    return ((Episode) content).getId() == ep.getId();
                }
                return false; 
            })
            .findFirst();
        if (alreadyWatched.isPresent() && alreadyWatched.get().getTime() > 5.0) {
            showResumeDialog(alreadyWatched.get().getTime());
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("⏸");
        }
        mediaPlayer.setOnEndOfMedia(() -> {
            if (currentIndex < playlist.size() - 1) {
                startBingeCountdown();
            }
        });
    }
    
    public void setOnCloseRequest(Runnable callback) {
        this.onCloseRequest = callback;
    }

    @FXML
    private void handleClosePlayer() {
        if (onCloseRequest != null) {
            onCloseRequest.run(); // This executes the code in MovieDetailController
        }
    }

    private void updateUI(Duration currentTime) {
    	if (mediaPlayer == null || mediaPlayer.getTotalDuration() == null || timeLabel == null) {
            return; 
        }
        // Update Slider position
        if (!progressBar.isValueChanging()) {
            double progress = (currentTime.toMillis() / mediaPlayer.getTotalDuration().toMillis()) * 100;
            progressBar.setValue(progress);
        }

        // Update Time Label (00:00 / 00:00)
        timeLabel.setText(formatTime(currentTime) + " / " + formatTime(mediaPlayer.getTotalDuration()));
    }

    private String formatTime(Duration elapsed) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int minutes = intElapsed / 60;
        int seconds = intElapsed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showResumeDialog(double savedSeconds) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Resume Playback");
        alert.setHeaderText("Pick up where you left off?");
        alert.setContentText("We saved your progress at " + formatTime(Duration.seconds(savedSeconds)));

        ButtonType btnResume = new ButtonType("Resume");
        ButtonType btnRestart = new ButtonType("Restart");
        alert.getButtonTypes().setAll(btnResume, btnRestart);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnResume) {
                mediaPlayer.setStartTime(Duration.seconds(savedSeconds));
            }
            mediaPlayer.play();
        });
    }

    @FXML
    private void togglePlay() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseBtn.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("⏸");
        }
    }

    public void stopVideo() {
    	User user=Session.getInstance().getUser();
        if (mediaPlayer != null && currentMovieId!=-1) {
            userService.marquerFilmCommeVu(user.getId(), currentMovieId, mediaPlayer.getCurrentTime().toSeconds());
            
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    @FXML private void rewind() { mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10))); }
    @FXML private void forward() { mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10))); }
    
    @FXML private void toggleFullScreen() {
        Stage stage = (Stage) mediaView.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
        
    }   
    
    public void loadSeries(List<Episode> episodes, int index) {
        this.playlist = episodes;
        this.currentIndex = index;
        playCurrentEpisode();
    }

    private void startBingeCountdown() {
        nextEpisodeOverlay.setVisible(true);
        final int[] secondsLeft = {10};
        
        bingeTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft[0]--;
            countdownLabel.setText(String.valueOf(secondsLeft[0]));
            if (secondsLeft[0] <= 0) {
                playNextImmediately();
            }
        }));
        bingeTimer.setCycleCount(10);
        bingeTimer.play();
    }

    @FXML
    private void playNextImmediately() {
        if (bingeTimer != null) bingeTimer.stop();
        currentIndex++;
        playCurrentEpisode();
    }

    @FXML
    private void cancelAutoPlay() {
        if (bingeTimer != null) bingeTimer.stop();
        nextEpisodeOverlay.setVisible(false);
    }
}
