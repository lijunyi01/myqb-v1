package allcom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jboss.logging.annotations.Field;

import javax.persistence.*;
import java.sql.Timestamp;


@SuppressWarnings("serial")
@Entity
//@Table(name = "login_history",indexes = {@Index(name = "i_1",columnList = "userName"),@Index(name = "i_2",columnList = "loginTime")})
@Table(name = "myqbauth_login_history",indexes = {@Index(name = "i_1",columnList = "userName,loginTime",unique = false)})
public class LoginHistory implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private String userName;
    private String ip;
    private Timestamp loginTime;
    private String errorCode;
    private String deviceType;
    private String deviceInfo;

    protected LoginHistory() {
    }

    public LoginHistory(int umid, String userName,Timestamp loginTime) {
        this.umid = umid;
        this.loginTime = loginTime;
        this.userName = userName;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){ return this.umid; }

    public void setUmid(int umid) { this.umid = umid; }


    public Timestamp getLoginTime() {
        return this.loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public String getIp(){ return this.ip; }
    public void setIp(String ip){ this.ip = ip;}

    public String getErrorCode() { return this.errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode;}

    public String getDeviceType(){ return this.deviceType; }
    public void setDeviceType(String deviceType){ this.deviceType = deviceType; }

    public String getDeviceInfo(){ return this.deviceInfo; }
    public void setDeviceInfo(String deviceInfo){ this.deviceInfo = deviceInfo; }

    public String getUserName(){ return  this.userName;}
    public void setUserName(String userName) {this.userName = userName;}

}
