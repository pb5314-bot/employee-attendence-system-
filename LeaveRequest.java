import java.io.Serializable;
import java.time.LocalDate;

public class LeaveRequest implements Serializable {
    private String email;
    private LocalDate date;
    private String reason;
    private String status;

    public LeaveRequest(String email, LocalDate date, String reason){
        this.email = email;
        this.date = date;
        this.reason = reason;
        this.status = "Pending";
    }

    public String getEmail(){ return email; }
    public LocalDate getDate(){ return date; }
    public String getReason(){ return reason; }
    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }
}
