package groupna.projectNetflix.controllers;

import groupna.projectNetflix.entities.Role;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Session;
import groupna.projectNetflix.utils.Test;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AuthController extends BaseController{
	private Test test=new Test();
	private UserService userService=new UserService();
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
    private boolean isSuperAdminCreation = false;
    
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

    public void setSuperAdminCreation(boolean value) {
        this.isSuperAdminCreation = value;
        if (value) {
            isLoginMode = true;
            toggleAuthMode();
            authTitle.setText("Create Admin");
            switchText.setVisible(false);
            switchLink.setVisible(false);
        }
    }
    
    @FXML
    private void handleSubmit() {
    	if(isLoginMode) {
    		//emailField.clear();
    		//passwordField.clear();
    		String email = emailField.getText();
            String pass = passwordField.getText();
            User user=userService.seConnecter(email, pass);
            if (user!=null) {
                Session.getInstance().setUser(user);;
                MainViewController.getInstance().unlockFullApp();
            } else {
            	handleAlert("Connexion failed", "please check your email or password.");
            }
    	}
    	else {
    		//emailField.clear();
    		//passwordField.clear();
    		String email = emailField.getText();
            String pass = passwordField.getText();
    		String nom=lastNameField.getText();
    		String prenom=firstNameField.getText();
    		String ConfirmPass=confirmPasswordField.getText();
    		if (!test.testName(nom) || !test.testName(prenom)) {
    		    handleAlert("short username", "your firstname or lastname is too short.");
    		    return;
    		}

    		if (!test.testEmail(email)) {
    		    handleAlert("email incorrect", "please enter a valid email");
    		    return;
    		}
    		if(userService.rechercherParEmail(email) !=0) {
    			handleAlert("User already exists", "User with this email already exists");
    		    return;
    		}

    		if (!test.testPassword(pass)) {
    		    handleAlert("password not valide", "your password has to be at least 8 characters with letters, numbers and symboles");
    		    return;
    		}

    		if (!pass.equals(ConfirmPass)) {
    		    handleAlert("password not confirmed", "you have to confirme your password");
    		    return;
    		}
    		Role assignedRole = isSuperAdminCreation ? Role.ADMIN : Role.USER;
    		int inscrireUtilisateur = userService.inscrireUtilisateur(new User(0, nom, prenom, email, ConfirmPass, assignedRole));
    		//Session.getInstance().setUser(userService.recupererUtilisateurParId(inscrireUtilisateur));
    		if (isSuperAdminCreation) {
    		    handleAlert("Success", "Admin account created! You can now close this and run the BingeBox app.");
    		    // ((Stage) submitBtn.getScene().getWindow()).close();
    		} else {
    		    MainViewController.getInstance().unlockFullApp();
    		    Session.getInstance().setUser(userService.recupererUtilisateurParId(inscrireUtilisateur));
    		}
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
    private void handleAlert(String Title,String Header) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Title);
        alert.setHeaderText(null);
        alert.setContentText(Header);
        alert.showAndWait();
        
        closeResetPane();
    }
}

