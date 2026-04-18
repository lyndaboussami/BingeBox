package groupna.projectNetflix.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Serie;
import groupna.projectNetflix.services.FilmService;
import groupna.projectNetflix.services.SerieService;
import groupna.projectNetflix.utils.ConxDB;

public class DAOStatics {
	private static Connection conn = ConxDB.getInstance();
	private static FilmService films=new FilmService();
	private static SerieService series=new SerieService();

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
	            	e.getMessage();
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
	            	e.getMessage();
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
	    String sql = "SELECT id_film, AVG(nbStars) as moyenne " +
	                 "FROM rate_film " + 
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
	
	public static int getTotalMovies() {
	    String sql = "SELECT COUNT(*) FROM films";
	    try (Statement stmt = conn.createStatement();
		         ResultSet rs = stmt.executeQuery(sql)) {
	    	
	        if (rs.next()) return rs.getInt(1);
	    } catch (SQLException e) { e.printStackTrace(); }
	    return 0;
	}

	public static int getTotalSeries() {
	    String sql = "SELECT COUNT(*) FROM series";
	    try (Statement stmt = conn.createStatement();
		         ResultSet rs = stmt.executeQuery(sql)) {
	    	
	        if (rs.next()) return rs.getInt(1);
	    } catch (SQLException e) { e.printStackTrace(); }
	    return 0;
	}

	public static int getTotalUsers() {
	    String sql = "SELECT COUNT(*) FROM user";
	    try (Statement stmt = conn.createStatement();
		         ResultSet rs = stmt.executeQuery(sql)) {
	    	
	        if (rs.next()) return rs.getInt(1);
	    } catch (SQLException e) { e.printStackTrace(); }
	    return 0;
	}
	
	public static Map<LocalDate, Integer> getStatsLoginsSeptDerniersJours() {
        Map<LocalDate, Integer> stats = new TreeMap<>();
        String sql = "SELECT date, nbLogins FROM LoginPerDay " +
                     "WHERE date >= CURRENT_DATE - INTERVAL 7 DAY " +
                     "ORDER BY date ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                int nbLogins = rs.getInt("nbLogins");
                
                stats.put(date, nbLogins);
            }

        } catch (SQLException e) {
        	e.getMessage();
        }

        return stats;
    }
	
	public static Map<Serie, Double> getTop5RatedSeries() {
	    Map<Serie, Double> top5 = new LinkedHashMap<>();
	    String sql = "SELECT id_serie, AVG(nbStars) as moyenne " +
	                 "FROM rate_serie " + 
	                 "GROUP BY id_serie " +
	                 "ORDER BY moyenne DESC " +
	                 "LIMIT 5";

	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            Serie s = series.getSerieById(rs.getInt("id_serie"));
	            if (s != null) {
	                top5.put(s, rs.getDouble("moyenne"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return top5;
	}
}
