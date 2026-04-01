package groupna.projectNetflix.controllers;

import groupna.projectNetflix.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileViewController {

    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;

    @FXML
    public void initialize() {
        var user = Session.getInstance().getUser();
        if (user != null) {
            userNameLabel.setText((user.getPrenom() + " " + user.getNom()).toUpperCase());
            userEmailLabel.setText(user.getEmail());
        } else {
            userNameLabel.setText("GUEST");
        }
    }

    @FXML
    private void handleSwitchAccount() {
        Session.getInstance().setUser(null);
        
        MainViewController main = MainViewController.getInstance();
        main.hideNavigation();

        main.loadPage("AuthView.fxml");
    }
}