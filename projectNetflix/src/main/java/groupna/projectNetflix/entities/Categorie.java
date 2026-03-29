package groupna.projectNetflix.entities;
	
	public enum Categorie {
		ACTION("Action"),
		AVENTURE("Adventure"),
		COMEDIE("Comedy"),
		DRAME("Drama"),
		HORREUR("Horror"),
		THRILLER("Thriller"),
		WESTERN("Western"),

		FANTASY("Fantasy"),
		SCIENCE_FICTION("Science Fiction"),
		POLICIER("Crime"),
		MYSTERE("Mystery"),

		ANIMATION("Animation"),
		ANIME("Japanese Anime"),
		FAMILLE("Family"),

		DOCUMENTAIRE("Documentary"),
		BIOPIC("Biopic"),
		HISTORIQUE("Historical"),
		REAL_TV("Reality TV"),

		ROMANCE("Romance"),
		MUSICAL("Musical"),
		GUERRE("War");
	
	    private final String label;
	
	    // Constructeur
	    Categorie(String label) {
	        this.label = label;
	    }
	
	    // Getter pour récupérer le nom lisible
	    public String getLabel() {
	        return label;
	    }
	}