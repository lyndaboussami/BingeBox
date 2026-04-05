package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.SerieDAO;
import groupna.projectNetflix.DAO.SaisonDAO;
import groupna.projectNetflix.DAO.EpisodeDAO;
import groupna.projectNetflix.DAO.OeuvreDAO;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Serie;

public class SerieService {
    public Serie getSerieById(int id) {
        if (id <= 0) return null;
        return SerieDAO.findById(id);
    }
    public List<Serie> getAllSeries() {
        return SerieDAO.findAll();
    }
    public boolean addSerie(Serie serie) {
        if (serie == null || serie.getTitre() == null || serie.getTitre().isEmpty()) {
            return false;
        }
        int existingId = OeuvreDAO.findIDifExisted(
            serie.getTitre(), 
            serie.getDateDeSortie().toString(), 
            serie.getResume(), 
            "series"
        );

        if (existingId != -1) {
            serie.setId(existingId);
            System.out.println("[Service] La série existe déjà (ID: " + existingId + ")");
            return true; 
        }
        SerieDAO.save(serie);
        
        return serie.getId() > 0;
    }
    public boolean deleteSerie(int id) {
        if (id <= 0) return false;
        return SerieDAO.delete(id);
    }
    public int getTotalSaisons(int serieId) {
        if (serieId <= 0) return 0;
        return SaisonDAO.findAllBySerie(serieId).size();
    }
    public void updateSerie(Serie s) {
        if (s != null && s.getId() > 0) {
            SerieDAO.update(s);
            if (s.getSaisons() != null) {
                s.getSaisons().forEach((saison, episodes) -> {
                    SaisonDAO.update(saison);
                    if (episodes != null) {
                        for (Episode ep : episodes) {
                            EpisodeDAO.updateEpisode(ep);
                        }
                    }
                });
            }
        }
    }

}