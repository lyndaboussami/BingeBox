package groupna.projectNetflix.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Film extends Oeuvre implements Visualisable {
	
	@Override
	public String toString() {
		return "Film [duree=" + duree + ", PathMovie=" + PathMovie + ", PathTrailer=" + PathTrailer + ", id=" + id
				+ ", resume=" + resume + ", cat=" + cat + ", titre=" + titre + ", DateDeSortie=" + DateDeSortie
				+ ", acteurs=" + acteurs + ", Directeurs=" + Directeurs + ", rate=" + rate + ", PathPoster="
				+ PathPoster + "]";
	}

	private LocalTime duree;
	private String PathMovie;
	private String PathTrailer;

	public Film(int id, String resume, List<Categorie> cat, String titre, LocalDate dateDeSortie, List<Artiste> acteurs,
			List<Artiste> directeurs, double rate, String pathPoster, LocalTime duree, String pathMovie,
			String pathTrailer) {
		super(id, resume, cat, titre, dateDeSortie, acteurs, directeurs, rate, pathPoster);
		this.duree = duree;
		setPathMovie(pathMovie);
		PathTrailer = pathTrailer;
	}

	public LocalTime getDuree() {
		return duree;
	}

	public void setDuree(LocalTime duree) {
		this.duree = duree;
	}
	

	public String getPathTrailer() {
		return PathTrailer;
	}

	public void setPathTrailer(String pathTrailer) {
		PathTrailer = pathTrailer;
	}

	public String getPathMovie() {
		return PathMovie;
	}

	public void setPathMovie(String pathMovie) {
		PathMovie = pathMovie;
	}
}