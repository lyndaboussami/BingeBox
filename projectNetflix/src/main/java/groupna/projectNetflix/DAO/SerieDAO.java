package groupna.projectNetflix.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.entities.saison;

public class SerieDAO extends OeuvreDAO {
    public static Serie findById(int id) {
        Serie serie = null;
        String sql = "SELECT * FROM series WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int serieId = rs.getInt("id");
                    String resume = rs.getString("resume");
                    String titre = rs.getString("titre");
                    double rate = rs.getDouble("rate");
                    String urlAnn = rs.getString("url_ann");
                    LocalDate dateSortie = (rs.getDate("date_sortie") != null) 
                                           ? rs.getDate("date_sortie").toLocalDate() : null;
                    List<Categorie> categories = getCategoriesByProduction(id, "serie_categorie", "id_serie");
                    List<Artiste> acteurs = getArtistes(id, "serie_acteurs", "id_serie");
                    List<Artiste> directeurs = getArtistes(id, "serie_directeurs", "id_serie");
                    Map<saison, List<Episode>> MapSaisons = new LinkedHashMap<>();
                    List<saison> listeSaisons = SaisonDAO.findAllBySerie(id);

                    for (saison s : listeSaisons) {
                        List<Episode> episodes = EpisodeDAO.getEpisodesBySaison(s.getId());
                        MapSaisons.put(s, episodes);
                    }

                    serie = new Serie(
                        serieId, resume, categories, titre, dateSortie, 
                        acteurs, directeurs, rate, urlAnn, MapSaisons
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serie;
    }
    public static int save(Serie s) {
        int generatedId = 0;
        String sql = "INSERT INTO series (resume, titre, date_sortie, rate, url_ann) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, s.getResume());
            pstmt.setString(2, s.getTitre());
            pstmt.setDate(3, s.getDateDeSortie() != null ? java.sql.Date.valueOf(s.getDateDeSortie()) : null);
            pstmt.setDouble(4, s.getRate());
            pstmt.setString(5, s.getURLann());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    s.setId(generatedId);
                }
            }

            if (generatedId > 0) {
                saveCategories(s.getId(), s.getCat(), "serie_categorie", "id_serie");
                saveArtistes(s.getId(), s.getActeurs(), "serie_acteurs", "id_serie");
                saveArtistes(s.getId(), s.getDirecteurs(), "serie_directeurs", "id_serie");
                if (s.getSaisons() != null) {
                    for (Map.Entry<saison, List<Episode>> entry : s.getSaisons().entrySet()) {
                        saison saisinObj = entry.getKey();
                        List<Episode> episodes = entry.getValue();
                        SaisonDAO.save(saisinObj, generatedId);
                        for (Episode ep : episodes) {
                            EpisodeDAO.addEpisode(ep, saisinObj.getId());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }
    public static List<Serie> findAll() {
        List<Serie> series = new ArrayList<>();
        String sql = "SELECT id FROM series";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Serie s = findById(rs.getInt("id"));
                if (s != null) series.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return series;
    }
    public static boolean delete(int serieId) {
        String deleteCats = "DELETE FROM serie_categorie WHERE id_serie = ?";
        String deleteActors = "DELETE FROM serie_acteurs WHERE id_serie = ?";
        String deleteDirectors = "DELETE FROM serie_directeurs WHERE id_serie = ?";
        String deleteSaisons = "DELETE FROM saisons WHERE id_serie = ?";
        String deleteSerie = "DELETE FROM series WHERE id = ?";

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(deleteCats)) {
                pstmt.setInt(1, serieId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteActors)) {
                pstmt.setInt(1, serieId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteDirectors)) {
                pstmt.setInt(1, serieId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSaisons)) {
                pstmt.setInt(1, serieId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSerie)) {
                pstmt.setInt(1, serieId);
                int rowsAffected = pstmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}