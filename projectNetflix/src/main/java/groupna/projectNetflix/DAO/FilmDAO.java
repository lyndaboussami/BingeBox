package groupna.projectNetflix.DAO;

import java.sql.Connection;
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
import groupna.projectNetflix.utils.ConxDB;
public class FilmDAO  extends OeuvreDAO{
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
	                double rate = rs.getDouble("rate");
	                String urlAnn = rs.getString("url_ann");
	                String urlVideo = rs.getString("url");
	                LocalDate dateSortie = (rs.getDate("date_sortie") != null) 
	                                       ? rs.getDate("date_sortie").toLocalDate() : null;
	                LocalTime duree = (rs.getTime("duree") != null) 
	                                  ? rs.getTime("duree").toLocalTime() : null;
	                List<Categorie> categories = getCategoriesByProduction(id, "film_categorie", "if_film");
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
	                    rate,
	                    urlAnn,
	                    duree,
	                    urlVideo
	                );
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return film;
	}
//-----------------------------------------------------------------------------------------------------
	public static int save(Film f) {
	    int generatedId = 0;
	    String sql = "INSERT INTO films (resume, titre, date_sortie, rate, url_ann, duree, url) VALUES (?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, f.getResume());
	        pstmt.setString(2, f.getTitre());
	        pstmt.setDate(3, f.getDateDeSortie() != null ? java.sql.Date.valueOf(f.getDateDeSortie()) : null);
	        pstmt.setDouble(4, f.getRate());
	        pstmt.setString(5, f.getURLann());
	        pstmt.setTime(6, f.getDuree() != null ? java.sql.Time.valueOf(f.getDuree()) : null);
	        pstmt.setString(7, f.getURL());

	        pstmt.executeUpdate();
	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                generatedId = rs.getInt(1);
	                f.setId(generatedId);
	            }
	        }
	        if (generatedId > 0) {
	            saveCategories(f.getId(),f.getCat(),"Film_categorie","id_film");
	            saveArtistes(f.getId(), f.getActeurs(), "Film_acteurs", "id_film");
	            saveArtistes(f.getId(), f.getDirecteurs(),"film_directeurs" , "id_film");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return generatedId;
	}
//------------------------------------------------------------------------------------------------------
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
//-----------------------------------------------------------------------------------------------------
	public static void saveAll(List<Film> films) {
	    if (films == null || films.isEmpty()) {
	        System.out.println("Aucun film à enregistrer.");
	        return;
	    }
	    for (Film f : films) {
	        try {
	            save(f); 
	        } catch (Exception e) {
	            System.err.println("Erreur lors de l'enregistrement de : " + f.getTitre());
	            e.printStackTrace();
	        }
	    }
	}
//-----------------------------------------------------------------------------------------------------------
	public static boolean delete(int filmId) {
	    boolean isDeleted = false;
	    String deleteCats = "DELETE FROM film_categorie WHERE id_film = ?";
	    String deleteActors = "DELETE FROM film_acteurs WHERE id_film = ?";
	    String deleteDirectors = "DELETE FROM film_directeurs WHERE id_film = ?";
	    String deleteFilm = "DELETE FROM films WHERE id = ?";

	    try {
	        try (PreparedStatement pstmt = conn.prepareStatement(deleteCats)) {
	            pstmt.setInt(1, filmId);
	            pstmt.executeUpdate();
	        }
	        try (PreparedStatement pstmt = conn.prepareStatement(deleteActors)) {
	            pstmt.setInt(1, filmId);
	            pstmt.executeUpdate();
	        }
	        try (PreparedStatement pstmt = conn.prepareStatement(deleteDirectors)) {
	            pstmt.setInt(1, filmId);
	            pstmt.executeUpdate();
	        
	        try (PreparedStatement pstmt1 = conn.prepareStatement(deleteFilm)) {
	            pstmt1.setInt(1, filmId);
	            int rowsAffected = pstmt1.executeUpdate();
	            isDeleted = (rowsAffected > 0);
	        }
	        }

	    } catch (SQLException e) {
	        System.err.println("Erreur lors de la suppression du film id : " + filmId);
	        e.printStackTrace();
	    }
	    
	    return isDeleted;
	    
	}
//--------------------------------------------------------------------------------------------
	
}
