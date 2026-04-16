package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.RateDAO;
import groupna.projectNetflix.entities.Rate;

public class RateService {
    public boolean noterContenu(int idUser, int idOeuvre, int nbStars, String type) {
        if (nbStars < 1 || nbStars > 5) {
        	return false;
        }

        Rate rate = new Rate(idUser, idOeuvre, nbStars);
        return RateDAO.saveOrUpdate(rate, type);
    }
    public int getNoteUtilisateur(int idUser, int idOeuvre, String type) {
        if (idUser <= 0 || idOeuvre <= 0) return 0;
        return RateDAO.findUserRating(idUser, idOeuvre, type);
    }

    public double getMoyenneOeuvre(int idOeuvre, String type) {
        double moyenne = RateDAO.getAverageRating(idOeuvre, type);
        return Math.round(moyenne * 10.0) / 10.0;
    }
    public boolean supprimerMaNote(int idUser, int idOeuvre, String type) {
        return RateDAO.deleteRate(idUser, idOeuvre, type);
    }
}