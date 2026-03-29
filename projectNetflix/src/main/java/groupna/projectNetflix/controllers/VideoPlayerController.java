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

import groupna.projectNetflix.entities.Episode;

public class VideoPlayerController {
    @FXML private MediaView mediaView;
    @FXML private Button playPauseBtn;
    @FXML private Slider progressBar;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;

    private MediaPlayer mediaPlayer;
    private int currentMovieId;

    @FXML private StackPane playerRoot;
    private Runnable onCloseRequest;
    
    
    private List<Episode> playlist;
    private int currentIndex;
    private Timeline bingeTimer;
    @FXML private VBox nextEpisodeOverlay;
    @FXML private Label countdownLabel;
    
    // Temporary memory (Static Map) - This replaces DB logic for now
    private static final Map<Integer, Double> localWatchHistory = new HashMap<>();

    
    
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
    
    public void loadVideo(String url, int movieId) {
        this.currentMovieId = movieId;
        initializePlayer(url);

        //resume
        if (localWatchHistory.containsKey(movieId) && localWatchHistory.get(movieId) > 5.0) {
            showResumeDialog(localWatchHistory.get(movieId));
        } else {
            mediaPlayer.play();
        }

        mediaPlayer.setOnEndOfMedia(() -> {
            localWatchHistory.remove(currentMovieId);
            playPauseBtn.setText("↺");
        });
    }
    

	private void playCurrentEpisode() {
        if (currentIndex < 0 || currentIndex >= playlist.size()) return;

        Episode ep = playlist.get(currentIndex);
        String url = getClass().getResource(ep.getPathEp()).toExternalForm();

        initializePlayer(url);
        nextEpisodeOverlay.setVisible(false);

        //binge wtch
        mediaPlayer.setOnEndOfMedia(() -> {
            if (currentIndex < playlist.size() - 1) {
                startBingeCountdown();
            }
        });

        mediaPlayer.play();
        playPauseBtn.setText("⏸");
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
        if (mediaPlayer != null) {
        	
            // SAVE TO MEMORY (Replace DB logic )
            localWatchHistory.put(currentMovieId, mediaPlayer.getCurrentTime().toSeconds());
            
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
