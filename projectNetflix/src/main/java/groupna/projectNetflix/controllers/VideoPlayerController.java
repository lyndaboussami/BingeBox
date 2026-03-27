package groupna.projectNetflix.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

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
    
    // Temporary memory (Static Map) - This replaces DB logic for now
    private static final Map<Integer, Double> localWatchHistory = new HashMap<>();

    public void loadVideo(String url, int movieId) {
        this.currentMovieId = movieId;

        try {
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            //resume logic
            if (localWatchHistory.containsKey(movieId) && localWatchHistory.get(movieId) > 5.0) {
                showResumeDialog(localWatchHistory.get(movieId));
            } else {
                mediaPlayer.play();
            }

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, currentTime) -> {
                updateUI(currentTime);
            });

            mediaPlayer.setOnReady(() -> {
                progressBar.setMax(100);
            });

            //skip
            progressBar.setOnMouseReleased(e -> {
                if (mediaPlayer.getTotalDuration() != null) {
                    double newTime = (progressBar.getValue() / 100) * mediaPlayer.getTotalDuration().toSeconds();
                    mediaPlayer.seek(Duration.seconds(newTime));
                }
            });

            //volume
            volumeSlider.setValue(70);
            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));

            //movie finished (replay option)
            mediaPlayer.setOnEndOfMedia(() -> {
                localWatchHistory.remove(currentMovieId); // Movie finished! Clear memory.
                playPauseBtn.setText("↺"); // Show replay icon
            });
        
            mediaView.fitWidthProperty().bind(((StackPane)mediaView.getParent()).widthProperty());
            mediaView.fitHeightProperty().bind(((StackPane)mediaView.getParent()).heightProperty());

            //keeping the movie's aspect ratio (no stretching)
            mediaView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Error initializing Media Player: " + e.getMessage());
        }
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
            // SAVE TO MEMORY (Replace this with DB logic later)
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
}