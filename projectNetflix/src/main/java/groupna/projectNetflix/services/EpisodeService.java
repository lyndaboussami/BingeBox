package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.EpisodeDAO;
import groupna.projectNetflix.entities.Episode;

public class EpisodeService {
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
    public List<Episode> getEpisodesBySaison(int idSaison) {
        if (idSaison <= 0) {
            return null;
        }
        return EpisodeDAO.getEpisodesBySaison(idSaison);
    }
    public boolean deleteEpisode(int numeroEpisode, int idSaison) {
        if (idSaison <= 0 || numeroEpisode < 0) {
            return false;
        }
        return EpisodeDAO.deleteEpisode(numeroEpisode, idSaison);
    }
    public int countEpisodesInSaison(int idSaison) {
        if (idSaison <= 0) return 0;
        List<Episode> episodes = EpisodeDAO.getEpisodesBySaison(idSaison);
        return (episodes != null) ? episodes.size() : 0;
    }
    public int getVues(int idEpisode) {
    	return EpisodeDAO.getNombreVuesEpisode(idEpisode);
    }
    public int recupererIdSaison(int idEpisode) {
        return EpisodeDAO.getIdSaisonByEpisode(idEpisode);
    }
}