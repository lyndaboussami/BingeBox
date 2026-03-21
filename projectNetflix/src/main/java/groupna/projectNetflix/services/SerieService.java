package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.SerieDAO;
import groupna.projectNetflix.entities.Serie;

public class SerieService {

    public Serie getSerieById(int id) {
        if (id <= 0) return null;
        return SerieDAO.findById(id);
    }

    // -------------------------------------------------------------------------

    public List<Serie> getAllSeries() {
        return SerieDAO.findAll();
    }

    // -------------------------------------------------------------------------

    public boolean addSerie(Serie serie) {
        if (serie == null || serie.getTitre() == null || serie.getTitre().isEmpty()) {
            return false;
        }
        int generatedId = SerieDAO.save(serie);
        return generatedId > 0;
    }

    // -------------------------------------------------------------------------

    public boolean deleteSerie(int id) {
        if (id <= 0) return false;
        return SerieDAO.delete(id);
    }

    // -------------------------------------------------------------------------

    public int getTotalSaisons(int serieId) {
        Serie s = SerieDAO.findById(serieId);
        return (s != null && s.getSaisons() != null) ? s.getSaisons().size() : 0;
    }

    // -------------------------------------------------------------------------

    public double getAverageRating() {
        List<Serie> series = SerieDAO.findAll();
        if (series.isEmpty()) return 0.0;
        return series.stream()
                .mapToDouble(Serie::getRate)
                .average()
                .orElse(0.0);
    }
}