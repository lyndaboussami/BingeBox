package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.EpisodeDAO;
import groupna.projectNetflix.entities.Episode;

public class EpisodeService {

    /**
     * Ajoute un épisode à une saison.
     * Met à jour l'ID de l'objet Episode avec l'ID généré en BDD.
     */
    public boolean addEpisode(Episode ep, int idSaison) {
        if (ep == null || idSaison <= 0) {
            return false;
        }
        int generatedId = EpisodeDAO.addEpisode(ep, idSaison);
        if (generatedId > 0) {
            ep.setId(generatedId); // Important pour garder l'objet à jour
            return true;
        }
        return false;
    }

    /**
     * Récupère un épisode spécifique.
     * CORRECTION : Comme getEpisodeBySeasonAndNumber n'existe pas dans le DAO, 
     * on filtre la liste de la saison.
     */
    public Episode getEpisode(int idSaison, int numeroEpisode) {
        if (idSaison <= 0 || numeroEpisode < 0) {
            return null;
        }
        List<Episode> episodes = EpisodeDAO.getEpisodesBySaison(idSaison);
        for (Episode ep : episodes) {
            if (ep.getNumero() == numeroEpisode) {
                return ep;
            }
        }
        return null;
    }

    /**
     * Récupère tous les épisodes d'une saison.
     */
    public List<Episode> getEpisodesBySaison(int idSaison) {
        if (idSaison <= 0) {
            return null;
        }
        return EpisodeDAO.getEpisodesBySaison(idSaison);
    }

    /**
     * Supprime un épisode via son numéro et son ID de saison.
     */
    public boolean deleteEpisode(int numeroEpisode, int idSaison) {
        if (idSaison <= 0 || numeroEpisode < 0) {
            return false;
        }
        return EpisodeDAO.deleteEpisode(numeroEpisode, idSaison);
    }

    /**
     * Compte le nombre d'épisodes dans une saison.
     */
    public int countEpisodesInSaison(int idSaison) {
        if (idSaison <= 0) return 0;
        List<Episode> episodes = EpisodeDAO.getEpisodesBySaison(idSaison);
        return (episodes != null) ? episodes.size() : 0;
    }
}