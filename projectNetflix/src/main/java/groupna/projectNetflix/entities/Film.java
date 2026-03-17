package groupna.projectNetflix.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Film extends Oeuvre {
	private LocalTime duree;
	private String URL;
	public Film(int id, String resume, List<Categorie> cat, String titre, LocalDate dateDeSortie, List<Artiste> acteurs,
			List<Artiste> directeurs, double rate, String uRLann, LocalTime duree, String uRL) {
		super(id, resume, cat, titre, dateDeSortie, acteurs, directeurs, rate, uRLann);
		this.duree = duree;
		URL = uRL;
	}

	public LocalTime getDuree() {
		return duree;
	}

	public void setDuree(LocalTime duree) {
		this.duree = duree;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
}
