package groupna.projectNetflix.services;

import java.util.List;
import groupna.projectNetflix.DAO.FilmDAO;
import groupna.projectNetflix.entities.Film;

public class FilmService {

    public Film getFilmById(int id) {
        if (id <= 0) return null;
        return FilmDAO.findById(id);
    }

    // -------------------------------------------------------------------------

    public List<Film> getAllFilms() {
        return FilmDAO.findAll();
    }

    // -------------------------------------------------------------------------

    public boolean addFilm(Film film) {
        if (film == null || film.getTitre() == null || film.getTitre().isEmpty()) {
            return false;
        }
        int generatedId = FilmDAO.save(film);
        return generatedId > 0;
    }

    // -------------------------------------------------------------------------

    public void saveMultipleFilms(List<Film> films) {
        if (films != null && !films.isEmpty()) {
            FilmDAO.saveAll(films);
        }
    }

    // -------------------------------------------------------------------------

    public boolean deleteFilm(int id) {
        Film f = FilmDAO.findById(id);
        if (f != null) {
            return FilmDAO.delete(id);
        }
        return false;
    }

    // -------------------------------------------------------------------------

    public List<Film> filterFilmsByTitle(String keyword) {
        List<Film> allFilms = FilmDAO.findAll();
        return allFilms.stream()
                .filter(f -> f.getTitre().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}
