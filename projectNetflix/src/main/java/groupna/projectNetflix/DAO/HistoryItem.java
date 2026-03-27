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
}
