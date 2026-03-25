package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.OeuvreDAO;
import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.entities.Categorie;

public class OeuvreService {

    /**
     * Récupère les catégories d'une production (Film ou Série).
     */
    public List<Categorie> getCategories(int productionId, String tableLiaison, String colIdProduction) {
        if (productionId <= 0 || tableLiaison == null || colIdProduction == null) {
            return List.of();
        }
        return OeuvreDAO.getCategoriesByProduction(productionId, tableLiaison, colIdProduction);
    }

    /**
     * Récupère les artistes d'une production.
     */
    public List<Artiste> getArtistes(int productionId, String tableLiaison, String colIdProduction) {
        if (productionId <= 0 || tableLiaison == null || colIdProduction == null) {
            return List.of();
        }
        return OeuvreDAO.getArtistes(productionId, tableLiaison, colIdProduction);
    }

    /**
     * Vérifie si une œuvre possède une catégorie spécifique.
     */
    public boolean hasCategory(int productionId, Categorie targetCat, String table, String col) {
        List<Categorie> categories = getCategories(productionId, table, col);
        return categories.contains(targetCat);
    }

    // --- Méthodes spécifiques basées sur les noms réels de votre BDD (selon FilmDAO/SerieDAO) ---

    public List<Categorie> getCategoriesForFilm(int filmId) {
        // Dans FilmDAO, vous utilisez "film_categorie" et "id_film"
        return OeuvreDAO.getCategoriesByProduction(filmId, "film_categorie", "id_film");
    }

    public List<Categorie> getCategoriesForSerie(int serieId) {
        // Dans SerieDAO, vous utilisez "serie_categorie" et "id_serie"
        return OeuvreDAO.getCategoriesByProduction(serieId, "serie_categorie", "id_serie");
    }

    public List<Artiste> getArtistesForFilm(int filmId) {
        // Selon la logique de vos DAO (Liaison film_acteur et film_directeur)
        // Note : Si vous voulez TOUS les artistes (acteurs + réalisateurs), 
        // il faudra peut-être appeler deux fois la méthode ou vérifier votre table globale.
        return OeuvreDAO.getArtistes(filmId, "film_artiste", "id_film");
    }

    public List<Artiste> getArtistesForSerie(int serieId) {
        return OeuvreDAO.getArtistes(serieId, "serie_artiste", "id_serie");
    }

    /**
     * Utilise la méthode utilitaire de OeuvreDAO pour vérifier l'existence d'une oeuvre
     * (Film ou Série) avant insertion.
     */
    public int verifierExistence(String titre, String dateSortie, String resume, boolean isFilm) {
        String table = isFilm ? "films" : "series";
        return OeuvreDAO.findIDifExisted(titre, dateSortie, resume, table);
    }
}