package groupna.projectNetflix.entities;

import java.util.List;
import java.util.Objects;
public class Artiste {
	private int id;
	private String fullname;
	public Artiste(int id, String fullname) {
		super();
		this.id = id;
		this.fullname = fullname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
}