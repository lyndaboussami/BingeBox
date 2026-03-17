package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.utils.ConxDB;

public class OeuvreDAO {
	protected static Connection conn = ConxDB.getInstance();
	protected static void saveCategories(int productionId, List<Categorie> categories, String tableName, String idColumnName) {
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
	        e.printStackTrace();
	    }
	}
//--------------------------------------------------------------------------------
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
	                    System.err.println("Catégorie inconnue dans l'Enum : " + nomCat);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return categories;
	}
//-------------------------------------------------------------------------------------------------------
	protected static void saveArtistes(int productionId, List<Artiste> artistes, String tableName, String idColumnName) {
	    if (artistes == null || artistes.isEmpty()) return;

	    String sql = "INSERT INTO " + tableName + " (" + idColumnName + ", id_artiste) VALUES (?, ?)";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        for (Artiste a : artistes) {
	            pstmt.setInt(1, productionId);
	            pstmt.setInt(2, a.getId());
	            pstmt.addBatch();
	        }
	        pstmt.executeBatch();
	    } catch (SQLException e) {
	        System.err.println("Erreur lors de la liaison dans " + tableName);
	        e.printStackTrace();
	    }
	}
//-------------------------------------------------------------------------------------------
	public static List<Artiste> getArtistes(int productionId, String tableLiaison, String colIdProduction) {
	    List<Artiste> artistes = new ArrayList<>();
	    String sql = "SELECT a.* FROM artistes a " +
	                 "JOIN " + tableLiaison + " li ON a.id = li.id_artiste " +
	                 "WHERE li." + colIdProduction + " = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, productionId);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                String oeuvresStr = rs.getString("oeuvre_majeurs");
	                List<String> oeuvresList = (oeuvresStr != null) 
	                    ? Arrays.asList(oeuvresStr.split(",")) 
	                    : new ArrayList<>();
	                Artiste a = new Artiste(
	                    rs.getInt("id"),
	                    rs.getString("nom"),
	                    rs.getString("prenom"),
	                    rs.getString("bio"),
	                    oeuvresList
	                );
	                artistes.add(a);
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erreur getArtistesByProduction: " + e.getMessage());
	    }
	    return artistes;
	}
//--------------------------------------------------------------------------------------
}
