package assara.group.ossaraanalytics.security.dto;

import java.util.Date;


public class HistoryReponse {

    private Long id;
    private String fullName;
    private String name;
    private Date dateHistory;

    public HistoryReponse() {
    }

    public HistoryReponse(Long id, String fullName, String name, Date dateHistory) {
        this.id = id;
        this.fullName = fullName;
        this.name = name;
        this.dateHistory = dateHistory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateHistory() {
        return dateHistory;
    }

    public void setDateHistory(Date dateHistory) {
        this.dateHistory = dateHistory;
    }
}
