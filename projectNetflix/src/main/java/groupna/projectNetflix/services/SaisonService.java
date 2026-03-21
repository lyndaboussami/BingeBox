package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.SaisonDAO;
import groupna.projectNetflix.entities.saison;

public class SaisonService {

    public void addSaison(saison s, int idSerie) {
        if (s != null && idSerie > 0) {
            SaisonDAO.save(s, idSerie);
        }
    }

    // -------------------------------------------------------------------------

    public saison getSaisonById(int idSaison) {
        if (idSaison <= 0) return null;
        return SaisonDAO.findById(idSaison);
    }

    // -------------------------------------------------------------------------

    public List<saison> getSaisonsBySerie(int idSerie) {
        if (idSerie <= 0) return List.of();
        return SaisonDAO.findAllBySerie(idSerie);
    }

    // -------------------------------------------------------------------------

    public void updateSaison(saison s) {
        if (s != null && s.getId() > 0) {
            SaisonDAO.update(s);
        }
    }

    // -------------------------------------------------------------------------

    public boolean deleteSaisonCompletement(int idSaison) {
        if (idSaison <= 0) return false;
        return SaisonDAO.deleteSaisonEtEpisodes(idSaison);
    }

    // -------------------------------------------------------------------------

    public int getNombreDeSaisons(int idSerie) {
        return getSaisonsBySerie(idSerie).size();
    }
}
