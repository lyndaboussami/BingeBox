package groupna.projectNetflix.entities;

import java.time.LocalTime;
import java.util.Objects;

public class Episode implements Visualisable {
	private int numero;
	private int id;
	private String resume;
	private String Titre;
	private LocalTime duree;
	private String PathEp;
	private String PathMiniaure;
	public Episode(int numero, int id, String resume, String titre, LocalTime duree, String pathEp,
			String pathMiniaure) {
		super();
		this.numero = numero;
		this.id = id;
		this.resume = resume;
		Titre = titre;
		this.duree = duree;
		PathEp = pathEp;
		PathMiniaure = pathMiniaure;
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
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public String getPathEp() {
		return PathEp;
	}
	public void setPathEp(String pathEp) {
		PathEp = pathEp;
	}
	public String getPathMiniaure() {
		return PathMiniaure;
	}
	public void setPathMiniaure(String pathMiniaure) {
		PathMiniaure = pathMiniaure;
	}
	public Episode(int numero, int id, String resume, String titre, LocalTime duree, String pathEp) {
		super();
		this.numero = numero;
		this.id = id;
		this.resume = resume;
		Titre = titre;
		this.duree = duree;
		PathEp = pathEp;
	}
	@Override
	public int hashCode() {
		return Objects.hash(PathEp, PathMiniaure, Titre, duree, id, numero, resume);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Episode other = (Episode) obj;
		return Objects.equals(PathEp, other.PathEp) && Objects.equals(PathMiniaure, other.PathMiniaure)
				&& Objects.equals(Titre, other.Titre) && Objects.equals(duree, other.duree) && id == other.id
				&& numero == other.numero && Objects.equals(resume, other.resume);
	}
	
	
}