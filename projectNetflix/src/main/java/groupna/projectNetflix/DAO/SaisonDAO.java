package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import groupna.projectNetflix.entities.saison;
import groupna.projectNetflix.utils.ConxDB;

public class SaisonDAO {
	private static Connection conn = ConxDB.getInstance();
	public static void save(saison s, int idSerie) {
	    String sql = "INSERT INTO saisons (num, date_de_sortie, titre, resume, id_serie) VALUES (?, ?, ?, ?, ?)";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setInt(1, s.getNum());
	        pstmt.setDate(2, java.sql.Date.valueOf(s.getDateDeSortie()));
	        pstmt.setString(3, s.getTitre());
	        pstmt.setString(4, s.getResume());
	        pstmt.setInt(5, idSerie);

	        pstmt.executeUpdate();

	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                s.setId(rs.getInt(1));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erreur lors de l'ajout de la saison : " + e.getMessage());
	    }
	}
//-----------------------------------------------------------------------------------------------------------
	public static saison findById(int idSaison) {
	    saison s = null;
	    String sql = "SELECT * FROM saisons WHERE id = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, idSaison);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            s = new saison(
	                rs.getInt("id"),
	                rs.getInt("num"),
	                rs.getDate("date_de_sortie").toLocalDate(),
	                rs.getString("titre"),
	                rs.getString("resume")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return s;
	}
//------------------------------------------------------------------------------------------------------------------
	public static List<saison> findAllBySerie(int idSerie) {
	    List<saison> liste = new ArrayList<>();
	    String sql = "SELECT * FROM saisons WHERE id_serie = ? ORDER BY num ASC";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, idSerie);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            liste.add(new saison(
	                rs.getInt("id"),
	                rs.getInt("num"),
	                rs.getDate("date_de_sortie").toLocalDate(),
	                rs.getString("titre"),
	                rs.getString("resume")
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return liste;
	}
//----------------------------------------------------------------------------------------------------------------------
	public static void update(saison s) {
	    String sql = "UPDATE saisons SET num = ?, date_de_sortie = ?, titre = ?, resume = ? WHERE id = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, s.getNum());
	        pstmt.setDate(2, java.sql.Date.valueOf(s.getDateDeSortie()));
	        pstmt.setString(3, s.getTitre());
	        pstmt.setString(4, s.getResume());
	        pstmt.setInt(5, s.getId());

	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
//--------------------------------------------------------------------------------------------------------------------------
	public static boolean deleteSaisonEtEpisodes(int idSaison) {
		String sqlEpisodes = "DELETE FROM episodes WHERE id_saison = ?";
	    String sqlSaison = "DELETE FROM saisons WHERE id = ?";

	    Connection conn = null;
	    try {
	        conn.setAutoCommit(false); 
	        try (PreparedStatement pstmt1 = conn.prepareStatement(sqlEpisodes)) {
	            pstmt1.setInt(1, idSaison);
	            pstmt1.executeUpdate();
	        }
	        try (PreparedStatement pstmt2 = conn.prepareStatement(sqlSaison)) {
	            pstmt2.setInt(1, idSaison);
	            int rows = pstmt2.executeUpdate();
	            
	            conn.commit();
	            return rows > 0;
	        }

	    } catch (SQLException e) {
	        try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
	        e.printStackTrace();
	        return false;
	    }
	}
	
}
