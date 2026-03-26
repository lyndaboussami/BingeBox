package groupna.projectNetflix.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AuthController extends BaseController{
	@FXML private Label authTitle, switchText;
    @FXML private Hyperlink switchLink;
    @FXML private Button submitBtn;
    
    @FXML private HBox nameBox;
    @FXML private TextField firstNameField, lastNameField, emailField;
    @FXML private PasswordField passwordField, confirmPasswordField;

    @FXML private Hyperlink forgotPasswordLink;
    @FXML private VBox forgotPasswordPane;
    @FXML private TextField resetEmailField;
    
    private boolean isLoginMode = true;

    @FXML
    private void toggleAuthMode() {
        isLoginMode = !isLoginMode;

        if (isLoginMode) {
            authTitle.setText("Sign In");
            submitBtn.setText("Sign In");
            switchText.setText("New to BingeBox?");
            switchLink.setText("Sign up now.");
            
            nameBox.setVisible(false);
            nameBox.setManaged(false);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            
            forgotPasswordLink.setVisible(true);
            forgotPasswordLink.setManaged(true);
        } else {
            authTitle.setText("Sign Up");
            submitBtn.setText("Register");
            switchText.setText("Already have an account?");
            switchLink.setText("Log in here.");
            
            nameBox.setVisible(true);
            nameBox.setManaged(true);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            
            forgotPasswordLink.setVisible(false);
            forgotPasswordLink.setManaged(false);
        }
    }

    @FXML
    private void handleSubmit() {
    	
    	String email = emailField.getText();
        String pass = passwordField.getText();
        
        if (!email.isEmpty() && !pass.isEmpty()) {
            System.out.println("Logging in: " + emailField.getText());

            MainViewController.getInstance().unlockFullApp();
        } else {
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                System.out.println("Passwords do not match!");
                return;
            }
            System.out.println("Registering: " + firstNameField.getText());
        }
    }
    @FXML
    private void openResetPane() {
        forgotPasswordPane.setVisible(true);
        forgotPasswordPane.setManaged(true);
    }

    @FXML
    private void closeResetPane() {
        forgotPasswordPane.setVisible(false);
        forgotPasswordPane.setManaged(false);
    }

    @FXML
    private void handleSendResetCode() {
        String email = resetEmailField.getText();
        if (email.isEmpty()) {
            System.out.println("Please enter an email!");
            return;
        }
        System.out.println("Simulation: Sending reset link to " + email);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reset Sent");
        alert.setHeaderText(null);
        alert.setContentText("A reset link has been sent to your email.");
        alert.showAndWait();
        
        closeResetPane();
    }
}
