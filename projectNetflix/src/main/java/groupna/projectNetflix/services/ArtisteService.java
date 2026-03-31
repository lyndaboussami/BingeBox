package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.ArtisteDAO;
import groupna.projectNetflix.entities.Artiste;
import java.util.List;

public class ArtisteService {
    public int enregistrerArtiste(Artiste a) {
        int idExistant = ArtisteDAO.getIdIfExists(a.getFullname());
        
        if (idExistant != -1) {
            System.out.println("[Service] L'artiste " + a.getFullname() + " existe déjà (ID: " + idExistant + ").");
            a.setId(idExistant);
            return idExistant;
        }
        return ArtisteDAO.save(a);
    }
//-------------------------------------------------------------------------
    public Artiste trouverArtisteParId(int id) {
        if (id <= 0) return null;
        return ArtisteDAO.findById(id);
    }
//-----------------------------------------------------------------------
    public List<Artiste> recupererTousLesArtistes() {
        return ArtisteDAO.findAll();
    }
//-----------------------------------------------------------------------------
}
