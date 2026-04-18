package groupna.projectNetflix.entities;

public class Commentaire {
	private int id;
	private int id_user;
	private int id_oeuvre;
	private boolean reported;
	private String content;
	private String raison;
	public Commentaire(int id_user, int id_oeuvre, String content,boolean reported,String raison,int id) {
		this.id_user = id_user;
		this.id_oeuvre = id_oeuvre;
		this.content = content;
		this.reported=reported;
		this.raison=raison;
		this.id=id;
	}
	public int getId_user() {
		return id_user;
	}
	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
	public int getId_oeuvre() {
		return id_oeuvre;
	}
	public void setId_oeuvre(int id_oeuvre) {
		this.id_oeuvre = id_oeuvre;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isReported() {
		return reported;
	}
	public void setReported(boolean reported) {
		this.reported = reported;
	}
	public String getRaison() {
		return raison;
	}
	public void setRaison(String raison) {
		this.raison = raison;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}