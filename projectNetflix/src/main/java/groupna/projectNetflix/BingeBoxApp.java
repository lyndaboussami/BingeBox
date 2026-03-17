package groupna.projectNetflix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class BingeBoxApp extends Application {

	@Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource(
                "/groupna/projectNetflix/view/MainView.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("BingeBox Streaming");
        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}