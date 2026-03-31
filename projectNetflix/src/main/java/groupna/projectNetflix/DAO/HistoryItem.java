package groupna.projectNetflix.DAO;

public class HistoryItem {
    private Object content;
    private java.sql.Timestamp dateVisionnage;
    private double time;

    public HistoryItem(Object content, java.sql.Timestamp dateVisionnage,double time) {
        this.content = content;
        this.dateVisionnage = dateVisionnage;
        this.time=time;
        
    }
    public Object getContent() { return content; }
    public java.sql.Timestamp getDateVisionnage() { return dateVisionnage; }
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	
}
