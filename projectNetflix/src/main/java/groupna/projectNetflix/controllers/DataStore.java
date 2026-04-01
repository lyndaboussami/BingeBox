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
               List.of(new Artiste(1, "Leonardo DiCaprio")),
               List.of(new Artiste(2, "Christopher Nolan")),
                "/groupna/projectNetflix/assets/inception.jpg",
               LocalTime.of(2, 28), "/groupna/projectNetflix/assets/Beast (2026) Movie Trailer.mp4", "/assets/inception_tr.mp4"),
           
            new Film(2,
               "The lives of two mob hitmen, a boxer, and a gangster's wife intertwine.",
               List.of(Categorie.POLICIER, Categorie.DRAME),
               "Pulp Fiction", LocalDate.of(1994, 10, 14),
               List.of(new Artiste(3, "Samuel L. Jackson")),
               List.of(new Artiste(4, "Quentin Tarantino")),
                "/groupna/projectNetflix/assets/pulp.jpg",
               LocalTime.of(2, 34), "/movies/pulp.mp4", "/trailers/pulp_tr.mp4"),

            new Film(3,
               "MMA legend Patton James is pulled back into the cage when his brother is in danger.",
               List.of(Categorie.ACTION, Categorie.DRAME),
               "Beast", LocalDate.of(2026, 4, 10),
               List.of(new Artiste(5, "Patton James")),
               List.of(new Artiste(6, "Xavier Grau")),
                "/groupna/projectNetflix/assets/beast.jpg",
               LocalTime.of(1, 53), "/groupna/projectNetflix/assets/Beast (2026) Movie Trailer.mp4", "/groupna/projectNetflix/assets/Beast (2026) Movie Trailer.mp4"),

            new Film(4,
               "A teenage hustler and a young man obsessed with alien abductions cross paths.",
               List.of(Categorie.DRAME, Categorie.MYSTERE),
               "Mysterious Skin", LocalDate.of(2004, 9, 3),
               List.of(new Artiste(7, "Joseph Gordon-Levitt"), new Artiste(8, "Brady Corbet")),
               List.of(new Artiste(9, "Gregg Araki")),
                "/groupna/projectNetflix/assets/mysterious_skin.jpg",
               LocalTime.of(1, 42), "/movies/mysterious_skin.mp4", "/trailers/mysterious_skin_tr.mp4"),

            new Film(5,
               "A troubled young boy risks everything to help a captive killer whale escape.",
               List.of(Categorie.FAMILLE, Categorie.AVENTURE),
               "Free Willy", LocalDate.of(1993, 7, 16),
               List.of(new Artiste(10, "Jason James Richter"), new Artiste(11, "Michael Madsen")),
               List.of(new Artiste(12, "Simon Wincer")),
                "/groupna/projectNetflix/assets/free_willy.jpeg",
               LocalTime.of(1, 52), "/movies/free_willy.mp4", "/trailers/free_willy_tr.mp4"),

            new Film(6,
               "An elderly man reads to a woman with Alzheimer's the story of two young lovers.",
               List.of(Categorie.DRAME, Categorie.ROMANCE),
               "The Notebook", LocalDate.of(2004, 6, 25),
               List.of(new Artiste(13, "Ryan Gosling"), new Artiste(14, "Rachel McAdams")),
               List.of(new Artiste(15, "Nick Cassavetes")),
                "/groupna/projectNetflix/assets/the_notebook.jpg",
               LocalTime.of(2, 3), "/movies/the_notebook.mp4", "/trailers/the_notebook_tr.mp4"),

            new Film(8,
               "A touching story where a girl's memories reset every night.",
               List.of(Categorie.ROMANCE, Categorie.DRAME),
               "Even If This Love Disappears (KR)", LocalDate.of(2024, 5, 10),
               List.of(new Artiste(19, "Kim Seon-ho"), new Artiste(20, "Go Youn-jung")),
               List.of(new Artiste(21, "Lee Jin-young")),
                "/groupna/projectNetflix/assets/even if this love disappears from the world tonight.jpg",
               LocalTime.of(1, 58), "C:/Users/benha/Downloads/love_disappears_kr.mp4", "/trailers/love_disappears_kr_tr.mp4"),

            new Film(10,
               "At the age of 21, Tim discovers he can travel in time.",
               List.of(Categorie.ROMANCE, Categorie.DRAME, Categorie.FANTASY),
               "About Time", LocalDate.of(2013, 9, 4),
               List.of(new Artiste(25, "Domhnall Gleeson"), new Artiste(26, "Rachel McAdams"), new Artiste(27, "Bill Nighy")),
               List.of(new Artiste(28, "Richard Curtis")),
                "/groupna/projectNetflix/assets/about_time.jpg",
               LocalTime.of(2, 3), "/movies/about_time.mp4", "/trailers/about_time_tr.mp4"),

            new Film(11,
               "On his fifth wedding anniversary, Nick Dunne reports that his wife has gone missing.",
               List.of(Categorie.THRILLER, Categorie.MYSTERE, Categorie.DRAME),
               "Gone Girl", LocalDate.of(2014, 10, 3),
               List.of(new Artiste(29, "Ben Affleck"), new Artiste(30, "Rosamund Pike"), new Artiste(31, "Neil Patrick Harris")),
               List.of(new Artiste(32, "David Fincher")),
                "/groupna/projectNetflix/assets/gone_girl.jpg",
               LocalTime.of(2, 29), "/movies/gone_girl.mp4", "/trailers/gone_girl_tr.mp4"),

            new Film(13,
               "A high school girl finds herself untangling her feelings for a boy in her class.",
               List.of(Categorie.ROMANCE, Categorie.DRAME),
               "Love Untangled", LocalDate.of(2025, 8, 29),
               List.of(new Artiste(36, "Shin Eun-soo")),
               List.of(new Artiste(38, "Netflix Director")),
                "/groupna/projectNetflix/assets/love_untangled.jpg",
               LocalTime.of(1, 45), "/movies/love_untangled.mp4", "/trailers/love_untangled_tr.mp4"),

            new Film(14,
               "An advice columnist meets an advertising executive who bets he can make any woman fall in love.",
               List.of(Categorie.ROMANCE, Categorie.DRAME),
               "How to Lose a Guy in 10 Days", LocalDate.of(2003, 2, 7),
               List.of(new Artiste(39, "Kate Hudson"), new Artiste(40, "Matthew McConaughey")),
               List.of(new Artiste(41, "Donald Petrie")),
                "/groupna/projectNetflix/assets/how to lose a guy in 10 days.jpg",
               LocalTime.of(1, 56), "/movies/how_to_lose.mp4", "/trailers/how_to_lose_tr.mp4"),

            new Film(15,
               "Elle Woods, a fashionable sorority queen, decides to follow her ex to Harvard Law School.",
               List.of(Categorie.DRAME, Categorie.ROMANCE),
               "Legally Blonde", LocalDate.of(2001, 7, 13),
               List.of(new Artiste(42, "Reese Witherspoon"), new Artiste(43, "Luke Wilson")),
               List.of(new Artiste(44, "Robert Luketic")),
                "/groupna/projectNetflix/assets/legally_blonde.jpg",
               LocalTime.of(1, 36), "/movies/legally_blonde.mp4", "/trailers/legally_blonde_tr.mp4")
        );

        // --- SEASONS & SERIES ---
        Map<Saison, List<Episode>> bbSaisons = new HashMap<>();
        List<Episode> s1Episodes = new ArrayList<>();
        Saison s1 = new Saison(1, 1, LocalDate.of(2008, 1, 20), "Season 1", "Walter White starts cooking.", "/trailers/bb_s1.mp4");
        s1Episodes.add(new Episode(1, 101, "Pilot episode.", "Pilot", LocalTime.of(0, 58), "/groupna/projectNetflix/assets/inception.mp4"));
        bbSaisons.put(s1, s1Episodes);

        Map<Saison, List<Episode>> fpSaisons = new HashMap<>();
        Saison fpS1 = new Saison(2, 1, LocalDate.of(2023, 5, 31), "Season 1", "Camila creates a sexy profile.", "/trailers/fp_s1.mp4");
        fpSaisons.put(fpS1, new ArrayList<>()); // Simplify for brevity

        series = List.of(
            new Serie(1,
               "A high school chemistry teacher turned crystal meth producer.",
               List.of(Categorie.POLICIER, Categorie.DRAME),
               "Breaking Bad", LocalDate.of(2008, 1, 20),
               List.of(new Artiste(7, "Bryan Cranston")),
               List.of(new Artiste(8, "Vince Gilligan")),
                "/groupna/projectNetflix/assets/bb.jpg",
               bbSaisons),
           
            new Serie(2,
               "Ninety-seven years after a nuclear war, 100 juveniles are sent back to Earth.",
               List.of(Categorie.SCIENCE_FICTION, Categorie.AVENTURE),
               "The 100", LocalDate.of(2014, 3, 19),
               List.of(new Artiste(9, "Eliza Taylor")),
               List.of(new Artiste(10, "Jason Rothenberg")),
                "/groupna/projectNetflix/assets/the100.jpg",
               new HashMap<>()),

            new Serie(3,
               "A woman's search for her soulmate leads her to a dangerous trap.",
               List.of(Categorie.DRAME, Categorie.POLICIER),
               "Fake Profile", LocalDate.of(2023, 5, 31),
               List.of(new Artiste(45, "Carolina Tapan")),
               List.of(new Artiste(46, "Pablo Guzmán")),
                "/groupna/projectNetflix/assets/fake_profile.jpg",
               fpSaisons),

            new Serie(4,
               "A woman who has forgotten everything and a man who remembers everything.",
               List.of(Categorie.ROMANCE, Categorie.DRAME),
               "What Comes After Love", LocalDate.of(2024, 9, 27),
               List.of(new Artiste(47, "Lee Se-young"), new Artiste(48, "Sakaguchi Kentaro")),
               List.of(new Artiste(49, "Moon Hyun-sung")),
                "/groupna/projectNetflix/assets/what_comes_after_love.jpg",
               new HashMap<>()),

            new Serie(5,
               "A gothic horror animation following Richter Belmont.",
               List.of(Categorie.ACTION, Categorie.AVENTURE, Categorie.ANIME),
               "Castlevania: Nocturne", LocalDate.of(2023, 9, 28),
               List.of(new Artiste(50, "Richter Belmont"), new Artiste(51, "Alucard")),
               List.of(new Artiste(52, "Kevin Bradley"))
               , "/groupna/projectNetflix/assets/castlevania_nocturne.jpg",
               new HashMap<>())
        );
    }

    public static List<Film> getMovies() { return movies; }
    public static List<Serie> getSeries() { return series; }
}