package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.SerieDAO;
import groupna.projectNetflix.DAO.SaisonDAO;
import groupna.projectNetflix.DAO.OeuvreDAO;
import groupna.projectNetflix.entities.Serie;

public class SerieService {

    /**
     * Récupère une série par son identifiant unique.
     */
    public Serie getSerieById(int id) {
        if (id <= 0) return null;
        return SerieDAO.findById(id);
    }

    /**
     * Retourne la liste de toutes les séries.
     */
    public List<Serie> getAllSeries() {
        return SerieDAO.findAll();
    }

    /**
     * Ajoute une série en vérifiant si elle n'existe pas déjà.
     */
    public boolean addSerie(Serie serie) {
        if (serie == null || serie.getTitre() == null || serie.getTitre().isEmpty()) {
            return false;
        }

        // Vérification de doublon via la méthode héritée de OeuvreDAO
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

        // Sauvegarde (SerieDAO.save injecte l'ID dans l'objet serie)
        SerieDAO.save(serie);
        
        return serie.getId() > 0;
    }

    /**
     * Supprime une série (et potentiellement ses dépendances selon vos contraintes SQL).
     */
    public boolean deleteSerie(int id) {
        if (id <= 0) return false;
        return SerieDAO.delete(id);
    }

    /**
     * Compte le nombre de saisons pour une série.
     * Optimisé : utilise SaisonDAO pour éviter de charger toute la structure de la série.
     */
    public int getTotalSaisons(int serieId) {
        if (serieId <= 0) return 0;
        return SaisonDAO.findAllBySerie(serieId).size();
    }

    /**
     * Calcule la moyenne des notes de toutes les séries.
     */
    public double getAverageRating() {
        List<Serie> series = SerieDAO.findAll();
        if (series == null || series.isEmpty()) return 0.0;
        
        return series.stream()
                .mapToDouble(Serie::getRate)
                .average()
                .orElse(0.0);
    }
}