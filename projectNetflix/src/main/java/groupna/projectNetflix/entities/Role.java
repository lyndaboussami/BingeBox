package groupna.projectNetflix.entities;

public enum Role {
	ADMIN("admin"),
	UTILISATEUR("utilisateur");
	private String role;

	private Role(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
