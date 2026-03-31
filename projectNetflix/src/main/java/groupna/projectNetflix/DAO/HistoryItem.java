package groupna.projectNetflix.DAO;

public class HistoryItem {
    private Object content;
    private java.sql.Timestamp dateVisionnage;

    public HistoryItem(Object content, java.sql.Timestamp dateVisionnage) {
        this.content = content;
        this.dateVisionnage = dateVisionnage;
    }
    public Object getContent() { return content; }
    public java.sql.Timestamp getDateVisionnage() { return dateVisionnage; }
    
    /*private String displayName; // e.g., "Inception" or "The Boys - S01E03"
    private String posterPath;
    private String watchedDate;

    public HistoryItem(String displayName, String posterPath, String watchedDate) {
        this.displayName = displayName;
        this.posterPath = posterPath;
        this.watchedDate = watchedDate;
    }

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

	public String getWatchedDate() {
		return watchedDate;
	}

	public void setWatchedDate(String watchedDate) {
		this.watchedDate = watchedDate;
	}*/
    
}
