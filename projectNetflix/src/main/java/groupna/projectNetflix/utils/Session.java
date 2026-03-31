package groupna.projectNetflix.utils;

import groupna.projectNetflix.entities.User;
import java.util.HashSet;
import java.util.Locale; // Add this
import java.util.ResourceBundle;

public class Session {
    private static Session instance;
    private User currentUser;
    
    private Locale currentLocale = Locale.ENGLISH;

    private Session() {
        this.currentUser = new User(0, "ben foulen", "foulen", "foulenbenfoulen@gmail.com", null, null, null, null); 
        if (this.currentUser.getFavs() == null) {
            this.currentUser.setFavs(new HashSet<>());
        }
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("groupna.projectNetflix.languages.bundle", currentLocale);
    }

    // Method to change the language globally
    public void setLocale(String langCode) {
        this.currentLocale = new Locale(langCode);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public User getUser() {
        return currentUser;
    }

    public void setUser(User user) {
        this.currentUser = user;
    }
}