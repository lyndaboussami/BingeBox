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

import java.net.URL;
import java.util.*;

import groupna.projectNetflix.DAO.HistoryItem;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;

public class VideoPlayerController {
    @FXML private MediaView mediaView;
    @FXML private Button playPauseBtn;
    @FXML private Slider progressBar;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;
    @FXML private HBox controlsBar;
    
    @FXML private VBox nextEpisodeOverlay;
    @FXML private Label countdownLabel;

    private MediaPlayer mediaPlayer;
    private int currentMovieId=-1;

    @FXML private StackPane playerRoot;
    private Runnable onCloseRequest;
    
    
    private List<Episode> playlist;
    private int currentIndex;
    private Timeline bingeTimer;
    private UserService userService=new UserService();
    private double time;
    private int currentIdEpisode;

    private double startOffset = 0;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (playerRoot.getScene() != null) {
                Stage stage = (Stage) playerRoot.getScene().getWindow();
                stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                    controlsBar.setVisible(!newVal);
                });
            }
        });
    }
    
    private void initializePlayer(String url) {
    	if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaView.setPreserveRatio(true);

            mediaPlayer.setOnReady(() -> {
                if (startOffset > 0) {
                    mediaPlayer.seek(Duration.seconds(startOffset));
                    startOffset = 0;
                }
                mediaPlayer.play();
                playPauseBtn.setText("⏸");
            });
            

            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));
            
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!progressBar.isValueChanging()) {
                    updateUI(newTime);
                }
            });
            
            progressBar.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
                if (!isChanging) {
                    seekFromSlider();
                }
            });
            
            progressBar.setOnMouseClicked(e -> seekFromSlider());

            mediaView.fitWidthProperty().bind(playerRoot.widthProperty());
            mediaView.fitHeightProperty().bind(playerRoot.heightProperty());

        } catch (Exception e) {
            System.err.println("Error initializing Media Player: " + e.getMessage());
        }		
	}
    
    private void seekFromSlider() {
        if (mediaPlayer == null) return;

        Duration total = mediaPlayer.getTotalDuration();

        if (total == null || total.isUnknown()) return;

        double percent = progressBar.getValue() / 100.0;
        mediaPlayer.seek(total.multiply(percent));
    }
    
    public void loadVideo(int idFilm ,double time,String url) {
    	currentMovieId=idFilm;
    	this.currentIdEpisode = -1;
       	 
    	if (time > 5.0) {
    		showResumeDialog(time,url);
    	} 
    	else {
    		initializePlayer(url);
    	}
        mediaPlayer.setOnEndOfMedia(() -> {
             playPauseBtn.setText("↺");
          });
    }    

    private void playCurrentEpisode() {
        if (currentIndex < 0 || currentIndex >= playlist.size()) return;

        Episode ep = playlist.get(currentIndex);
        this.currentIdEpisode = ep.getId();
        
        String path = ep.getPathEp(); 
        URL resource = getClass().getResource(path);
        
        if (resource == null) {
            System.err.println("Could not find video file at: " + path);
            return;
        }
        
        User user = Session.getInstance().getUser();
        Optional<HistoryItem> history = userService.recupererHistoriqueComplet(user.getId())
                .stream()
                .filter(h -> h.getContent() instanceof Episode e && e.getId() == ep.getId())
                .findFirst();

        if (history.isPresent() && history.get().getTime() > 10.0) {
            showResumeDialog(history.get().getTime(), resource.toExternalForm());
        } else {
            initializePlayer(resource.toExternalForm());
        }

        mediaPlayer.setOnEndOfMedia(() -> {
            userService.marquerEpisodeCommeVu(user.getId(), ep.getId(), mediaPlayer.getCurrentTime().toSeconds());
            if (currentIndex < playlist.size() - 1) startBingeCountdown();
        });
        
        /*
        String url = resource.toExternalForm();
        this.currentIdEpisode=ep.getId();
        
        initializePlayer(url);
        nextEpisodeOverlay.setVisible(false);
        
        User user = Session.getInstance().getUser();
        
        Optional<HistoryItem> alreadyWatched = userService.recupererHistoriqueComplet(user.getId())
                .stream()
                .filter(a -> a.getContent() instanceof Episode e && e.getId() == ep.getId())
                .findFirst();
        
        if (alreadyWatched.isPresent() && alreadyWatched.get().getTime() > 10.0) {
            showResumeDialog(alreadyWatched.get().getTime());
        } 
        else {
            mediaPlayer.play();
            playPauseBtn.setText("⏸");
        }
        
        mediaPlayer.setOnEndOfMedia(() -> {
        	userService.marquerEpisodeCommeVu(user.getId(), ep.getId(),mediaPlayer.getCurrentTime().toSeconds());
            if (currentIndex < playlist.size() - 1) {
                startBingeCountdown();
   
            }
        });*/
    }
    
    public void setOnCloseRequest(Runnable callback) {
        this.onCloseRequest = callback;
    }

    @FXML
    private void handleClosePlayer() {
        if (onCloseRequest != null) {
            onCloseRequest.run();
        }
    }

    private void updateUI(Duration currentTime) {
    	if (mediaPlayer == null) return;
        Duration total = mediaPlayer.getTotalDuration();

        if (total == null || total.isUnknown()) return;

        progressBar.setValue(currentTime.toMillis() / total.toMillis() * 100);
        timeLabel.setText(formatTime(currentTime) + " / " + formatTime(total));
        timeLabel.setStyle("-fx-text-fill: #F5F5DC;");
    }
    

    private String formatTime(Duration elapsed) {
    	int intElapsed = (int) Math.floor(elapsed.toSeconds());
        return String.format("%02d:%02d", intElapsed / 60, intElapsed % 60);
    }

    private void showResumeDialog(double savedSeconds, String url) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Resume Playback");
        alert.setHeaderText("Pick up where you left off?");
        alert.setContentText("Progress: " + formatTime(Duration.seconds(savedSeconds)));

        ButtonType btnResume = new ButtonType("Resume");
        ButtonType btnRestart = new ButtonType("Restart");
        alert.getButtonTypes().setAll(btnResume, btnRestart);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnResume) {
            	this.startOffset = savedSeconds;
            }
            initializePlayer(url);
        });
    }

    @FXML
    private void togglePlay() {
    	
    	if (mediaPlayer == null) {
            return;
        }
    	
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
            double currentTime = mediaPlayer.getCurrentTime().toSeconds();
            User user = Session.getInstance().getUser();

            if (currentMovieId != -1) {
                userService.marquerFilmCommeVu(user.getId(), currentMovieId, currentTime);
            } else if (currentIdEpisode != -1) {
                userService.marquerEpisodeCommeVu(user.getId(), currentIdEpisode, currentTime);
            }

            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    @FXML
    private void rewind() {
        if (mediaPlayer == null) return;
        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
    }

    @FXML
    private void forward() {
        if (mediaPlayer == null) return;
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }
    
    @FXML private void toggleFullScreen() {
        Stage stage = (Stage) mediaView.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
        
    }   
    
    public void loadSeries(List<Episode> episodes, int index) {
        this.playlist = episodes;
        this.currentIndex = index;
        this.currentMovieId = -1;
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
    public int getCurrentIdEpisode() {
    	return currentIdEpisode;
    }
    public double getTime() {
		return time;
	}
    
    public void resumeAt(double seconds) {
        this.startOffset = seconds;
    }
    
}
