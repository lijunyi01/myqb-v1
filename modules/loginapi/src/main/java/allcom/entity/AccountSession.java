package allcom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@SuppressWarnings("serial")
@Entity
@Table(name = "myqbauth_accountsession")
//@NamedQuery(name = Account.FIND_BY_EMAIL, query = "select a from Account a where a.email = :email")
public class AccountSession implements java.io.Serializable {

    //public static final String FIND_BY_EMAIL = "Account.findByEmail";

    @Id
    private int umid;

    @JsonIgnore
    private String sessionId;

    private Timestamp timestamp;

    protected AccountSession() {

    }

    public AccountSession(int umid, String sessionId, Timestamp timestamp) {
        this.umid = umid;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
    }

    public int getId(){
        return this.umid;
    }



    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
