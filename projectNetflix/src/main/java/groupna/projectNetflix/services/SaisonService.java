package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.SaisonDAO;
import groupna.projectNetflix.entities.Saison;

public class SaisonService {

    public boolean addSaison(Saison s, int idSerie) {
        if (s != null && idSerie > 0) {
            int generatedId = SaisonDAO.save(s, idSerie);
            if (generatedId > 0) {
                s.setId(generatedId);
                return true;
            }
        }
        return false;
    }

    
    public Saison getSaisonById(int idSaison) {
        if (idSaison <= 0) return null;
        return SaisonDAO.findById(idSaison);
    }

    
    public List<Saison> getSaisonsBySerie(int idSerie) {
        if (idSerie <= 0) return List.of();
        return SaisonDAO.findAllBySerie(idSerie);
    }

    
    public boolean updateSaison(Saison s) {
        if (s != null && s.getId() > 0) {
            SaisonDAO.update(s);
            return true;
        }
        return false;
    }
    public boolean deleteSaisonCompletement(int idSaison) {
        if (idSaison <= 0) return false;
        return SaisonDAO.deleteSaison(idSaison);
    }

    public int getNombreDeSaisons(int idSerie) {
        if (idSerie <= 0) return 0;
        List<Saison> saisons = SaisonDAO.findAllBySerie(idSerie);
        return (saisons != null) ? saisons.size() : 0;
    }
    public int recupererIdSerie(int idSaison) {
        return SaisonDAO.getIdSerieBySaison(idSaison);
    }
}