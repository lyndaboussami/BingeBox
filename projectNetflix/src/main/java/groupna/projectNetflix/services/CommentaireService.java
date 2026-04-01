package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.CommentaireDAO;
import groupna.projectNetflix.entities.Commentaire;
import java.util.List;

public class CommentaireService {
	
    public boolean posterCommentaire(int idUser, int idOeuvre, String contenu, String type) {
        if (contenu == null || contenu.trim().isEmpty()) {
            System.out.println("[Service] Le contenu du Commentaire ne peut pas être vide."); //à modifier Alert.. (notification au lieu de syso)
            return false;
        }

        Commentaire nouveauCom = new Commentaire(idUser, idOeuvre, contenu, false,null);
        return CommentaireDAO.save(nouveauCom, type);
    }
//---------------------------------------------------------------------------------------
    public List<Commentaire> recupererCommentairesOeuvre(int idOeuvre, String type) {
        return CommentaireDAO.findAllByOeuvre(idOeuvre, type);
    }
//-------------------------------------------------------------------------------------------
    public boolean signalerAbus(int idUser, int idOeuvre, String type,String raison) {
        return CommentaireDAO.reportCommentaire(idUser, idOeuvre, type,raison);
    }
//----------------------------------------------------------------------------------------------
    public boolean supprimerCommentaire(int idUser, int idOeuvre, String type) {
        return CommentaireDAO.delete(idUser, idOeuvre, type);
    }
//----------------------------------------------------------------------------------------------
    public List<Commentaire> listerCommentairesSignales() {
        return CommentaireDAO.findReported();
    }
}