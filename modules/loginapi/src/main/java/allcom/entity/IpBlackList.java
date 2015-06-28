package allcom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@SuppressWarnings("serial")
@Entity
@Table(name = "myqbauth_ipblacklist")
//@NamedQuery(name = Account.FIND_BY_EMAIL, query = "select a from Account a where a.email = :email")
public class IpBlackList implements java.io.Serializable {

    //public static final String FIND_BY_EMAIL = "Account.findByEmail";

    @Id
    private String ip;

    private Timestamp createTime;

    protected IpBlackList() {

    }

    public IpBlackList(String ip,Timestamp createTime) {
        this.ip = ip;
        this.createTime = createTime;
    }

    public String getIp(){
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }


    public Timestamp getCreateTime () {
        return this.createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }



}
