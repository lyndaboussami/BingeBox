package groupna.projectNetflix.entities;

import java.util.Set;

public class User {
	private int id;
	private String nom;
	private String prenom;
	private String email;
	private String mdp;
	private Role role;
	private Set<Oeuvre> favs;
	private Set<Oeuvre> his;
	public User(int id, String nom, String prenom, String email, String mdp, Role role, Set<Oeuvre> favs,
			Set<Oeuvre> his) {
		super();
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.mdp = mdp;
		this.setRole(role);
		this.favs = favs;
		this.his = his;
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
	public Set<Oeuvre> getHis() {
		return his;
	}
	public void setHis(Set<Oeuvre> his) {
		this.his = his;
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
}
