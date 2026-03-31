package groupna.projectNetflix.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groupna.projectNetflix.entities.Artiste;
import groupna.projectNetflix.entities.Categorie;
import groupna.projectNetflix.entities.Episode;
import groupna.projectNetflix.entities.Film;
import groupna.projectNetflix.entities.Saison;
import groupna.projectNetflix.entities.Serie;

public class DataStore {
	private static List<Film> movies = new ArrayList<>();
    private static List<Serie> series = new ArrayList<>();
    static {
    movies = List.of(
            new Film(1, 
            	    "A thief who steals corporate secrets through the use of dream-sharing technology.", 
            	    List.of(Categorie.SCIENCE_FICTION, Categorie.ACTION), 
            	    "Inception", LocalDate.of(2010, 7, 16), 
            	    List.of(new Artiste(1, "DiCaprio", "Leonardo", "Oscar-winning actor...", List.of("Titanic", "The Revenant"))), 
            	    List.of(new Artiste(2, "Nolan", "Christopher", "Visionary director...", List.of("Interstellar", "The Dark Knight"))),
            	    8.8, "/groupna/projectNetflix/assets/inception.jpg", 
            	    LocalTime.of(2, 28), "/groupna/projectNetflix/assets/inception.mp4", "/assets/inception_tr.mp4"),
            new Film(2, 
            	    "The lives of two mob hitmen, a boxer, and a gangster's wife intertwine.", 
            	    List.of(Categorie.POLICIER, Categorie.DRAME), 
            	    "Pulp Fiction", LocalDate.of(1994, 10, 14), 
            	    List.of(new Artiste(3, "Jackson", "Samuel L.", "Iconic actor...", List.of("The Avengers"))), 
            	    List.of(new Artiste(4, "Tarantino", "Quentin", "Stylistic director...", List.of("Kill Bill"))), 
            	    8.9, "/groupna/projectNetflix/assets/pulp.jpg", 
            	    LocalTime.of(2, 34), "/movies/pulp.mp4", "/trailers/pulp_tr.mp4"),
	        new Film(3, 
	        	    "MMA legend Patton James is pulled back into the cage when his brother is in danger.", 
	        	    List.of(Categorie.ACTION, Categorie.DRAME), 
	        	    "Beast", LocalDate.of(2026, 4, 10), 
	        	    List.of(new Artiste(5, "James", "Patton", "MMA fighter turned actor...", List.of("Cage Fight"))), 
	        	    List.of(new Artiste(6, "Grau", "Xavier", "Action director...", List.of("The Ring"))), 
	        	    7.5, "/groupna/projectNetflix/assets/beast.jpg", 
	        	    LocalTime.of(1, 53), "/movies/beast.mp4", "/groupna/projectNetflix/assets/Beast (2026) Movie Trailer.mp4")
	        );

        Map<Saison, List<Episode>> bbSaisons = new HashMap<>();
        List<Episode> s1Episodes = new ArrayList<>();

        Saison s1 = new Saison(
        	    1, 
        	    1, 
        	    LocalDate.of(2008, 1, 20), 
        	    "Season 1", 
        	    "High school chemistry teacher Walter White is diagnosed with inoperable lung cancer.", 
        	    "/trailers/bb_s1.mp4"
        );
        s1Episodes.add(new Episode(
            1, 
            101, 
            "Diagnosed with terminal lung cancer, a chemistry teacher teams up with a former student.", 
            "Pilot", 
            LocalTime.of(0, 58), 
            "/groupna/projectNetflix/assets/inception.mp4"
        ));

        s1Episodes.add(new Episode(
            2, 
            102, 
            "Walt and Jesse attempt to tie up loose ends, but things get messy.", 
            "Cat's in the Bag...", 
            LocalTime.of(0, 48), 
            "/episodes/bb_s1_e2.mp4"
        ));

        bbSaisons.put(s1, s1Episodes);
        
        series = List.of(
        		new Serie(1, 
        			    "A high school chemistry teacher turned crystal meth producer.", 
        			    List.of(Categorie.POLICIER, Categorie.DRAME), 
        			    "Breaking Bad", LocalDate.of(2008, 1, 20), 
        			    List.of(new Artiste(7, "Cranston", "Bryan", "Versatile actor...", List.of("Malcolm in the Middle"))), 
        			    List.of(new Artiste(8, "Gilligan", "Vince", "Creative writer...", List.of("Better Call Saul"))), 
        			    9.5, "/groupna/projectNetflix/assets/bb.jpg", 
        			    bbSaisons),
	            new Serie(2, 
	            	    "Ninety-seven years after a nuclear war, 100 juveniles are sent back to Earth.", 
	            	    List.of(Categorie.SCIENCE_FICTION, Categorie.AVENTURE), 
	            	    "The 100", LocalDate.of(2014, 3, 19), 
	            	    List.of(new Artiste(9, "Taylor", "Eliza", "Australian actress...", List.of("Neighbours"))), 
	            	    List.of(new Artiste(10, "Rothenberg", "Jason", "Producer...", List.of("Searchers"))), 
	            	    7.6, "/groupna/projectNetflix/assets/the100.jpg", 
	            	    new HashMap<>())
	            );
    }
    public static List<Film> getMovies() { return movies; }
    public static List<Serie> getSeries() { return series; }
	
}
