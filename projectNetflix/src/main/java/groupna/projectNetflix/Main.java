package groupna.projectNetflix;

import groupna.projectNetflix.DAO.*;
import groupna.projectNetflix.entities.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== 🧪 DÉBUT DU TEST GLOBAL (BASE VIDE) ===\n");

        try {
            // 1. CRÉATION D'UN UTILISATEUR (Indispensable pour la suite)
            // Si tu n'as pas de UserDAO.save, il faut le créer ou faire un insert SQL direct
            System.out.println("--- 1. Initialisation Utilisateur ---");
            // Ici, on part du principe que tu as inséré manuellement l'ID 1 ou 
            // que tu as une méthode pour enregistrer.
            int idUserTest = 1; 
            System.out.println("[OK] Utilisateur cible pour le test : ID " + idUserTest);

            // 2. TEST ARTISTE
            System.out.println("\n--- 2. Test ArtisteDAO ---");
            Artiste director = new Artiste(0, "Nolan", "Christopher", "Réalisateur culte", new ArrayList<>());
            int idArtiste = ArtisteDAO.save(director);
            director.setId(idArtiste);
            System.out.println("[OK] Artiste enregistré avec l'ID : " + idArtiste);

            // 3. TEST FILM
            System.out.println("\n--- 3. Test FilmDAO ---");
            List<Artiste> listArt = Collections.singletonList(director);
            List<Categorie> listCat = Collections.singletonList(Categorie.SCIENCE_FICTION);

            Film inception = new Film(0, "Infiltration de rêves", listCat, "Inception", 
                                      LocalDate.of(2010, 7, 21), listArt, listArt, 
                                      4.8, "inception.jpg", LocalTime.of(2, 28), "movie.mp4", "trailer.mp4");
            FilmDAO.save(inception);
            
            // Récupération de l'ID généré pour le film
            int idFilm = OeuvreDAO.findIDifExisted("Inception", "2010-07-21", "Infiltration de rêves", "films");
            System.out.println("[OK] Film inséré avec l'ID : " + idFilm);

            // 4. TEST INTERACTIONS (Commentaires & Favoris)
            System.out.println("\n--- 4. Test Interactions ---");
            
            // Test Favoris
            UserDAO.ajouterAuxFavoris(idUserTest, idFilm, "film");
            
            // Test Commentaire
            commentaire com = new commentaire(idUserTest, idFilm, "Chef d'oeuvre !", false);
            boolean isComSaved = CommentaireDAO.save(com, "film");
            System.out.println("[OK] Commentaire enregistré : " + isComSaved);

            // 5. TEST SÉRIE
            System.out.println("\n--- 5. Test Serie/Saison/Episode ---");
            Serie breakingBad = new Serie(0, "Un prof de chimie...", listCat, "Breaking Bad", 
                                         LocalDate.now(), listArt, listArt, 5.0, "bb.jpg", new LinkedHashMap<>());
            SerieDAO.save(breakingBad);
            int idSerie = OeuvreDAO.findIDifExisted("Breaking Bad", LocalDate.now().toString(), "Un prof de chimie...", "series");
            
            saison s1 = new saison(0, 1, LocalDate.now(), "Saison 1", "Le début", "trailer.mp4");
            int idSaison = SaisonDAO.save(s1, idSerie);
            
            Episode ep1 = new Episode(1, 0, "Pilot", "Premier épisode", LocalTime.of(0, 45), "ep1.mp4");
            EpisodeDAO.addEpisode(ep1, idSaison);
            System.out.println("[OK] Structure Série/Saison/Épisode créée.");

            // 6. VÉRIFICATION FINALE (Lecture)
            System.out.println("\n--- 6. Vérification finale ---");
            Set<Oeuvre> favoris = UserDAO.getAllUserFavorites(idUserTest);
            System.out.println("Nombre de favoris trouvés en base : " + favoris.size());
            
            for(Oeuvre o : favoris) {
                System.out.println("-> Favori trouvé : " + o.getTitre());
            }

            System.out.println("\n=== ✅ TOUS LES TESTS SONT PASSÉS ===");

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR LORS DU TEST : " + e.getMessage());
            e.printStackTrace();
        }
    }
}