package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import groupna.projectNetflix.entities.Rate;
import groupna.projectNetflix.utils.ConxDB;

public class RateDAO {
    private static Connection conn = ConxDB.getInstance();
    public static boolean saveOrUpdate(Rate r, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "rate_film" : "rate_serie";
        String idColumn = type.equalsIgnoreCase("film") ? "id_film" : "id_serie";

        String sql = "INSERT INTO " + tableName + " (id_user, " + idColumn + ", nbStars) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nbStars = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, r.getId_user());
            pstmt.setInt(2, r.getId_oeuvre());
            pstmt.setInt(3, r.getNbStars());
            pstmt.setInt(4, r.getNbStars()); 

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Impossible de sauvegarder la note : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static int findUserRating(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "rate_film" : "rate_serie";
        String idColumn = type.equalsIgnoreCase("film") ? "id_film" : "id_serie";
        
        String sql = "SELECT nbStars FROM " + tableName + " WHERE id_user = ? AND " + idColumn + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idOeuvre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("nbStars");
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Lecture note utilisateur impossible : " + e.getMessage());
        }
        return 0;
    }
    public static double getAverageRating(int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "rate_film" : "rate_serie";
        String idColumn = type.equalsIgnoreCase("film") ? "id_film" : "id_serie";
        
        String sql = "SELECT AVG(nbStars) as moyenne FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idOeuvre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("moyenne");
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Calcul moyenne impossible : " + e.getMessage());
        }
        return 0.0;
    }
    public static boolean deleteRate(int idUser, int idOeuvre, String type) {
        String tableName = type.equalsIgnoreCase("film") ? "rate_film" : "rate_serie";
        String idColumn = type.equalsIgnoreCase("film") ? "id_film" : "id_serie";
        
        String sql = "DELETE FROM " + tableName + " WHERE id_user = ? AND " + idColumn + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idOeuvre);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Suppression note impossible : " + e.getMessage());
            return false;
        }
    }
}