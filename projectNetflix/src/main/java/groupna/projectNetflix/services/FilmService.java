package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.FilmDAO;
import groupna.projectNetflix.entities.Film;

public class FilmService {

    public Film getFilmById(int id) {
        if (id <= 0) return null;
        return FilmDAO.findById(id);
    }
    public List<Film> getAllFilms() {
        return FilmDAO.findAll();
    }
    public boolean addFilm(Film film) {
        if (film == null || film.getTitre() == null || film.getTitre().isEmpty()) {
            return false;
        }
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
        FilmDAO.save(film);
        return film.getId() > 0;
    }
    public void saveMultipleFilms(List<Film> films) {
        if (films != null && !films.isEmpty()) {
            FilmDAO.saveAll(films);
        }
    }
    public boolean deleteFilm(int id) {
        if (id <= 0) return false;
        return FilmDAO.delete(id);
    }
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
    public void updateFilm(Film f) {
        if (f != null && f.getId() > 0) {
            FilmDAO.update(f);
        }
    }
}