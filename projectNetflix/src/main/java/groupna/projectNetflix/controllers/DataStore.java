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
           LocalTime.of(1, 53), "/movies/beast.mp4", "/trailers/beast_tr.mp4"),
       new Film(
           4,
           "A teenage hustler and a young man obsessed with alien abductions cross paths as they uncover a dark shared secret from their childhood.",
           List.of(Categorie.DRAME, Categorie.MYSTERE),
           "Mysterious Skin",
           java.time.LocalDate.of(2004, 9, 3),
           List.of(
               new Artiste(7, "Gordon-Levitt", "Joseph", "Versatile actor known for indie and blockbuster hits.", List.of("Inception", "500 Days of Summer")),
               new Artiste(8, "Corbet", "Brady", "Actor and director known for intense roles.", List.of("Thirteen", "Melancholia"))
           ),
           List.of(
               new Artiste(9, "Araki", "Gregg", "Iconic independent filmmaker.", List.of("The Doom Generation"))
           ),
           7.7,
           "/groupna/projectNetflix/assets/mysterious_skin.jpg",
           java.time.LocalTime.of(1, 42),
           "/movies/mysterious_skin.mp4",
           "/trailers/mysterious_skin_tr.mp4"
        ),
       new Film(
           5, // ID 5
           "A troubled young boy risks everything to help a captive killer whale escape from an amusement park and return to the ocean.",
           List.of(Categorie.FAMILLE, Categorie.AVENTURE),
           "Free Willy",
           java.time.LocalDate.of(1993, 7, 16),
           List.of(
               new Artiste(10, "Richter", "Jason James", "Lead child actor.", List.of("Free Willy 2", "The NeverEnding Story III")),
               new Artiste(11, "Madsen", "Michael", "Versatile character actor.", List.of("Reservoir Dogs", "Kill Bill"))
           ),
           List.of(
               new Artiste(12, "Wincer", "Simon", "Director known for family films.", List.of("The Phantom"))
           ),
           6.0,
           "/groupna/projectNetflix/assets/free_willy.jpeg",
           java.time.LocalTime.of(1, 52),
           "/movies/free_willy.mp4",
           "/trailers/free_willy_tr.mp4"
        ),
       new Film(
           6, // ID 6
           "An elderly man reads to a woman with Alzheimer's the story of two young lovers whose romance was stopped by their different social classes.",
           List.of(Categorie.DRAME, Categorie.ROMANCE),
           "The Notebook",
           java.time.LocalDate.of(2004, 6, 25),
           List.of(
               new Artiste(13, "Gosling", "Ryan", "Acclaimed actor known for romantic and dramatic roles.", List.of("La La Land", "Drive")),
               new Artiste(14, "McAdams", "Rachel", "Known for her leading roles in romance and comedy.", List.of("About Time", "Mean Girls"))
           ),
           List.of(
               new Artiste(15, "Cassavetes", "Nick", "Director specializing in emotional dramas.", List.of("My Sister's Keeper"))
           ),
           7.8,
           "/groupna/projectNetflix/assets/the_notebook.jpg",
           java.time.LocalTime.of(2, 3),
           "/movies/the_notebook.mp4",
           "/trailers/the_notebook_tr.mp4"
        ),
       new Film(
           8, // ID 8
           "A touching story where a girl's memories reset every night, and a boy dedicated to making her every day unforgettable.",
           List.of(Categorie.ROMANCE, Categorie.DRAME),
           "Even If This Love Disappears (KR)",
           java.time.LocalDate.of(2024, 5, 10),
           List.of(
               new Artiste(19, "Seon-ho", "Kim", "Popular Korean actor.", List.of("Start-Up")),
               new Artiste(20, "Youn-jung", "Go", "Rising star in K-Dramas.", List.of("Alchemy of Souls"))
           ),
           List.of(
               new Artiste(21, "Jin-young", "Lee", "Director known for visual storytelling.", List.of("Spring Waltz"))
           ),
           8.5,
           "/groupna/projectNetflix/assets/even if this love disappears from the world tonight.jpg",
           java.time.LocalTime.of(1, 58),
           "/movies/love_disappears_kr.mp4",
           "/trailers/love_disappears_kr_tr.mp4"
        ),
       new Film(
           10, // Unique ID
           "At the age of 21, Tim discovers he can travel in time and change what happens and has happened in his own life. His decision to make his world a better place by getting a girlfriend turns out not to be as easy as you might think.",
           List.of(Categorie.ROMANCE, Categorie.DRAME, Categorie.FANTASY),
           "About Time",
           java.time.LocalDate.of(2013, 9, 4),
           List.of(
               new Artiste(25, "Gleeson", "Domhnall", "Irish actor known for versatile roles.", List.of("Harry Potter", "Ex Machina")),
               new Artiste(26, "McAdams", "Rachel", "The queen of romantic dramas.", List.of("The Notebook", "Mean Girls")),
               new Artiste(27, "Nighy", "Bill", "Legendary British actor.", List.of("Love Actually"))
           ),
           List.of(
               new Artiste(28, "Curtis", "Richard", "Master of British romantic comedy.", List.of("Notting Hill", "Love Actually"))
           ),
           7.8,
           "/groupna/projectNetflix/assets/about_time.jpg",
           java.time.LocalTime.of(2, 3),
           "/movies/about_time.mp4",
           "/trailers/about_time_tr.mp4"
        ),
       new Film(
           11, // Unique ID
           "On his fifth wedding anniversary, Nick Dunne reports that his wife, Amy, has gone missing. Under pressure from the police and a growing media frenzy, Nick's portrait of a blissful union begins to crumble.",
           List.of(Categorie.THRILLER, Categorie.MYSTERE, Categorie.DRAME),
           "Gone Girl",
           java.time.LocalDate.of(2014, 10, 3),
           List.of(
               new Artiste(29, "Affleck", "Ben", "Plays the embattled husband Nick Dunne.", List.of("Argo", "Batman v Superman")),
               new Artiste(30, "Pike", "Rosamund", "Oscar-nominated for her role as 'Amazing Amy'.", List.of("I Care a Lot", "Pride & Prejudice")),
               new Artiste(31, "Harris", "Neil Patrick", "Plays the mysterious Desi Collings.", List.of("How I Met Your Mother"))
           ),
           List.of(
               new Artiste(32, "Fincher", "David", "Master of dark, psychological thrillers.", List.of("Seven", "The Social Network"))
           ),
           8.1,
           "/groupna/projectNetflix/assets/gone_girl.jpg",
           java.time.LocalTime.of(2, 29),
           "/movies/gone_girl.mp4",
           "/trailers/gone_girl_tr.mp4"
        ),
       new Film(
           13,
           "A charming story about a high school girl with messy hair and a messy heart who finds herself 'untangling' her feelings for a boy in her class.",
           List.of(Categorie.ROMANCE, Categorie.DRAME),
           "Love Untangled",
           java.time.LocalDate.of(2025, 8, 29),
           List.of(
               new Artiste(36, "Eun-soo", "Shin", "Talented actress known for her expressive roles.", List.of("Twinkling Watermelon")),
               new Artiste(37, "Unknown", "Lead", "Rising star in the Korean romantic scene.", List.of())
           ),
           List.of(
               new Artiste(38, "Netflix", "Director", "Creating modern Korean classics.", List.of())
           ),
           8.6,
           "/groupna/projectNetflix/assets/love_untangled.jpg", // Make sure to use this exact name
           java.time.LocalTime.of(1, 45),
           "/movies/love_untangled.mp4",
           "/trailers/love_untangled_tr.mp4"
        ),
       new Film(
           14, // ID 14
           "An advice columnist trying to push a guy away in 10 days meets an advertising executive who bets he can make any woman fall in love with him in the same amount of time.",
           List.of(Categorie.ROMANCE, Categorie.DRAME),
           "How to Lose a Guy in 10 Days",
           java.time.LocalDate.of(2003, 2, 7),
           List.of(
               new Artiste(39, "Hudson", "Kate", "The iconic Andie Anderson.", List.of("Almost Famous")),
               new Artiste(40, "McConaughey", "Matthew", "The charming Benjamin Barry.", List.of("Interstellar", "Dallas Buyers Club"))
           ),
           List.of(
               new Artiste(41, "Petrie", "Donald", "Director of classic comedies.", List.of("Miss Congeniality"))
           ),
           6.4,
           "/groupna/projectNetflix/assets/how to lose a guy in 10 days.jpg",
           java.time.LocalTime.of(1, 56),
           "/movies/how_to_lose.mp4",
           "/trailers/how_to_lose_tr.mp4"
        ),
       new Film(
           15, // ID 15
           "Elle Woods, a fashionable sorority queen, is dumped by her boyfriend. She decides to follow him to Harvard Law School to win him back, only to discover she has a natural talent for the law.",
           List.of(Categorie.DRAME, Categorie.ROMANCE),
           "Legally Blonde",
           java.time.LocalDate.of(2001, 7, 13),
           List.of(
               new Artiste(42, "Witherspoon", "Reese", "The unforgettable Elle Woods.", List.of("Big Little Lies", "Walk the Line")),
               new Artiste(43, "Wilson", "Luke", "Plays the supportive Emmett Richmond.", List.of("Old School"))
           ),
           List.of(
               new Artiste(44, "Luketic", "Robert", "Director of hit romantic comedies.", List.of("21", "The Ugly Truth"))
           ),
           6.4,
           "/groupna/projectNetflix/assets/legally_blonde.jpg",
           java.time.LocalTime.of(1, 36),
           "/movies/legally_blonde.mp4",
           "/trailers/legally_blonde_tr.mp4"
        )
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
        Map<Saison, List<Episode>> fpSaisons = new HashMap<>();

     // --- SEASON 1 ---
     List<Episode> fpS1Episodes = new ArrayList<>();
     Saison fpS1 = new Saison(2, 1, LocalDate.of(2023, 5, 31), "Season 1", "Camila creates a sexy profile on a dating app.", "/trailers/fp_s1.mp4");

     fpS1Episodes.add(new Episode(1, 1, "Camila meets her Prince Charming through a dating app.", "The Illusion", LocalTime.of(0, 45), "/episodes/fp_s1_e1.mp4"));
     fpS1Episodes.add(new Episode(2, 2, "Camila discovers Fernando is not who he says he is.", "Shadows", LocalTime.of(0, 47), "/episodes/fp_s1_e2.mp4"));
     fpS1Episodes.add(new Episode(3, 3, "Determined to find the truth, Camila begins her own investigation.", "The Trap", LocalTime.of(0, 42), "/episodes/fp_s1_e3.mp4"));
     fpS1Episodes.add(new Episode(4, 4, "A mysterious figure from Fernando's past appears.", "The Secret", LocalTime.of(0, 50), "/episodes/fp_s1_e4.mp4"));
     fpS1Episodes.add(new Episode(5, 5, "Camila realizes the danger she has put herself in.", "No Way Out", LocalTime.of(0, 48), "/episodes/fp_s1_e5.mp4"));
     fpS1Episodes.add(new Episode(6, 6, "Tensions rise as the lies become harder to maintain.", "Broken Trust", LocalTime.of(0, 45), "/episodes/fp_s1_e6.mp4"));
     fpS1Episodes.add(new Episode(7, 7, "A shocking revelation changes everything for Camila.", "The Reveal", LocalTime.of(0, 52), "/episodes/fp_s1_e7.mp4"));
     fpS1Episodes.add(new Episode(8, 8, "The true identity of the man behind the profile is found.", "Masks Off", LocalTime.of(0, 44), "/episodes/fp_s1_e8.mp4"));
     fpS1Episodes.add(new Episode(9, 9, "Camila plans her escape from the web of deceit.", "The Escape", LocalTime.of(0, 49), "/episodes/fp_s1_e9.mp4"));
     fpS1Episodes.add(new Episode(10, 10, "The final confrontation leads to a deadly conclusion.", "The End", LocalTime.of(0, 55), "/episodes/fp_s1_e10.mp4"));

     fpSaisons.put(fpS1, fpS1Episodes);
     
  // --- SEASON 2 ---
     List<Episode> fpS2Episodes = new ArrayList<>();
     Saison fpS2 = new Saison(3, 2, LocalDate.of(2024, 6, 15), "Season 2", "The web of lies expands.", "/trailers/fp_s2.mp4");

     fpS2Episodes.add(new Episode(11, 1, "Camila tries to restart her life but her past follows her.", "New Life", LocalTime.of(0, 50), "/episodes/fp_s2_e1.mp4"));
     fpS2Episodes.add(new Episode(12, 2, "A new profile emerges that looks eerily familiar.", "Double Trouble", LocalTime.of(0, 48), "/episodes/fp_s2_e2.mp4"));
     fpS2Episodes.add(new Episode(13, 3, "Old enemies return to settle their scores.", "Vendetta", LocalTime.of(0, 51), "/episodes/fp_s2_e3.mp4"));
     fpS2Episodes.add(new Episode(14, 4, "Camila finds an unexpected ally in her fight for the truth.", "Allies", LocalTime.of(0, 46), "/episodes/fp_s2_e4.mp4"));
     fpS2Episodes.add(new Episode(15, 5, "A high-stakes game of cat and mouse begins.", "The Game", LocalTime.of(0, 53), "/episodes/fp_s2_e5.mp4"));
     fpS2Episodes.add(new Episode(16, 6, "Secrets from the first season come back to haunt everyone.", "Haunted", LocalTime.of(0, 44), "/episodes/fp_s2_e6.mp4"));
     fpS2Episodes.add(new Episode(17, 7, "A betrayal from within the circle threatens everything.", "Betrayal", LocalTime.of(0, 47), "/episodes/fp_s2_e7.mp4"));
     fpS2Episodes.add(new Episode(18, 8, "The truth about the dating app company is uncovered.", "The Network", LocalTime.of(0, 50), "/episodes/fp_s2_e8.mp4"));
     fpS2Episodes.add(new Episode(19, 9, "Camila must make a sacrifice to protect those she loves.", "Sacrifice", LocalTime.of(0, 52), "/episodes/fp_s2_e9.mp4"));
     fpS2Episodes.add(new Episode(20, 10, "The final showdown leaves no one untouched.", "Final Justice", LocalTime.of(1, 00), "/episodes/fp_s2_e10.mp4"));

     fpSaisons.put(fpS2, fpS2Episodes);
     
  // --- What Comes After Love Data Setup ---
     Map<Saison, List<Episode>> wcalSaisons = new HashMap<>();

     // --- SEASON 1 ---
     List<Episode> wcalS1Episodes = new ArrayList<>();
     Saison wcalS1 = new Saison(
         4, // Unique ID for Season
         1,
         LocalDate.of(2024, 9, 27),
         "Season 1",
         "Five years after a heartbreaking breakup in Japan, Hong and Jung-go meet again in Korea.",
         "/trailers/wcal_s1.mp4"
     );

     wcalS1Episodes.add(new Episode(1, 1, "Hong arrives in Japan for study and meets the charming Jung-go.", "First Encounter", LocalTime.of(0, 55), "/episodes/wcal_s1_e1.mp4"));
     wcalS1Episodes.add(new Episode(2, 2, "The couple shares a beautiful spring together in Tokyo.", "Spring in Tokyo", LocalTime.of(0, 52), "/episodes/wcal_s1_e2.mp4"));
     wcalS1Episodes.add(new Episode(3, 3, "Misunderstandings begin to tear their young love apart.", "Fading Memories", LocalTime.of(0, 50), "/episodes/wcal_s1_e3.mp4"));
     wcalS1Episodes.add(new Episode(4, 4, "Five years later, Hong is shocked to see Jung-go at her workplace.", "The Reunion", LocalTime.of(0, 58), "/episodes/wcal_s1_e4.mp4"));
     wcalS1Episodes.add(new Episode(5, 5, "Hong struggles with the pain of the past while Jung-go tries to apologize.", "Words Unspoken", LocalTime.of(0, 54), "/episodes/wcal_s1_e5.mp4"));
     wcalS1Episodes.add(new Episode(6, 6, "Can love truly survive five years of silence?", "The Final Choice", LocalTime.of(1, 05), "/episodes/wcal_s1_e6.mp4"));

     wcalSaisons.put(wcalS1, wcalS1Episodes);
     
  // --- Castlevania: Nocturne Data Setup ---
     Map<Saison, List<Episode>> castlevaniaSaisons = new HashMap<>();

     // --- SEASON 1 (8 Episodes) ---
     List<Episode> nocturneS1Episodes = new ArrayList<>();
     Saison nocturneS1 = new Saison(5, 1, LocalDate.of(2023, 9, 28), "Season 1", "During the French Revolution, Richter Belmont fights to prevent the rise of a vampire messiah.", "/trailers/nocturne_s1.mp4");

     nocturneS1Episodes.add(new Episode(1, 1, "Richter Belmont meets Maria Renard amidst the chaos of the Revolution.", "A Shared Grief", LocalTime.of(0, 25), "/episodes/nocturne_s1_e1.mp4"));
     nocturneS1Episodes.add(new Episode(2, 2, "The origins of the Vampire Messiah are revealed through dark omens.", "The Last Hope", LocalTime.of(0, 27), "/episodes/nocturne_s1_e2.mp4"));
     nocturneS1Episodes.add(new Episode(3, 3, "Abbot Emanuel makes a dangerous pact to preserve the Church.", "The Night Creature", LocalTime.of(0, 30), "/episodes/nocturne_s1_e3.mp4"));
     nocturneS1Episodes.add(new Episode(4, 4, "Olrox makes his presence known, complicating Richter's mission.", "Horror Show", LocalTime.of(0, 28), "/episodes/nocturne_s1_e4.mp4"));
     nocturneS1Episodes.add(new Episode(5, 5, "Richter must confront the trauma of his mother's death to find his magic.", "Natural Born Viking", LocalTime.of(0, 29), "/episodes/nocturne_s1_e5.mp4"));
     nocturneS1Episodes.add(new Episode(6, 6, "The revolutionaries and hunters stage a desperate raid on the chateau.", "Guilty Instrument", LocalTime.of(0, 26), "/episodes/nocturne_s1_e6.mp4"));
     nocturneS1Episodes.add(new Episode(7, 7, "Erzsebet Báthory reveals her true power as the Sekhmet reincarnation.", "She is the Blood", LocalTime.of(0, 31), "/episodes/nocturne_s1_e7.mp4"));
     nocturneS1Episodes.add(new Episode(8, 8, "The heroes face a crushing defeat until a legendary ally returns.", "Devourer of Light", LocalTime.of(0, 35), "/episodes/nocturne_s1_e8.mp4"));

     castlevaniaSaisons.put(nocturneS1, nocturneS1Episodes);
     
  // --- SEASON 2 (8 Episodes) ---
     List<Episode> nocturneS2Episodes = new ArrayList<>();
     Saison nocturneS2 = new Saison(
         6,
         2,
         LocalDate.of(2025, 1, 16),
         "Season 2",
         "The fight against the Vampire Messiah reaches its climax as Alucard joins the fray.",
         "/trailers/nocturne_s2.mp4"
     );

     nocturneS2Episodes.add(new Episode(1, 1, "Alucard makes a stunning entrance to save Richter and Maria from the brink of defeat.", "The Son of Dracula", LocalTime.of(0, 32), "/episodes/nocturne_s2_e1.mp4"));
     nocturneS2Episodes.add(new Episode(2, 2, "The heroes retreat to a safe haven to plan their next move against Erzsebet.", "Shadow of the Castle", LocalTime.of(0, 28), "/episodes/nocturne_s2_e2.mp4"));
     nocturneS2Episodes.add(new Episode(3, 3, "Richter and Alucard clash over their different methods of hunting.", "Old Blood, New Steel", LocalTime.of(0, 30), "/episodes/nocturne_s2_e3.mp4"));
     nocturneS2Episodes.add(new Episode(4, 4, "Maria discovers a hidden power within her magic that could turn the tide.", "The Speaker's Legacy", LocalTime.of(0, 29), "/episodes/nocturne_s2_e4.mp4"));
     nocturneS2Episodes.add(new Episode(5, 5, "Erzsebet's army begins their final march across the French countryside.", "The Red Dawn", LocalTime.of(0, 31), "/episodes/nocturne_s2_e5.mp4"));
     nocturneS2Episodes.add(new Episode(6, 6, "A deep betrayal within the revolutionary ranks leaves the heroes vulnerable.", "Traitors in the Mist", LocalTime.of(0, 27), "/episodes/nocturne_s2_e6.mp4"));
     nocturneS2Episodes.add(new Episode(7, 7, "The solar eclipse begins, and the Vampire Messiah ascends to her full power.", "Eternal Eclipse", LocalTime.of(0, 35), "/episodes/nocturne_s2_e7.mp4"));
     nocturneS2Episodes.add(new Episode(8, 8, "The ultimate battle for the soul of France concludes in a tragic sacrifice.", "Nocturne of Fate", LocalTime.of(0, 40), "/episodes/nocturne_s2_e8.mp4"));

     castlevaniaSaisons.put(nocturneS2, nocturneS2Episodes);
     

       
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
               new HashMap<>()),
           new Serie(
                   3,
                   "A woman's search for her soulmate leads her to an online profile that turns out to be a dangerous trap.",
                   List.of(Categorie.DRAME, Categorie.POLICIER),
                   "Fake Profile",
                   LocalDate.of(2023, 5, 31),
                   List.of(new Artiste(45, "Tapan", "Carolina", "Lead actress.", List.of())),
                   List.of(new Artiste(46, "Guzmán", "Pablo", "Director.", List.of())),
                   7.0,
                   "/groupna/projectNetflix/assets/fake_profile.jpg",
                   fpSaisons
               ),
           new Serie(
               4,
               "A miracle of a story about a woman who has forgotten everything and a man who remembers everything.",
               List.of(Categorie.ROMANCE, Categorie.DRAME),
               "What Comes After Love",
               LocalDate.of(2024, 9, 27),
               List.of(
                   new Artiste(47, "Se-young", "Lee", "Korean actress.", List.of("The Red Sleeve")),
                   new Artiste(48, "Kentaro", "Sakaguchi", "Japanese actor.", List.of("Signal"))
               ),
               List.of(new Artiste(49, "Moon", "Hyun-sung", "Director.", List.of())),
               8.8,
               "/groupna/projectNetflix/assets/what_comes_after_love.jpg",
               wcalSaisons
            ),
           new Serie(
               5,
               "A gothic horror animation following Richter Belmont, a descendant of the legendary monster-hunting family.",
               List.of(Categorie.ACTION, Categorie.AVENTURE,Categorie.ANIME ),
               "Castlevania: Nocturne",
               LocalDate.of(2023, 9, 28),
               List.of(
                   new Artiste(50, "Belmont", "Richter", "Belmont clan successor.", List.of()),
                   new Artiste(51, "Alucard", "Adrian", "The Son of Dracula.", List.of())
               ),
               List.of(new Artiste(52, "Bradley", "Kevin", "Showrunner.", List.of())),
               8.2,
               "/groupna/projectNetflix/assets/castlevania_nocturne.jpg",
               castlevaniaSaisons
            )
           );
    }
    public static List<Film> getMovies() { return movies; }
    public static List<Serie> getSeries() { return series; }

}

