package allcom.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Entity
@Table(name = "myqbauth_smsverifycode",indexes = {@Index(name = "i_1",columnList = "sendTime",unique = false)})
public class SmsVerifyCode implements java.io.Serializable {

    //public static final String FIND_BY_EMAIL = "Account.findByEmail";

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private String sessionId;
    private String phoneNumber;
    private String smsContent;
    private Timestamp sendTime;
    private String sendResult;

    protected SmsVerifyCode() {
    }

    public SmsVerifyCode(String sessionId,int umid,String phoneNumber,String smsContent,Timestamp sendTime,String sendResult) {
        this.phoneNumber = phoneNumber;
        this.smsContent = smsContent;
        this.sendTime = sendTime;
        this.sendResult = sendResult;
        this.umid=umid;
        this.sessionId=sessionId;
    }

    public long getId(){
        return this.id;
    }

    public String getPhoneNumber(){ return this.phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Timestamp getSendTime() {
        return this.sendTime;
    }
    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendResult(){ return this.sendResult; }
    public void setSendResult(String sendResult){ this.sendResult = sendResult;}

    public String getSmsContent(){ return this.smsContent;}
    public void setSmsContent(String smsContent){ this.smsContent = smsContent;}

    public int getUmid(){return this.umid;}
    public void setUmid(int umid) {this.umid = umid;}

    public String getSessionId(){return this.sessionId;}
    public void setSessionId(String sessionId){this.sessionId = sessionId;}

}
