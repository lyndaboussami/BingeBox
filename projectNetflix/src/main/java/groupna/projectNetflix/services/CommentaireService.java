package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.CommentaireDAO;
import groupna.projectNetflix.entities.commentaire;
import java.util.List;

public class CommentaireService {
    public boolean posterCommentaire(int idUser, int idOeuvre, String contenu, String type) {
        if (contenu == null || contenu.trim().isEmpty()) {
            System.out.println("[Service] Le contenu du commentaire ne peut pas être vide.");
            return false;
        }

        commentaire nouveauCom = new commentaire(idUser, idOeuvre, contenu, false);
        return CommentaireDAO.save(nouveauCom, type);
    }
//---------------------------------------------------------------------------------------
    public List<commentaire> recupererCommentairesOeuvre(int idOeuvre, String type) {
        return CommentaireDAO.findAllByOeuvre(idOeuvre, type);
    }
//-------------------------------------------------------------------------------------------
    public boolean signalerAbus(int idUser, int idOeuvre, String type) {
        return CommentaireDAO.reportCommentaire(idUser, idOeuvre, type);
    }
//----------------------------------------------------------------------------------------------
    public boolean supprimerCommentaire(int idUser, int idOeuvre, String type) {
        return CommentaireDAO.delete(idUser, idOeuvre, type);
    }
//----------------------------------------------------------------------------------------------
    public List<commentaire> listerCommentairesSignales() {
        return CommentaireDAO.findReported();
    }
}