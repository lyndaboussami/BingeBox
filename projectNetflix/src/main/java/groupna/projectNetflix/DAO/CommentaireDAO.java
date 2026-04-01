package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import groupna.projectNetflix.entities.Commentaire;
import groupna.projectNetflix.utils.ConxDB;

public class CommentaireDAO {
    private static Connection conn = ConxDB.getInstance();
    private static String getTableName(String type) {
        return type.equalsIgnoreCase("film") ? "film_commentaire" : "serie_commentaire";
    }
    private static String getIdColumnName(String type) {
        return type.equalsIgnoreCase("film") ? "id_movie" : "id_serie";
    }

    public static boolean save(Commentaire c, String type) {
        String table = getTableName(type);
        String idCol = getIdColumnName(type);
        
        String sql = "INSERT INTO " + table + " (id_user, " + idCol + ", content, reported) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, c.getId_user());
            pstmt.setInt(2, c.getId_oeuvre());
            pstmt.setString(3, c.getContent());
            pstmt.setBoolean(4, c.isReported());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion dans " + table + " : " + e.getMessage());
            return false;
        }
    }

    public static List<Commentaire> findAllByOeuvre(int idOeuvre, String type) {
        List<Commentaire> liste = new ArrayList<>();
        String table = getTableName(type);
        String idCol = getIdColumnName(type);
        
        String sql = "SELECT * FROM " + table + " WHERE " + idCol + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idOeuvre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Commentaire(
                        rs.getInt("id_user"),
                        rs.getInt(idCol),
                        rs.getString("content"),
                        rs.getBoolean("reported"),
                        rs.getString("raison")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static boolean reportCommentaire(int idUser, int idOeuvre, String type,String raison) {
        String table = getTableName(type);
        String idCol = getIdColumnName(type);
        
        String sql = "UPDATE " + table + " SET reported = true ,raison=? WHERE id_user = ? AND " + idCol + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(2, idUser);
            pstmt.setInt(3, idOeuvre);
            pstmt.setNString(1,raison);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(int idUser, int idOeuvre, String type) {
        String table = getTableName(type);
        String idCol = getIdColumnName(type);
        
        String sql = "DELETE FROM " + table + " WHERE id_user = ? AND " + idCol + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idOeuvre);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<Commentaire> findReported() {
        List<Commentaire> reportedList = new ArrayList<>();
        String sql = "SELECT id_user, id_movie AS id_oeuvre, content, reported FROM film_commentaire WHERE reported = true " +
                     "UNION ALL " +
                     "SELECT id_user, id_serie AS id_oeuvre, content, reported FROM serie_commentaire WHERE reported = true";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                reportedList.add(new Commentaire(
                    rs.getInt("id_user"),
                    rs.getInt("id_oeuvre"),
                    rs.getString("content"),
                    rs.getBoolean("reported"),
                    rs.getString("raison")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des signalements : " + e.getMessage());
            e.printStackTrace();
        }
        return reportedList;
    }
}