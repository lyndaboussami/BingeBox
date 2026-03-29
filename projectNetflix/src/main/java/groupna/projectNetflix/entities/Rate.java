package groupna.projectNetflix.entities;

public class Rate {
	private int id_user;
	private int id_oeuvre;
	private int nbStars;
	public Rate(int id_user, int id_oeuvre, int nbStars) {
		super();
		this.id_user = id_user;
		this.id_oeuvre = id_oeuvre;
		this.nbStars = nbStars;
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
	public int getNbStars() {
		return nbStars;
	}
	public void setNbStars(int nbStars) {
		this.nbStars = nbStars;
	}
}
