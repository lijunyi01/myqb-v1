package allcom.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Entity
@Table(name = "myqbauth_emailverifycode",indexes = {@Index(name = "i_1",columnList = "sendTime",unique = false)})
public class EmailVerifyCode implements java.io.Serializable {

    //public static final String FIND_BY_EMAIL = "Account.findByEmail";

    @Id
    @GeneratedValue
    private long id;
    private String email;
    private String emailKey;
    private Timestamp sendTime;
    private String sendResult;

    protected EmailVerifyCode() {
    }

    public EmailVerifyCode(String email, String emailKey, Timestamp sendTime, String sendResult) {
        this.email = email;
        this.emailKey = emailKey;
        this.sendTime = sendTime;
        this.sendResult = sendResult;
    }

    public long getId(){
        return this.id;
    }

    public String getEmail(){ return this.email; }

    public void setEmail(String email) { this.email = email; }

    public Timestamp getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendResult(){ return this.sendResult; }

    public void setSendResult(String sendResult){ this.sendResult = sendResult;}

    public String getEmailKey(){ return this.emailKey;}

    public void setEmailKey(String emailkey){ this.emailKey = emailKey;}

}
