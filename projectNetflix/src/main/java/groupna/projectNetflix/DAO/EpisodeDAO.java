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
        String sql = "INSERT INTO episodes (titre, numero, duree, resume, id_saison, Path_ep) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ep.getTitre());
            pstmt.setInt(2, ep.getNumero());
            pstmt.setTime(3, ep.getDuree() != null ? java.sql.Time.valueOf(ep.getDuree()) : null);
            pstmt.setString(4, ep.getResume());
            pstmt.setInt(5, idSaison);
            pstmt.setString(6, ep.getPathEp());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    ep.setId(generatedId);
                }
            }
            System.out.println("Épisode ajouté avec succès !");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'épisode : " + e.getMessage());
            e.printStackTrace();
        }
        return generatedId;
    }

    public static Episode getEpisodeBySeasonAndNumber(int idSaison, int numeroEpisode) {
        Episode ep = null;
        String sql = "SELECT * FROM episodes WHERE id_saison = ? AND numero = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idSaison);
            pstmt.setInt(2, numeroEpisode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String titre = rs.getString("titre");
                    int num = rs.getInt("numero");
                    String resume = rs.getString("resume");
                    String pathEp = rs.getString("Path_ep");
                    
                    java.sql.Time sqlTime = rs.getTime("duree");
                    LocalTime duree = (sqlTime != null) ? sqlTime.toLocalTime() : null;
                    ep = new Episode(num, id, resume, titre, duree, pathEp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'épisode : " + e.getMessage());
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
                    int id = rs.getInt("id");
                    String titre = rs.getString("titre");
                    int num = rs.getInt("numero");
                    String resume = rs.getString("resume");
                    String pathEp = rs.getString("path_ep");
                    
                    java.sql.Time sqlTime = rs.getTime("duree");
                    LocalTime duree = (sqlTime != null) ? sqlTime.toLocalTime() : null;

                    episodes.add(new Episode(num, id, resume, titre, duree, pathEp));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des épisodes : " + e.getMessage());
        }
        return episodes;
    }

    public static boolean deleteEpisode(int numeroEpisode, int idSaison) {
        String sql = "DELETE FROM episodes WHERE numero = ? AND id_saison = ?";
        boolean isDeleted = false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroEpisode);
            pstmt.setInt(2, idSaison);

            int rowsAffected = pstmt.executeUpdate();
            isDeleted = (rowsAffected > 0);

            if (isDeleted) {
                System.out.println("Épisode n°" + numeroEpisode + " de la Saison " + idSaison + " supprimé.");
            } else {
                System.out.println("Aucun épisode trouvé avec ces paramètres.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression : " + e.getMessage());
        }

        return isDeleted;
    }
}