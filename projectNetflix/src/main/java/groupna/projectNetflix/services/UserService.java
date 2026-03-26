package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.UserDAO;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.utils.PasswordHasher;

import java.util.Set;

public class UserService {
	/**
     * Login : On hache le MDP saisi par l'utilisateur pour le comparer 
     * avec celui (déjà haché) en base de données.
     */
    public User seConnecter(String email, String mdpSaisi) {
        if (email == null || mdpSaisi == null) return null;
        String mdpHache = PasswordHasher.hashPassword(mdpSaisi);
        return UserDAO.login(email, mdpHache);
    }
//-----------------------------------------------------------------------------------
    /**
     * Inscription : On hache le MDP avant de sauvegarder.
     */
    public int inscrireUtilisateur(User u) {
        String mdpHache = PasswordHasher.hashPassword(u.getMdp());
        u.setMdp(mdpHache); 
        
        return UserDAO.save(u);
    }
//-------------------------------------------------------------------------------
    public void ajouterAuxFavoris(int idUser, int idOeuvre, String type) {
        UserDAO.ajouterAuxFavoris(idUser, idOeuvre, type);
    }
//-----------------------------------------------------------------------------------
    public void retirerDesFavoris(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "fav_film" : "fav_serie";
        UserDAO.removeFromCollection(idUser, idOeuvre, tableName);
    }
//-----------------------------------------------------------
    public void marquerCommeVu(int idUser, int idOeuvre, String type) {
        UserDAO.ajouterAHistorique(idUser, idOeuvre, type);
    }
//------------------------------------------------------------------------
    public Set<Oeuvre> recupererFavoris(int idUser) {
        return UserDAO.getAllUserFavorites(idUser);
    }
//----------------------------------------------------------------------------------    
    public Set<Oeuvre> recupererHistorique(int idUser) {
        return UserDAO.getUserFullHistory(idUser);
    }
//-------------------------------------------------------------------------------
}