package groupna.projectNetflix.utils;

import groupna.projectNetflix.entities.User;
import java.util.HashSet;
import java.util.Locale; // Add this
import java.util.ResourceBundle;

public class Session {
    private static Session instance;
    private User currentUser;
    
    private Locale currentLocale = Locale.ENGLISH;	
    public Session(User user) {
        this.currentUser = user; //new User(0, "User123", "password", null, null, null, null, null); 
    }
    public static Session getInstance(User user) {
        if (instance == null) {
            instance = new Session(user);
        }
        return instance;
    } 
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session(null); 
        }
        return instance;
    }

    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("groupna.projectNetflix.languages.bundle", currentLocale);
    }
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