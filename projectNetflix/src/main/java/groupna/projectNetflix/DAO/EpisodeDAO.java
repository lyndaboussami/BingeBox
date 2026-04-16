package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.utils.ConxDB;

public class EpisodeDAO {
    private static Connection conn = ConxDB.getInstance();

    public static int addEpisode(Episode ep, int idSaison) {
        int generatedId = 0;
        String sql = "INSERT INTO episodes (titre, numero, duree, resume, id_saison, path_ep, path_miniature) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ep.getTitre());
            pstmt.setInt(2, ep.getNumero());
            pstmt.setTime(3, ep.getDuree() != null ? java.sql.Time.valueOf(ep.getDuree()) : null);
            pstmt.setString(4, ep.getResume());
            pstmt.setInt(5, idSaison);
            pstmt.setString(6, ep.getPathEp());
            pstmt.setString(7, ep.getPathMiniaure());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    ep.setId(generatedId);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }
    public static int getIdSaisonByEpisode(int idEpisode) {
        int idSaison = -1;
        String sql = "SELECT id_saison FROM episodes WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEpisode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    idSaison = rs.getInt("id_saison");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idSaison;
    }
    public static Episode getEpisodeBySeasonAndNumber(int idSaison, int numeroEpisode) {
        Episode ep = null;
        String sql = "SELECT * FROM episodes WHERE id_saison = ? AND numero = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idSaison);
            pstmt.setInt(2, numeroEpisode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ep = mapResultSetToEpisode(rs);
                }
            }
        } catch (SQLException e) {
        	e.getMessage();
        }
        return ep;
    }

    public static List<Episode> getEpisodesBySaison(int idSaison) {
        List<Episode> episodes = new ArrayList<>();
        String sql = "SELECT * FROM episodes WHERE id_saison = ? ORDER BY numero ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idSaison);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    episodes.add(mapResultSetToEpisode(rs));
                }
            }
        } catch (SQLException e) {
        	e.getMessage();
        }
        return episodes;
    }
    private static Episode mapResultSetToEpisode(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String titre = rs.getString("titre");
        int num = rs.getInt("numero");
        String resume = rs.getString("resume");
        String pathEp = rs.getString("path_ep");
        String pathMin = rs.getString("path_miniature");
        
        java.sql.Time sqlTime = rs.getTime("duree");
        LocalTime duree = (sqlTime != null) ? sqlTime.toLocalTime() : null;
        return new Episode(num, id, resume, titre, duree, pathEp, pathMin);
    }

    public static boolean deleteEpisode(int numeroEpisode, int idSaison) {
        String sql = "DELETE FROM episodes WHERE numero = ? AND id_saison = ?";
        boolean isDeleted = false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroEpisode);
            pstmt.setInt(2, idSaison);

            int rowsAffected = pstmt.executeUpdate();
            isDeleted = (rowsAffected > 0);

        } catch (SQLException e) {
        	e.getMessage();
        }

        return isDeleted;
    }
    public static Episode getEpisodeById(int id) {
        Episode episode = null;
        String sql = "SELECT * FROM episodes WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    episode = new Episode(
                        rs.getInt("numero"),
                        rs.getInt("id"),
                        rs.getString("resume"),
                        rs.getString("titre"),
                        rs.getTime("duree") != null ? rs.getTime("duree").toLocalTime() : null,
                        rs.getString("path_ep"),
                        rs.getString("path_miniature")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return episode;
    }
    public static int getNombreVuesEpisode(int idEpisode) {
        int totalVues = 0;
        String sql = "SELECT COUNT(*) FROM historique_episodes WHERE id_episode = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEpisode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalVues = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalVues;
    }
    public static void updateEpisode(Episode ep) {
        String sql = "UPDATE episodes SET titre = ?, numero = ?, duree = ?, resume = ?, path_ep = ?, path_miniature = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ep.getTitre());
            pstmt.setInt(2, ep.getNumero());
            pstmt.setTime(3, ep.getDuree() != null ? java.sql.Time.valueOf(ep.getDuree()) : null);
            pstmt.setString(4, ep.getResume());
            pstmt.setString(5, ep.getPathEp());
            pstmt.setString(6, ep.getPathMiniaure());
            pstmt.setInt(7, ep.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}