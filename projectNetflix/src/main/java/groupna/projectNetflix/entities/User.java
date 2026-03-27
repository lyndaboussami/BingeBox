package groupna.projectNetflix.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {
	private int id;
	private String nom;
	private String prenom;
	private String email;
	private String mdp;
	private Role role;
	private Set<Oeuvre> favs;
	private Map<LocalDate, List<Visualisable>> his;
	public User(int id, String nom, String prenom, String email, String mdp, Role role, Set<Oeuvre> favs,
			Map<LocalDate, List<Visualisable>> his) {
		super();
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.mdp = mdp;
		this.role = role;
		this.favs = favs;
		this.his=his;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	public Set<Oeuvre> getFavs() {
		return favs;
	}
	public void setFavs(Set<Oeuvre> favs) {
		this.favs = favs;
	}
	public int  getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Map<LocalDate, List<Visualisable>> getHis() {
		return his;
	}
	public void setHis(Map<LocalDate, List<Visualisable>> his) {
		this.his = his;
	}
}