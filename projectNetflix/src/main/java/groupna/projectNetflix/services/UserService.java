package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.UserDAO;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.User;
import java.util.Set;

public class UserService {
    public User seConnecter(String email, String mdp) {
        if (email == null || mdp == null || email.isEmpty()) {
            return null;
        }
        return UserDAO.login(email, mdp);
    }
    public int inscrireUtilisateur(User u) {
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