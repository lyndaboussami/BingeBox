package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.services.FilmService;
import groupna.projectNetflix.utils.ConxDB;

public class DAOStatics {
	private static Connection conn = ConxDB.getInstance();
	private static FilmService films=new FilmService();
	public static java.util.Map<Categorie, Integer> getMoviesCountByCategory() {
	    java.util.Map<Categorie, Integer> stats = new java.util.HashMap<>();
	    String sql = "SELECT c.nom, COUNT(fc.id_film) as total " +
	                 "FROM categories c " +
	                 "LEFT JOIN film_categorie fc ON c.id = fc.id_categorie " +
	                 "GROUP BY c.nom";

	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            String nomCat = rs.getString("nom");
	            int count = rs.getInt("total");
	            try {
	                Categorie cat = Categorie.valueOf(nomCat.toUpperCase());
	                stats.put(cat, count);
	            } catch (IllegalArgumentException e) {
	                System.err.println("Catégorie inconnue dans l'Enum : " + nomCat);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return stats;
	}
	public static java.util.Map<Categorie, Integer> getSeriesCountByCategory() {
	    java.util.Map<Categorie, Integer> stats = new java.util.HashMap<>();
	    String sql = "SELECT c.nom, COUNT(sc.id_serie) as total " +
	                 "FROM categories c " +
	                 "LEFT JOIN serie_categorie sc ON c.id = sc.id_categorie " +
	                 "GROUP BY c.nom";

	    try (java.sql.Statement stmt = conn.createStatement();
	         java.sql.ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            String nomCat = rs.getString("nom");
	            int count = rs.getInt("total");
	            try {
	                Categorie cat = Categorie.valueOf(nomCat.toUpperCase());
	                stats.put(cat, count);
	            } catch (IllegalArgumentException e) {
	                System.err.println("Catégorie inconnue : " + nomCat);
	            }
	        }
	    } catch (java.sql.SQLException e) {
	        e.printStackTrace();
	    }
	    return stats;
	}
	public static Map<Film, Integer> getTop5MostViewed() {
	    Map<Film, Integer> top5 = new LinkedHashMap<>();
	    String sql = "SELECT id_oeuvre, COUNT(*) as nb_vues " +
	                 "FROM historique_film " +
	                 "GROUP BY id_oeuvre " +
	                 "ORDER BY nb_vues DESC " +
	                 "LIMIT 5";

	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            Film f = films.getFilmById(rs.getInt("id_oeuvre"));
	            if (f != null) {
	                top5.put(f, rs.getInt("nb_vues"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return top5;
	}
	public static Map<Film, Double> getTop5Rated() {
	    Map<Film, Double> top5 = new LinkedHashMap<>();
	    String sql = "SELECT id_film, AVG(note) as moyenne " +
	                 "FROM avis_film " +
	                 "GROUP BY id_film " +
	                 "ORDER BY moyenne DESC " +
	                 "LIMIT 5";

	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            Film f = films.getFilmById(rs.getInt("id_film"));
	            if (f != null) {
	                top5.put(f, rs.getDouble("moyenne"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return top5;
	}
}
