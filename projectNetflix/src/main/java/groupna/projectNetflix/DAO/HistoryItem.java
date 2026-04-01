package groupna.projectNetflix.DAO;

import java.sql.Timestamp;

public class HistoryItem {
    private Object content;
    private java.sql.Timestamp dateVisionnage;
    private double time;

    public HistoryItem(Object content, java.sql.Timestamp dateVisionnage,double time) {
        this.content = content;
        this.dateVisionnage = dateVisionnage;
        this.setTime(time);
        
    }
    public Object getContent() { return content; }
    public Timestamp getDateVisionnage() { return dateVisionnage; }
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}

    
    /*
    private String displayName; // e.g., "Inception" or "The Boys - S01E03"
    private String posterPath;
    private String watchedDate;

    public HistoryItem(String displayName, String posterPath, String watchedDate) {
        this.displayName = displayName;
        this.posterPath = posterPath;
        this.watchedDate = watchedDate;
    }

	public String getDisplayName() {
		return displayName;
=======
	public double getTime() {
		return time;
>>>>>>> branch 'master' of https://github.com/lyndaboussami/BingeBox.git
	}
	public void setTime(double time) {
		this.time = time;
	}
<<<<<<< HEAD

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
