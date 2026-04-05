package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.UserDAO;
import groupna.projectNetflix.DAO.HistoryItem;
import groupna.projectNetflix.entities.Oeuvre;
import groupna.projectNetflix.entities.User;
import groupna.projectNetflix.entities.Visualisable;
import groupna.projectNetflix.utils.PasswordHasher;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserService {
    public User seConnecter(String email, String mdpSaisi) {
        if (email == null || mdpSaisi == null) return null;
        String mdpHache = PasswordHasher.hashPassword(mdpSaisi);
        return UserDAO.login(email, mdpHache);
    }
    public int inscrireUtilisateur(User u) {
        String mdpHache = PasswordHasher.hashPassword(u.getMdp());
        u.setMdp(mdpHache); 
        return UserDAO.save(u);
    }
//------------------------------------------------------------------------------
    public User recupererUtilisateurParId(int id) {
        return UserDAO.findById(id);
    }

    public void ajouterAuxFavoris(int idUser, int idOeuvre, String type) {
        UserDAO.ajouterAuxFavoris(idUser, idOeuvre, type);
    }

    public void retirerDesFavoris(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "fav_film" : "fav_serie";
        UserDAO.removeFromCollection(idUser, idOeuvre, tableName);
    }

    public List<Oeuvre> recupererFavoris(int idUser) {
        return UserDAO.getAllUserFavorites(idUser);
    }

    // --- GESTION DE L'HISTORIQUE ---
    public void marquerFilmCommeVu(int idUser, int idFilm,double time) {
        UserDAO.ajouterAHistoriqueFilm(idUser, idFilm,time);
    }

    public void marquerEpisodeCommeVu(int idUser, int idEpisode,double time) {
        UserDAO.ajouterAHistoriqueEpisode(idUser, idEpisode,time);
    }
    
    public List<HistoryItem> recupererHistoriqueComplet(int idUser) {
        return UserDAO.getUserFullGlobalHistory(idUser);
    }
    public void updateUser(User u) {
        if (u != null && u.getId() > 0) {
            UserDAO.update(u);
        }
    }
    public void updateUserPassword(User user) {
    	String mdpHache = PasswordHasher.hashPassword(user.getMdp());
        user.setMdp(mdpHache);
        UserDAO.update(user);
    }
    public void viderHistorique(int idUser) {
        UserDAO.clearHistory(idUser);
        
        System.out.println("[Service] Demande de suppression d'historique traitée pour l'utilisateur : " + idUser);
    }
    // --- GESTION DES UTILISATEURS (ADMIN) ---

    public List<User> recupererTousLesUtilisateurs() {
        return UserDAO.getAllUsers();
    }
    
    public int rechercherParEmail(String email) {
        return UserDAO.getIdParEmail(email);
    }
}