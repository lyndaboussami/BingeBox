package groupna.projectNetflix.entities;

public class commentaire {
	private int id_user;
	private int id_oeuvre;
	private String content;
	public commentaire(int id_user, int id_oeuvre, String content) {
		super();
		this.id_user = id_user;
		this.id_oeuvre = id_oeuvre;
		this.content = content;
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
	
	
}

