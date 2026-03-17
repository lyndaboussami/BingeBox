package groupna.projectNetflix.entities;

import java.util.List;
public class Artiste {
	private int id;
	private String nom;
	private String prenom;
	private String Bio;
	private List<String> oeuvreMajeurs;
	public Artiste(int id, String nom, String prenom, String bio, List<String> oeuvreMajeurs) {
		this.setId(id);
		this.nom = nom;
		this.prenom = prenom;
		Bio = bio;
		this.oeuvreMajeurs = oeuvreMajeurs;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getBio() {
		return Bio;
	}
	public void setBio(String bio) {
		Bio = bio;
	}
	public List<String> getOeuvreMajeurs() {
		return oeuvreMajeurs;
	}
	public void setOeuvreMajeurs(List<String> oeuvreMajeurs) {
		this.oeuvreMajeurs = oeuvreMajeurs;
	}
	@Override
	public String toString() {
		return "Artiste [nom=" + nom + ", prenom=" + prenom + ", Bio=" + Bio + ", oeuvreMajeurs=" + oeuvreMajeurs + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
