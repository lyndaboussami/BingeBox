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
    public void ajouterAuxFavoris(int idUser, int idOeuvre, String type) {
        UserDAO.ajouterAuxFavoris(idUser, idOeuvre, type);
    }

    public void retirerDesFavoris(int idUser, int idOeuvre, String type) {
        // Le DAO gère le choix de la table selon le type
        String tableName = type.equalsIgnoreCase("film") ? "fav_film" : "fav_serie";
        UserDAO.removeFromCollection(idUser, idOeuvre, tableName);
    }

    public Set<Oeuvre> recupererFavoris(int idUser) {
        return UserDAO.getAllUserFavorites(idUser);
    }

    // --- GESTION DE L'HISTORIQUE ---
    public void marquerFilmCommeVu(int idUser, int idFilm) {
        UserDAO.ajouterAHistoriqueFilm(idUser, idFilm);
    }

    public void marquerEpisodeCommeVu(int idUser, int idEpisode) {
        UserDAO.ajouterAHistoriqueEpisode(idUser, idEpisode);
    }
    
    public List<HistoryItem> recupererHistoriqueComplet(int idUser) {
        return UserDAO.getUserFullGlobalHistory(idUser);
    }
    public Map<LocalDate, List<Visualisable>> recupererHistoriqueGroupéParDate(int idUser) {
        return UserDAO.getHistoryGroupedByDate(idUser);
    }

    // --- GESTION DES UTILISATEURS (ADMIN) ---

    public List<User> recupererTousLesUtilisateurs() {
        return UserDAO.getAllUsers();
    }
    
    public int rechercherParEmail(String email) {
        return UserDAO.getIdParEmail(email);
    }
}