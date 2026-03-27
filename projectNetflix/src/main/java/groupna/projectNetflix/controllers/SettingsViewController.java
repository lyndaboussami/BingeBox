package groupna.projectNetflix.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsViewController extends BaseController{
	@FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
    	
        System.out.println("Settings View loaded.");
    }

    @FXML
    private void handleSaveChanges() {
        String newUsername = usernameField.getText();
        //..
        //enregistrer les modifications faites dans settings
        
        System.out.println("Saving profile for: " + newUsername);
    }
}
