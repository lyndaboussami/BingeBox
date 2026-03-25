package groupna.projectNetflix.entities;

import java.time.LocalDate;

public class saison {
	private int id;
	private int num;
	private LocalDate dateDeSortie;
	private String Titre;
	private String resume;
	private String PathTrailer;
	public saison(int id, int num, LocalDate dateDeSortie, String titre, String resume, String pathTrailer) {
		super();
		this.id = id;
		this.num = num;
		this.dateDeSortie = dateDeSortie;
		Titre = titre;
		this.resume = resume;
		PathTrailer = pathTrailer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDate getDateDeSortie() {
		return dateDeSortie;
	}
	public void setDateDeSortie(LocalDate dateDeSortie) {
		this.dateDeSortie = dateDeSortie;
	}
	public String getTitre() {
		return Titre;
	}
	public void setTitre(String titre) {
		Titre = titre;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getPathTrailer() {
		return PathTrailer;
	}
	public void setPathTrailer(String pathTrailer) {
		PathTrailer = pathTrailer;
	}
}