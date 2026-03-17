package groupna.projectNetflix.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Serie extends Oeuvre{
	private Map<saison,List<Episode>> saisons;

	public Serie(int id, String resume, List<Categorie> cat, String titre, LocalDate dateDeSortie,
			List<Artiste> acteurs, List<Artiste> directeurs, double rate, String uRLann,
			Map<saison, List<Episode>> saisons) {
		super(id, resume, cat, titre, dateDeSortie, acteurs, directeurs, rate, uRLann);
		this.saisons = saisons;
	}

	public Map<saison, List<Episode>> getSaisons() {
		return saisons;
	}

	public void setSaisons(Map<saison, List<Episode>> saisons) {
		this.saisons = saisons;
	}
	
}
