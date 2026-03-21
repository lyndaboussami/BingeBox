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

    // -------------------------------------------------------------------------

    public List<Artiste> getArtistes(int productionId, String tableLiaison, String colIdProduction) {
        if (productionId <= 0 || tableLiaison == null || colIdProduction == null) {
            return List.of();
        }
        return OeuvreDAO.getArtistes(productionId, tableLiaison, colIdProduction);
    }

    // -------------------------------------------------------------------------

    public boolean hasCategory(int productionId, Categorie targetCat, String table, String col) {
        List<Categorie> categories = OeuvreDAO.getCategoriesByProduction(productionId, table, col);
        return categories.contains(targetCat);
    }

    // -------------------------------------------------------------------------

}
