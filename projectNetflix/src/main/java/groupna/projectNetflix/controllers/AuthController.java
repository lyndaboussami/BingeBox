package groupna.projectNetflix.controllers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.Role;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.entities.Visualisable;
import groupna.projectNetflix.services.UserService;
import groupna.projectNetflix.utils.Test;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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
    	if(isLoginMode) {
            
            if (userService.seConnecter(email, pass)!=null) {
                System.out.println("Logging in: " + emailField.getText());

                MainViewController.getInstance().unlockFullApp();
            } else {
            	handleAlert("Connexion failed", "please check your email or password.");
            }
    	}
    	else {
    		String nom=lastNameField.getText();
    		String prenom=firstNameField.getText();
    		String ConfirmPass=confirmPasswordField.getText();
    		if(!test.testName(nom)||!test.testName(prenom)) {
    			handleAlert("short username", "your firstname or lastname is too short.");
    		}
    		else {
    			if(!test.testEmail(email)) {
    				handleAlert("email incorrect ", "please enter a valid email");
    			}
    			else {
    				if(!test.testPassword(pass)) {
    					handleAlert("password not valide", "your password has to be at least 8 characters with letters,numbers and symboles");
    				}
    				else {
    					if(!pass.equals(ConfirmPass)) {
    						handleAlert("possword not confirmed", "you have to confirme your password");
    					}
    					else {
    						int inscrireUtilisateur = userService.inscrireUtilisateur(new User(0, nom, prenom, email, ConfirmPass, Role.USER,new HashSet<Oeuvre>(), new TreeMap<LocalDate, List<Visualisable>>()));
    						MainViewController.getInstance().unlockFullApp();
    					}
    				}
    			}
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
