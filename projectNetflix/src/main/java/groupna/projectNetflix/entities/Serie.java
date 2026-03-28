package groupna.projectNetflix.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Serie extends Oeuvre{
	
	private Map<Saison,List<Episode>> saisons;
	
	public Serie(int id, String resume, List<Categorie> cat, String titre, LocalDate dateDeSortie,
			List<Artiste> acteurs, List<Artiste> directeurs, double rate, String pathPoster,
			Map<Saison, List<Episode>> saisons) {
		super(id, resume, cat, titre, dateDeSortie, acteurs, directeurs, rate, pathPoster);
		this.saisons = saisons;
	}

	public Map<Saison, List<Episode>> getSaisons() {
		return saisons;
	}

	public void setSaisons(Map<Saison, List<Episode>> saisons) {
		this.saisons = saisons;
	}

	@Override
	public String toString() {
		return "Serie [saisons=" + saisons + ", id=" + id + ", resume=" + resume + ", cat=" + cat + ", titre=" + titre
				+ ", DateDeSortie=" + DateDeSortie + ", acteurs=" + acteurs + ", Directeurs=" + Directeurs + ", rate="
				+ rate + ", PathPoster=" + PathPoster + "]";
	}

}