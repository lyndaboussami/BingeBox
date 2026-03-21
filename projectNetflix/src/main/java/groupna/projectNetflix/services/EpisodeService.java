package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.EpisodeDAO;
import groupna.projectNetflix.entities.Episode;

public class EpisodeService {

    public boolean addEpisode(Episode ep, int idSaison) {
        if (ep == null || idSaison <= 0) {
            return false;
        }
        int id = EpisodeDAO.addEpisode(ep, idSaison);
        return id > 0;
    }

    // -------------------------------------------------------------------------

    public Episode getEpisode(int idSaison, int numeroEpisode) {
        if (idSaison <= 0 || numeroEpisode < 0) {
            return null;
        }
        return EpisodeDAO.getEpisodeBySeasonAndNumber(idSaison, numeroEpisode);
    }

    // -------------------------------------------------------------------------

    public List<Episode> getEpisodesBySaison(int idSaison) {
        if (idSaison <= 0) {
            return null;
        }
        return EpisodeDAO.getEpisodesBySaison(idSaison);
    }

    // -------------------------------------------------------------------------

    public boolean deleteEpisode(int numeroEpisode, int idSaison) {
        if (idSaison <= 0 || numeroEpisode < 0) {
            return false;
        }
        return EpisodeDAO.deleteEpisode(numeroEpisode, idSaison);
    }

    // -------------------------------------------------------------------------

    public int countEpisodesInSaison(int idSaison) {
        List<Episode> episodes = EpisodeDAO.getEpisodesBySaison(idSaison);
        return (episodes != null) ? episodes.size() : 0;
    }
}
