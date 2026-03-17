package groupna.projectNetflix.entities;

import java.time.LocalDate;
import java.util.List;

public class Oeuvre {
	protected int id;
	protected String resume;
	protected List<Categorie> cat;
	protected String titre;
	protected LocalDate DateDeSortie;
	protected List<Artiste> acteurs;
	protected List<Artiste> Directeurs;
	protected double rate;
	protected String URLann;
	
	public Oeuvre(int id, String resume, List<Categorie> cat, String titre, LocalDate dateDeSortie,
			List<Artiste> acteurs, List<Artiste> directeurs, double rate, String uRLann) {
		super();
		this.id = id;
		this.resume = resume;
		this.cat = cat;
		this.titre = titre;
		DateDeSortie = dateDeSortie;
		this.acteurs = acteurs;
		Directeurs = directeurs;
		this.rate = rate;
		URLann = uRLann;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public LocalDate getDateDeSortie() {
		return DateDeSortie;
	}
	public void setDateDeSortie(LocalDate dateDeSortie) {
		DateDeSortie = dateDeSortie;
	}
	public List<Artiste> getActeurs() {
		return acteurs;
	}
	public void setActeurs(List<Artiste> acteurs) {
		this.acteurs = acteurs;
	}
	public List<Artiste> getDirecteurs() {
		return Directeurs;
	}
	public void setDirecteurs(List<Artiste> directeurs) {
		Directeurs = directeurs;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Categorie> getCat() {
		return cat;
	}
	public void setCat(List<Categorie> cat) {
		this.cat = cat;
	}
	public String getURLann() {
		return URLann;
	}
	public void setURLann(String uRLann) {
		URLann = uRLann;
	}
}
