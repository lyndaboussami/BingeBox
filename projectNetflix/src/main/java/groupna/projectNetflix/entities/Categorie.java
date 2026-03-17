	package groupna.projectNetflix.entities;
	
	public enum Categorie {
	    // Catégories Générales
	    ACTION("Action"),
	    AVENTURE("Aventure"),
	    COMEDIE("Comédie"),
	    DRAME("Drame"),
	    HORREUR("Horreur"),
	    THRILLER("Thriller"),
	    WESTERN("Western"),
	    
	    // Imaginaire et Suspense
	    FANTASY("Fantasy"),
	    SCIENCE_FICTION("Science-Fiction"),
	    POLICIER("Policier"),
	    MYSTERE("Mystère"),
	    
	    // Animation et Jeunesse
	    ANIMATION("Animation"),
	    ANIME("Anime Japonais"),
	    FAMILLE("Famille"),
	    
	    // Réalité et Information
	    DOCUMENTAIRE("Documentaire"),
	    BIOPIC("Biopic"),
	    HISTORIQUE("Historique"),
	    REAL_TV("Télé-Réalité"),
	    
	    // Autres genres spécifiques
	    ROMANCE("Romance"),
	    MUSICAL("Comédie Musicale"),
	    GUERRE("Guerre");
	
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