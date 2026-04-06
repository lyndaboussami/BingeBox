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
import groupna.projectNetflix.entities.Saison;

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
                    String pathPoster = rs.getString("path_poster"); 
                    
                    LocalDate dateSortie = (rs.getDate("date_de_sortie") != null) 
                                           ? rs.getDate("date_de_sortie").toLocalDate() : null;

                    List<Categorie> categories = getCategoriesByProduction(id, "serie_categorie", "id_serie");
                    List<Artiste> acteurs = getArtistes(id, "serie_acteurs", "id_serie");
                    List<Artiste> directeurs = getArtistes(id, "serie_directeurs", "id_serie");

                    Map<Saison, List<Episode>> mapSaisons = new LinkedHashMap<>();
                    List<Saison> listeSaisons = SaisonDAO.findAllBySerie(id);

                    for (Saison s : listeSaisons) {
                        List<Episode> episodes = EpisodeDAO.getEpisodesBySaison(s.getId());
                        mapSaisons.put(s, episodes);
                    }
                    serie = new Serie(
                        serieId, resume, categories, titre, dateSortie, 
                        acteurs, directeurs, pathPoster, mapSaisons
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
        String sql = "INSERT INTO series (resume, titre, date_de_sortie, path_poster) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, s.getResume());
            pstmt.setString(2, s.getTitre());
            pstmt.setDate(3, s.getDateDeSortie() != null ? java.sql.Date.valueOf(s.getDateDeSortie()) : null);
            pstmt.setString(4, s.getPathPoster());

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
                    for (Map.Entry<Saison, List<Episode>> entry : s.getSaisons().entrySet()) {
                        Saison saisonObj = entry.getKey();
                        List<Episode> episodes = entry.getValue();
                        
                        int id=SaisonDAO.save(saisonObj, generatedId);
                        
                        if (episodes != null) {
                            for (Episode ep : episodes) {
                                EpisodeDAO.addEpisode(ep, id);
                            }
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
        String sql = "DELETE FROM series WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serieId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void update(Serie s) {
        String sql = "UPDATE series SET resume = ?, titre = ?, date_de_sortie = ?, path_poster = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getResume());
            pstmt.setString(2, s.getTitre());
            pstmt.setDate(3, s.getDateDeSortie() != null ? java.sql.Date.valueOf(s.getDateDeSortie()) : null);
            pstmt.setString(4, s.getPathPoster());
            pstmt.setInt(5, s.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}