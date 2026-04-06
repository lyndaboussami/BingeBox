package groupna.projectNetflix.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;

public class FilmDAO extends OeuvreDAO {
    public static Film findById(int id) {
        Film film = null;
        String sql = "SELECT * FROM films WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int filmId = rs.getInt("id");
                    String resume = rs.getString("resume");
                    String titre = rs.getString("titre");
                    String pathTrailer = rs.getString("path_trailer"); 
                    String pathMovie = rs.getString("path_movie");
                    String pathPoster = rs.getString("path_poster"); 
                    LocalDate dateSortie = (rs.getDate("date_de_sortie") != null) 
                                           ? rs.getDate("date_de_sortie").toLocalDate() : null;
                    LocalTime duree = (rs.getTime("duree") != null) 
                                      ? rs.getTime("duree").toLocalTime() : null;

                    List<Categorie> categories = getCategoriesByProduction(id, "film_categorie", "id_film");
                    List<Artiste> acteurs = getArtistes(id, "film_acteurs", "id_film");
                    List<Artiste> directeurs = getArtistes(id, "film_directeurs", "id_film");
                    film = new Film(
                        filmId,
                        resume,
                        categories,
                        titre,
                        dateSortie,
                        acteurs,
                        directeurs,
                        pathPoster,
                        duree,
                        pathMovie,
                        pathTrailer
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return film;
    }

    public static int save(Film f) {
        int generatedId = 0;
        String sql = "INSERT INTO films (resume, titre, date_de_sortie, path_poster, duree, path_movie, path_trailer) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, f.getResume());
            pstmt.setString(2, f.getTitre());
            pstmt.setDate(3, f.getDateDeSortie() != null ? java.sql.Date.valueOf(f.getDateDeSortie()) : null);
            pstmt.setString(4, f.getPathPoster());
            pstmt.setTime(5, f.getDuree() != null ? java.sql.Time.valueOf(f.getDuree()) : null);
            pstmt.setString(6, f.getPathMovie());
            pstmt.setString(7, f.getPathTrailer());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    f.setId(generatedId);
                }
            }
            
            if (generatedId > 0) {
                saveCategories(f.getId(), f.getCat(), "film_categorie", "id_film");
                saveArtistes(f.getId(), f.getActeurs(), "film_acteurs", "id_film");
                saveArtistes(f.getId(), f.getDirecteurs(), "film_directeurs", "id_film");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public static List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT id FROM films";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Film f = findById(id);
                if (f != null) {
                    films.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    public static void saveAll(List<Film> films) {
        if (films == null || films.isEmpty()) return;
        for (Film f : films) {
            save(f);
        }
    }
    public static boolean delete(int filmId) {
        boolean isDeleted = false;
        String sqlDelete = "DELETE FROM films WHERE id = ?";

        try {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
                
                pstmt.setInt(1, filmId);
                
                int rowsAffected = pstmt.executeUpdate();
                isDeleted = (rowsAffected > 0);
                
                if (isDeleted) {
                    System.out.println("Le film avec l'ID " + filmId + " a été supprimé avec succès.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du film : " + e.getMessage());
            e.printStackTrace();
        }
        
        return isDeleted;
    }
    public static int getNombreVuesFilm(int idFilm) {
        int totalVues = 0;
        String sql = "SELECT COUNT(*) FROM historique_film WHERE id_oeuvre = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFilm);
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
    public static void update(Film f) {
        String sql = "UPDATE films SET resume = ?, titre = ?, date_de_sortie = ?, path_poster = ?, duree = ?, path_movie = ?, path_trailer = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getResume());
            pstmt.setString(2, f.getTitre());
            pstmt.setDate(3, f.getDateDeSortie() != null ? java.sql.Date.valueOf(f.getDateDeSortie()) : null);
            pstmt.setString(4, f.getPathPoster());
            pstmt.setTime(5, f.getDuree() != null ? java.sql.Time.valueOf(f.getDuree()) : null);
            pstmt.setString(6, f.getPathMovie());
            pstmt.setString(7, f.getPathTrailer());
            pstmt.setInt(8, f.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}