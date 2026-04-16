package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.utils.ConxDB;

public class OeuvreDAO {
    protected static Connection conn = ConxDB.getInstance();
    protected static void saveCategories(int productionId, List<Categorie> categories, String tableName, String idColumnName) {
        if (categories == null || categories.isEmpty()) return;
        
        String sql = "INSERT INTO " + tableName + " (" + idColumnName + ", id_categorie) " +
                     "VALUES (?, (SELECT id FROM categories WHERE nom = ?))";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Categorie cat : categories) {
                pstmt.setInt(1, productionId);
                pstmt.setString(2, cat.name());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
        	e.getMessage();
        }
    }
    protected static void saveArtistes(int productionId, List<Artiste> artistes, String tableName, String idColumnName) {
        if (artistes == null || artistes.isEmpty()) return;
        
        String sqlLiaison = "INSERT IGNORE INTO " + tableName + " (" + idColumnName + ", id_artiste) VALUES (?, ?)";

        try (PreparedStatement pstmtLiaison = conn.prepareStatement(sqlLiaison)) {
            conn.setAutoCommit(false);

            for (Artiste a : artistes) {
                int artisteId = ArtisteDAO.getIdIfExists(a.getFullname());
                
                if (artisteId <= 0) {
                    artisteId = ArtisteDAO.save(a);
                }
                
                if (artisteId > 0) {
                    pstmtLiaison.setInt(1, productionId);
                    pstmtLiaison.setInt(2, artisteId);
                    pstmtLiaison.addBatch();
                }
            }
            pstmtLiaison.executeBatch();
            conn.commit();
            
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public static List<Artiste> getArtistes(int productionId, String tableLiaison, String colIdProduction) {
        List<Artiste> artistes = new ArrayList<>();
        String sql = "SELECT a.* FROM artistes a " +
                     "JOIN " + tableLiaison + " li ON a.id = li.id_artiste " +
                     "WHERE li." + colIdProduction + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Artiste a = new Artiste(
                        rs.getInt("id"),
                        rs.getString("fullname")
                    );
                    artistes.add(a);
                }
            }
        } catch (SQLException e) {
        	e.getMessage();
        }
        return artistes;
    }
    public static List<Categorie> getCategoriesByProduction(int productionId, String tableLiaison, String colIdProduction) {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT c.nom FROM categories c " +
                     "JOIN " + tableLiaison + " li ON c.id = li.id_categorie " +
                     "WHERE li." + colIdProduction + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nomCat = rs.getString("nom");
                    try {
                        categories.add(Categorie.valueOf(nomCat.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                    	e.getMessage();
                    }
                }
            }
        } catch (SQLException e) {
        	e.getMessage();
        }
        return categories;
    }
    public static int findIDifExisted(String titre, String dateSortie, String resume, String tableName) {
        String sql = "SELECT id FROM " + tableName + " WHERE titre = ? AND date_de_sortie = ? AND resume = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titre);
            pstmt.setString(2, dateSortie);
            pstmt.setString(3, resume);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
        	e.getMessage();
        }
        return -1;
    }
    public static void updateArtistes(int productionId, List<Artiste> nouveauxArtistes, String tableName, String colIdProduction) {
        String sqlDelete = "DELETE FROM " + tableName + " WHERE " + colIdProduction + " = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setInt(1, productionId);
            pstmt.executeUpdate();
            saveArtistes(productionId, nouveauxArtistes, tableName, colIdProduction);
        } catch (SQLException e) {
        	e.getMessage();
        }
    }
    public static void updateCategories(int productionId, List<Categorie> nouvellesCategories, String tableName, String colIdProduction) {
        String sqlDelete = "DELETE FROM " + tableName + " WHERE " + colIdProduction + " = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setInt(1, productionId);
            pstmt.executeUpdate();
            saveCategories(productionId, nouvellesCategories, tableName, colIdProduction);
        } catch (SQLException e) {
        	e.getMessage();
        }
    }
}