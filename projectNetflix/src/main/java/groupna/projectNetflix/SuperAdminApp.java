package groupna.projectNetflix;

import groupna.projectNetflix.controllers.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

public class SuperAdminApp extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(
	            getClass().getResource("/groupna/projectNetflix/view/MainView.fxml")
	        );
		stage.initStyle(StageStyle.UNDECORATED);
	    Scene scene = new Scene(loader.load());
	    stage.setScene(scene);
	    stage.setMaximized(true);
	    stage.show();
	    
	    MainViewController.getInstance().loadSuperAdminCreator();
	        
	}
	
	public static void main(String[] args) {
        launch(args);
    }

}
