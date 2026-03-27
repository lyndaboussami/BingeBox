package groupna.projectNetflix.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public abstract class BaseController {
	@FXML
    private void showDetails(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        Label title = (Label) card.getChildren().get(1);
        
        Tooltip info = new Tooltip(
            "Title: " + title.getText() + "\n" +
            "Genre: Drama/Action\n" +
            "Rating: ★★★★☆\n" +
            "Duration: 2h 15m"
        );
        info.setShowDelay(Duration.millis(100));
        Tooltip.install(card, info);
    }

    @FXML
    private void hideDetails(MouseEvent event) {
    }
}
