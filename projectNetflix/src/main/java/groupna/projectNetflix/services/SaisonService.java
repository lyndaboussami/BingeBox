package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.SaisonDAO;
import groupna.projectNetflix.entities.saison;

public class SaisonService {

    /**
     * Ajoute une saison à une série.
     * @return true si l'ID généré est valide.
     */
    public boolean addSaison(saison s, int idSerie) {
        if (s != null && idSerie > 0) {
            int generatedId = SaisonDAO.save(s, idSerie);
            if (generatedId > 0) {
                s.setId(generatedId); // Crucial pour l'ajout d'épisodes après !
                return true;
            }
        }
        return false;
    }

    /**
     * Récupère une saison par son identifiant unique.
     */
    public saison getSaisonById(int idSaison) {
        if (idSaison <= 0) return null;
        return SaisonDAO.findById(idSaison);
    }

    /**
     * Récupère la liste des saisons d'une série.
     */
    public List<saison> getSaisonsBySerie(int idSerie) {
        if (idSerie <= 0) return List.of();
        return SaisonDAO.findAllBySerie(idSerie);
    }

    /**
     * Met à jour les informations d'une saison (titre, résumé, etc.).
     */
    public boolean updateSaison(saison s) {
        if (s != null && s.getId() > 0) {
            // On suppose que SaisonDAO.update renvoie un boolean ou le nombre de lignes
            SaisonDAO.update(s);
            return true;
        }
        return false;
    }

    /**
     * Supprime une saison et tous ses épisodes associés (Cascade).
     */
    public boolean deleteSaisonCompletement(int idSaison) {
        if (idSaison <= 0) return false;
        // Votre DAO possède déjà la méthode gérant la suppression des épisodes
        return SaisonDAO.deleteSaisonEtEpisodes(idSaison);
    }

    /**
     * Compte le nombre de saisons pour une série donnée.
     */
    public int getNombreDeSaisons(int idSerie) {
        if (idSerie <= 0) return 0;
        List<saison> saisons = SaisonDAO.findAllBySerie(idSerie);
        return (saisons != null) ? saisons.size() : 0;
    }
}