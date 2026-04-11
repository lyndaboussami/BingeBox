package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.OeuvreDAO;
import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.entities.Categorie;

public class OeuvreService {

    public List<Categorie> getCategories(int productionId, String tableLiaison, String colIdProduction) {
        if (productionId <= 0 || tableLiaison == null || colIdProduction == null) {
            return List.of();
        }
        return OeuvreDAO.getCategoriesByProduction(productionId, tableLiaison, colIdProduction);
    }
    public List<Artiste> getArtistes(int productionId, String tableLiaison, String colIdProduction) {
        if (productionId <= 0 || tableLiaison == null || colIdProduction == null) {
            return List.of();
        }
        return OeuvreDAO.getArtistes(productionId, tableLiaison, colIdProduction);
    }
    public boolean hasCategory(int productionId, Categorie targetCat, String table, String col) {
        List<Categorie> categories = getCategories(productionId, table, col);
        return categories.contains(targetCat);
    }

    public List<Categorie> getCategoriesForFilm(int filmId) {
        return OeuvreDAO.getCategoriesByProduction(filmId, "film_categorie", "id_film");
    }

    public List<Categorie> getCategoriesForSerie(int serieId) {
        return OeuvreDAO.getCategoriesByProduction(serieId, "serie_categorie", "id_serie");
    }

    public List<Artiste> getArtistesForFilm(int filmId) {
        return OeuvreDAO.getArtistes(filmId, "film_artiste", "id_film");
    }

    public List<Artiste> getArtistesForSerie(int serieId) {
        return OeuvreDAO.getArtistes(serieId, "serie_artiste", "id_serie");
    }
    public int verifierExistence(String titre, String dateSortie, String resume, boolean isFilm) {
        String table = isFilm ? "films" : "series";
        return OeuvreDAO.findIDifExisted(titre, dateSortie, resume, table);
    }
}