package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;
import groupna.projectNetflix.utils.Test;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsViewController {
	private UserService userService=new UserService();
	private User currentUser = Session.getInstance().getUser();
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    private Test test=new Test();
    
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
         if (currentUser != null) {
            firstNameField.setText(currentUser.getPrenom());
            lastNameField.setText(currentUser.getNom());
            emailField.setText(currentUser.getEmail());
         }
    }

    @FXML
    private void handleUpdateProfile() {
        String fName = firstNameField.getText().trim();
        String lName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        if (!test.testName(lName)||!test.testName(fName)) {
            showStatus("Your firstname and lastname must contain at least 2 characters", true);
            return;
        }
        if(!test.testEmail(email)) {
        	showStatus("Please enter a valid email", true);
        	return;
        }
        if(userService.rechercherParEmail(email)!=0) {
        	showStatus("User with this email already exists", true);
        	return;
        }
        currentUser.setNom(lName);
        currentUser.setPrenom(fName);
        currentUser.setEmail(email);
        userService.updateUser(currentUser);
        int id= currentUser.getId();
        showStatus("Profile updated successfully!", false);
        Session.getInstance().setUser(userService.recupererUtilisateurParId(id));
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

        if (!test.testPassword(newPass)) {
            showStatus("New password must be at least 8 characters with letters,numbers and symboles", true);
            return;
        }

        boolean isOldPassCorrect = userService.seConnecter(currentUser.getEmail(), oldPass)!=null;

        if (isOldPassCorrect) {
            currentUser.setMdp(newPass);
            userService.updateUserPassword(currentUser);
            int id=currentUser.getId();
            Session.getInstance().setUser(userService.recupererUtilisateurParId(id));
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

    @FXML
    private void handleDeleteAccount() {
        Alert confirmation = new Alert(Alert.AlertType.WARNING);
        confirmation.setTitle("Account Deletion");
        confirmation.setHeaderText("Final Warning");
        confirmation.setContentText("Are you sure you want to delete your account? This cannot be undone.");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Confirm Identity");
            passwordDialog.setHeaderText("Security Check");
            passwordDialog.setContentText("Please enter your password to confirm deletion:");

            passwordDialog.showAndWait().ifPresent(password -> {
                boolean isPasswordCorrect = userService.seConnecter(currentUser.getEmail(), password) != null;

                if (isPasswordCorrect) {
                    boolean success = userService.SupprimerCompteUser(currentUser.getId());
                    
                    if (success) {
                        Session.getInstance().setUser(null);
                        MainViewController mainCtrl = MainViewController.getInstance();
                        
                        if (mainCtrl != null) {
                            mainCtrl.loadPage("AuthView.fxml");
                            
                            mainCtrl.hideNavigation();
                            
                            showStatus("Account deleted. Redirecting...", false);
                        } else {
                            showStatus("Account deleted. Please restart the app.", false);
                        }
                        
                    } else {
                        showStatus("Error: Database could not delete user.", true);
                    }
                } else {
                    showStatus("Incorrect password. Deletion cancelled.", true);
                }
            });
        }
    }
}