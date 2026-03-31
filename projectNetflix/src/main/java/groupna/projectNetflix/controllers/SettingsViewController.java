package groupna.projectNetflix.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsViewController extends BaseController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // User currentUser = SessionManager.getInstance().getCurrentUser();
        // if (currentUser != null) {
        //    firstNameField.setText(currentUser.getFirstName());
        //    lastNameField.setText(currentUser.getLastName());
        //    emailField.setText(currentUser.getEmail());
        // }
    }

    @FXML
    private void handleUpdateProfile() {
        String fName = firstNameField.getText().trim();
        String lName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty()) {
            showStatus("All personal information fields are required.", true);
            return;
        }

        // userService.updateProfile(userId, fName, lName, email);
        
        showStatus("Profile updated successfully!", false);
    }

    @FXML
    private void handleUpdatePassword() {
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showStatus("Please fill in all security fields.", true);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showStatus("New passwords do not match.", true);
            return;
        }

        if (newPass.length() < 6) {
            showStatus("New password must be at least 6 characters.", true);
            return;
        }

        // boolean isOldPassCorrect = userService.verifyPassword(userId, oldPass);
        boolean isOldPassCorrect = true;

        if (isOldPassCorrect) {
            // userService.updatePassword(userId, newPass);
            showStatus("Password updated securely.", false);
            clearPasswordFields();
        } else {
            showStatus("Current password is incorrect.", true);
        }
    }
    
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;"); // Soft Red
        } else {
            statusLabel.setStyle("-fx-text-fill: #F5F5DC; -fx-font-weight: bold;"); // Beige
        }
    }

    private void clearPasswordFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

}