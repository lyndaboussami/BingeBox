package groupna.projectNetflix.entities;

public enum Role {
	ADMIN("admin"),
	USER("user");
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