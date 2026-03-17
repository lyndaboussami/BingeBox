package groupna.projectNetflix.entities;

import java.time.LocalTime;

public class Episode {
	private int numero;
	private int id;
	private String resume;
	private String Titre;
	private LocalTime duree;
	private String URLann;
	private String URL;
	public Episode(String resume, String titre, LocalTime duree,String URLann,String URL,int num) {
		this.resume = resume;
		Titre = titre;
		this.duree = duree;
		this.URLann=URLann;
		this.setURL(URL);
		this.numero=num;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getTitre() {
		return Titre;
	}
	public void setTitre(String titre) {
		Titre = titre;
	}
	public LocalTime getDuree() {
		return duree;
	}
	public void setDuree(LocalTime duree) {
		this.duree = duree;
	}
	@Override
	public String toString() {
		return "Episode [resume=" + resume + ", Titre=" + Titre + ", duree=" + duree + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getURLann() {
		return URLann;
	}
	public void setURLann(String uRLann) {
		URLann = uRLann;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	
}
