package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import groupna.projectNetflix.entities.Saison;
import groupna.projectNetflix.utils.ConxDB;

public class SaisonDAO {
    private static Connection conn = ConxDB.getInstance();

    public static int save(Saison s, int idSerie) {
        int generatedId = -1;
        String sql = "INSERT INTO saisons (num, date_sortie, titre, resume, path_trailer, id_serie) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, s.getNum());
            pstmt.setDate(2, java.sql.Date.valueOf(s.getDateDeSortie()));
            pstmt.setString(3, s.getTitre());
            pstmt.setString(4, s.getResume());
            pstmt.setString(5, s.getPathTrailer());
            pstmt.setInt(6, idSerie);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    s.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la Saison : " + e.getMessage());
        }
        
        return generatedId;
    }

    public static Saison findById(int idSaison) {
        Saison s = null;
        String sql = "SELECT * FROM saisons WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idSaison);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    s = new Saison(
                        rs.getInt("id"),
                        rs.getInt("num"),
                        rs.getDate("date_sortie").toLocalDate(),
                        rs.getString("titre"),
                        rs.getString("resume"),
                        rs.getString("path_trailer")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static List<Saison> findAllBySerie(int idSerie) {
        List<Saison> liste = new ArrayList<>();
        String sql = "SELECT * FROM saisons WHERE id_serie = ? ORDER BY num ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idSerie);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Saison(
                        rs.getInt("id"),
                        rs.getInt("num"),
                        rs.getDate("date_sortie").toLocalDate(),
                        rs.getString("titre"),
                        rs.getString("resume"),
                        rs.getString("path_trailer") 
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static void update(Saison s) {
        String sql = "UPDATE saisons SET num = ?, date_sortie = ?, titre = ?, resume = ?, path_trailer = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, s.getNum());
            pstmt.setDate(2, java.sql.Date.valueOf(s.getDateDeSortie()));
            pstmt.setString(3, s.getTitre());
            pstmt.setString(4, s.getResume());
            pstmt.setString(5, s.getPathTrailer());
            pstmt.setInt(6, s.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteSaison(int idSaison) {
        String sql = "DELETE FROM saisons WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idSaison);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[Erreur SQL] Impossible de supprimer la saison : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}