package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.FilmDAO;
import groupna.projectNetflix.entities.Film;

public class FilmService {

    /**
     * Récupère un film par son identifiant.
     */
    public Film getFilmById(int id) {
        if (id <= 0) return null;
        return FilmDAO.findById(id);
    }

    /**
     * Retourne la liste de tous les films en base.
     */
    public List<Film> getAllFilms() {
        return FilmDAO.findAll();
    }

    /**
     * Ajoute un film s'il n'existe pas déjà.
     * @return true si l'opération a réussi (ou si le film existe déjà avec un ID valide).
     */
    public boolean addFilm(Film film) {
        if (film == null || film.getTitre() == null || film.getTitre().isEmpty()) {
            return false;
        }

        // Vérification de doublon via la méthode héritée de OeuvreDAO
        int existingId = FilmDAO.findIDifExisted(
            film.getTitre(), 
            film.getDateDeSortie().toString(), 
            film.getResume(), 
            "films"
        );

        if (existingId != -1) {
            film.setId(existingId);
            System.out.println("[Service] Le film existe déjà (ID: " + existingId + ")");
            return true; 
        }

        // Sauvegarde (La méthode save de FilmDAO gère l'insertion et les clés générées)
        FilmDAO.save(film);
        
        // On vérifie si l'ID a bien été injecté dans l'objet par le DAO
        return film.getId() > 0;
    }

    /**
     * Sauvegarde une liste de films.
     */
    public void saveMultipleFilms(List<Film> films) {
        if (films != null && !films.isEmpty()) {
            FilmDAO.saveAll(films);
        }
    }

    /**
     * Supprime un film par son ID.
     */
    public boolean deleteFilm(int id) {
        if (id <= 0) return false;
        // Le DAO retourne déjà un boolean pour indiquer si la suppression a eu lieu
        return FilmDAO.delete(id);
    }

    /**
     * Filtre les films par mot-clé dans le titre.
     */
    public List<Film> filterFilmsByTitle(String keyword) {
        if (keyword == null || keyword.isEmpty()) return getAllFilms();
        
        List<Film> allFilms = FilmDAO.findAll();
        return allFilms.stream()
                .filter(f -> f.getTitre() != null && 
                             f.getTitre().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
    public int getVues(int idFilm) {
        return FilmDAO.getNombreVuesFilm(idFilm);
    }
    
}