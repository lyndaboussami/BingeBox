package groupna.projectNetflix.services;

import groupna.projectNetflix.DAO.CommentaireDAO;
import groupna.projectNetflix.entities.Commentaire;
import java.util.List;

public class CommentaireService {
	
    public boolean posterCommentaire(Commentaire c, String type) {
        return CommentaireDAO.save(c, type);
    }
//---------------------------------------------------------------------------------------
    public List<Commentaire> recupererCommentairesOeuvre(int idOeuvre, String type) {
        return CommentaireDAO.findAllByOeuvre(idOeuvre, type);
    }
//-------------------------------------------------------------------------------------------
    public boolean signalerAbus(int idComment, String type,String raison) {
        return CommentaireDAO.reportCommentaire(idComment, type,raison);
    }
//----------------------------------------------------------------------------------------------
    public boolean supprimerCommentaire(int idComment, String type) {
        return CommentaireDAO.delete(idComment, type);
    }
//----------------------------------------------------------------------------------------------
    public List<Commentaire> listerCommentairesSignales() {
        return CommentaireDAO.findReported();
    }
//---------------------------------------------------------------------------------------
    public Boolean validerCommentaire(int idComment, String type) {
    	return CommentaireDAO.validCommentaire(idComment, type);
    }
}